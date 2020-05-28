package com.moxtar_1s.android.disease_charts.rumors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.moxtar_1s.android.disease_charts.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RumorsRecyclerViewAdapter extends
        RecyclerView.Adapter<RumorsRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<RumorsData.RumorsItem> mRumorsItemList;

    public RumorsRecyclerViewAdapter(ArrayList<RumorsData.RumorsItem> rumorsItemList) {
        mRumorsItemList = rumorsItemList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_rumors, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = mRumorsItemList.get(position);
        holder.tvRumorsTitle.setText(mRumorsItemList.get(position).title);
        holder.tvRumorsMainSummary.setText(mRumorsItemList.get(position).mainSummary);
        holder.tvRumorsBody.setText(mRumorsItemList.get(position).body);
    }

    @Override
    public int getItemCount() {
        return mRumorsItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public RumorsData.RumorsItem item;
        public final TextView tvRumorsTitle;
        public final TextView tvRumorsMainSummary;
        public final TextView tvRumorsBody;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvRumorsTitle = view.findViewById(R.id.tv_rumors_title);
            tvRumorsMainSummary = view.findViewById(R.id.tv_rumors_main_summary);
            tvRumorsBody = view.findViewById(R.id.tv_rumors_body);
        }
    }
}
