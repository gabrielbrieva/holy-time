package com.tuxan.holytime.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.tuxan.holytime.data.provider.MeditationColumns;

public class MeditationLoader extends CursorLoader {

    public MeditationLoader(Context context, Uri uri) {
        super(context, uri, DetailQuery.PROJECTION, null, null, null);
    }

    public interface DetailQuery {
        String[] PROJECTION = {
                MeditationColumns._ID,
                MeditationColumns.TITLE,
                MeditationColumns.AUTHOR,
                MeditationColumns.WEEK_NUMBER,
                MeditationColumns.VERSE,
                MeditationColumns.BODY,
                MeditationColumns.IS_FAVORITE
        };

        int _ID = 0;
        int TITLE = 1;
        int AUTHOR = 2;
        int WEEK_NUMBER = 3;
        int VERSE = 4;
        int BODY = 5;
        int IS_FAVORITE = 6;
    }
}
