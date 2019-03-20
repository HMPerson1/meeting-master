package com.example.meetingmasterclient;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.example.meetingmasterclient.server.MeetingService;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.rule.ActivityTestRule;

public class AttendeeListViewTest {
    private static Matcher<MeetingService.UserProfile> attendeeIsEqual(final MeetingService.UserProfile profile) {
        return new BoundedMatcher<MeetingService.UserProfile, MeetingService.UserProfile>(MeetingService.UserProfile.class) {
            @Override
            public boolean matchesSafely(MeetingService.UserProfile up) {
                // TODO: Insert equality condition for remaining attributes
                return up.pk == profile.pk && up.username.equals(profile.username) && up.first_name.equals(profile.first_name);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("attendee is equal");
            }
        };
    }

    @Rule
    public ActivityTestRule<AttendeeListView> activityRule = new ActivityTestRule<>(AttendeeListView.class, false, false);

    @Test
    public void testAttendeeList() {
        Intent intent = new Intent();
        intent.putExtra("id", 1);
        activityRule.launchActivity(intent);
    }

    // Placeholder so that the file compiles; will be removed when the attendee list activity is completed
    class AttendeeListView extends AppCompatActivity {}
}