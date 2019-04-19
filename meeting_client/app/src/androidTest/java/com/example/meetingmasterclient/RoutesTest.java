package com.example.meetingmasterclient;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.example.meetingmasterclient.utils.Route;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RoutesTest {
    @Rule
    public ActivityTestRule<MapsActivity> activityRule
            = new ActivityTestRule<>(MapsActivity.class, false, false);

    private Intent createRoutesIntent(int id) {
        Intent intent = new Intent();
        intent.putExtra("event_id", id);
        return intent;
    }

    @Before
    public void setUp() {
        MockServerUtils.setUpMockService();
    }

    @Test
    public void routesFirstTest() throws InterruptedException {
        activityRule.launchActivity(createRoutesIntent(1));
        Thread.sleep(10000);
        assertTrue(!Route.polylines.isEmpty());
    }

    @Test
    public void routesSecondTest() throws InterruptedException {
        activityRule.launchActivity(createRoutesIntent(2));
        Thread.sleep(10000);
        assertTrue(!Route.polylines.isEmpty());
    }

    @Test
    public void routesThirdTest() throws InterruptedException {
        activityRule.launchActivity(createRoutesIntent(3));
        Thread.sleep(10000);
        assertTrue(!Route.polylines.isEmpty());
    }

    @Test
    public void routesFourthTest() throws InterruptedException {
        activityRule.launchActivity(createRoutesIntent(4));
        Thread.sleep(10000);
        assertTrue(!Route.polylines.isEmpty());
    }
}
