package com.example.meetingmasterclient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;
import com.example.meetingmasterclient.utils.FileDownload;
import com.example.meetingmasterclient.utils.StartingSoonAlarm;

import java.util.List;
import java.util.Optional;

import androidx.test.espresso.idling.CountingIdlingResource;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetails extends AppCompatActivity {
    public CountingIdlingResource idlingResource = new CountingIdlingResource("Event Details Network");
    private static final int LOCATION_PERMISSION = 90;
    private static final int FILE_PERMISSION = 20;
    public static final String PREFS_NAME = "App_Settings";
    private int eventID;
    private String userID;
    private Button attendeeListButton;
    private Button suggestLocationButton;
    private Button mapButton;
    private Button viewAttachmentButton;
    private UserInvitationStatus userInvitationStatus = UserInvitationStatus.NONE;
    private UserEventState userEventState = UserEventState.DIFFERENT_EVENT;
    private MeetingService.EventsData eventInfo;
    private ViewAnimator statusContainer;
    private ViewGroup contentView;

    //TODO disable "View Attachment" button if no attachment exists in document

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        //testing changing text fields
        final TextInputEditText nameInput = (TextInputEditText) findViewById(R.id.meeting_name);
        final TextInputEditText textInputDate = findViewById(R.id.date);
        final TextInputEditText textInputTime = findViewById(R.id.time);
        final TextInputEditText textDuration = findViewById(R.id.duration);
        final TextInputEditText textInputNotes = findViewById(R.id.notes);
        final TextInputEditText textInputStreetAddr = findViewById(R.id.street);
        final TextInputEditText textInputCity = findViewById(R.id.city);
        final TextInputEditText textInputState = findViewById(R.id.state);
        final TextInputEditText textInputRoomNo = findViewById(R.id.room_num);

        attendeeListButton = (Button) findViewById(R.id.view_attendees_button);
        attendeeListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventDetails.this, AttendeeList.class));
            }
        });

        //TODO: get info from backend
        //MeetingService.EventData eventData = new MeetingService.EventData();
        //get the current intent
        Intent intent = getIntent();
        eventID = intent.getIntExtra("event_id", -1);
        userID = intent.getStringExtra("user_id");


        userID="1";

        viewAttachmentButton = (Button) findViewById(R.id.add_attachments);

        if (eventID<0){
            finish();  //did not pass event_id
        }

        //TODO: get event info from backend
        idlingResource.increment();
        Call<MeetingService.EventsData> call = Server.getService().getEventfromId(eventID);
        call.enqueue(new Callback<MeetingService.EventsData>() {
            @Override
            public void onResponse(Call<MeetingService.EventsData> call, Response<MeetingService.EventsData>response) {
                if(!response.isSuccessful()){ //404 error?
                    Toast.makeText(EventDetails.this, "Oops, Something is wrong: " +
                            response.code(), Toast.LENGTH_LONG).show();
                    idlingResource.decrement();
                    return;
                }
                Toast.makeText(EventDetails.this,"response" , Toast.LENGTH_LONG).show();
                Toast.makeText(EventDetails.this,response.toString() , Toast.LENGTH_LONG).show();


                eventInfo = response.body();//store response

                //display the event details to the user
                nameInput.setText(eventInfo.getEvent_name());
                textInputDate.setText(textInputDate.getText()+ "    "+eventInfo.getEvent_date());
                textInputTime.setText(textInputTime.getText()+"    "+eventInfo.getEvent_time());
                textDuration.setText(textDuration.getText()+"    "+eventInfo.getEvent_duration());
                textInputNotes.setText(textInputNotes.getText()+"    "+eventInfo.getNotes());

                //get location details from server

                MeetingService.LocationData locationInfo= eventInfo.getEvent_location();


                textInputStreetAddr.setText(textInputStreetAddr.getText()+ "    "+locationInfo.getStreet_address());
                textInputCity.setText(textInputCity.getText()+ "    "+locationInfo.getCity());
                textInputState.setText(textInputState.getText()+ "    "+locationInfo.getState());

                //textInputRoomNo.setText(locationInfo.);

                viewAttachmentButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {
                            getFileFromServer(
                                    eventInfo.getFile_attachment(),
                                    eventInfo.getEvent_name());
                        } else {
                            ActivityCompat.requestPermissions(EventDetails.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    FILE_PERMISSION);
                        }
                    }
                });

                idlingResource.decrement();
                updateUiStatusContainer();
            }

            @Override
            public void onFailure(Call<MeetingService.EventsData> call, Throwable t) {//error from server

                Toast.makeText(EventDetails.this,t.getMessage() , Toast.LENGTH_LONG).show();
                idlingResource.decrement();

            }

        });

        suggestLocationButton = (Button) findViewById(R.id.suggest_location_button);
        suggestLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent suggest = new Intent(getApplicationContext(), LocationSuggestion.class);
                suggest.putExtra("event_id", eventID);
                startActivity(suggest);
            }
        });

        mapButton = (Button) findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent map = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(map);
            }
        });

        contentView = findViewById(R.id.content_event_details);
        statusContainer = findViewById(R.id.active_status_container);

        fetchUserEventState();
    }

    private void fetchEventData() {
        Server.getService().getEventfromId(eventID).enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        eventInfo = response.body();
                        updateUiStatusContainer();
                    } else {
                        Toast.makeText(getApplicationContext(), "An error has occurred while retrieving event data", Toast.LENGTH_SHORT).show();
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    private void fetchUserEventState() {
        Server.getService().getUserStatus().enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        MeetingService.ActiveEventsData body = response.body();
                        assert body != null;
                        onUpdatedUserActiveEventState(body);
                    } else {
                        Toast.makeText(getApplicationContext(), "An error has occurred while retrieving status", Toast.LENGTH_SHORT).show();
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
        Server.getService().getUsersInvitations().enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        List<MeetingService.InvitationData> body = response.body();
                        assert body != null;
                        Optional<MeetingService.InvitationData> invite =
                                body.stream().filter(inv -> inv.event_id == eventID).findAny();
                        onUpdatedInvitationStatus(invite.orElse(null));
                    } else {
                        Toast.makeText(getApplicationContext(), "An error has occurred while retrieving status", Toast.LENGTH_SHORT).show();
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    private void onUpdatedInvitationStatus(@Nullable MeetingService.InvitationData invite) {
        userInvitationStatus =
                Optional.ofNullable(invite)
                        .map(inv -> UserInvitationStatus.values()[inv.status])
                        .orElse(UserInvitationStatus.NONE);
        updateUiStatusContainer();
    }

    private void onUpdatedUserActiveEventState(@NonNull MeetingService.ActiveEventsData body) {
        userEventState = (body.state == 0 || body.event == eventID)
                ? UserEventState.values()[body.state]
                : UserEventState.DIFFERENT_EVENT;
        updateUiStatusContainer();
    }

    private void getFileFromServer(String url, String name) {
        Call<ResponseBody> c = Server.getService().downloadFile(url);
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        (new FileDownload(getApplicationContext(), response.body(), name + url.substring(url.lastIndexOf("."))))
                                .execute();
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    private void changeInvitationStatus(int eventID, String userID, int newStatus) {
        idlingResource.increment();
        Server.getService().setInvitationStatus(String.valueOf(eventID), userID, newStatus
        ).enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        MeetingService.InvitationData body = response.body();
                        assert body != null;
                        onUpdatedInvitationStatus(body);
                    } else {
                        Toast.makeText(getApplicationContext(), "An error has occurred while updating invitation status", Toast.LENGTH_SHORT).show();
                    }
                    idlingResource.decrement();
                },
                (call, t) -> {
                    t.printStackTrace();
                    idlingResource.decrement();
                }
        ));
    }

    public boolean openAlertDialog(){
        AlertDialogDelete alertDialogDelete = new AlertDialogDelete();
        alertDialogDelete.show(getSupportFragmentManager(), "warning");
        boolean userConfirm = alertDialogDelete.getStatus();
        System.out.println("Sure? " + userConfirm);
        return userConfirm;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.notificationSwitch).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.isChecked()){
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                String NotificationStatusKey= String.valueOf(eventID) +"checkStatus";
                editor.putBoolean(NotificationStatusKey, menuItem.isChecked());
                editor.commit();
                /* true = notifications on, false= notifications off
                    key= eventID+checkStatus;
                 */
                /*
                //get pref
                Boolean value = sharedPref.getBoolean(NotificationStatusKey, false);
                Log.d(TAG, value.toString());
                */
                return false;
            }
        });

        menu.findItem(R.id.edit_event).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent intent = new Intent(getApplicationContext(), EventEdition.class);
                intent.putExtra("event_id", eventID);
                startActivity(intent);


                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_event_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.delete_event_menu:
                boolean userConfirm = openAlertDialog();
                userConfirm = true; //TODO delete this line once openAlertDialog() is fixed
                if (userConfirm) {
                    deleteEvent();
                }
                return true;
            case R.id.export_event_menu:
                exportEvent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteEvent(){
        idlingResource.increment();
        Call<Void> d = Server.getService().deleteEvent("/events/" + eventID + "/");
        d.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        //Server.authenticate(response.body().key);
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.RegistrationError.class,
                                System.out::println, System.out::println);
                    }
                    idlingResource.decrement();
                },
                (call, t) -> {
                    t.printStackTrace();
                    idlingResource.decrement();
                }
        ));
    }

    public void exportEvent(){
        //TODO implement
        CSVExporter csvExporter = new CSVExporter();

        //display the event details to the user
        String name = eventInfo.getEvent_name();
        String date = eventInfo.getEvent_date();
        String time = eventInfo.getEvent_time();
        String notes = eventInfo.getNotes();

        csvExporter.setEvent(name, date, time, notes);
        csvExporter.writeToFile("test.csv");

        //TODO finish implementation. Needs an intent and sent to calendar apps
    }

    private void updateUiStatusContainer() {
        if (eventInfo == null) return;
        // TODO: if (eventInfo.event_admin == userID) userInvitationStatus = UserInvitationStatus.ACCEPTED;
        int statusContainerVisibility;
        int statusContainerChildIdx;
        switch (userInvitationStatus) {
            case NONE:
                statusContainerVisibility = View.GONE;
                statusContainerChildIdx = 0;
                break;
            case PENDING:
                statusContainerVisibility = View.VISIBLE;
                statusContainerChildIdx = 0;
                break;
            case DECLINED:
                statusContainerVisibility = View.VISIBLE;
                statusContainerChildIdx = 2;
                break;
            case ACCEPTED:
            default:
                switch (eventInfo.current_overall_state) {
                    case 0: // NOT_STARTED
                        statusContainerVisibility = View.VISIBLE;
                        statusContainerChildIdx = 1;
                        break;
                    case 1: // STARTING
                        switch (userEventState) {
                            case NOT_ACTIVE:
                                statusContainerVisibility = View.VISIBLE;
                                statusContainerChildIdx = 3;
                                break;
                            case GOING_TO:
                                statusContainerVisibility = View.VISIBLE;
                                statusContainerChildIdx = 4;
                                break;
                            case CURRENTLY_AT:
                                statusContainerVisibility = View.VISIBLE;
                                statusContainerChildIdx = 5;
                                break;
                            case LEAVING_FROM:
                                statusContainerVisibility = View.VISIBLE;
                                statusContainerChildIdx = 6;
                                break;
                            case DIFFERENT_EVENT:
                            default:
                                statusContainerVisibility = View.GONE;
                                statusContainerChildIdx = 0;
                                break;
                        }
                        break;
                    case 2: // ONGOING
                        switch (userEventState) {
                            case NOT_ACTIVE:
                            case DIFFERENT_EVENT:
                                statusContainerVisibility = View.GONE;
                                statusContainerChildIdx = 0;
                                break;
                            case CURRENTLY_AT:
                            default:
                                statusContainerVisibility = View.VISIBLE;
                                statusContainerChildIdx = 5;
                                break;
                            case LEAVING_FROM:
                                statusContainerVisibility = View.VISIBLE;
                                statusContainerChildIdx = 6;
                                break;
                        }
                        break;
                    case 3: // ENDING
                        switch (userEventState) {
                            case NOT_ACTIVE:
                            case DIFFERENT_EVENT:
                                statusContainerVisibility = View.GONE;
                                statusContainerChildIdx = 0;
                                break;
                            case LEAVING_FROM:
                            default:
                                statusContainerVisibility = View.VISIBLE;
                                statusContainerChildIdx = 6;
                                break;
                        }
                        break;
                    case 4: // OVER
                    default:
                        statusContainerVisibility = View.VISIBLE;
                        statusContainerChildIdx = 7;
                        break;
                }
        }

        TransitionManager.beginDelayedTransition(contentView, new Slide(Gravity.TOP));
        statusContainer.setVisibility(statusContainerVisibility);
        statusContainer.setDisplayedChild(statusContainerChildIdx);
    }

    public void onAcceptInviteClicked(View _ignored) {
        changeInvitationStatus(eventID, userID, 2);
        StartingSoonAlarm.scheduleStartingSoonAlarm(getApplicationContext(),
                eventID, eventInfo.getEvent_date(), eventInfo.getEvent_time());
    }

    public void onDeclineInviteClicked(View _ignored) {
        changeInvitationStatus(eventID, userID, 3);
        StartingSoonAlarm.cancelStartingSoonAlarm(getApplicationContext(), eventID);
    }

    public void onDepartClicked(View _ignored) {
        changeUserActiveEventState(1, this::startLocationUpdateService);
    }

    public void onArriveClicked(View _ignored) {
        changeUserActiveEventState(2, () ->
                stopService(new Intent(getBaseContext(), LocationUpdateService.class)));
    }

    public void onLeaveClicked(View _ignored) {
        changeUserActiveEventState(3, this::startLocationUpdateService);
    }

    public void onArriveHomeClicked(View _ignored) {
        changeUserActiveEventState(0, () ->
                stopService(new Intent(getBaseContext(), LocationUpdateService.class)));
    }

    private void changeUserActiveEventState(int newState, Runnable onSuccess) {
        if (newState == 0) {
            Server.getService().deleteUserStatus().enqueue(Server.mkCallback((call, response) -> {
                if (response.isSuccessful()) {
                    onUpdatedUserActiveEventState(new MeetingService.ActiveEventsData(0, 0));
                    onSuccess.run();
                    fetchEventData();
                } else {
                    Toast.makeText(getApplicationContext(), "Status Update Error", Toast.LENGTH_SHORT).show();
                }
            }, (call, t) -> t.printStackTrace()));
        } else {
            Server.getService().putUserStatus(
                    new MeetingService.ActiveEventsData(eventID, newState)
            ).enqueue(Server.mkCallback(
                    (call, response) -> {
                        if (response.isSuccessful()) {
                            MeetingService.ActiveEventsData body = response.body();
                            assert body != null;
                            onUpdatedUserActiveEventState(body);
                            onSuccess.run();
                            fetchEventData();
                        } else {
                            Toast.makeText(getApplicationContext(), "Status Update Error", Toast.LENGTH_SHORT).show();
                        }
                    },
                    (call, t) -> t.printStackTrace()
            ));
        }
    }

    private void startLocationUpdateService() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // TODO
            //LocationUpdateService.start(getApplicationContext(), eventID);
            System.out.println("Permitted");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        //LocationUpdateService.start(getApplicationContext(), eventID);
                        System.out.println("Permitted");
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "You cannot use the location features without these permissions",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case FILE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getFileFromServer(
                            eventInfo.getFile_attachment(),
                            eventInfo.getEvent_name());
                } else {
                    Toast.makeText(getApplicationContext(),
                            "You cannot get the file without these permissions",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    enum UserEventState {
        NOT_ACTIVE, GOING_TO, CURRENTLY_AT, LEAVING_FROM, DIFFERENT_EVENT
    }

    enum UserInvitationStatus {
        NONE, PENDING, ACCEPTED, DECLINED
    }
}
