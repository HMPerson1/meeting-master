package com.example.meetingmasterclient;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import retrofit2.Call;

public class EventDetails extends AppCompatActivity {
    private int eventID;
    Button confirmButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        //testing changing text fields
        final TextInputEditText nameInput = (TextInputEditText) findViewById(R.id.meeting_name);
        final TextInputEditText textInputDate = findViewById(R.id.date);
        final TextInputEditText textInputTime = findViewById(R.id.time);
        final TextInputEditText textInputNotes = findViewById(R.id.notes);
        final TextInputEditText textInputStreetAddr = findViewById(R.id.street);
        final TextInputEditText textInputCity = findViewById(R.id.city);
        final TextInputEditText textInputState = findViewById(R.id.state);
        final TextInputEditText textInputRoomNo = findViewById(R.id.room_num);

        eventID = getEventByID(getIntent().getIntExtra("id", -1));

        //TODO: get info from backend
        //MeetingService.EventData eventData = new MeetingService.EventData();
    }

    public boolean openAlertDialog(){
        AlertDialogDelete alertDialogDelete = new AlertDialogDelete();
        alertDialogDelete.show(getSupportFragmentManager(), "warning");
        boolean userConfirm = alertDialogDelete.getStatus();
        System.out.println("Sure? " + userConfirm);
        return userConfirm;
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

    private int getEventByID(int id){
        if (id == -1) {
            Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
            return -1;
        }

        Call<MeetingService.EventData> c = Server.getService().getEvent("/events/" + id + "/");
        c.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        // TODO: set TextField values to retrieved event
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.EventDataError.class, System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));

        return id;
    }

    public void deleteEvent(){
        Call<Void> d = Server.getService().deleteEvent("/events/" + eventID + "/");
        d.enqueue(Server.mkCallback(
                (call, response) -> {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        //Server.authenticate(response.body().key);
                    } else {
                        Server.parseUnsuccessful(response, MeetingService.RegistrationError.class, System.out::println, System.out::println);
                    }
                },
                (call, t) -> t.printStackTrace()
        ));
    }

    public void exportEvent(){
        //TODO implement
    }
}
