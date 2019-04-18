package com.example.meetingmasterclient;

import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class EventEditionTest {

    @Rule
    public ActivityTestRule<SuggestionsListActivity> activityRule = new ActivityTestRule<>(SuggestionsListActivity.class, false, false);

    @Before
    public void setUp() {
        MockServerUtils.setUpMockService();
    }

    private Intent createEventEditionIntent(int id) {
        Intent intent = new Intent();
        intent.putExtra("event_id", 1);
        return intent;
    }

    @Test
    public void eventEditionFirstTest() { //check current info data populates fields
        activityRule.launchActivity(createEventEditionIntent(1));

        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());


        device.waitForWindowUpdate(null,5000);



    }

    @Test
    public void eventEditionSecondTest() { //check event name is changed
        activityRule.launchActivity(createEventEditionIntent(3));

        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());


        device.waitForWindowUpdate(null,5000);


    }

}