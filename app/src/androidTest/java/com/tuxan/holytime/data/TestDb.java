package com.tuxan.holytime.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.tuxan.holytime.data.provider.HolyTimeDatabase;
import com.tuxan.holytime.data.provider.MeditationColumns;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // start with an empty DB
        mContext.deleteDatabase(HolyTimeDatabase.DATABASE_FILE_NAME);
    }

    public void testCreateDb() {

        SQLiteDatabase db =
                com.tuxan.holytime.data.provider.generated.HolyTimeDatabase.getInstance(mContext)
                        .getWritableDatabase();

        assertEquals("Db is not open :(", true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        // we made a list with all the tables must be created on the DB
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(HolyTimeDatabase.MEDITATIONS);

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Some of the tables was not created :(", tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" +  HolyTimeDatabase.MEDITATIONS + ")", null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> columnHashSet = new HashSet<String>();
        columnHashSet.add(MeditationColumns._ID);
        columnHashSet.add(MeditationColumns.AUTHOR);
        columnHashSet.add(MeditationColumns.TITLE);
        columnHashSet.add(MeditationColumns.WEEK_NUMBER);
        columnHashSet.add(MeditationColumns.BODY);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                columnHashSet.isEmpty());
        db.close();
    }

    public void testMeditationTable() {
        SQLiteDatabase db =
                com.tuxan.holytime.data.provider.generated.HolyTimeDatabase.getInstance(mContext)
                        .getWritableDatabase();

        assertEquals("Db is not open :(", true, db.isOpen());

        ContentValues meditationValues = createMeditationValues("1234");

        long insertedMeditaionId = db.insert(HolyTimeDatabase.MEDITATIONS, null, meditationValues);
        assertTrue(insertedMeditaionId != -1);

        Cursor c = db.query(HolyTimeDatabase.MEDITATIONS,
                null, null, null, null, null, null);

        assertTrue("table is empty :(", c.moveToFirst());

        validateCurrentRecord("unexpected value was inserted :(", c, meditationValues);
        assertFalse("more than one record from movie query ?? ¬¬", c.moveToNext());

        c.close();
        db.close();
    }

    static ContentValues createMeditationValues(String meditationId) {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MeditationColumns._ID, meditationId);
        movieValues.put(MeditationColumns.AUTHOR, "author");
        movieValues.put(MeditationColumns.WEEK_NUMBER, 1);
        movieValues.put(MeditationColumns.TITLE, "title");
        movieValues.put(MeditationColumns.BODY, "body");

        return movieValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

}
