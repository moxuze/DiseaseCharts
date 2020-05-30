package com.moxtar_1s.android.disease_charts.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    /**
     * 简介：要人老命的日期转float方法。
     * 功能：将JSON字符串中的M.dd格式的日期转化成float。
     * 注意：1577808000000L为2020-01-01这一天的Date类的fastTime，86400000L为1天（24*60*60*1000）
     *       应与{@link #floatToDate}联用
     * @return 返回构造Entry所需要的float型数据（构造Entry只能用float）。
     */
    public static float dateToFloat(String lastDate) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(lastDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        // 获取当前日期与2020-01-01这一天的fastTime的差值。
        long diff = (date.getTime() - 1577808000000L) / 86400000L;
        return (float) diff;
    }

    /**
     * 简介：要人老命的float转日期方法。
     * 功能：将float转化成M月d日格式的日期。
     * 注意：1577808000000L为2020-01-01这一天的Date类的fastTime，86400000L为1天（24*60*60*1000）
     *       应与{@link #dateToFloat}联用
     * @return 返回Entry中float型数据转换回来的日期。
     */
    public static String floatToDate(float diff) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
        Date date = new Date((long) diff * 86400000L + 1577808000000L);
        return sdf.format(date);
    }
}
