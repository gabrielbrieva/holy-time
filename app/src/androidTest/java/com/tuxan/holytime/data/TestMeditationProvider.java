package com.tuxan.holytime.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.tuxan.holytime.data.provider.MeditationProvider;

public class TestMeditationProvider extends AndroidTestCase {

    /**
     * Delete all from Meditations table and test if all was deleted.
     */
    private void deleteAllFromProvider() {
        mContext.getContentResolver().delete(MeditationProvider.Meditations.meditationList, null, null);

        Cursor c = mContext.getContentResolver().query(MeditationProvider.Meditations.meditationList,
                null,
                null,
                null,
                null);

        assertEquals("Error: Records not deleted from Meditations table :(", 0, c.getCount());
        c.close();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // start with an empty content provider
        deleteAllFromProvider();
    }

    /**
     * Test to check if the provider is registered correctly.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                com.tuxan.holytime.data.provider.generated.MeditationProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: MeditationProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MeditationProvider.AUTHORITY,
                    providerInfo.authority, MeditationProvider.AUTHORITY);

        } catch (PackageManager.NameNotFoundException ex) {
            assertTrue("Error: MeditationProvider not register at " + mContext.getPackageName(), false);
        }
    }

    /**
     * Test to check the type returned by the MeditationProvider.
     */
    public void testGetType() {
        // content://com.tuxan.holytime/meditations/
        String type =  mContext.getContentResolver().getType(MeditationProvider.Meditations.meditationList);

        // vnd.android.cursor.dir/vnd.com.tuxan.holytime.meditation
        assertEquals("Error: the MeditationEntry CONTENT_URI should return MeditationEntry.CONTENT_TYPE",
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.tuxan.holytime.meditation", type);

        String meditationId = "1234";
        // content://com.tuxan.holytime/meditations/1234
        type = mContext.getContentResolver().getType(MeditationProvider.Meditations.byId(meditationId));
        // vnd.android.cursor.item/vnd.com.tuxan.holytime.meditation
        assertEquals("Error: the MeditationEntry CONTENT_URI with meditationId should return MeditationEntry.CONTENT_ITEM_TYPE",
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.tuxan.holytime.meditation", type);

    }

}
