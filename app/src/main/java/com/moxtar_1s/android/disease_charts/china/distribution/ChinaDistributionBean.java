package com.moxtar_1s.android.disease_charts.china.distribution;

import com.github.mikephil.charting.data.PieEntry;

import java.util.List;

public class ChinaDistributionBean {
    private List<PieEntry> totalConfirmedEntries;
    private List<PieEntry> existingConfirmedEntries;

    public void setTotalConfirmedEntries(List<PieEntry> totalConfirmedEntries) {
        this.totalConfirmedEntries = totalConfirmedEntries;
    }

    public List<PieEntry> getTotalConfirmedEntries() {
        return totalConfirmedEntries;
    }

    public void setExistingConfirmedEntries(List<PieEntry> existingConfirmedEntries) {
        this.existingConfirmedEntries = existingConfirmedEntries;
    }

    public List<PieEntry> getExistingConfirmedEntries() {
        return existingConfirmedEntries;
    }
}
