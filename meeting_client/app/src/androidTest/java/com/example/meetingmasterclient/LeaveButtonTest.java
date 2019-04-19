package com.example.meetingmasterclient;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;


public class LeaveButtonTest {
    @Rule
    public ActivityTestRule<EventDetails> activityRule
            = new ActivityTestRule<>(EventDetails.class, false, false);

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
    public void buttonAppearsOnlyDuringEvent() throws InterruptedException {
        activityRule.launchActivity(createEventDetailsIntent(1));
        Thread.sleep(5000);
        Espresso.onView(withId(R.id.leave_button)).check(matches(isDisplayed()));
    }

    @Test
    public void buttonDoesNotAppearOutsideEvent() throws InterruptedException {
        activityRule.launchActivity(createEventDetailsIntent(2));
        Thread.sleep(5000);
        Espresso.onView(withId(R.id.leave_button)).check(matches(not(isDisplayed())));
    }
}
