package com.tuxan.holytime.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.tuxan.holytime.R;
import com.tuxan.holytime.data.provider.MeditationColumns;
import com.tuxan.holytime.data.provider.MeditationProvider;
import com.tuxan.holytime.utils.PermissionHelper;
import com.tuxan.holytime.utils.Utils;

import java.util.Calendar;

public class NotificationService extends IntentService implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String ACTION_SCHEDULE_NOTIFICATION_SERVICE = "com.tuxan.holytime.action.START_SCHEDULE_NOTIFICATION_SERVICE";
    public static final String ACTION_SCHEDULE_NEXT_NOTIFICATION = "com.tuxan.holytime.action.SCHEDULE_NEXT_NOTIFICATION";
    public static final String ACTION_SHOW_NOTIFICATION = "com.tuxan.holytime.action.SHOW_NOTIFICATION";
    public static final String EXTRA_MEDITATION_ID = "com.tuxan.holytime.EXTRA_MEDITATION_ID";

    public NotificationService() {
        super("HolyTimeNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction() == null)
            return;

        Log.d("NotificationService", "onHandleIntent");

        Log.d("NotificationService", "checking permission to current location");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationService", "Asking for permission");
                PermissionHelper.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION },
                        0, getString(R.string.app_name), "Requesting permission to access your current location", R.mipmap.ic_launcher);
            } else {
                handleIntent(intent);
            }
        } else {
            handleIntent(intent);
        }
    }

    private void handleIntent(Intent intent) {
        switch (intent.getAction()) {
            case ACTION_SCHEDULE_NOTIFICATION_SERVICE: handleStartSchedule(); break;
            case ACTION_SCHEDULE_NEXT_NOTIFICATION: handleNextNotification(); break;
            case ACTION_SHOW_NOTIFICATION: handleShowNotification(intent); break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            handleStartSchedule();
        }
    }

    private void handleStartSchedule() {

        Log.d("NotificationService", "Starting schedule checking");

        Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(ACTION_SCHEDULE_NEXT_NOTIFICATION);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                alarmIntent);
    }

    private void handleNextNotification() {

        Log.d("NotificationService", "Starting set next notification if is friday");

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.FRIDAY) {

            Log.d("NotificationService", "Is friday so set next notification alarm");

            SunriseSunsetCalculator calculator = Utils.getSunriseSunsetCalculator(this);
            Calendar sunset = calculator.getOfficialSunsetCalendarForDate(c);

            setNextNotificationAlarm(c.get(Calendar.WEEK_OF_YEAR), sunset.getTimeInMillis() - c.getTimeInMillis());
        }
    }

    private void setNextNotificationAlarm(int weekNumber, long delay) {
        Log.d("NotificationService", "Notification will trigger in " + delay + " milliseconds");
        // TODO: get meditation ID by weekNumber and schedule notification with intent to corresponding Meditation Content...

        Cursor c = getContentResolver().query(MeditationProvider.Meditations.byWeekNumber(weekNumber),
                new String[] { MeditationColumns._ID, MeditationColumns.WEEK_NUMBER },
                null,
                null,
                null);

        if (c == null)
            return;

        if (c.moveToNext()) {
            Intent intent = new Intent(this, NotificationService.class);
            intent.setAction(ACTION_SHOW_NOTIFICATION);
            intent.putExtra(EXTRA_MEDITATION_ID, c.getString(0));
            PendingIntent alarmIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    delay,
                    alarmIntent);
        }
    }

    private void handleShowNotification(Intent intent) {
        if (intent.hasExtra(EXTRA_MEDITATION_ID)) {

        }
    }
}
