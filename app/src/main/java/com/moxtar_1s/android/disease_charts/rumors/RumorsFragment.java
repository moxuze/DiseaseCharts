package com.moxtar_1s.android.disease_charts.rumors;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.moxtar_1s.android.disease_charts.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RumorsFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RumorsDataSubject mRumorsDataSubject;
    private RumorsListDrawer mRumorsListDrawer;
    private ExecutorService mRefreshExecutor;
    private boolean isInitialized;

    /**
     * 构造器必须为空，否则在横竖屏切换时会丢失参数
     */
    public RumorsFragment() {
    }

    public static RumorsFragment newInstance() {
        return new RumorsFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rumors_list, container, false);
        // 单例线程调度器，串行执行更新线程
        mRefreshExecutor = Executors.newFixedThreadPool(2);
        // 初始化标志，只有第一次加载时自动刷新一次
        isInitialized = false;
        // 创建Subject
        mRumorsDataSubject = new RumorsDataSubject();
        // 设置适配器
        RecyclerView recyclerView = view.findViewById(R.id.list);
        mRumorsListDrawer = new RumorsListDrawer(recyclerView, getActivity());
        mRumorsDataSubject.addObserver(mRumorsListDrawer);
        // 下拉刷新
        mSwipeRefreshLayout = view.findViewById(R.id.rumors_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshExecutor.execute(new RumorsFragment.RefreshRunnable());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        // 上拉加载
        mRumorsListDrawer.setOnLoadMoreListener(new RumorsListDrawer.OnLoadMoreListener() {
            @Override
            public void OnLoadMore() {
                mRefreshExecutor.execute(new RumorsFragment.LoadMoreRunnable());
            }
        });

        return view;
    }

    class RefreshRunnable implements Runnable {
        @Override
        public void run() {
            try {
                mRumorsDataSubject.refreshData();
                printMessage(getResources().getString(R.string.msg_success_refresh));
            } catch (IOException e) {
                printMessage(getResources().getString(R.string.msg_err_request));
                e.printStackTrace();
            } catch (JSONException e) {
                printMessage(getResources().getString(R.string.msg_err_json));
                e.printStackTrace();
            }
        }
    }

    class LoadMoreRunnable implements Runnable {
        @Override
        public void run() {
            try {
                mRumorsDataSubject.loadMoreData();
                printMessage(getResources().getString(R.string.msg_success_load));
            } catch (IOException e) {
                printMessage(getResources().getString(R.string.msg_err_request));
                e.printStackTrace();
            } catch (JSONException e) {
                printMessage(getResources().getString(R.string.msg_err_json));
                e.printStackTrace();
            }
        }
    }

    private void printMessage(final String msg) {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 首次刷新
        if (!isInitialized) {
            mRefreshExecutor.execute(new RumorsFragment.RefreshRunnable());
            isInitialized = true;
        }
    }
}