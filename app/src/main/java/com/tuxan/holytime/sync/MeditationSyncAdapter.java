package com.tuxan.holytime.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.tuxan.holytime.R;
import com.tuxan.holytime.Utils;
import com.tuxan.holytime.api.APIService;
import com.tuxan.holytime.api.APIServiceFactory;
import com.tuxan.holytime.data.dto.MeditationContent;
import com.tuxan.holytime.data.provider.MeditationColumns;
import com.tuxan.holytime.data.provider.MeditationProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Response;

public class MeditationSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String LOG_TAG = MeditationSyncAdapter.class.getSimpleName();

    // Interval at which to sync with HolyTime API, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    ContentResolver mContentResolver;

    public MeditationSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }

    public MeditationSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if (Utils.isNetworkConnected(getContext())) {

            APIService apiService = APIServiceFactory.createService(getContext().getString(R.string.api_key));

            int weekNumber = Utils.getCurrentWeekNumber();

            try {
                Response<List<MeditationContent>> result = apiService.getSyncList(weekNumber).execute();

                if (result.isSuccessful())
                    syncMeditations(result.body());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void syncMeditations(List<MeditationContent> meditations) {

        if (meditations == null || meditations.isEmpty())
            return;

        List<String> meditationsId = new ArrayList<>();

        int newItems = 0;
        int updatedItems = 0;

        for (MeditationContent m : meditations) {

            meditationsId.add(m.getId());

            ContentValues v = new ContentValues();

            v.put(MeditationColumns._ID, m.getId());
            v.put(MeditationColumns.TITLE, m.getTitle());
            v.put(MeditationColumns.VERSE, m.getVerse());
            v.put(MeditationColumns.AUTHOR, m.getAuthor());
            v.put(MeditationColumns.BODY, m.getBody());
            v.put(MeditationColumns.WEEK_NUMBER, m.getWeekNumber());

            Cursor result = mContentResolver.query(MeditationProvider.Meditations.withId(m.getId()),
                    new String[]{ MeditationColumns._ID },
                    null,
                    null,
                    null);

            // insert or update each meditation result
            if (result == null || result.getCount() == 0) {
                mContentResolver.insert(MeditationProvider.Meditations.meditationList, v);
                newItems++;
            } else {
                v.remove(MeditationColumns._ID);
                updatedItems += mContentResolver.update(MeditationProvider.Meditations.withId(m.getId()), v, null, null);
            }

            if (result != null)
                result.close();
        }

        if (newItems > 0)
            Log.d(LOG_TAG, newItems + " New Meditation Synchronized (inserted)");

        if (updatedItems > 0)
            Log.d(LOG_TAG, updatedItems + " Meditation Synchronized (updated)");

        int deletedItems = mContentResolver.delete(MeditationProvider.Meditations.meditationList,
                MeditationColumns._ID + " NOT IN ( \"" + TextUtils.join("\",\"", meditationsId) + "\" ) " +
                " AND " + MeditationColumns.IS_FAVORITE + " = 0 ",
                null);

        if (deletedItems > 0)
            Log.d(LOG_TAG, deletedItems + " old meditations was deleted by MeditationSyncAdapter");
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, MeditationProvider.AUTHORITY).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    MeditationProvider.AUTHORITY, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(getSyncAccount(context),
                MeditationProvider.AUTHORITY, bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

            // Add the account and account type, no password or user data
            // If successful, return the Account object, otherwise report an error.
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MeditationSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, MeditationProvider.AUTHORITY, true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
