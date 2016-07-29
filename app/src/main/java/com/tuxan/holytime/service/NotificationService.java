package com.tuxan.holytime.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.tuxan.holytime.R;
import com.tuxan.holytime.data.provider.MeditationColumns;
import com.tuxan.holytime.data.provider.MeditationProvider;
import com.tuxan.holytime.utils.PermissionHelper;
import com.tuxan.holytime.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NotificationService extends IntentService implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String LOG_TAG = "NotificationService";

    public static final String ACTION_SCHEDULE_NOTIFICATION_SERVICE = "com.tuxan.holytime.action.START_SCHEDULE_NOTIFICATION_SERVICE";
    public static final String ACTION_SCHEDULE_NEXT_NOTIFICATION = "com.tuxan.holytime.action.SCHEDULE_NEXT_NOTIFICATION";
    public static final String ACTION_SHOW_NOTIFICATION = "com.tuxan.holytime.action.SHOW_NOTIFICATION";
    public static final String EXTRA_MEDITATION_ID = "com.tuxan.holytime.EXTRA_MEDITATION_ID";

    private static final SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static final int mNotificationId = 1234;

    public NotificationService() {
        super("HolyTimeNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction() == null)
            return;

        Log.d(LOG_TAG, "onHandleIntent...");

        Log.d(LOG_TAG, "Checking permission to current location");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG_TAG, "Using notification to asking for permission");
                PermissionHelper.requestPermissions(this,
                        new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION },
                        0, getString(R.string.app_name), getString(R.string.location_permission),
                        R.mipmap.ic_launcher);
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

        Log.d(LOG_TAG, "Scheduling NotificationService...");

        Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(ACTION_SCHEDULE_NEXT_NOTIFICATION);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                AlarmManager.INTERVAL_HOUR * 3,
                alarmIntent);
    }

    private void handleNextNotification() {

        Log.d(LOG_TAG, "Trying to set next notification...");

        Calendar c = Calendar.getInstance();

        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (dayOfWeek == Calendar.FRIDAY) {

            SunriseSunsetCalculator calculator = Utils.getSunriseSunsetCalculator(this);
            Calendar sunset = calculator.getOfficialSunsetCalendarForDate(c);

            if (c.getTimeInMillis() <= sunset.getTimeInMillis()) {
                Log.d(LOG_TAG, "Trying to se notification at " + formater.format(sunset.getTime()));
                setNextNotificationAlarm(c.get(Calendar.WEEK_OF_YEAR), sunset.getTimeInMillis());
            } else {
                Log.d(LOG_TAG, "Is Friday but sunset already happened.");
            }
        } else {
            Log.d(LOG_TAG, "It is not Friday.");
        }
    }

    private void setNextNotificationAlarm(int weekNumber, long delay) {
        Log.d(LOG_TAG, "Notification will trigger in " + delay + " milliseconds");

        Cursor c = null;

        try {
            c = getContentResolver().query(MeditationProvider.Meditations.byWeekNumber(weekNumber),
                    new String[]{MeditationColumns._ID, MeditationColumns.WEEK_NUMBER},
                    null,
                    null,
                    null);

            if (c == null) {
                Log.d(LOG_TAG, "Meditation not found using week number " + weekNumber);
                return;
            }

            if (c.getCount() > 0 && c.moveToNext()) {
                Intent intent = new Intent(this, NotificationService.class);
                intent.setAction(ACTION_SHOW_NOTIFICATION);
                intent.putExtra(EXTRA_MEDITATION_ID, c.getString(0));
                PendingIntent alarmIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        delay,
                        alarmIntent);
            }
        } finally {
            if (c != null && !c.isClosed())
                c.close();
        }
    }

    private void handleShowNotification(Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean notificationEnabled = prefs.getBoolean(getString(R.string.pref_notification_enabled_key), true);

        if (intent.hasExtra(EXTRA_MEDITATION_ID) && notificationEnabled) {

            Log.d(LOG_TAG, "Trying to get meditation and show notification using meditation id " + intent.getStringExtra(EXTRA_MEDITATION_ID));

            Cursor c = null;

            try {
                c = getContentResolver().query(MeditationProvider.Meditations.byId(intent.getStringExtra(EXTRA_MEDITATION_ID)),
                        new String[]{MeditationColumns._ID, MeditationColumns.WEEK_NUMBER, MeditationColumns.TITLE},
                        null,
                        null,
                        null);

                if (c == null) {
                    Log.d(LOG_TAG, "Meditation not found using id " + intent.getStringExtra(EXTRA_MEDITATION_ID));
                    return;
                }

                if (c.getCount() > 0 && c.moveToNext()) {

                    // TODO: add better LargeIcon

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_VIBRATE)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(c.getString(2));

                    boolean notificationVibrateEnabled = prefs.getBoolean(getString(R.string.pref_notification_vibrate_key), true);
                    String notificationLedColor = prefs.getString(getString(R.string.pref_notification_led_color_key), "WHITE");

                    mBuilder.setVibrate(new long[] { notificationVibrateEnabled ? 1000 : 0 });

                    int ledColor = Color.WHITE;

                    switch (notificationLedColor) {
                        case "RED": ledColor = Color.RED; break;
                        case "GREEN": ledColor = Color.GREEN; break;
                        case "BLUE": ledColor = Color.BLUE; break;
                    }

                    mBuilder.setLights(ledColor, 1500, 3000);

                    Uri uri = MeditationProvider.Meditations.byId(intent.getStringExtra(EXTRA_MEDITATION_ID));
                    Intent meditationIntent = new Intent(Intent.ACTION_VIEW, uri);
                    PendingIntent meditationPendingIntent = PendingIntent.getActivity(this,
                            3,
                            meditationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

                    mBuilder.setContentIntent(meditationPendingIntent);

                    NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(this);
                    mNotifyMgr.notify(mNotificationId, mBuilder.build());

                    Log.d(LOG_TAG, "Notification created and showed");
                }
            } finally {
                if (c != null && !c.isClosed())
                    c.close();
            }
        } else {
            Log.d(LOG_TAG, "Notification is disabled");
        }
    }
}
