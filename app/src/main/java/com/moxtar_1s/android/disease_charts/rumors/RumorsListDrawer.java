package com.moxtar_1s.android.disease_charts.rumors;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;

import java.util.ArrayList;

public class RumorsListDrawer implements Observer {
    private Activity mActivity;
    private Subject mSubject;
    private RecyclerView mRecyclerView;
    private RumorsRecyclerViewAdapter mRumorsRecyclerViewAdapter;
    private final ArrayList<RumorsData.RumorsItem> mRumorsItemList;

    RumorsListDrawer(RecyclerView recyclerView, Activity activity) {
        mRecyclerView = recyclerView;
        mActivity = activity;
        mRumorsItemList = new ArrayList<>();
    }

    @Override
    public void initialize() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRumorsRecyclerViewAdapter = new RumorsRecyclerViewAdapter(mRumorsItemList);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(mRumorsRecyclerViewAdapter);
            }
        });
    }

    @Override
    public void update(Subject subject, Object data) {
        mSubject = subject;
        RumorsData rumorsData = (RumorsData) data;
        if (rumorsData.isRefreshedData()) {
            mRumorsItemList.clear();
        }
        mRumorsItemList.addAll(rumorsData.getRumorsItemList());
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRumorsRecyclerViewAdapter.notifyDataSetChanged();
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
                        && lastVisibleItem + 1 == mRumorsRecyclerViewAdapter.getItemCount()) {
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
