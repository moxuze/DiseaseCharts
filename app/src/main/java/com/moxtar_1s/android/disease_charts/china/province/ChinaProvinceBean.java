package com.moxtar_1s.android.disease_charts.china.province;

import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class ChinaProvinceBean {
    private List<BarEntry> totalCuredEntries;
    private List<BarEntry> totalDeadEntries;
    private List<BarEntry> totalConfirmedEntries;
    private List<BarEntry> existingConfirmedEntries;

    public void setTotalCuredEntries(List<BarEntry> totalCuredEntries) {
        this.totalCuredEntries = totalCuredEntries;
    }

    public List<BarEntry> getTotalCuredEntries() {
        return totalCuredEntries;
    }

    public void setTotalDeadEntries(List<BarEntry> totalDeadEntries) {
        this.totalDeadEntries = totalDeadEntries;
    }

    public List<BarEntry> getTotalDeadEntries() {
        return totalDeadEntries;
    }

    public void setTotalConfirmedEntries(List<BarEntry> totalConfirmedEntries) {
        this.totalConfirmedEntries = totalConfirmedEntries;
    }

    public List<BarEntry> getTotalConfirmedEntries() {
        return totalConfirmedEntries;
    }

    public void setExistingConfirmedEntries(List<BarEntry> existingConfirmedEntries) {
        this.existingConfirmedEntries = existingConfirmedEntries;
    }

    public List<BarEntry> getExistingConfirmedEntries() {
        return existingConfirmedEntries;
    }
}
