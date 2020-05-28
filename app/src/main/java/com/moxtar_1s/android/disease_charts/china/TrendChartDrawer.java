package com.moxtar_1s.android.disease_charts.china;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.moxtar_1s.android.disease_charts.R;
import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class TrendChartDrawer implements Observer, OnChartValueSelectedListener {
    private Activity mActivity;
    private Subject mSubject;
    private LineChart mChart;
    private LineDataSet mConfirmedSet;
    private LineDataSet mDeadSet;
    private LineDataSet mCuredSet;
    private LineDataSet mSuspectedSet;
    private boolean isDataInitialized;
    private boolean isConfirmedEnabled;
    private boolean isDeadEnabled;
    private boolean isCuredEnabled;
    private boolean isSuspectedEnabled;

    TrendChartDrawer(LineChart chart, Activity activity) {
        mChart = chart;
        mActivity = activity;
        isDataInitialized = false;
        isConfirmedEnabled = true;
        isDeadEnabled = true;
        isCuredEnabled = true;
        isSuspectedEnabled = true;
    }

    @Override
    public void initialize() {
        {   // *样式设置* //
            // 禁用描述文本
            mChart.getDescription().setEnabled(false);
            // 允许触摸控制
            mChart.setTouchEnabled(true);
            // 设置监听
            mChart.setDrawGridBackground(false);
            // 当数值被选中时显示Marker
            MyMarkerView mv = new MyMarkerView(mActivity, R.layout.marker_view_trend);
            // 设置监听数据被选中时开启MarkerView
            // 搭配setXXXCountEnabled方法中的关闭MarkView（防止出现找不到纵坐标的空指针异常）
            mChart.setOnChartValueSelectedListener(this);
            // 将Marker设置进Chart中
            mv.setChartView(mChart);
            mChart.setMarker(mv);
            // 设置拖拽和坐标缩放
            mChart.setDragEnabled(true);
            mChart.setScaleXEnabled(true);
            // XY双轴缩放
            mChart.setPinchZoom(true);
        }
        {   // *X轴样式* //
            XAxis xAxis = mChart.getXAxis();
            // 纵向坐标参考线
            xAxis.enableGridDashedLine(10f, 10f, 0f);
            xAxis.setDrawGridLines(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            // X轴数据样式
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return ChinaData.floatToDate(value);
                }
            });
        }
        {   // *Y轴样式* //
            YAxis yAxis = mChart.getAxisLeft();
            // 只使用左Y轴
            mChart.getAxisRight().setEnabled(false);
            // 横向坐标参考线
            yAxis.enableGridDashedLine(1f, 1f, 0f);
            // 值域
            yAxis.setAxisMinimum(0f);
        }
        mChart.setData(new LineData());
    }

    @Override
    public void update(Subject subject, Object data) {
        mSubject = subject;
        ChinaData chinaData = (ChinaData) data;
        Map<String, ArrayList<Entry>> trendEntriesMap = chinaData.getTrendEntriesMap();
        if (isDataInitialized) {
            mConfirmedSet.setValues(trendEntriesMap.get("confirmedEntries"));
            mDeadSet.setValues(trendEntriesMap.get("deadEntries"));
            mCuredSet.setValues(trendEntriesMap.get("curedEntries"));
            mSuspectedSet.setValues(trendEntriesMap.get("suspectedEntries"));
        } else {
            mConfirmedSet = initDateSet(trendEntriesMap.get("confirmedEntries"), mActivity.getString(R.string.confirmed),
                    ContextCompat.getColor(mActivity, R.color.total_confirmed));
            mDeadSet = initDateSet(trendEntriesMap.get("deadEntries"), mActivity.getString(R.string.dead),
                    ContextCompat.getColor(mActivity, R.color.total_dead));
            mCuredSet = initDateSet(trendEntriesMap.get("curedEntries"), mActivity.getString(R.string.cured),
                    ContextCompat.getColor(mActivity, R.color.total_cured));
            mSuspectedSet = initDateSet(trendEntriesMap.get("suspectedEntries"), mActivity.getString(R.string.suspected),
                    ContextCompat.getColor(mActivity, R.color.existing_suspected));
            isDataInitialized = true;
        }
        // 设置数据
        setData();
        // 这个图表用不到播放动画，单纯只有刷新。
        mChart.invalidate();
        // 获取legend，只有在填充数据后才可使用
        Legend l = mChart.getLegend();
        // 画legend线
        l.setForm(Legend.LegendForm.SQUARE);
    }

    private LineDataSet initDateSet(List<Entry> list, String label, int color) {
        LineDataSet set = new LineDataSet(list, label);
        set.setDrawIcons(false);
        // 设置点和圆的颜色
        set.setColor(color);
        set.setCircleColor(color);
        // 线宽和点大小
        set.setLineWidth(3f);
        set.setCircleRadius(1f);
        // 节点画空心圆
        set.setDrawCircleHole(false);
        // 自定义legend效果
        set.setFormLineWidth(1f);
        set.setFormSize(8f);
        // 内容文本的尺寸
        set.setValueTextSize(9f);
        set.setValueTextColor(ContextCompat.getColor(mActivity, R.color.value));
        // 设置包括的范围区域填充颜色
        set.setDrawFilled(false);
        // 设置平滑曲线
        set.setCubicIntensity(0.1f);
        // 设置数据样式
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Utils.formatNumber(value, 0, true);
            }
        });
        return set;
    }

    private void setData() {
        LineData data = mChart.getData();
        data.clearValues();
        if (isConfirmedEnabled) {
            data.addDataSet(mConfirmedSet);
        }
        if (isDeadEnabled) {
            data.addDataSet(mDeadSet);
        }
        if (isCuredEnabled) {
            data.addDataSet(mCuredSet);
        }
        if (isSuspectedEnabled) {
            data.addDataSet(mSuspectedSet);
        }
        data.notifyDataChanged();
        mChart.notifyDataSetChanged();
    }

    void setConfirmedEnabled(boolean enabled) {
        mChart.setDrawMarkers(false);
        isConfirmedEnabled = enabled;
        setData();
        mChart.invalidate();
    }

    void setDeadEnabled(boolean enabled) {
        mChart.setDrawMarkers(false);
        isDeadEnabled = enabled;
        setData();
        mChart.invalidate();
    }

    void setCuredEnabled(boolean enabled) {
        mChart.setDrawMarkers(false);
        isCuredEnabled = enabled;
        setData();
        mChart.invalidate();
    }

    void setSuspectedEnabled(boolean enabled) {
        mChart.setDrawMarkers(false);
        isSuspectedEnabled = enabled;
        setData();
        mChart.invalidate();
    }

    public void disableObserve() {
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
        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            tvContent = findViewById(R.id.tv_mv_trend);
        }

        // 每次MarkerView被重绘时调用, 可被用于更新或格式化数据。
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            if (e instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) e;
                tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
            } else {
                String text = Utils.formatNumber(e.getY(), 0, true) +
                        getResources().getString(R.string.format) + '\n' + ChinaData.floatToDate(e.getX());
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
