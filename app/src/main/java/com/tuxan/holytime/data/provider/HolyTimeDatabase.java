package com.tuxan.holytime.data.provider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = HolyTimeDatabase.VERSION, fileName = HolyTimeDatabase.DATABASE_FILE_NAME)
public final class HolyTimeDatabase {
    public static final int VERSION = 1;
    public static final String DATABASE_FILE_NAME = "holytime.db";

    @Table(MeditationColumns.class)
    public static final String MEDITATIONS = "meditations";
}
