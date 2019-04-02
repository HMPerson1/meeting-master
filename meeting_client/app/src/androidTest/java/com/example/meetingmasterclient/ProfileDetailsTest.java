package com.example.meetingmasterclient;

import android.content.Intent;

import com.example.meetingmasterclient.server.MeetingService;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

public class ProfileDetailsTest {
    @Rule
    public ActivityTestRule<ProfileDetails> activityRule
            = new ActivityTestRule<>(ProfileDetails.class, false, false);

    private Intent createProfileDetailsIntent(int id) {
        Intent intent = new Intent();
        intent.putExtra("id", id);
        return intent;
    }

/*
    @Test
    public void profileDetailsFirstTest() {
        activityRule.launchActivity(createProfileDetailsIntent(1));

        // TODO: Check profile picture

        Espresso.onView(withId(R.id.profile_details_name))
                .check(matches(withText("Daniel Sanchez (sanch232)")));

        Espresso.onView(withId(R.id.profile_details_email))
                .check(matches(withText("sanch232@purdue.edu")));

        Espresso.onView(withId(R.id.profile_details_phone))
                .check(matches(withText("469-210-5396")));
    }

    @Test
    public void profileDetailsSecondTest() {
        activityRule.launchActivity(createProfileDetailsIntent(2));

        // TODO: Check profile picture

        Espresso.onView(withId(R.id.profile_details_name))
                .check(matches(withText("Kinoko Nasu (masternasu)")));

        Espresso.onView(withId(R.id.profile_details_email))
                .check(matches(withText("nasufate@gmail.com")));

        Espresso.onView(withId(R.id.profile_details_phone))
                .check(matches(withText("030-001-2004")));
    }
*/
}
