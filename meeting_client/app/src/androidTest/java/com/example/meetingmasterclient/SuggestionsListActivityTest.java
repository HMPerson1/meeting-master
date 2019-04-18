package com.example.meetingmasterclient;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.*;

public class SuggestionsListActivityTest {

    @Rule
    public ActivityTestRule<SuggestionsListActivity> activityRule = new ActivityTestRule<>(SuggestionsListActivity.class, false, false);

    @Before
    public void setUp() {
        MockServerUtils.setUpMockService();
    }

    private Intent createSuggestiosnsListIntent(int id) {
        Intent intent = new Intent();
        intent.putExtra("event_id", 1);
        return intent;
    }

    @Test
    public void suggestionsListFirstTest() {
        activityRule.launchActivity(createSuggestiosnsListIntent(1));

        Espresso.onView(new SuggestionsListActivityTest.RecyclerViewMatcher(R.id.suggestions_recycler_view).atPosition(0))
                .check(matches(hasDescendant(withText("ghf"))));
    }
/*
    @Test
    public void attendeeListSecondTest() {
        activityRule.launchActivity(createAttendeeListIntent(3));

        AttendeeListViewTest.RecyclerViewMatcher rvm = new AttendeeListViewTest.RecyclerViewMatcher(R.id.recycler_view_invited_people);

        Espresso.onView(rvm.atPosition(1))
                .check(matches(hasDescendant(withText("John Doe"))));

        Espresso.onView(rvm.atPosition(2))
                .check(matches(hasDescendant(withText("John Dutchman"))));
    }
*/
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

    public SuggestionsListActivityTest.RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new SuggestionsListActivityTest.RecyclerViewMatcher(recyclerViewId);
    }

}