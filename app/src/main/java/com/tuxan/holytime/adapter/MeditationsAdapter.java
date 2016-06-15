package com.tuxan.holytime.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuxan.holytime.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeditationsAdapter extends RecyclerView.Adapter<MeditationsAdapter.ListItemViewHolder> {

    Cursor mCursor;
    Context mContext;

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
        holder.subtitleView.setText(mCursor.getString(MeditationsLoader.Query.AUTHOR));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public static class ListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_meditation_title)
        public TextView titleView;

        @BindView(R.id.tv_meditation_text)
        public TextView subtitleView;

        public ListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
