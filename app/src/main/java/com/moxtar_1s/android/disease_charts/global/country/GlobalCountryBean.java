package com.moxtar_1s.android.disease_charts.global.country;

import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

public class GlobalCountryBean {
    private List<BarEntry> totalConfirmedEntries;
    private List<BarEntry> totalDeathEntries;
    private List<BarEntry> totalCuredEntries;
    private List<BarEntry> existingConfirmedEntries;

    public void setTotalConfirmedEntries(List<BarEntry> totalConfirmedEntries) {
        this.totalConfirmedEntries = totalConfirmedEntries;
    }

    public List<BarEntry> getTotalConfirmedEntries() {
        return totalConfirmedEntries;
    }

    public void setTotalDeathEntries(List<BarEntry> totalDeathEntries) {
        this.totalDeathEntries = totalDeathEntries;
    }

    public List<BarEntry> getTotalDeathEntries() {
        return totalDeathEntries;
    }

    public void setTotalCuredEntries(List<BarEntry> totalCuredEntries) {
        this.totalCuredEntries = totalCuredEntries;
    }

    public List<BarEntry> getTotalCuredEntries() {
        return totalCuredEntries;
    }

    public void setExistingConfirmedEntries(List<BarEntry> existingConfirmedEntries) {
        this.existingConfirmedEntries = existingConfirmedEntries;
    }

    public List<BarEntry> getExistingConfirmedEntries() {
        return existingConfirmedEntries;
    }
}
