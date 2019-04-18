package com.example.meetingmasterclient;

import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

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
    public void buttonAppearsOnlyDuringEvent() {

    }

    @Test
    public void buttonDoesNotAppearOutsideEvent() {

    }
}
