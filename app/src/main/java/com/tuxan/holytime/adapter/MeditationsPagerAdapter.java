package com.tuxan.holytime.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tuxan.holytime.MeditationsFragment;
import com.tuxan.holytime.R;

public class MeditationsPagerAdapter extends FragmentStatePagerAdapter {

    public static final int CURRENT_LIST = 0;
    public static final int FAVORITE_LIST = 1;

    Context mContext;

    public MeditationsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case CURRENT_LIST:
            case FAVORITE_LIST:
                return MeditationsFragment.newInstance(position);
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case CURRENT_LIST:
                return mContext.getString(R.string.tab_current);
            case FAVORITE_LIST:
                return mContext.getString(R.string.tab_favorites);
        }

        return null;
    }
}
