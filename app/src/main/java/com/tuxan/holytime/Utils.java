package com.tuxan.holytime;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableStringBuilder;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Utilities class ...
 */
public class Utils {

    public static final String API_BASE_URL = "http://holytime.gabrielbrieva.cl/api/";

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

    public static int getCurrentWeekNumber() {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    public static Calendar getTimeOfSunset(Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            android.location.Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(new Location(loc.getLatitude(), loc.getLongitude()), TimeZone.getDefault());
            return calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());
        }

        return null;
    }

    public static SpannableStringBuilder trimSpannable(SpannableStringBuilder spannable) {
        int trimStart = 0;
        int trimEnd = 0;

        String text = spannable.toString();

        while (text.length() > 0 && text.startsWith("\n")) {
            text = text.substring(1);
            trimStart += 1;
        }

        while (text.length() > 0 && text.endsWith("\n")) {
            text = text.substring(0, text.length() - 1);
            trimEnd += 1;
        }

        return spannable.delete(0, trimStart).delete(spannable.length() - trimEnd, spannable.length());
    }
}
