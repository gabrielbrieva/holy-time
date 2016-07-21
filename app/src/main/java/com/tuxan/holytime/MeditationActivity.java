package com.tuxan.holytime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class MeditationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meditation_detail_activity);

        if (savedInstanceState == null) {

            MeditationFragment fragment = MeditationFragment.newInstance();
            Bundle arguments = new Bundle();

            if (getIntent() != null) {

                if (getIntent().hasExtra(MeditationFragment.MEDITATION_TITLE_KEY))
                    arguments.putString(MeditationFragment.MEDITATION_TITLE_KEY, getIntent().getStringExtra(MeditationFragment.MEDITATION_TITLE_KEY));

                if (getIntent().hasExtra(MeditationFragment.MEDITATION_VERSE_KEY))
                    arguments.putString(MeditationFragment.MEDITATION_VERSE_KEY, getIntent().getStringExtra(MeditationFragment.MEDITATION_VERSE_KEY));

                if (getIntent().getData() != null)
                    arguments.putString(MeditationFragment.MEDITATION_ID_KEY, getIntent().getData().getPathSegments().get(1));
            }

            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.meditation_detail_container, fragment)
                    .commit();

        }

    }

}
