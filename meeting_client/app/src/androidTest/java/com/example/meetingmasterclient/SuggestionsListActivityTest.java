package com.example.meetingmasterclient;

import android.content.Intent;
import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.meetingmasterclient.server.MeetingService;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.example.meetingmasterclient.server.Server.BASE_URL;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.*;


public class SuggestionsListActivityTest {

    @Rule
    public ActivityTestRule<SuggestionsListActivity> activityRule = new ActivityTestRule<>(SuggestionsListActivity.class, false, false);

    @Before
    public void setUp() {
        MockServerUtils.setUpMockService();
    }

    private Intent createSuggestionsListIntent(int id) {
        Intent intent = new Intent();
        intent.putExtra("event_id", id);
        return intent;
    }

    @Test
    public void suggestionsListFirstTest() {
        activityRule.launchActivity(createSuggestionsListIntent(1));

        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());


        device.waitForWindowUpdate(null,5000);

        onView(withRecyclerView(R.id.suggestions_recycler_view)
                .atPositionOnView(0, R.id.address))
                .check(matches(withText("abc")));

    }

    @Test
    public void attendeeListSecondTest() {
        activityRule.launchActivity(createSuggestionsListIntent(3));
        UiDevice device = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());


            device.waitForWindowUpdate(null,5000);



        onView(withRecyclerView(R.id.suggestions_recycler_view)
                .atPositionOnView(0, R.id.city_info))
                .check(matches(withText("testCity3")));


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

    public SuggestionsListActivityTest.RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new SuggestionsListActivityTest.RecyclerViewMatcher(recyclerViewId);
    }

}