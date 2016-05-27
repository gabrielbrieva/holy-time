package com.tuxan.holytime;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utilities class ...
 */
public class Utils {

    public static final String API_BASE_URL = "https://holytime.gabrielbrieva.cl/api";

    /**
     * Method to check if the device have access to internet
     * @param context
     * @return true if internet connection is available
     */
    public static boolean isNetworkConnected(Context context) {

        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            }
        }

        return false;
    }
}
