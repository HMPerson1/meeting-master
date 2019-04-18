package com.example.meetingmasterclient;

import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class EventCreationTest {
    private String someText = "some text";

    @Rule//launch activity
    public ActivityTestRule<EventCreation> activityRule =
            new ActivityTestRule(EventCreation.class);

    @Before
    public void setUp() throws Exception {
    }
    @Test
    public void attendeeListButton() {
        Espresso.onView(withId(R.id.attendees_list_button)).perform(scrollTo()).perform(click());

    }

    @Test
    public void addUsersButton() {

        Espresso.onView(withId(R.id.add_users_button)).perform(scrollTo()).perform(click());


    }

    @Test
    public void create_a_meeting_Button() {

        Espresso.onView(withId(R.id.create_meeting_button)).perform(scrollTo()).perform(click());

    }

    @Test
    public void testInputLocationDetails(){
        //display name of Event, check if name of Event is displayed
        Espresso.onView(withId(R.id.text_input_room_no)).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_city)).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_street_address)).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_state)).perform(typeText(someText));



        //Espresso.onView(withId(R.id.create_meeting_button));

        //Espresso.onView(withId(R.id.text_input_street_address)).check(matches(withText(someText)));


    }

    @Test
    public void testInputEventDetails(){
        //display name of Event, check if name of Event is displayed
        Espresso.onView(withId(R.id.text_input_event_name)).perform(scrollTo()).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_date)).perform(scrollTo()).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_duration)).perform(scrollTo()).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_notes)).perform(scrollTo()).perform(typeText(someText));


    }

    @Test
    public void inputAndCreate() {
        //display name of Event, check if name of Event is displayed
        Espresso.onView(withId(R.id.text_input_event_name)).perform(scrollTo()).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_date)).perform(scrollTo()).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_duration)).perform(scrollTo()).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_notes)).perform(scrollTo()).perform(typeText(someText));

        //display name of Event, check if name of Event is displayed
        Espresso.onView(withId(R.id.text_input_room_no)).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_city)).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_street_address)).perform(typeText(someText));
        Espresso.onView(withId(R.id.text_input_state)).perform(typeText(someText));


        Espresso.onView(withId(R.id.create_meeting_button)).perform(scrollTo()).perform(click());

    }


    @After
    public void tearDown() throws Exception {
    }



}