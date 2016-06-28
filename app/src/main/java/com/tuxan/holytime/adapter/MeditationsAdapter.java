package com.tuxan.holytime.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuxan.holytime.R;
import com.tuxan.holytime.data.provider.MeditationProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeditationsAdapter extends RecyclerView.Adapter<MeditationsAdapter.ListItemViewHolder> {

    Cursor mCursor;
    Context mContext;

    SimpleDateFormat mDateFormater = new SimpleDateFormat("MMM");

    public MeditationsAdapter(Cursor cursor, Context context) {
        this.mCursor = cursor;
        this.mContext = context;
    }

    private String getMeditationId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getString(MeditationsLoader.Query._ID);
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.meditation_item_list, parent, false);

        final ListItemViewHolder viewHolder = new ListItemViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String meditationId = getMeditationId(viewHolder.getAdapterPosition());

                Intent intent = new Intent(Intent.ACTION_VIEW, MeditationProvider.Meditations.withId(meditationId));

                mContext.startActivity(intent);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.titleView.setText(mCursor.getString(MeditationsLoader.Query.TITLE));

        String body = mCursor.getString(MeditationsLoader.Query.BODY);
        if (body != null && body.length() >= 250)
            body = body.substring(0, 250);

        holder.textView.setText(Html.fromHtml(body));

        int weekNumber = mCursor.getInt(MeditationsLoader.Query.WEEK_NUMBER);

        Calendar c = Calendar.getInstance();

        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        c.set(Calendar.WEEK_OF_YEAR, weekNumber);

        holder.monthView.setText(mDateFormater.format(c.getTime()).toUpperCase().replace(".", ""));
        holder.dayView.setText(String.format("%02d", c.get(Calendar.DAY_OF_MONTH)));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public static class ListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_date_month)
        public TextView monthView;

        @BindView(R.id.tv_date_day)
        public TextView dayView;

        @BindView(R.id.tv_meditation_title)
        public TextView titleView;

        @BindView(R.id.tv_meditation_text)
        public TextView textView;

        public ListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
