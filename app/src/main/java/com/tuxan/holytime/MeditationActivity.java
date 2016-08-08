package com.tuxan.holytime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.tuxan.holytime.utils.FirebaseAnalyticsProxy;
import com.tuxan.holytime.widget.HolyTimeWidgetService;

public class MeditationActivity extends AppCompatActivity {

    private FirebaseAnalyticsProxy mFirebaseAnalyticsProxy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meditation_detail_activity);

        if (savedInstanceState == null) {

            mFirebaseAnalyticsProxy = new FirebaseAnalyticsProxy(this);

            MeditationFragment fragment = MeditationFragment.newInstance();
            Bundle arguments = new Bundle();

            if (getIntent() != null) {

                if (getIntent().hasExtra(MeditationFragment.MEDITATION_TITLE_KEY))
                    arguments.putString(MeditationFragment.MEDITATION_TITLE_KEY, getIntent().getStringExtra(MeditationFragment.MEDITATION_TITLE_KEY));

                if (getIntent().hasExtra(MeditationFragment.MEDITATION_VERSE_KEY))
                    arguments.putString(MeditationFragment.MEDITATION_VERSE_KEY, getIntent().getStringExtra(MeditationFragment.MEDITATION_VERSE_KEY));

                if (getIntent().getData() != null) {
                    String meditationId = getIntent().getData().getPathSegments().get(1);
                    arguments.putString(MeditationFragment.MEDITATION_ID_KEY, meditationId);

                    if (getIntent().hasExtra(HolyTimeWidgetService.EXTRA_FROM_WIDGETS_KEY))
                        mFirebaseAnalyticsProxy.LogSelectMeditationActionEvent(meditationId, null, FirebaseAnalyticsProxy.ActionCategory.FROM_WIDGET);
                }
            }

            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.meditation_detail_container, fragment)
                    .commit();

        }

    }

}
