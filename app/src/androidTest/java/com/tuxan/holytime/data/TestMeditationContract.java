package com.tuxan.holytime.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.tuxan.holytime.data.provider.MeditationProvider;

public class TestMeditationContract extends AndroidTestCase{

    /**
     * Method to test Uri generation
     */
    public void testBuildMeditationUri() {

        String meditationId = "1234";

        Uri meditationUri = MeditationProvider.Meditations.byId(meditationId);

        assertNotNull("byId method return null :(", meditationUri);

        assertEquals("ID don't match", meditationId, meditationUri.getLastPathSegment());

        assertEquals("Uri don't match", meditationUri.toString(), "content://" + MeditationProvider.AUTHORITY + "/meditations/" + meditationId);

    }

}
