package com.tuxan.holytime.ui;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.TimeZone;

public abstract class CalculatorByLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final static String LOG_TAG = "CalculatorByLocation";
    public final static int REQUEST_CHECK_SETTINGS = 1;

    Context mContext;
    GoogleApiClient mGoogleClient;

    public CalculatorByLocation(Context context) {
        mContext = context;

        mGoogleClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void checkSettings(final AppCompatActivity activity) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY));

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult r) {
                final Status status = r.getStatus();
                final LocationSettingsStates s = r.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        onLocationSettingEnabled();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            status.startResolutionForResult(
                                    activity,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        onConnectionError();
                        break;
                }
            }
        });
    }

    public abstract void onLocationSettingEnabled();

    public void createCalculator() {
        if (mGoogleClient == null && !mGoogleClient.isConnected())
            return;

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            android.location.Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);
            if (loc != null)
                onCalculatorCreated(new SunriseSunsetCalculator(new Location(loc.getLatitude(), loc.getLongitude()), TimeZone.getDefault()));
        }
    }

    public void connect() {
        if (mGoogleClient != null && !mGoogleClient.isConnected())
            mGoogleClient.connect();
        else
            onConnected();
    }

    public void disconnect() {
        if (mGoogleClient != null && mGoogleClient.isConnected())
            mGoogleClient.disconnect();
    }

    public abstract void onConnected();

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(LOG_TAG, "onConnected " + bundle);

        onConnected();
    }

    public abstract void onCalculatorCreated(SunriseSunsetCalculator calculator);
    public abstract void onConnectionError();

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(LOG_TAG, "onConnectionSuspended " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "onConnectionFailed " + connectionResult.getErrorMessage());
        onConnectionError();
    }
}
