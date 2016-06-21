package com.tuxan.holytime.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuxan.holytime.R;

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

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.meditation_item_list, parent, false);

        ListItemViewHolder viewHolder = new ListItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.titleView.setText(mCursor.getString(MeditationsLoader.Query.TITLE));

        String body = mCursor.getString(MeditationsLoader.Query.BODY);
        if (body != null && body.length() >= 250)
            body = body.substring(0, 250);

        holder.textView.setText(body);

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
