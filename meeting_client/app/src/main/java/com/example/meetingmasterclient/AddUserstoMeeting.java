package com.example.meetingmasterclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class AddUserstoMeeting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_usersto_meeting);

//TODO: get information on invited people from database

        //TODO: option2 addusers is part of create a meeting activity


        List list = new ArrayList<>(); //used for testing functionality of list

        for (int i = 0; i < 20; i++) {

            list.add("Person" + i);

        }


        //Scrollable listview of invited people

        ListView listViewInvitedPeople = (ListView) findViewById(R.id.list_view_invited_people);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddUserstoMeeting.this, android.R.layout.simple_list_item_multiple_choice, list);

        boolean [] checkedItems;
        listViewInvitedPeople.setItemChecked(1, true);
        for (int i = 0; i < adapter.getCount(); i++) {
            listViewInvitedPeople.setItemChecked(i, true);
        }

        listViewInvitedPeople.setAdapter(adapter);

        //TODO: make listview checkable so that people can be removed from the list

    }
}

//TODO: exit the activity and return to Create a meeting page

//TODO: Store data in database once user exits the activity

