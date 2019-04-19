package com.example.meetingmasterclient;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocationSuggestionTest {


    private Intent createLocationSuggestion(int id){
        Intent intent = new Intent();
        intent.putExtra("event_id", id);
        return intent;
    }

    private String streetSuggest = "305 N University St";
    private String citySuggest = "West Lafayette";
    private String stateSuggest = "IN";
    private String roomNoSuggest = "3102 A/B";

    @Rule//launch activity
    public ActivityTestRule<LocationSuggestion> activityTestRule =
            new ActivityTestRule<>(LocationSuggestion.class);

    @Before
    public void setUp() throws Exception {
        MockServerUtils.setUpMockService();
    }

    @Test
    public void suggestLocationTest(){
        activityTestRule.launchActivity(createLocationSuggestion(2));

        //fill in fields
        Espresso.onView(withId(R.id.suggest_edit_street_address)).perform(typeText(streetSuggest));
        Espresso.onView(withId(R.id.suggest_edit_city)).perform(typeText(citySuggest));
        Espresso.onView(withId(R.id.suggest_edit_state)).perform(typeText(stateSuggest));
        Espresso.onView(withId(R.id.suggest_edit_room_no)).perform(typeText(roomNoSuggest));

        //click "suggest"
        Espresso.onView(withId(R.id.suggest_button)).perform(click());
    }

    @Test
    public void suggestLocationwithoutStreetAddrTest(){
        activityTestRule.launchActivity(createLocationSuggestion(2));

        //fill in some fields
        Espresso.onView(withId(R.id.suggest_edit_city)).perform(typeText(citySuggest));
        Espresso.onView(withId(R.id.suggest_edit_state)).perform(typeText(stateSuggest));
        Espresso.onView(withId(R.id.suggest_edit_room_no)).perform(typeText(roomNoSuggest));

        Espresso.onView(withId(R.id.suggest_button)).perform(click());

        assertFalse(activityTestRule.getActivity().isFinishing());
    }
}
