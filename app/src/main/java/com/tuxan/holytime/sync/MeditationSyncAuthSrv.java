package com.tuxan.holytime.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class MeditationSyncAuthSrv extends Service {

    MeditationAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new MeditationAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
