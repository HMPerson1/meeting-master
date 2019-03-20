package com.example.meetingmasterclient;

import android.content.Intent;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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

        DataInteraction data = Espresso.onData(anything()).inAdapterView(withId(R.id.event_data)).atPosition(0);

        data.onChildView(withId(R.id.from)).check(matches(withText("masternasu")));
        data.onChildView(withId(R.id.event_name)).check(matches(withText("Autographs From Nasu")));
        data.onChildView(withId(R.id.event_date)).check(matches(withText("2019-03-30")));
        // TODO: Check location
        //data.onChildView(withId(R.id.event_place)).check(matches(withText("2019-03-30")));
    }

    @Test
    public void eventListViewSecondTest() {
        activityRule.launchActivity(createEventListViewIntent(2));


    }
}

