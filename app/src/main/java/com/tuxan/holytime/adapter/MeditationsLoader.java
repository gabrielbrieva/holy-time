package com.tuxan.holytime.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.tuxan.holytime.data.provider.MeditationColumns;

public class MeditationsLoader extends CursorLoader {

    public MeditationsLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, MeditationColumns.WEEK_NUMBER + " DESC");
    }

    public interface Query {
        String[] PROJECTION = {
                MeditationColumns._ID,
                MeditationColumns.TITLE,
                MeditationColumns.AUTHOR,
                MeditationColumns.WEEK_NUMBER,
                MeditationColumns.BODY
        };

        int _ID = 0;
        int TITLE = 1;
        int AUTHOR = 2;
        int WEEK_NUMBER = 3;
        int BODY = 4;
    }
}
