package com.example.meetingmasterclient;

import android.content.Context;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NotificationTest {

    private static final int TIMEOUT = 1000;

    @Test
    public void notifInvitedHasName() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifier notifier = new Notifier(appContext);

        String event_name = "Alice's Party!";

        notifier.onMessageRecieved(makeNotifyInivteData("999", event_name));
        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        // check notification content
        assertNotNull(uiDevice.findObject(By.textContains(event_name)));

        clearAllNotifications();
    }

    @Test
    public void notifInvitedStartsEventDetails() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifier notifier = new Notifier(appContext);

        String event_name = "Alice's Party!";

        notifier.onMessageRecieved(makeNotifyInivteData("999", event_name));
        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        uiDevice.findObject(By.textContains(event_name)).click();
        // check activity
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.title_activity_event_details))), TIMEOUT));

        clearAllNotifications();
    }

    @Test
    public void notifEditedHasName() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifier notifier = new Notifier(appContext);

        String event_name = "Alice's Party!";

        notifier.onMessageRecieved(makeNotifyEditData("999", event_name));
        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        // check notification content
        assertNotNull(uiDevice.findObject(By.textContains(event_name)));

        clearAllNotifications();
    }

    @Test
    public void notifEditedStartsEventDetails() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifier notifier = new Notifier(appContext);

        String event_name = "Alice's Party!";

        notifier.onMessageRecieved(makeNotifyEditData("999", event_name));
        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        uiDevice.findObject(By.textContains(event_name)).click();
        // check activity
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.title_activity_event_details))), TIMEOUT));

        clearAllNotifications();
    }

    // Helpers

    private void clearAllNotifications() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();
        uiDevice.wait(Until.hasObject(By.text("CLEAR ALL")), TIMEOUT);
        UiObject2 clearAll = uiDevice.findObject(By.text("CLEAR ALL"));
        clearAll.click();
        uiDevice.wait(Until.gone(By.text("CLEAR ALL")), TIMEOUT);
    }

    private Map<String, String> makeNotifyInivteData(String event_id, String event_name) {
        Map<String, String> ret = new HashMap<>();
        ret.put("kind", "invite");
        ret.put("event_id", event_id);
        ret.put("event_name", event_name);
        return ret;
    }

    private Map<String, String> makeNotifyEditData(String event_id, String event_name) {
        Map<String, String> ret = new HashMap<>();
        ret.put("kind", "edit");
        ret.put("event_id", event_id);
        ret.put("event_name", event_name);
        return ret;
    }
}
