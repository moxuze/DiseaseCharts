package com.moxtar_1s.android.disease_charts.global;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.moxtar_1s.android.disease_charts.R;
import com.moxtar_1s.android.disease_charts.global.country.GlobalCountryDrawer;
import com.moxtar_1s.android.disease_charts.global.distribution.GlobalDistributionDrawer;
import com.moxtar_1s.android.disease_charts.global.introduction.GlobalIntroDrawer;
import com.moxtar_1s.android.disease_charts.global.introduction.GlobalIntroPrinter;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalFragment extends Fragment implements GlobalIntroPrinter {
    private ExecutorService mRefreshExecutor;
    private boolean isInitialized;

    private BarChart mCountryChart;
    private PieChart mDistributionChart;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView mTVIntroExistingConfirmed;
    private TextView mTVIntroConfirmed;
    private TextView mTVIntroDead;
    private TextView mTVIntroCured;
    private TextView mTVIntroDate;

    private RadioButton mRadioTotalCured;
    private RadioButton mRadioTotalDeath;
    private RadioButton mRadioTotalConfirmed;
    private RadioButton mRadioExistingConfirmed;

    private Switch mSwitchConfirmedExisting;

    private GlobalDataSubject mGlobalDataSubject;
    private GlobalIntroDrawer mGlobalIntroDrawer;
    private GlobalDistributionDrawer mGlobalDistributionDrawer;
    private GlobalCountryDrawer mGlobalCountryDrawer;

    public GlobalFragment() {}

    /**
     * 简介：生成新对象的方法，可以添加相关参数，防止横竖屏切换时丢失参数。
     * @return 返回对象实例
     */
    public static GlobalFragment newInstance() {
        return new GlobalFragment();
    }

    /**
     * 简介：这个方法中可以获得布局及其控件，完成各种初始化工作。
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_global, container, false);
        // 单例线程调度器，串行执行更新线程
        mRefreshExecutor = Executors.newSingleThreadExecutor();
        // 初始化标志，只有第一次加载时自动刷新一次
        isInitialized = false;
        // 创建Subject
        mGlobalDataSubject = new GlobalDataSubject();
        // 下拉刷新
        mSwipeRefreshLayout = view.findViewById(R.id.global_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefreshExecutor.execute(new RefreshRunnable());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        {   // *Introduction* //
            mTVIntroExistingConfirmed = view.findViewById(R.id.tv_intro_existing_confirmed);
            mTVIntroConfirmed = view.findViewById(R.id.tv_intro_confirmed);
            mTVIntroDead = view.findViewById(R.id.tv_intro_dead);
            mTVIntroCured = view.findViewById(R.id.tv_intro_cured);
            mTVIntroDate = view.findViewById(R.id.tv_intro_date);
            // 创建Drawer
            mGlobalIntroDrawer = new GlobalIntroDrawer(this);
            mGlobalDataSubject.addObserver(mGlobalIntroDrawer);
        }
        {   // *DistributionChart* //
            mDistributionChart = view.findViewById(R.id.distribution_chart);
            mSwitchConfirmedExisting = view.findViewById(R.id.switch_confirmed_existing);
            // 创建Drawer
            mGlobalDistributionDrawer = new GlobalDistributionDrawer(mDistributionChart, getActivity());
            mGlobalDataSubject.addObserver(mGlobalDistributionDrawer);
            // 设置控件
            mSwitchConfirmedExisting.setChecked(false);
            mSwitchConfirmedExisting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mGlobalDistributionDrawer.setTotalConfirmedEnabled(isChecked);
                }
            });
        }
        {   // *CountryChart* //
            mCountryChart = view.findViewById(R.id.country_chart);
            mRadioTotalCured = view.findViewById(R.id.radio_total_cured);
            mRadioTotalDeath = view.findViewById(R.id.radio_total_dead);
            mRadioTotalConfirmed = view.findViewById(R.id.radio_total_confirmed);
            mRadioExistingConfirmed = view.findViewById(R.id.radio_existing_confirmed);
            // 创建Drawer
            mGlobalCountryDrawer = new GlobalCountryDrawer(mCountryChart, getActivity());
            mGlobalDataSubject.addObserver(mGlobalCountryDrawer);
            // 设置控件
            mRadioTotalCured.setChecked(true);
            mRadioTotalCured.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mGlobalCountryDrawer.loadTotalCured();
                    }
                }
            });
            mRadioTotalDeath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mGlobalCountryDrawer.loadTotalDead();
                    }
                }
            });
            mRadioTotalConfirmed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mGlobalCountryDrawer.loadTotalConfirmed();
                    }
                }
            });
            mRadioExistingConfirmed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mGlobalCountryDrawer.loadExistingConfirmed();
                    }
                }
            });
        }
        return view;
    }

    /**
     * 简介:可供线程运行的刷新类。
     */
    class RefreshRunnable implements Runnable {
        @Override
        public void run() {
            try {
                mGlobalDataSubject.refreshData();
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

    /**
     * 简介；运行于主线程上发送Toast的方法。为了防止泄露，不能使用Looper来发送Toast（线程不会消亡）。
     */
    private void printMessage(final String msg) {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 简介：切换进入某个Fragment时就会调用的方法。首次刷新可以在这里调用。
     */
    @Override
    public void onResume() {
        super.onResume();
        // 首次刷新
        if (!isInitialized) {
            mRefreshExecutor.execute(new RefreshRunnable());
            isInitialized = true;
        }
    }

    @Override
    public void printGlobalIntro(String existingConfirmed, String confirmed,
                                 String dead, String cured, String date) {
        mTVIntroExistingConfirmed.setText(String.format(
                getString(R.string.intro_existing_confirmed), existingConfirmed));
        mTVIntroConfirmed.setText(String.format(
                getString(R.string.intro_total_confirmed), confirmed));
        mTVIntroDead.setText(String.format(
                getString(R.string.intro_total_dead), dead));
        mTVIntroCured.setText(String.format(
                getString(R.string.intro_total_cured), cured));
        mTVIntroDate.setText(String.format(
                getString(R.string.intro_date), date));
    }
}
