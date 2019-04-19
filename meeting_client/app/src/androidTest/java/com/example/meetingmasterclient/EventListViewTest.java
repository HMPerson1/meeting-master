package com.example.meetingmasterclient;

import android.content.Intent;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

public class EventListViewTest {
    @Rule
    public ActivityTestRule<ProfileDetails> activityRule
            = new ActivityTestRule<>(ProfileDetails.class, false, false);

    private Intent createEventListViewIntent(int id) {
        Intent intent = new Intent();
        intent.putExtra("user_id", id);
        return intent;
    }

    @Test
    public void eventListViewFirstTest() {
        activityRule.launchActivity(createEventListViewIntent(1));

        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Espresso.onView(withId(R.id.show_declined_events)).perform(click());

        DataInteraction data1 = Espresso.onData(anything()).inAdapterView(withId(R.id.event_data)).atPosition(0);
        data1.onChildView(withId(R.id.event_name)).check(matches(withText("Autographs From Nasu")));
        data1.onChildView(withId(R.id.event_date)).check(matches(withText("2019-03-22")));
        // TODO: Check location
        //data1.onChildView(withId(R.id.event_place)).check(matches(withText("2019-03-30")));

        DataInteraction data2 = Espresso.onData(anything()).inAdapterView(withId(R.id.event_data)).atPosition(1);
        data2.onChildView(withId(R.id.event_name)).check(matches(withText("CS 307 Review")));
        data2.onChildView(withId(R.id.event_date)).check(matches(withText("2019-03-22")));
        // TODO: Check location
        //data2.onChildView(withId(R.id.event_place)).check(matches(withText("2019-03-30")));
    }

    @Test
    public void eventListViewSecondTest() {
        activityRule.launchActivity(createEventListViewIntent(2));

        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        Espresso.onView(withId(R.id.show_declined_events)).perform(click());

        DataInteraction data1 = Espresso.onData(anything()).inAdapterView(withId(R.id.event_data)).atPosition(0);
        data1.onChildView(withId(R.id.event_name)).check(matches(withText("Autographs From Nasu")));
        data1.onChildView(withId(R.id.event_date)).check(matches(withText("2019-03-22")));
        // TODO: Check location
        //data1.onChildView(withId(R.id.event_place)).check(matches(withText("2019-03-30")));

        DataInteraction data2 = Espresso.onData(anything()).inAdapterView(withId(R.id.event_data)).atPosition(1);
        data2.onChildView(withId(R.id.event_name)).check(matches(withText("CS 307 Review")));
        data2.onChildView(withId(R.id.event_date)).check(matches(withText("2019-03-22")));
        // TODO: Check location
        //data2.onChildView(withId(R.id.event_place)).check(matches(withText("2019-03-30")));
    }
}

