package com.tuxan.holytime.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tuxan.holytime.R;

public class FcmIdService extends FirebaseInstanceIdService {

    private static final String LOG_TAG = "FcmIdService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(LOG_TAG, "Refreshed token: " + refreshedToken);

        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.sync_topic));
        Log.d(LOG_TAG, "Subscribed to " + getString(R.string.sync_topic));
    }

}
