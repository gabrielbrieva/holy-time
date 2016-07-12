package com.tuxan.holytime.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.tuxan.holytime.R;
import com.tuxan.holytime.data.dto.MeditationContent;
import com.tuxan.holytime.data.provider.MeditationProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeditationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int VIEWHOLDER_MEDITATION_TYPE = 0;
    static final int VIEWHOLDER_LOADING_TYPE = 1;

    private boolean isLoading = false;

    Cursor mCursor;
    Context mContext;

    SimpleDateFormat mDateFormater = new SimpleDateFormat("MMM");

    public MeditationsAdapter(Context context) {
        this.mContext = context;
    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
    }

    public void addMeditations(List<MeditationContent> meditations) {

        MatrixCursor matrixCursor = new MatrixCursor(MeditationsLoader.Query.PROJECTION);

        for (MeditationContent m : meditations) {
            matrixCursor.addRow(new Object[] {
                    m.getId(),
                    m.getTitle(),
                    m.getAuthor(),
                    m.getWeekNumber(),
                    m.getBody()
            });
        }

        //setIsLoading(false);

        int countBeforeMerge = mCursor.getCount();

        mCursor = new MergeCursor(new Cursor[] { mCursor, matrixCursor });

        notifyItemRangeInserted(countBeforeMerge, matrixCursor.getCount());
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        if (isLoading) {
            this.isLoading = isLoading;
            notifyItemInserted(mCursor.getCount());
        } else {
            notifyItemRemoved(mCursor.getCount());
            this.isLoading = isLoading;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        if (viewType == VIEWHOLDER_MEDITATION_TYPE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.meditation_item_list, parent, false);
            viewHolder = new ListItemViewHolder(view);

            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String meditationId = getMeditationId(viewHolder.getAdapterPosition());

                    Intent intent = new Intent(Intent.ACTION_VIEW, MeditationProvider.Meditations.withId(meditationId));

                    mContext.startActivity(intent);
                }
            });*/
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.loading_item_list, parent, false);
            viewHolder = new LoadingViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ListItemViewHolder) {

            mCursor.moveToPosition(position);

            ListItemViewHolder holder = (ListItemViewHolder) viewHolder;

            holder.titleView.setText(mCursor.getString(MeditationsLoader.Query.TITLE));

            String body = mCursor.getString(MeditationsLoader.Query.BODY);
            if (body != null) {
                if (body.length() >= 250)
                    body = body.substring(0, 250);

                holder.textView.setText(Html.fromHtml(body));
            } else {
                holder.textView.setText("");
            }

            int weekNumber = mCursor.getInt(MeditationsLoader.Query.WEEK_NUMBER);

            Calendar c = Calendar.getInstance();

            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            c.set(Calendar.WEEK_OF_YEAR, weekNumber);

            holder.monthView.setText(mDateFormater.format(c.getTime()).toUpperCase().replace(".", ""));
            holder.dayView.setText(String.format("%02d", c.get(Calendar.DAY_OF_MONTH)));
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null)
            return mCursor.getCount() + (isLoading ? 1 : 0);

        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position > (mCursor.getCount() - 1) && isLoading)
            return VIEWHOLDER_LOADING_TYPE;

        return VIEWHOLDER_MEDITATION_TYPE;
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

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
