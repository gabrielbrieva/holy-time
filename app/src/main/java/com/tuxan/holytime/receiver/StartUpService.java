package com.tuxan.holytime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tuxan.holytime.service.NotificationService;

public class StartUpService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            start(context);
        }
    }

    public static void start(Context context) {
        Intent i = new Intent(context, NotificationService.class);
        i.setAction(NotificationService.ACTION_SCHEDULE_NOTIFICATION_SERVICE);
        context.startService(i);
    }

}
