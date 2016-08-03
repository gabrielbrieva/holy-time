package com.tuxan.holytime;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.tuxan.holytime.ui.SunriseSunsetView;
import com.tuxan.holytime.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SunsetInfoActivity extends AppCompatActivity {

    @BindView(R.id.tbSettings)
    Toolbar mToolbar;

    @BindView(R.id.tv_sunrise)
    TextView mTvSunrise;

    @BindView(R.id.tv_sunset)
    TextView mTvSunset;

    @BindView(R.id.sv_plot)
    SunriseSunsetView mSunriseSunsetView;

    @BindView(R.id.tv_next_holytime)
    TextView mTvNextHolyTime;

    SimpleDateFormat formater;
    SimpleDateFormat nextFridayFormater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        formater = new SimpleDateFormat("HH:mm");
        nextFridayFormater = new SimpleDateFormat(getString(R.string.next_friday_sunset_info));

        setContentView(R.layout.sunset_info_activity);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.sunrise_sunset_info_title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION }, 0);
            } else {
                initTime(Utils.getSunriseSunsetCalculator(this));
            }
        } else {
            initTime(Utils.getSunriseSunsetCalculator(this));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initTime(Utils.getSunriseSunsetCalculator(this));
        }
    }

    private void initTime(SunriseSunsetCalculator calculator) {
        mTvSunrise.setText(calculator.getOfficialSunriseForDate(Calendar.getInstance()));
        mTvSunset.setText(calculator.getOfficialSunsetForDate(Calendar.getInstance()));
        mSunriseSunsetView.setSunriseSunsetCalculator(calculator);

        Calendar currentCal = Calendar.getInstance();
        currentCal.setFirstDayOfWeek(Calendar.SUNDAY);
        int currentDay = currentCal.get(Calendar.DAY_OF_WEEK);

        Calendar sunsetCal = calculator.getOfficialSunsetCalendarForDate(currentCal);

        if (currentDay == Calendar.FRIDAY && currentCal.getTimeInMillis() >= sunsetCal.getTimeInMillis() ||
                currentDay == Calendar.SATURDAY && currentCal.getTimeInMillis() <= sunsetCal.getTimeInMillis()) {
            mTvNextHolyTime.setText(R.string.sunrise_sunset_info_is_holy_time);
        } else {
            Calendar nextFriday = currentCal;

            int currentWeek = nextFriday.get(Calendar.WEEK_OF_YEAR);
            nextFriday.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            nextFriday.set(Calendar.HOUR_OF_DAY, 12);
            nextFriday.set(Calendar.WEEK_OF_YEAR, currentWeek);

            nextFriday = calculator.getOfficialSunsetCalendarForDate(nextFriday);

            mTvNextHolyTime.setText(nextFridayFormater.format(nextFriday.getTime()));
        }
    }
}
