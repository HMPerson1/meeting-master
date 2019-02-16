
package com.example.meetingmasterclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
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

/*
        final List list = new ArrayList<>(); //used for testing functionality of list

        for (int i = 0; i < 20; i++) {

            list.add("Person" + i);

        }


        //Scrollable listview of invited people

        final ListView listViewInvitedPeople = (ListView) findViewById(R.id.list_view_invited_people);


        final ArrayAdapter<String> adapter = new ArrayAdapter<>(AddUserstoMeeting.this, android.R.layout.simple_list_item_multiple_choice, list);

        //make listview checkable so that people can be removed from the list
        //people are removed from the list once a checkbox is checked
        boolean [] checkedItems;
        listViewInvitedPeople.setItemChecked(1, true);


        listViewInvitedPeople.setAdapter(adapter);

        listViewInvitedPeople.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               list.remove(position);
               listViewInvitedPeople.setItemChecked(position, false);
               adapter.notifyDataSetChanged();
            }
        });


    }
    private void checkIfBoxChecked(Adapter adapter, ListView listViewInvitedPeople, List list){
        for (int i = 0; i < adapter.getCount(); i++) {
            boolean check_stat = listViewInvitedPeople.isItemChecked(i);
            if (check_stat == true) {
                list.remove(0);
                adapter.notify();
            }
        }
    */}//check if box checked

}

//TODO: exit the activity and return to Create a meeting page

//TODO: Store data in database once user exits the activity

