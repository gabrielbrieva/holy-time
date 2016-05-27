package com.tuxan.holytime.data.provider;

import android.content.ContentResolver;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = MeditationProvider.AUTHORITY, database = HolyTimeDatabase.class)
public final class MeditationProvider {

    public static final String AUTHORITY = "com.tuxan.holytime.data.provider.MeditationProvider";

    @TableEndpoint(table = HolyTimeDatabase.MEDITATIONS)
    public static class Meditations {

        @ContentUri(path = "meditations",
                type = ContentResolver.CURSOR_DIR_BASE_TYPE + "/meditation",
                defaultSort = MeditationColumns.WEEK_NUMBER + " DESC")
        public static final Uri MEDITATIONS = Uri.parse("content://" + AUTHORITY + "/meditations");


    }

}
