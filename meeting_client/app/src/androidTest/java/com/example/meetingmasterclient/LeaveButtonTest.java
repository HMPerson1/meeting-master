package com.example.meetingmasterclient;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


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
