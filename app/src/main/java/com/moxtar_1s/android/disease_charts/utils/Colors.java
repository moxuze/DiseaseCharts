package com.moxtar_1s.android.disease_charts.utils;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Colors {
    private static final ArrayList<Integer> COLOR_TEMPLATE;
    static {
        COLOR_TEMPLATE = new ArrayList<>();
        for (int c : ColorTemplate.COLORFUL_COLORS) {
            COLOR_TEMPLATE.add(c);
        }
        for (int c : ColorTemplate.MATERIAL_COLORS) {
            COLOR_TEMPLATE.add(c);
        }
        for (int c : ColorTemplate.PASTEL_COLORS) {
            COLOR_TEMPLATE.add(c);
        }
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            COLOR_TEMPLATE.add(c);
        }

    }

    public static ArrayList<Integer> getColorTemplate() {
        return COLOR_TEMPLATE;
    }
}
