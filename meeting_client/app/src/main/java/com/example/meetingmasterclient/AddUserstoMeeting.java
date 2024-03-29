package com.example.meetingmasterclient;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.server.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddUserstoMeeting extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_usersto_meeting);

        final List<String> list = new ArrayList<>(); //used for testing functionality of list

        //Scrollable listview of invited people

        final ListView listViewInvitedPeople = (ListView) findViewById(R.id.list_view_invited_people);

        final ArrayAdapter adapter = new ArrayAdapter<>(AddUserstoMeeting.this, android.R.layout.simple_list_item_multiple_choice, list);

        //make listview checkable so that people can be removed from the list

        final boolean [] checkedItems;

        // listViewInvitedPeople.setItemChecked(1, true);

        listViewInvitedPeople.setAdapter(adapter);

        //if User clicks the remove button, remove all checked items in listview
        Button remove_button = (Button)findViewById(R.id.removebutton);

        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = listViewInvitedPeople.getAdapter().getCount(); i >=0 ; i--){ //loop in reverse order so that arraylist indexes are not shifted upon removal
                    if (listViewInvitedPeople.isItemChecked(i)){ //check if item in listview position i is checked
                        listViewInvitedPeople.setItemChecked(i,false);
                        //remove from shared preferences
                        String userName= list.get(i);
                        SharedPreferences sharedPref = getSharedPreferences("invited_users_IDs",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.remove(userName);
                        editor.commit();
                        list.remove(i);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        //if User clicks the add button, get the email input and search for it in the user database
        Button add_button = (Button)findViewById(R.id.add_button);
        final TextInputEditText Email_Input = (TextInputEditText) findViewById(R.id.EmailInput);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = String.valueOf(Email_Input.getText()); //get Email from input

                Call<List<MeetingService.UserProfile>> call = Server.getService().users(Email_Input.getText().toString());
                call.enqueue(new Callback<List<MeetingService.UserProfile>>() {
                    @Override
                    public void onResponse(Call<List<MeetingService.UserProfile>> call, Response<List<MeetingService.UserProfile>> response) {
                        if(!response.isSuccessful()){ //404 error?
                            Toast.makeText(AddUserstoMeeting.this, "Oops, Something is wrong: "+response.code() , Toast.LENGTH_LONG).show();
                            return;
                        }
                    //    Toast.makeText(AddUserstoMeeting.this,"response" , Toast.LENGTH_LONG).show();
                    //    Toast.makeText(AddUserstoMeeting.this,response.toString() , Toast.LENGTH_LONG).show();

                        //add user to list if successful
                        List<MeetingService.UserProfile> userProfs = response.body();//store response
                        for (MeetingService.UserProfile userProf : userProfs){

                            //check if user already listed, if user is already listed notify user
                            if(list.contains(userProf.username)){
                                Toast.makeText(AddUserstoMeeting.this,"User Already added to List" , Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(AddUserstoMeeting.this, "User added to List", Toast.LENGTH_LONG).show();
                            }
                            addInvitedUser(userProf.username, String.valueOf(userProf.getPk()));//store in shared preferences4
                            list.add(userProf.username);
                            adapter.notifyDataSetChanged();

                        }//end for
                    }
                    @Override
                    public void onFailure(Call<List<MeetingService.UserProfile>> call, Throwable t) {//error from server
                        Toast.makeText(AddUserstoMeeting.this,t.getMessage() , Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        //exit the activity and return to Create a meeting page when the admin presses the save changes button
        configureSaveButton();
    }

    public void addInvitedUser(String key, String value) {
        SharedPreferences sharedPref = getSharedPreferences("invited_users_IDs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void configureSaveButton(){
        Button save_button = (Button)findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//return to create a meeting
            }
        });
    }
}
