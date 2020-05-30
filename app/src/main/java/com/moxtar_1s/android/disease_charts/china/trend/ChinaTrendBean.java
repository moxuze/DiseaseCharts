package com.moxtar_1s.android.disease_charts.china.trend;

import com.github.mikephil.charting.data.Entry;

import java.util.List;

public class ChinaTrendBean {
    private List<Entry> confirmedEntries;
    private List<Entry> deadEntries;
    private List<Entry> curedEntries;
    private List<Entry> suspectedEntries;

    public void setConfirmedEntries(List<Entry> confirmedEntries) {
        this.confirmedEntries = confirmedEntries;
    }

    public List<Entry> getConfirmedEntries() {
        return confirmedEntries;
    }

    public void setDeadEntries(List<Entry> deadEntries) {
        this.deadEntries = deadEntries;
    }

    public List<Entry> getDeadEntries() {
        return deadEntries;
    }

    public void setCuredEntries(List<Entry> curedEntries) {
        this.curedEntries = curedEntries;
    }

    public List<Entry> getCuredEntries() {
        return curedEntries;
    }

    public void setSuspectedEntries(List<Entry> suspectedEntries) {
        this.suspectedEntries = suspectedEntries;
    }

    public List<Entry> getSuspectedEntries() {
        return suspectedEntries;
    }
}
