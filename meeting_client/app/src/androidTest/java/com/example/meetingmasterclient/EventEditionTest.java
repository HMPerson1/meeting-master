package com.example.meetingmasterclient;

import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

public class EventEditionTest {

    @Rule
    public ActivityTestRule<EventEdition> activityRule = new ActivityTestRule<>(EventEdition.class, false, false);

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

        Espresso.onView(withId(R.id.suggested_locations_button)).perform(scrollTo()).perform(click());
        Espresso.onView(withId(R.id.suggested_locations_button)).perform(scrollTo()).perform(click());
        Espresso.onView(withId(R.id.suggested_locations_button)).perform(scrollTo()).perform(click());

        Espresso.onView(withId(R.id.text_input_street_address)).perform(scrollTo()).check(matches(withText("abc")));
        Espresso.onView(withId(R.id.text_input_city)).perform(scrollTo()).check(matches(withText("easy")));
        Espresso.onView(withId(R.id.text_input_state)).perform(scrollTo()).check(matches(withText("state")));



    }

    @Test
    public void eventEditionSecondTest() { //check event name is changed
        activityRule.launchActivity(createEventEditionIntent(1));

        Espresso.onView(withId(R.id.text_input_event_name)).perform(scrollTo()).perform(typeText("newName"));

        Espresso.onView(withId(R.id.save_meeting_button)).perform(scrollTo()).perform(click());

        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        device.waitForWindowUpdate(null,5000);

        Espresso.onView(withId(R.id.meeting_name)).perform(scrollTo()).check(matches(withText("newName"))); //name


    }

}