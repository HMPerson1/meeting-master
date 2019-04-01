package com.example.meetingmasterclient;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.meetingmasterclient.utils.Notifications;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NotificationTest {

    private static final int TIMEOUT = 1000;

    @Test
    public void notifInvitedHasName() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifications.ensureNotificationChannels(appContext);
        Notifier notifier = new Notifier(appContext);

        String event_name = "Alice's Party!";

        notifier.onMessageRecieved(makeNotifyInivteData("999", event_name));
        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.notification_invite_title))), TIMEOUT));
        // check notification content
        assertNotNull(uiDevice.findObject(By.textContains(event_name)));

        clearAllNotifications();
    }

    @Test
    public void notifInvitedStartsEventDetails() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifications.ensureNotificationChannels(appContext);
        Notifier notifier = new Notifier(appContext);

        String event_name = "Alice's Party!";

        notifier.onMessageRecieved(makeNotifyInivteData("999", event_name));
        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.notification_invite_title))), TIMEOUT));
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
        Notifications.ensureNotificationChannels(appContext);
        Notifier notifier = new Notifier(appContext);

        String event_name = "Alice's Party!";

        notifier.onMessageRecieved(makeNotifyEditData("999", event_name));
        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.notification_edit_title))), TIMEOUT));
        // check notification content
        assertNotNull(uiDevice.findObject(By.textContains(event_name)));

        clearAllNotifications();
    }

    @Test
    public void notifEditedStartsEventDetails() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifications.ensureNotificationChannels(appContext);
        Notifier notifier = new Notifier(appContext);

        String event_name = "Alice's Party!";

        notifier.onMessageRecieved(makeNotifyEditData("999", event_name));
        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.notification_edit_title))), TIMEOUT));
        uiDevice.findObject(By.textContains(event_name)).click();
        // check activity
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.title_activity_event_details))), TIMEOUT));

        clearAllNotifications();
    }

    @Test
    public void notifEditedSurpressed() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifications.ensureNotificationChannels(appContext);
        Notifier notifier = new Notifier(appContext);

        String event_name = "Alice's Party!";

        SharedPreferences preferences = appContext.getSharedPreferences(EventDetails.PREFS_NAME, Context.MODE_PRIVATE);
        synchronized (appContext.getApplicationContext()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("3355checkStatus", false);
            editor.commit();
        }

        notifier.onMessageRecieved(makeNotifyEditData("3355", event_name));
        // wait for (lack of) notification
        assertFalse(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));

        synchronized (appContext.getApplicationContext()) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("3355checkStatus");
            editor.commit();
        }

        clearAllNotifications();
    }

    @Test
    public void notifArrivedHomeHasName() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifications.ensureNotificationChannels(appContext);
        Notifier notifier = new Notifier(appContext);

        String user_full_name = "Alice Fourier";

        notifier.onMessageRecieved(makeNotifyArrivedHomeData(user_full_name));
        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.notification_arrived_home_title))), TIMEOUT));
        // check notification content
        assertNotNull(uiDevice.findObject(By.textContains(user_full_name)));

        clearAllNotifications();
    }

    @Test
    public void notifArrivedHomeAutoCancels() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifications.ensureNotificationChannels(appContext);
        Notifier notifier = new Notifier(appContext);

        String user_full_name = "Alice Fourier";

        notifier.onMessageRecieved(makeNotifyArrivedHomeData(user_full_name));
        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.notification_arrived_home_title))), TIMEOUT));
        uiDevice.findObject(By.textContains(user_full_name)).click();
        // check dismissed
        uiDevice.openNotification();
        assertFalse(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.notification_arrived_home_title))), TIMEOUT));

        clearAllNotifications();
    }

    // Helpers

    private void clearAllNotifications() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();
        UiObject2 clearAll = uiDevice.wait(Until.findObject(By.text("CLEAR ALL")), TIMEOUT);
        if (clearAll != null) {
            clearAll.click();
        }
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

    private Map<String, String> makeNotifyArrivedHomeData(String user_full_name) {
        Map<String, String> ret = new HashMap<>();
        ret.put("kind", "arrived_home");
        ret.put("user_full_name", user_full_name);
        return ret;
    }
}
