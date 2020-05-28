package com.moxtar_1s.android.disease_charts.utils;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChartComparator {
    private static final Comparator<Entry> CHART_COMPARATOR = new Comparator<Entry>() {
        @Override
        public int compare(Entry o1, Entry o2) {
            return (int) (o2.getY() - o1.getY());
        }
    };

    public static void sortPieEntries(ArrayList<PieEntry> list) {
        Collections.sort(list, CHART_COMPARATOR);
    }

    public static void sortBarEntries(ArrayList<BarEntry> list) {
        Collections.sort(list, CHART_COMPARATOR);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setX(i);
        }
    }
}
