package com.tuxan.holytime.data.provider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = HolyTimeDatabase.VERSION)
public final class HolyTimeDatabase {
    public static final int VERSION = 1;

    @Table(MeditationColumns.class)
    public static final String MEDITATIONS = "meditations";
}
