package com.tuxan.holytime;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.tbSettings)
    Toolbar mToolbar;

    @BindView(R.id.tv_about)
    TextView mTvAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.about_title);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTvAbout.setText(Html.fromHtml(getString(R.string.about_text), Html.FROM_HTML_MODE_LEGACY));
        } else {
            mTvAbout.setText(Html.fromHtml(getString(R.string.about_text)));
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
}
