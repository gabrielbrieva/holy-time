package com.tuxan.holytime.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.tuxan.holytime.MeditationActivity;
import com.tuxan.holytime.MeditationsActivity;
import com.tuxan.holytime.R;
import com.tuxan.holytime.adapter.MeditationLoader;
import com.tuxan.holytime.data.provider.MeditationProvider;
import com.tuxan.holytime.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HolyTimeWidgetService extends IntentService {

    public static final String EXTRA_WIDGETS_ID_KEY = "EXTRA_WIDGETS_ID_KEY";
    public static final String EXTRA_FROM_WIDGETS_KEY = "EXTRA_FROM_WIDGETS_KEY";

    SimpleDateFormat mDateFormater = new SimpleDateFormat("MMM");
    SimpleDateFormat nextFridayFormater;

    public HolyTimeWidgetService() {
        super("HolyTimeWidgetService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        nextFridayFormater = new SimpleDateFormat(getString(R.string.next_friday_sunset_info));
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent == null || !intent.hasExtra(EXTRA_WIDGETS_ID_KEY))
            return;

        int appWidgetId = intent.getIntExtra(EXTRA_WIDGETS_ID_KEY, -1);

        if (appWidgetId == -1)
            return;

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        Calendar currentCal = Calendar.getInstance();
        currentCal.setFirstDayOfWeek(Calendar.SUNDAY);
        int currentDay = currentCal.get(Calendar.DAY_OF_WEEK);

        SunriseSunsetCalculator calculator = Utils.getSunriseSunsetCalculator(this);

        Calendar sunsetCal = calculator.getOfficialSunsetCalendarForDate(currentCal);

        if (currentDay == Calendar.FRIDAY && currentCal.getTimeInMillis() >= sunsetCal.getTimeInMillis() ||
                currentDay == Calendar.SATURDAY && currentCal.getTimeInMillis() <= sunsetCal.getTimeInMillis()) {
            // it's holy time

            int currentWeek = currentCal.get(Calendar.WEEK_OF_YEAR);

            Cursor c = null;

            try {
                c = getContentResolver().query(MeditationProvider.Meditations.byWeekNumber(currentWeek),
                        MeditationLoader.DetailQuery.PROJECTION,
                        null,
                        null,
                        null);

                if (c != null && c.moveToNext()) {

                    RemoteViews views = new RemoteViews(getPackageName(), R.layout.holy_time_widget);

                    String date = String.format("%02d", currentCal.get(Calendar.DAY_OF_MONTH)) + " " +
                            mDateFormater.format(currentCal.getTime()).toUpperCase().replace(".", "");

                    views.setTextViewText(R.id.tv_date_month, date);
                    views.setTextViewText(R.id.tv_meditation_title, c.getString(MeditationLoader.DetailQuery.TITLE));

                    Intent launchIntent = new Intent(this, MeditationActivity.class);
                    launchIntent.setData(MeditationProvider.Meditations.byId(c.getString(MeditationLoader.DetailQuery._ID)));
                    launchIntent.putExtra(EXTRA_FROM_WIDGETS_KEY, EXTRA_FROM_WIDGETS_KEY);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                    views.setOnClickPendingIntent(R.id.ll_widget_container, pendingIntent);

                    appWidgetManager.updateAppWidget(appWidgetId, views);

                }
            } finally {
                if (c != null && !c.isClosed())
                    c.close();
            }


        } else {
            Calendar nextFriday = currentCal;

            int currentWeek = nextFriday.get(Calendar.WEEK_OF_YEAR);
            nextFriday.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            nextFriday.set(Calendar.HOUR_OF_DAY, 12);
            nextFriday.set(Calendar.WEEK_OF_YEAR, currentWeek);

            nextFriday = calculator.getOfficialSunsetCalendarForDate(nextFriday);

            // next Holy Time date ...

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.no_holy_time_widget);

            views.setTextViewText(R.id.tv_next_holy_time, nextFridayFormater.format(nextFriday.getTime()));

            Intent launchIntent = new Intent(this, MeditationsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.ll_widget_container, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
