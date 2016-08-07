package com.tuxan.holytime.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

public class FirebaseAnalyticsProxy {

    public static enum ActionCategory {
        FROM_MEDITATIONS_LIST,
        FROM_WIDGET,
        DO_FAVORITE,
        NO_FAVORITE
    }

    private FirebaseAnalytics mFirebaseAnalytics;

    public FirebaseAnalyticsProxy(Context context) {
        this.mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void LogSelectMeditationActionEvent(String meditationId, String title, ActionCategory actionCategory) {
        Bundle event = new Bundle();
        event.putString(FirebaseAnalytics.Param.ITEM_ID, meditationId);
        event.putString(FirebaseAnalytics.Param.ITEM_NAME, title);
        event.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, actionCategory.name());

        LogEvent(FirebaseAnalytics.Event.SELECT_CONTENT, event);
    }

    public void LogGoToEvent(String origin, String destination) {
        Bundle event = new Bundle();
        event.putString(FirebaseAnalytics.Param.ORIGIN, origin);
        event.putString(FirebaseAnalytics.Param.DESTINATION, destination);
        event.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "GO_TO");

        LogEvent(FirebaseAnalytics.Event.SELECT_CONTENT, event);
    }

    public void LogEvent(@NonNull String name, Bundle params) {
        if (mFirebaseAnalytics == null)
            return;

        mFirebaseAnalytics.logEvent(name, params);
    }
}
