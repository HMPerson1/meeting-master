package com.example.meetingmasterclient;

import android.content.Intent;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

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


    }
}
