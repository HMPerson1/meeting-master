package com.example.meetingmasterclient;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.EventLog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;
import com.example.meetingmasterclient.utils.FileDownload;
import com.example.meetingmasterclient.utils.StartingSoonAlarm;
import androidx.test.espresso.idling.CountingIdlingResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetails extends AppCompatActivity {
    public CountingIdlingResource idlingResource = new CountingIdlingResource("Event Details Network");
    private static final int LOCATION_PERMISSION = 90;
    private static final int FILE_PERMISSION = 20;
    public static final String PREFS_NAME = "App_Settings";
    private static final String TAG = "DebugLauncherActivity";
    int eventID;    //TODO this will change to string
    String userID;
    private Button attendeeListButton;
    private Button acceptInviteButton;
    private Button declineInviteButton;
    private Button suggestLocationButton;
    private Button mapButton;
    private Button leaveEventButton;
    private Button viewAttachmentButton;

    MeetingService.EventsData eventInfo;
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

        eventID = getEventByID(getIntent().getIntExtra("id", -1));

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

        idlingResource.increment();
        Call<MeetingService.EventsData> call = Server.getService().getEventfromId(String.valueOf(eventID));
        call.enqueue(new Callback<MeetingService.EventsData>() {
            @Override
            public void onResponse(Call<MeetingService.EventsData> call, Response<MeetingService.EventsData>response) {
                if(!response.isSuccessful()){ //404 error?
                    Toast.makeText(EventDetails.this, "Oops, Something is wrong: " +
                            response.code(), Toast.LENGTH_LONG).show();
                    idlingResource.decrement();
                    return;
                }
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
            }

            @Override
            public void onFailure(Call<MeetingService.EventsData> call, Throwable t) {//error from server
                Toast.makeText(EventDetails.this,t.getMessage() , Toast.LENGTH_LONG).show();
                idlingResource.decrement();
            }

        });

        //TODO figure out which button starts enabled/disabled
        acceptInviteButton = (Button) findViewById(R.id.Accept);
        acceptInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeInvitationStatus(eventID, userID, 2);
                acceptInviteButton.setEnabled(false);
                declineInviteButton.setEnabled(true);
                StartingSoonAlarm.scheduleStartingSoonAlarm(getApplicationContext(), eventID);
            }
        });

        declineInviteButton = (Button) findViewById(R.id.decline);
        declineInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeInvitationStatus(eventID, userID, 3);
                declineInviteButton.setEnabled(false);
                acceptInviteButton.setEnabled(true);
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

        attendeeListButton = (Button) findViewById(R.id.view_attendees_button);
        attendeeListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EventDetails.this, "Passing event_id " + eventID, Toast.LENGTH_LONG).show();
                Intent attendees = new Intent(EventDetails.this, AttendeeList.class);
                attendees.putExtra("event_id", eventID);
                startActivity(attendees);
            }
        });

        checkEventLeave();

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

    public String statusToString(int status){
        if (status == 1){
            return "Pending";
        } else if (status == 2){
            return "Accepted";
        } else if (status == 3){
            return "Declined";
        } else {
            return "Ope";
        }
    }

    public void changeInvitationStatus(int eventID, String userID, int newStatus){
        idlingResource.increment();
        Call<Void> c = Server.getService().setInvitationStatus(String.valueOf(eventID), userID, newStatus);
        c.enqueue
                (Server.mkCallback(
                        (call, response) -> {
                            Toast.makeText(EventDetails.this, "Response = " + response.toString(),
                                    Toast.LENGTH_LONG).show();
                            if (response.isSuccessful()) {
                                Toast.makeText(EventDetails.this, "Status successfully changed" +
                                        " to " + statusToString(newStatus), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(EventDetails.this, "Status failure",
                                        Toast.LENGTH_LONG).show();
                                //TODO: make InvitationData error
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

    private int getEventByID(int eventID){
        /*if (eventID == -1) {
            Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
            return -1;
        }*/

        idlingResource.increment();
        Call<MeetingService.EventData> c = Server.getService().getEvent("/events/" + eventID+ "/");
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        // TODO: set TextField values to retrieved event
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.EventDataError.class,
                                System.out::println, System.out::println);
                    }
                    idlingResource.decrement();
                },
                (call, t) -> {
                    t.printStackTrace();
                    idlingResource.decrement();
                }
        ));

        return eventID;
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

    private void checkEventLeave() {
        leaveEventButton = (Button) findViewById(R.id.leave_event_button);
        idlingResource.increment();
        Call<MeetingService.ActiveEventsData> c = Server.getService().getUserStatus();
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        if (response.body().state == 2) {
                            leaveEventButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    leaveEvent();
                                }
                            });

                            leaveEventButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "An error has occurred while retrieving status", Toast.LENGTH_SHORT).show();
                    }

                    idlingResource.decrement();
                },
                (call, t) -> {
                    t.printStackTrace();
                    idlingResource.decrement();
                }
        ));
    }

    private void leaveEvent() {
        idlingResource.increment();
        Call<MeetingService.ActiveEventsData> c = Server.getService().putUserStatus(
                new MeetingService.ActiveEventsData(eventID, 3)
        );
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            //LocationUpdateService.start(getApplicationContext(), eventID);
                            System.out.println("Permitted");
                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                    LOCATION_PERMISSION);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Status Update Error", Toast.LENGTH_SHORT).show();
                    }

                    idlingResource.decrement();
                },
                (call, t) -> {
                    t.printStackTrace();
                    idlingResource.decrement();
                }
        ));
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
}
