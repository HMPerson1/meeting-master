package com.example.meetingmasterclient;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class EventDetailsTest {

    @Rule//launch activity
    public ActivityTestRule<EventDetails> activityRule =
            new ActivityTestRule(EventDetails.class);
    private String meetingName = "Birthday Party";

    @Before//preload things for test
    public void setUp() throws Exception {

    }

    @Test
    public void testDisplayEventName(){
        //display name of Event, check if name of Event is displayed
        Espresso.onView(withId(R.id.meeting_name)).perform(replaceText(meetingName));
        Espresso.onView(withId(R.id.meeting_name)).check(matches(withText(meetingName)));
    }

    @After
    public void tearDown() throws Exception {
    }
}