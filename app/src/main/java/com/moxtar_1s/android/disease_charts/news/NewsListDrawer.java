package com.moxtar_1s.android.disease_charts.news;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;

import java.util.ArrayList;

public class NewsListDrawer implements Observer {
    private Activity mActivity;
    private Subject mSubject;
    private NewsFragment.OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private NewsRecyclerViewAdapter mNewsRecyclerViewAdapter;
    private final ArrayList<NewsData.NewsItem> mNewsItemList;

    NewsListDrawer(RecyclerView recyclerView,
                   NewsFragment.OnListFragmentInteractionListener listener, Activity activity) {
        mRecyclerView = recyclerView;
        mListener = listener;
        mActivity = activity;
        mNewsItemList = new ArrayList<>();
    }

    @Override
    public void initialize() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mNewsRecyclerViewAdapter = new NewsRecyclerViewAdapter(mNewsItemList, mListener);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mNewsRecyclerViewAdapter);
            }
        });
    }

    @Override
    public void update(Subject subject, Object data) {
        mSubject = subject;
        NewsData newsData = (NewsData) data;
        if (newsData.isRefreshedData()) {
            mNewsItemList.clear();
        }
        mNewsItemList.addAll(newsData.getNewsItemList());
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mNewsRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    // 一定要在initialize()执行过后再执行，否则会找不到linearLayoutManager
    void setOnLoadMoreListener(final OnLoadMoreListener listener) {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager =
                        (LinearLayoutManager) mRecyclerView.getLayoutManager();
                if (linearLayoutManager != null) {
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == mNewsRecyclerViewAdapter.getItemCount()) {
                    listener.OnLoadMore();
                }
            }
        });
    }

    interface OnLoadMoreListener {
        void OnLoadMore();
    }

    public void disableObserve() {
        if (mSubject != null) {
            mSubject.deleteObserver(this);
        }
    }

}
