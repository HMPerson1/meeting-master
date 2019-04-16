package com.example.meetingmasterclient;

import android.content.Intent;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class LeaveButtonTest {
    @Rule
    public ActivityTestRule<EventDetails> activityRule
            = new ActivityTestRule<>(EventDetails.class, false, false);

    private Intent createEventDetailsIntent(int id) {
        Intent intent = new Intent();
        intent.putExtra("event_id", id);
        return intent;
    }

    @Test
    public void buttonAppearsOnlyDuringEvent() {
        IdlingRegistry.getInstance().register(activityRule.getActivity().idlingResource);

        activityRule.launchActivity(createEventDetailsIntent(1));

        Espresso.onView(withId(R.id.leave_event_button))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void buttonDoesNotAppearOutsideEvent() {
        IdlingRegistry.getInstance().register(activityRule.getActivity().idlingResource);

        activityRule.launchActivity(createEventDetailsIntent(2));

        Espresso.onView(withId(R.id.leave_event_button))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }
}
