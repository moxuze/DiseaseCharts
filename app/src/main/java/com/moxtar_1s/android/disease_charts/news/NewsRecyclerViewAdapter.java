package com.moxtar_1s.android.disease_charts.news;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.moxtar_1s.android.disease_charts.R;
import com.moxtar_1s.android.disease_charts.news.NewsFragment.OnListFragmentInteractionListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NewsRecyclerViewAdapter extends
        RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<NewsData.NewsItem> mNewsItemList;
    private final OnListFragmentInteractionListener mListener;

    public NewsRecyclerViewAdapter(ArrayList<NewsData.NewsItem> newsItemList,
                                   OnListFragmentInteractionListener listener) {
        mNewsItemList = newsItemList;
        mListener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = mNewsItemList.get(position);
        holder.tvNewsTitle.setText(mNewsItemList.get(position).title);
        holder.tvNewsTitle.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);  // 下划线
        holder.tvNewsTitle.getPaint().setAntiAlias(true);                   // 抗锯齿
        holder.tvNewsSummary.setText(mNewsItemList.get(position).summary);
        holder.tvNewsDate.setText(mNewsItemList.get(position).date);
        holder.tvNewsSource.setText(mNewsItemList.get(position).infoSource);

        holder.tvNewsTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewsItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView tvNewsTitle;
        public final TextView tvNewsSummary;
        public final TextView tvNewsDate;
        public final TextView tvNewsSource;
        public NewsData.NewsItem item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            tvNewsTitle = view.findViewById(R.id.tv_news_title);
            tvNewsSummary = view.findViewById(R.id.tv_news_summary);
            tvNewsDate = view.findViewById(R.id.tv_news_date);
            tvNewsSource = view.findViewById(R.id.tv_news_source);
        }
    }
}
