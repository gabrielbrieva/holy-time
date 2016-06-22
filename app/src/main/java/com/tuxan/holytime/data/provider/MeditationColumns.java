package com.tuxan.holytime.data.provider;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface MeditationColumns {

    @DataType(DataType.Type.TEXT) @PrimaryKey String _ID = "_id";
    @DataType(DataType.Type.TEXT) String TITLE = "title";
    @DataType(DataType.Type.TEXT) String AUTHOR = "author";
    @DataType(DataType.Type.TEXT) String BODY = "body";
    @DataType(DataType.Type.INTEGER) String WEEK_NUMBER = "week_number";
    // TODO: add IS_FAVORITE column
}
