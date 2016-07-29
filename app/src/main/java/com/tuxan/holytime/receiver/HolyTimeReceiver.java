package com.tuxan.holytime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tuxan.holytime.service.NotificationService;

public class HolyTimeReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "HolyTimeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null)
            return;

        String action = intent.getAction();

        if (action == null)
            return;

        Log.d(LOG_TAG, "onReceive broadcast intent: " + action);

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_TIME_CHANGED:
            case Intent.ACTION_TIMEZONE_CHANGED:
            case Intent.ACTION_DATE_CHANGED:
                start(context);
                break;
        }
    }

    public static void start(Context context) {
        Intent i = new Intent(context, NotificationService.class);
        i.setAction(NotificationService.ACTION_SCHEDULE_NOTIFICATION_SERVICE);
        context.startService(i);
    }

}
