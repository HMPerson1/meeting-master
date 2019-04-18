package com.example.meetingmasterclient;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;


import org.junit.Rule;

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
