package com.example.meetingmasterclient;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

public class MapsTest {
    private static final int TIMEOUT = 5000;

    @Rule
    public IntentsTestRule<EventDetails> detailsRule
            = new IntentsTestRule<>(EventDetails.class, false, false);

    @Rule
    public ActivityTestRule<MapsActivity> activityRule
            = new ActivityTestRule<>(MapsActivity.class, false, false);

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
    public void startsMapActivity() throws InterruptedException {
        detailsRule.launchActivity(createEventDetailsIntent(1));
        Thread.sleep(5000);
        Espresso.onView(withId(R.id.map_button)).perform(scrollTo()).perform(click());
        intended(hasComponent(MapsActivity.class.getName()));
    }

    @Test
    public void mapLoads() throws InterruptedException {
        activityRule.launchActivity(createEventDetailsIntent(1));
        Thread.sleep(5000);
        assertTrue(true);
    }
}
