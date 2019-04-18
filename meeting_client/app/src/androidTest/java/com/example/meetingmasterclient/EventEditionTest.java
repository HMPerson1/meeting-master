package com.example.meetingmasterclient;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;



import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


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

        onView(withId(R.id.suggested_locations_button)).perform(scrollTo()).perform(click());
        /*onView(withRecyclerView(R.id.suggestions_recycler_view)
                .atPositionOnView(0, R.id.city_info))
                .perform(click());*/

        device.waitForWindowUpdate(null,5000);

        onView(withId(R.id.text_input_street_address)).perform(scrollTo()).check(matches(withText("abc")));
        onView(withId(R.id.text_input_city)).perform(scrollTo()).check(matches(withText("easy")));
        onView(withId(R.id.text_input_state)).perform(scrollTo()).check(matches(withText("state")));



    }

    @Test
    public void eventEditionSecondTest() { //check event name is changed
        activityRule.launchActivity(createEventEditionIntent(1));

        onView(withId(R.id.text_input_event_name)).perform(scrollTo()).perform(typeText("newName"));

        onView(withId(R.id.save_meeting_button)).perform(scrollTo()).perform(click());

        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        device.waitForWindowUpdate(null,5000);

        onView(withId(R.id.meeting_name)).perform(scrollTo()).check(matches(withText("newName"))); //name


    }
/*
    class RecyclerViewMatcher {
        private final int recyclerViewId;

        public RecyclerViewMatcher(int recyclerViewId) {
            this.recyclerViewId = recyclerViewId;
        }

        public Matcher<View> atPosition(final int position) {
            return atPositionOnView(position, -1);
        }

        public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

            return new TypeSafeMatcher<View>() {
                Resources resources = null;
                View childView;

                public void describeTo(Description description) {
                    String idDescription = Integer.toString(recyclerViewId);
                    if (this.resources != null) {
                        try {
                            idDescription = this.resources.getResourceName(recyclerViewId);
                        } catch (Resources.NotFoundException var4) {
                            idDescription = String.format("%s (resource name not found)",
                                    new Object[] { Integer.valueOf
                                            (recyclerViewId) });
                        }
                    }

                    description.appendText("with id: " + idDescription);
                }

                public boolean matchesSafely(View view) {

                    this.resources = view.getResources();

                    if (childView == null) {
                        RecyclerView recyclerView =
                                (RecyclerView) view.getRootView().findViewById(recyclerViewId);
                        if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
                            childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
                        }
                        else {
                            return false;
                        }
                    }

                    if (targetViewId == -1) {
                        return view == childView;
                    } else {
                        View targetView = childView.findViewById(targetViewId);
                        return view == targetView;
                    }

                }
            };
        }
    }

    public EventEditionTest.RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new EventEditionTest.RecyclerViewMatcher(recyclerViewId);
    }

*/

}

