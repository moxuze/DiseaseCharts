package com.moxtar_1s.android.disease_charts.global;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.moxtar_1s.android.disease_charts.R;
import com.moxtar_1s.android.disease_charts.utils.ColorUtil;
import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class CountryChartDrawer implements Observer, OnChartValueSelectedListener {
    private Activity mActivity;
    private Subject mSubject;
    private GlobalData mGlobalData;
    private BarChart mChart;
    private BarDataSet mTotalCuredSet;
    private BarDataSet mTotalDeadSet;
    private BarDataSet mTotalConfirmedSet;
    private BarDataSet mExistingConfirmedSet;
    private boolean isDataInitialized;

    CountryChartDrawer(BarChart chart, Activity activity) {
        mChart = chart;
        mActivity = activity;
        isDataInitialized = false;
    }

    @Override
    public void initialize() {
        {   // *样式设置* //
            // 禁用描述文本
            mChart.getDescription().setEnabled(true);
            mChart.getDescription().setTextSize(15f);
            mChart.getDescription().setTextColor(ContextCompat.getColor(mActivity, R.color.text));
            mChart.getDescription().setYOffset(200f);
            // 设定最大数值
            mChart.setMaxVisibleValueCount(35);
            // XY双轴缩放
            mChart.setPinchZoom(false);
            // 绘制阴影
            mChart.setDrawBarShadow(false);
            // 绘制参考线
            mChart.setDrawGridBackground(false);
            // 两边增加一点防止太挤
            mChart.setFitBars(true);
            // 当数值被选中时显示Marker
            MyMarkerView mv = new MyMarkerView(mActivity, R.layout.marker_view_country);
            mv.setChartView(mChart);
            mChart.setMarker(mv);
        }
        {   // *X轴样式* //
            final XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelRotationAngle(-60f);
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    IBarDataSet set = mChart.getData().getDataSetByIndex(0);
                    if (set != null) {
                        try {
                            Entry e = set.getEntryForIndex((int) value);
                            return (String) e.getData();
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                    return "?";
                }
            });
            mChart.getAxisLeft().setDrawGridLines(false);
        }
        {   // *Y轴样式* //
            YAxis yAxis = mChart.getAxisLeft();
            // 设置最低值
            yAxis.setAxisMinimum(0f);
        }
        {   // *legend设置* //
            mChart.getLegend().setEnabled(false);
        }
        mChart.setData(new BarData());
    }

    @Override
    public void update(Subject subject, Object data) {
        mSubject = subject;
        mGlobalData = (GlobalData) data;
        Map<String, ArrayList<BarEntry>> countryEntriesMap = mGlobalData.getCountryEntriesMap();
        if (isDataInitialized) {
            mTotalCuredSet.setValues(countryEntriesMap.get("totalCuredEntries"));
            mTotalDeadSet.setValues(countryEntriesMap.get("totalDeathEntries"));
            mTotalConfirmedSet.setValues(countryEntriesMap.get("totalConfirmedEntries"));
            mExistingConfirmedSet.setValues(countryEntriesMap.get("existingConfirmedEntries"));
        } else {
            mTotalCuredSet = initDateSet(countryEntriesMap.get("totalCuredEntries"),
                    mActivity.getString(R.string.total_cured));
            mTotalDeadSet = initDateSet(countryEntriesMap.get("totalDeathEntries"),
                    mActivity.getString(R.string.total_dead));
            mTotalConfirmedSet = initDateSet(countryEntriesMap.get("totalConfirmedEntries"),
                    mActivity.getString(R.string.total_confirmed));
            mExistingConfirmedSet = initDateSet(countryEntriesMap.get("existingConfirmedEntries"),
                    mActivity.getString(R.string.existing_confirmed));
            isDataInitialized = true;
        }
        // 设置数据
        setData(mTotalCuredSet);
        // 防止界面一下子加载太多动画而卡顿，更新过后只刷新一下图表（刷新要拉到顶的，看不到也无所谓）。
        mChart.invalidate();
    }

    private BarDataSet initDateSet(List<BarEntry> list, String label) {
        BarDataSet set = new BarDataSet(list, label);
        set.setColors(ColorUtil.getColorTemplate());
        set.setDrawValues(true);
        return set;
    }

    private void setData(BarDataSet set) {
        BarData data = mChart.getData();
        try {
            mChart.getDescription().setText(set.getLabel());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        data.clearValues();
        data.addDataSet(set);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Utils.formatNumber(value, 0, true);
            }
        });
        data.notifyDataChanged();
        mChart.notifyDataSetChanged();
    }

    private void animate() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChart.animateY(2000);
            }
        });
    }

    void loadTotalCured() {
        setData(mTotalCuredSet);
        animate();
    }

    void loadTotalDead() {
        setData(mTotalDeadSet);
        animate();
    }

    void loadTotalConfirmed() {
        setData(mTotalConfirmedSet);
        animate();
    }

    void loadExistingConfirmed() {
        setData(mExistingConfirmedSet);
        animate();
    }

    void disableObserve() {
        if (mSubject != null) {
            mSubject.deleteObserver(this);
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        mChart.setDrawMarkers(true);
    }

    @Override
    public void onNothingSelected() {}

    @SuppressLint("ViewConstructor")
    static class MyMarkerView extends MarkerView {
        private final TextView tvContent;

        public MyMarkerView(Activity context, int layoutResource) {
            super(context, layoutResource);
            tvContent = findViewById(R.id.tv_mv_country);
        }

        // 每次MarkerView被重绘时调用, 可被用于更新或格式化数据。
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            if (e instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) e;
                tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
            } else {
                String text = Utils.formatNumber(e.getY(), 0, true) +
                        getResources().getString(R.string.format) + '\n' + e.getData();
                tvContent.setText(text);
            }
            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2f), -getHeight());
        }
    }
}
