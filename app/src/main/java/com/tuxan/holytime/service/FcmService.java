package com.tuxan.holytime.service;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tuxan.holytime.R;
import com.tuxan.holytime.api.APIService;
import com.tuxan.holytime.api.APIServiceFactory;
import com.tuxan.holytime.data.dto.MeditationContent;
import com.tuxan.holytime.data.provider.MeditationColumns;
import com.tuxan.holytime.data.provider.MeditationProvider;
import com.tuxan.holytime.utils.Utils;

import java.io.IOException;
import java.util.Calendar;

import retrofit2.Response;

public class FcmService extends FirebaseMessagingService {

    private static final String LOG_TAG = "FcmService";

    private static final String ACTION_KEY = "action";
    private static final String ACTION_SYNC = "sync";
    private static final String WEEK_KEY = "week";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage == null)
            return;

        Log.d(LOG_TAG, "FCM Message received!!");

        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().containsKey(ACTION_KEY)) {
                String action = remoteMessage.getData().get(ACTION_KEY);

                Log.d(LOG_TAG, "Action received is : " + action);

                if (ACTION_SYNC.equals(action)) {

                    Calendar c = Calendar.getInstance();
                    c.setFirstDayOfWeek(Calendar.SUNDAY);

                    int week = -1;

                    if (remoteMessage.getData().containsKey(WEEK_KEY))
                        week = Integer.parseInt(remoteMessage.getData().get(WEEK_KEY));

                    if (week > -1) {

                        Cursor fav = null;

                        try {
                            fav = getContentResolver().query(MeditationProvider.Meditations.byWeekNumber(week),
                                    new String[]{ MeditationColumns._ID },
                                    null,
                                    null,
                                    null);

                            // if meditation exist in local DB we updated
                            if (fav != null && fav.getCount() > 0 && fav.moveToNext() && Utils.isNetworkConnected(this)) {
                                String id = fav.getString(0);

                                APIService service = APIServiceFactory.createService(getString(R.string.api_key));
                                Response<MeditationContent> resp = service.getContentDetail(id).execute();
                                MeditationContent m = resp.body();

                                ContentValues v = new ContentValues();

                                v.put(MeditationColumns._ID, m.getId());
                                v.put(MeditationColumns.TITLE, m.getTitle());
                                v.put(MeditationColumns.VERSE, m.getVerse());
                                v.put(MeditationColumns.AUTHOR, m.getAuthor());
                                v.put(MeditationColumns.BODY, m.getBody());
                                v.put(MeditationColumns.WEEK_NUMBER, m.getWeekNumber());

                                int updated = getContentResolver().update(MeditationProvider.Meditations.byId(m.getId()), v, null, null);

                                if (updated > 0)
                                    Log.d(LOG_TAG, "Meditation " + id + " updated.");
                            }

                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Error getting meditation content from HolyTime API: " + e.getMessage());
                            e.printStackTrace();
                        } finally {
                            if (fav != null && !fav.isClosed())
                                fav.close();
                        }
                    }
                }
            }
        }
    }
}
