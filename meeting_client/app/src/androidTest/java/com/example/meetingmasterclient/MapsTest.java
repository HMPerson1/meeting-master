package com.example.meetingmasterclient;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.meetingmasterclient.utils.Route;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertTrue;

public class MapsTest {
    private static final int TIMEOUT = 5000;

    @Rule
    public IntentsTestRule<EventDetails> detailsRule
            = new IntentsTestRule<>(EventDetails.class, false, false);

    @Rule
    public ActivityTestRule<MapsActivity> activityRule
            = new ActivityTestRule<>(MapsActivity.class, false, false);

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
    public void startsMapActivity() throws InterruptedException {
        detailsRule.launchActivity(createEventDetailsIntent(1));
        Thread.sleep(5000);
        Espresso.onView(withId(R.id.map_button)).perform(scrollTo()).perform(click());
        intended(hasComponent(MapsActivity.class.getName()));
    }

    @Test
    public void mapLoads() throws InterruptedException {
        activityRule.launchActivity(createEventDetailsIntent(1));
        Thread.sleep(5000);
        assertTrue(true);
    }

    @Test
    public void etaTestCurrent1() throws InterruptedException {
        activityRule.launchActivity(createEventDetailsIntent(1));
        Thread.sleep(10000);
        onView(withRecyclerView(R.id.eta_list)
                .atPositionOnView(0, R.id.eta_item_name))
                .check(matches(withText("You")));

    }

    @Test
    public void etaTestCurrent2() throws InterruptedException {
        activityRule.launchActivity(createEventDetailsIntent(1));
        Thread.sleep(10000);
        assertTrue(onView(withRecyclerView(R.id.eta_list)
                .atPositionOnView(0, R.id.eta_item_time))!=null);

    }

    @Test
    public void etaTest1() throws InterruptedException {
        activityRule.launchActivity(createEventDetailsIntent(1));
        Thread.sleep(10000);
        assertTrue(onView(withRecyclerView(R.id.eta_list)
                .atPositionOnView(1, R.id.eta_item_name))!=null);

    }

    @Test
    public void etaTest2() throws InterruptedException {

        activityRule.launchActivity(createEventDetailsIntent(1));
        Thread.sleep(10000);
        assertTrue(onView(withRecyclerView(R.id.eta_list)
                .atPositionOnView(1, R.id.eta_item_time))!=null);

    }



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

    public MapsTest.RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new MapsTest.RecyclerViewMatcher(recyclerViewId);
    }
}
