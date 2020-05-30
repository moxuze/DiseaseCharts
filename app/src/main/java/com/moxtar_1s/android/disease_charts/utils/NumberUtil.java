package com.moxtar_1s.android.disease_charts.utils;

import java.text.DecimalFormat;

public class NumberUtil {
    public static String formatNumber(int number) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(number);
    }
}
