package com.tuxan.holytime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tuxan.holytime.data.dto.MeditationContent;

import butterknife.ButterKnife;

public class MeditationFragment extends Fragment {

    public static final String MEDITATION_CONTENT_KEY = "MEDITATION_CONTENT_KEY";

    MeditationContent mMeditationContent;

    public MeditationFragment() { }

    public static MeditationFragment newInstance() {
        MeditationFragment fragment = new MeditationFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMeditationContent = arguments.getParcelable(MEDITATION_CONTENT_KEY);
        } else if (savedInstanceState != null) {
            mMeditationContent = savedInstanceState.getParcelable(MEDITATION_CONTENT_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meditation_detail_fragment, container, false);

        ButterKnife.bind(this, view);



        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MEDITATION_CONTENT_KEY, mMeditationContent);
    }
}
