package com.tuxan.holytime.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.tuxan.holytime.data.provider.MeditationColumns;

public class MeditationsLoader extends CursorLoader {

    public MeditationsLoader(Context context, Uri uri) {
        super(context, uri, ResumeQuery.PROJECTION, null, null, MeditationColumns.WEEK_NUMBER + " DESC");
    }

    public interface ResumeQuery {
        String[] PROJECTION = {
                MeditationColumns._ID,
                MeditationColumns.TITLE,
                MeditationColumns.WEEK_NUMBER,
                MeditationColumns.VERSE
        };

        int _ID = 0;
        int TITLE = 1;
        int WEEK_NUMBER = 2;
        int VERSE = 3;
    }
}
