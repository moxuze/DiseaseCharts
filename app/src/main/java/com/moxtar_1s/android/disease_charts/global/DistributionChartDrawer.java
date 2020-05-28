package com.moxtar_1s.android.disease_charts.global;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.moxtar_1s.android.disease_charts.R;
import com.moxtar_1s.android.disease_charts.utils.Colors;
import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class DistributionChartDrawer implements Observer {
    private Activity mActivity;
    private Subject mSubject;
    private PieChart mChart;
    private PieDataSet mTotalConfirmedSet;
    private PieDataSet mExistingConfirmedSet;
    private boolean isDataInitialized;
    private boolean isTotalConfirmedSelected;

    DistributionChartDrawer(PieChart chart, Activity activity) {
        mChart = chart;
        mActivity = activity;
        isTotalConfirmedSelected = false;
        isDataInitialized = false;
    }

    @Override
    public void initialize() {
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setCenterText(generateCenterSpannableText());
        // 设置中空圆颜色与大小
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        // 设置能否旋转
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        // 设置是否使用百分比
        mChart.setUsePercentValues(true);
        // 饼图的绘制高度依赖于数据，必须在初始化的时候填充一个没有意义的数据进去防闪退
        PieData data = new PieData();
        ArrayList<PieEntry> list = new ArrayList<>();
        list.add(new PieEntry(0, "?"));
        PieDataSet set = new PieDataSet(list, "?");
        data.setDataSet(set);
        mChart.setData(data);
        // 设置legend
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString(mActivity.getString(R.string.self_introduction));
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 4, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 4, s.length() - 6, 0);
        s.setSpan(new RelativeSizeSpan(1f), 4, s.length() - 6, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 6, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mActivity, R.color.text)),
                s.length() - 6, s.length(), 0);
        return s;
    }

    @Override
    public void update(Subject subject, Object data) {
        mSubject = subject;
        GlobalData globalData = (GlobalData) data;
        Map<String, ArrayList<PieEntry>> distributionEntriesMap = globalData.getDistributionEntriesMap();
        if (isDataInitialized) {
            mTotalConfirmedSet.setValues(distributionEntriesMap.get("totalConfirmedEntries"));
            mExistingConfirmedSet.setValues(distributionEntriesMap.get("existingConfirmedEntries"));
        } else {
            mTotalConfirmedSet = initDateSet(distributionEntriesMap.get("totalConfirmedEntries"),
                    mActivity.getString(R.string.total_confirmed));
            mExistingConfirmedSet = initDateSet(distributionEntriesMap.get("existingConfirmedEntries"),
                    mActivity.getString(R.string.existing_confirmed));
            isDataInitialized = true;
        }
        // 设置数据
        setData();
        // 更新后播放动画
        animate();
    }

    private PieDataSet initDateSet(List<PieEntry> list, String label) {
        PieDataSet set = new PieDataSet(list, label);
        set.setSliceSpace(3f);
        set.setSelectionShift(5f);
        // 增加颜色
        set.setColors(Colors.getColorTemplate());
        // 设置数据偏移位置
        set.setValueLinePart1OffsetPercentage(80.f);
        set.setValueLinePart1Length(0.2f);
        set.setValueLinePart2Length(0.4f);
        // 设置数据位置
        set.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        return set;
    }

    // 饼图的绘制高度依赖于数据情况，为了防止未初始化数据后闪退，必须抓取空指针异常
    private void setData() {
        PieData data = new PieData();
        try {
            if (isTotalConfirmedSelected) {
                data.setDataSet(mTotalConfirmedSet);
            } else {
                data.setDataSet(mExistingConfirmedSet);
            }
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return new DecimalFormat("0.00").format(value) + "%";
                }
            });
            data.setValueTextSize(12f);
            data.setValueTextColor(ContextCompat.getColor(mActivity, R.color.text));
            mChart.setData(data);
            data.notifyDataChanged();
            mChart.notifyDataSetChanged();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void animate() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChart.animateY(1400, Easing.EaseInOutQuad);
            }
        });
    }

    void setTotalConfirmedEnabled(boolean selected) {
        isTotalConfirmedSelected = selected;
        setData();
        // 更新后播放动画
        animate();
    }

    void disableObserve() {
        if (mSubject != null) {
            mSubject.deleteObserver(this);
        }
    }
}
