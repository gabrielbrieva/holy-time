package com.tuxan.holytime;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.tuxan.holytime.adapter.MeditationsAdapter;
import com.tuxan.holytime.adapter.MeditationsLoader;
import com.tuxan.holytime.data.provider.MeditationProvider;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeditationsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int LOADER_ID = 0;

    @BindView(R.id.rv_meditations)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            } else {
                calculateSunsetDateTime();
            }
        } else {
            calculateSunsetDateTime();
        }

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void calculateSunsetDateTime() {
        Calendar c = Utils.getTimeOfSunset(this);
        new AlertDialog.Builder(this)
                .setTitle("Sunset Date Time")
                .setMessage(c.toString())
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            calculateSunsetDateTime();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MeditationsLoader(this, MeditationProvider.Meditations.MEDITATIONS);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        MeditationsAdapter adapter = new MeditationsAdapter(data, this);
        adapter.setHasStableIds(true);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }
}
