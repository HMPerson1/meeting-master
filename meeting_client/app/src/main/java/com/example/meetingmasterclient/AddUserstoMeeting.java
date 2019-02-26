
package com.example.meetingmasterclient;

import android.app.ActionBar;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AddUserstoMeeting extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_usersto_meeting);

    //TODO: get information(list) of invited people from database
        

        final List<String> list = new ArrayList<>(); //used for testing functionality of list

        for (int i = 0; i < 20; i++) {

            list.add("Person" + i);

        }


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
                Log.d("Email", Email);
                //Check if Email in the Correct Components
               if (!(Email.contains("@")||Email.contains("."))){
                    //edit hint in textbox to indicate to user that they entered an invalid email
                    Log.d("insideif", "if");
                    Toast.makeText(AddUserstoMeeting.this, "Invalid Email", Toast.LENGTH_SHORT).show();

                }
                //TODO
                //check if user is in database
                //if user not in database indicate to user that the email is invalid
                //if user is in database, add user to the list of invited people

            }
        });

        //TODO: Before user returns to create a meeting page, store the list of users in the database
        //exit the activity and return to Create a meeting page when the admin presses the save changes button
        configureSaveButton();
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






