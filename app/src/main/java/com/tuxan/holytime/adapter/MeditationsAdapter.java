package com.tuxan.holytime.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuxan.holytime.MeditationFragment;
import com.tuxan.holytime.R;
import com.tuxan.holytime.Utils;
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

    private Cursor mCursor;
    protected Context mContext;

    SimpleDateFormat mDateFormater = new SimpleDateFormat("MMM");

    public MeditationsAdapter(Context context) {
        this.mContext = context;
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public void addMeditations(List<MeditationContent> meditations) {

        MatrixCursor matrixCursor = new MatrixCursor(MeditationsLoader.ResumeQuery.PROJECTION);

        for (MeditationContent m : meditations) {
            matrixCursor.addRow(new Object[] {
                    m.getId(),
                    m.getTitle(),
                    m.getWeekNumber(),
                    m.getVerse()
            });
        }

        int countBeforeMerge = mCursor == null ? 0 : mCursor.getCount();

        if (mCursor != null)
            mCursor = new MergeCursor(new Cursor[] { mCursor, matrixCursor });
        else
            mCursor = matrixCursor;

        notifyItemRangeInserted(countBeforeMerge, matrixCursor.getCount());
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {

        int itemsCount = mCursor == null ? 0 : mCursor.getCount();

        if (isLoading) {
            this.isLoading = isLoading;
            notifyItemInserted(itemsCount);
        } else {
            notifyItemRemoved(itemsCount);
            this.isLoading = isLoading;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEWHOLDER_MEDITATION_TYPE) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.meditations_list_item, parent, false);
            final RecyclerView.ViewHolder viewHolder = new ListItemViewHolder(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String meditationId =  getMeditationData(viewHolder.getAdapterPosition(), MeditationsLoader.ResumeQuery._ID);
                    Uri uri = MeditationProvider.Meditations.withId(meditationId);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.putExtra(MeditationFragment.MEDITATION_TITLE_KEY, getMeditationData(viewHolder.getAdapterPosition(), MeditationsLoader.ResumeQuery.TITLE));
                    intent.putExtra(MeditationFragment.MEDITATION_VERSE_KEY, getMeditationData(viewHolder.getAdapterPosition(), MeditationsLoader.ResumeQuery.VERSE));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {

                        View containerShared = view.findViewById(R.id.ll_shared_container);
                        View titleShared = view.findViewById(R.id.tv_meditation_title);
                        View verseShared = view.findViewById(R.id.tv_meditation_verse);

                        Pair<View, String> containerTransition = new Pair<>(containerShared, containerShared.getTransitionName());
                        Pair<View, String> titleTransition = new Pair<>(titleShared, titleShared.getTransitionName());
                        Pair<View, String> verseTransition = new Pair<>(verseShared, verseShared.getTransitionName());

                        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                (AppCompatActivity) mContext,
                                containerTransition,
                                titleTransition,
                                verseTransition
                        ).toBundle();

                        mContext.startActivity(intent, bundle);

                    } else {
                        mContext.startActivity(intent);
                    }

                }
            });

            return viewHolder;
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.meditations_loading_list_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    private String getMeditationData(int position, int rowIndex) {
        mCursor.moveToPosition(position);
        return mCursor.getString(rowIndex);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ListItemViewHolder) {

            mCursor.moveToPosition(position);

            ListItemViewHolder holder = (ListItemViewHolder) viewHolder;

            holder.titleView.setText(mCursor.getString(MeditationsLoader.ResumeQuery.TITLE));

            String verse = mCursor.getString(MeditationsLoader.ResumeQuery.VERSE);
            if (verse != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    holder.textView.setText(Utils.trimSpannable((SpannableStringBuilder) Html.fromHtml(verse.trim(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)));
                else
                    holder.textView.setText(Utils.trimSpannable((SpannableStringBuilder) Html.fromHtml(verse.trim())));

            } else {
                holder.textView.setText("");
            }

            int weekNumber = mCursor.getInt(MeditationsLoader.ResumeQuery.WEEK_NUMBER);

            Calendar c = Calendar.getInstance();

            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            c.set(Calendar.WEEK_OF_YEAR, weekNumber);

            //holder.monthView.setText(mDateFormater.format(c.getTime()).toUpperCase().replace(".", ""));
            //holder.dayView.setText(String.format("%02d", c.get(Calendar.DAY_OF_MONTH)));
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

    public Cursor getCursor() {
        return this.mCursor;
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor != mCursor && mCursor != null && !mCursor.isClosed())
            mCursor.close();

        mCursor = newCursor;

        notifyDataSetChanged();
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }

    }

    public class ListItemViewHolder extends RecyclerView.ViewHolder {

        /*@BindView(R.id.tv_date_month)
        public TextView monthView;

        @BindView(R.id.tv_date_day)
        public TextView dayView;*/

        @BindView(R.id.tv_meditation_title)
        public TextView titleView;

        @BindView(R.id.tv_meditation_verse)
        public TextView textView;

        public ListItemViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);

            /*Typeface titleTypeFace = Typeface.createFromAsset(mContext.getAssets(), "RobotoSlab-Bold.ttf");
            Typeface typeFace = Typeface.createFromAsset(mContext.getAssets(), "RobotoSlab-Regular.ttf");

            titleView.setTypeface(titleTypeFace);
            textView.setTypeface(typeFace);*/
        }
    }

}
