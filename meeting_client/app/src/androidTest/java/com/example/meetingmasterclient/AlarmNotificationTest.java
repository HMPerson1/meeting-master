package com.example.meetingmasterclient;

import android.content.Context;

import com.example.meetingmasterclient.server.MeetingService;
import com.example.meetingmasterclient.utils.LeaveNowAlarm;
import com.example.meetingmasterclient.utils.Notifications;

import org.junit.Test;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AlarmNotificationTest {
    private static final int TIMEOUT = 1000;

    @Test
    public void leaveNotifFirstTest() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifications.ensureNotificationChannels(appContext);
        MeetingService.EventsData event = new MeetingService.EventsData();
        event.pk = 1;
        event.event_name = "Autographs";
        LeaveNowAlarm.setNotification(appContext, event);

        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        assertTrue(uiDevice.wait(Until.hasObject(By.text(event.event_name)), TIMEOUT));
        // check notification content
        assertNotNull(uiDevice.findObject(By.textContains(appContext.getString(R.string.notification_leave_now_body))));
    }

    @Test
    public void leaveNotifSecondTest() {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.openNotification();

        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Notifications.ensureNotificationChannels(appContext);
        MeetingService.EventsData event = new MeetingService.EventsData();
        event.pk = 2;
        event.event_name = "Soccer Game";
        LeaveNowAlarm.setNotification(appContext, event);

        // wait for notification
        assertTrue(uiDevice.wait(Until.hasObject(By.text(appContext.getString(R.string.app_name))), TIMEOUT));
        assertTrue(uiDevice.wait(Until.hasObject(By.text(event.event_name)), TIMEOUT));
        // check notification content
        assertNotNull(uiDevice.findObject(By.textContains(appContext.getString(R.string.notification_leave_now_body))));
    }
}
