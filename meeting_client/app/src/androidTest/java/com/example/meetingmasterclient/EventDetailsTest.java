package com.example.meetingmasterclient;


import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.not;

public class EventDetailsTest {

    @Rule//launch activity
    public ActivityTestRule<EventDetails> activityRule =
            new ActivityTestRule(EventDetails.class);
    private String meetingName = "Birthday Party";
    private String date = "10/05/18";
    private String time = "10:30pm";
    private String notes = "random data";
    private String someText = "some text";


    private Intent createEventDetailsIntent(int id) {
        Intent intent = new Intent();
        intent.putExtra("event_id", id);
        return intent;
    }

    @Before
    public void setUp() {
        MockServerUtils.setUpMockService();
    }

    @Test
    public void testDisplayEventDetails(){
        //display name of Event, check if name of Event is displayed
        Espresso.onView(withId(R.id.meeting_name)).perform(replaceText(meetingName))
                .check(matches(withText(meetingName)));

        Espresso.onView(withId(R.id.date)).perform(replaceText(date))
                .check(matches(withText(date)));

        Espresso.onView(withId(R.id.time)).perform(replaceText(time))
                .check(matches(withText(time)));

        Espresso.onView(withId(R.id.notes)).perform(replaceText(notes))
                .check(matches(withText(notes)));

    }

    @Test
    public void testDisplayLocationDetails(){
        //display name of Event, check if name of Event is displayed
        Espresso.onView(withId(R.id.street)).perform(replaceText(someText))
                .check(matches(withText(someText)));

        Espresso.onView(withId(R.id.city)).perform(replaceText(someText))
                .check(matches(withText(someText)));

        Espresso.onView(withId(R.id.state)).perform(replaceText(someText))
                .check(matches(withText(someText)));

        Espresso.onView(withId(R.id.room_num)).perform(replaceText(someText))
                .check(matches(withText(someText)));

    }

    @Test
    public void testViewAttendeesButton(){
        Espresso.onView(withId(R.id.view_attendees_button)).perform(click());

    }

    @Test
    public void testArriveHomeButton1(){
        activityRule.launchActivity(createEventDetailsIntent(1));
        try{
            sleep(5000);
        }catch (Exception e){}

        onView(withId(R.id.leave_button)).perform(click());
        try{
            sleep(5000);
        }catch (Exception e){}
        onView(withId(R.id.arrive_home_button)).perform(click());


    }

    @Test
    public void testArriveHomeButton2(){
        activityRule.launchActivity(createEventDetailsIntent(1));
        try{
            sleep(5000);
        }catch (Exception e){}
        onView(withId(R.id.leave_button)).perform(click());

        try{
            sleep(5000);
        }catch (Exception e){}

        Espresso.onView(withId(R.id.arrive_home_button)).check(matches((isDisplayed())));

    }



    @After
    public void tearDown() throws Exception {
    }
}