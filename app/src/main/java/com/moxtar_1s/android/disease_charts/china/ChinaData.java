package com.moxtar_1s.android.disease_charts.china;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.moxtar_1s.android.disease_charts.utils.ChartComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MoXtar
 * 简介：{@link ChinaDataSubject}的数据实体部分。
 * 功能：将JSON字符串拆分并封装成各种Entry的列表（只记录数据，不设计外观），
 *       以便于{@link com.moxtar_1s.android.disease_charts.pattern.Observer}更新数据时调用。
 * 注意：这个类与服务器端发来的信息高度耦合。
 * <p>
 * 如果想提取出自己感兴趣的内容，建议尽量继承这个类，添加自己的成员变量并实现get方法。
 * 按顺序做：
 * 1. 添加自己的成员变量，通常为EntryList的List或Map（Map会更好，尽量避免由List的有序性造成增删困难）。
 * 2. 添加自己的load方法，提取{@link #mJSONObject}的信息并存入自己成员元变量中。
 * 3. 添加自己的get方法，返回对应的成员变量。
 * 4. 更改{@link ChinaDataSubject#mChinaData}的实体数据类型为你继承的新类型。
 * 5. 实现{@link com.moxtar_1s.android.disease_charts.pattern.Observer}类
 *    并在{@link com.moxtar_1s.android.disease_charts.pattern.Observer#update}中调用get方法。
 */
class ChinaData {
    // 封装了信息，被get方法返回的成员变量。
    protected final String mJSONString;
    protected final JSONObject mJSONObject;
    private JSONArray mProvinces;
    private Map<String, String> mIntroductionMap;
    private Map<String, ArrayList<PieEntry>> mDistributionEntriesMap;
    private Map<String, ArrayList<BarEntry>> mProvinceEntriesMap;
    private Map<String, ArrayList<Entry>> mTrendEntriesMap;
    private float mTotalConfirmed = 0;
    private float mExistingConfirmed = 0;
    private static float DISTRIBUTiON_LIMIT_RATE = 0.015f;

    /**
     * 简介：构造方法。
     * 功能：将参数JSON字符串封装成DiseaseInfo对象。
     * 调用：{@link ChinaDataSubject#refreshData()}
     * @param jsonString 服务器端发来的JSON字符串
     * @throws JSONException JSON字符串解析出错时抛出的异常
     */
    ChinaData(String jsonString) throws JSONException {
        mJSONString = jsonString;
        mJSONObject = new JSONObject(jsonString);
        JSONArray areaTree = mJSONObject.getJSONObject("data").getJSONArray("areaTree");
        JSONObject china = new JSONObject();
        for (int i = 0; i < areaTree.length(); i++) {
            china = areaTree.getJSONObject(i);
            if (china.getString("name").equals("中国")) {
                break;
            }
        }
        mProvinces = china.getJSONArray("children");
        loadIntroductionMap();
        loadDistributionEntriesMap();
        loadProvinceEntriesMap();
        loadTrendEntriesMap();
    }

    /**
     * 简介：要人老命的日期转float方法。
     * 功能：将JSON字符串中的M.dd格式的日期转化成float。
     * 调用：{@link #getTrendEntriesMap()}
     * 注意：1577808000000L为2020-01-01这一天的Date类的fastTime，86400000L为1天（24*60*60*1000）
     *       应与{@link #floatToDate}联用
     * @return 返回构造Entry所需要的float型数据（构造Entry只能用float）。
     */
    static float dateToFloat(String lastDate) {
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
     * 调用：{@link TrendChartDrawer#initialize()}
     * 注意：1577808000000L为2020-01-01这一天的Date类的fastTime，86400000L为1天（24*60*60*1000）
     *       应与{@link #dateToFloat}联用
     * @return 返回Entry中float型数据转换回来的日期。
     */
    static String floatToDate(float diff) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
        Date date = new Date((long) diff * 86400000L + 1577808000000L);
        return sdf.format(date);
    }

    /**
     * 简介：{@link #mJSONString}的get方法。
     * @return 返回成员变量mJSONString
     */
    String getJSONString() {
        return mJSONString;
    }

    /**
     * 简介：{@link #mJSONObject}的get方法。
     * @return 返回成员变量mJSONObject
     */
    JSONObject getJSONObject() {
        return mJSONObject;
    }

    /**
     * 简介：{@link #mIntroductionMap}的set方法。
     * 调用：{@link #ChinaData}
     */
    private void loadIntroductionMap() {
        mIntroductionMap = new HashMap<>();
        try {
            JSONObject data = mJSONObject.getJSONObject("data");
            JSONObject chinaTotal = data.getJSONObject("chinaTotal");
            JSONObject today = chinaTotal.getJSONObject("today");
            JSONObject total = chinaTotal.getJSONObject("total");
            JSONObject extData = chinaTotal.getJSONObject("extData");

            int totalConfirmed = getInt(total, "confirm");
            int totalDead = getInt(total, "dead");
            int totalCured = getInt(total, "heal");
            int existingConfirmed = totalConfirmed - totalDead - totalCured;
            int noSymptom = getInt(extData, "noSymptom");
            int input = getInt(total, "input");

            int increaseConfirmed = getInt(today, "confirm");
            int increaseDead = getInt(today, "dead");
            int increaseCured = getInt(today, "heal");
            int increaseExistingConfirmed = getInt(today, "storeConfirm");
            int increaseNoSymptom = getInt(extData, "incrNoSymptom");
            int increaseInput = getInt(today, "input");

            mTotalConfirmed = totalConfirmed;
            mExistingConfirmed = existingConfirmed;
            mIntroductionMap.put("existingConfirmed", format(existingConfirmed, increaseExistingConfirmed));
            mIntroductionMap.put("noSymptom", format(noSymptom, increaseNoSymptom));
            mIntroductionMap.put("input", format(input, increaseInput));
            mIntroductionMap.put("confirmed", format(totalConfirmed, increaseConfirmed));
            mIntroductionMap.put("dead", format(totalDead, increaseDead));
            mIntroductionMap.put("cured", format(totalCured, increaseCured));
            mIntroductionMap.put("date", data.getString("lastUpdateTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String format(int cases, int increase) {
        if (increase > 0) {
            return cases + "\n+" + increase;
        } else if (increase == 0) {
            return cases + "\n--";
        } else {
            return cases + "\n" + increase;
        }
    }

    private int getInt(JSONObject obj, String name) {
        int i = 0;
        try {
            i = obj.getInt(name);
        } catch (NullPointerException | JSONException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 简介：{@link #mIntroductionMap}的get方法。
     * 调用：{@link IntroductionDrawer#update}
     * @return 返回成员变量mIntroductionList
     */
    Map<String, String> getIntroductionMap() {
        return mIntroductionMap;
    }

    /**
     * 简介：{@link #mDistributionEntriesMap}的set方法。
     * 调用：{@link #ChinaData}
     */
    private void loadDistributionEntriesMap() {
        mDistributionEntriesMap = new HashMap<>();
        ArrayList<PieEntry> totalConfirmedEntries = new ArrayList<>();
        ArrayList<PieEntry> existingConfirmedEntries = new ArrayList<>();
        try {
            int otherTotalConfirmed = 0;
            int otherExistingConfirmed = 0;
            for (int i = 0; i < mProvinces.length(); i++) {
                JSONObject province = mProvinces.getJSONObject(i);
                String provinceName = province.getString("name");
                JSONObject total = province.getJSONObject("total");
                int totalConfirmed = getInt(total, "confirm");
                int totalDead = getInt(total, "dead");
                int totalCured = getInt(total, "heal");
                int existingConfirmed = totalConfirmed - totalDead - totalCured;
                if (totalConfirmed >= mTotalConfirmed * DISTRIBUTiON_LIMIT_RATE) {
                    totalConfirmedEntries.add(new PieEntry(totalConfirmed, provinceName));
                } else {
                    otherTotalConfirmed += totalConfirmed;
                }
                if (existingConfirmed >= mExistingConfirmed * DISTRIBUTiON_LIMIT_RATE) {
                    existingConfirmedEntries.add(new PieEntry(existingConfirmed, provinceName));
                } else {
                    otherExistingConfirmed += existingConfirmed;
                }
            }
            ChartComparator.sortPieEntries(totalConfirmedEntries);
            ChartComparator.sortPieEntries(existingConfirmedEntries);
            if (otherTotalConfirmed > 0) {
                totalConfirmedEntries.add(new PieEntry(otherTotalConfirmed, "其他"));
            }
            if (otherExistingConfirmed > 0) {
                existingConfirmedEntries.add(new PieEntry(otherExistingConfirmed, "其他"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mDistributionEntriesMap.put("totalConfirmedEntries", totalConfirmedEntries);
        mDistributionEntriesMap.put("existingConfirmedEntries", existingConfirmedEntries);
    }

    /**
     * 简介：{@link #mDistributionEntriesMap}的get方法。
     * 调用：{@link DistributionChartDrawer#update}
     * @return 返回成员变量mDistributionLists
     */
    Map<String, ArrayList<PieEntry>> getDistributionEntriesMap() {
        return mDistributionEntriesMap;
    }

    /**
     * 简介：{@link #mTrendEntriesMap}的set方法。
     * 调用：{@link #ChinaData}
     */
    private void loadTrendEntriesMap() {
        mTrendEntriesMap = new HashMap<>();
        ArrayList<Entry> confirmedEntries = new ArrayList<>();
        ArrayList<Entry> deadEntries = new ArrayList<>();
        ArrayList<Entry> curedEntries = new ArrayList<>();
        ArrayList<Entry> suspectedEntries = new ArrayList<>();
        try {
            JSONObject data = mJSONObject.getJSONObject("data");
            JSONArray chinaDayList = data.getJSONArray("chinaDayList");
            for (int i = 0; i < chinaDayList.length(); i++) {
                JSONObject chinaDay = chinaDayList.getJSONObject(i);
                JSONObject chinaDayTotal = chinaDay.getJSONObject("total");
                float date = dateToFloat(chinaDay.getString("date"));
                confirmedEntries.add(new Entry(date, chinaDayTotal.getInt("confirm")));
                deadEntries.add(new Entry(date, chinaDayTotal.getInt("dead")));
                curedEntries.add(new Entry(date, chinaDayTotal.getInt("heal")));
                suspectedEntries.add(new Entry(date, chinaDayTotal.getInt("suspect")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mTrendEntriesMap.put("confirmedEntries", confirmedEntries);
        mTrendEntriesMap.put("deadEntries", deadEntries);
        mTrendEntriesMap.put("curedEntries", curedEntries);
        mTrendEntriesMap.put("suspectedEntries", suspectedEntries);
    }

    /**
     * 简介：{@link #mTrendEntriesMap}的get方法。
     * 调用：{@link TrendChartDrawer#update}
     * @return 返回成员变量mTrendLists
     */
    Map<String, ArrayList<Entry>> getTrendEntriesMap(){
        return mTrendEntriesMap;
    }

    /**
     * 简介：{@link #mProvinceEntriesMap}的set方法。
     * 调用：{@link #ChinaData}
     */
    private void loadProvinceEntriesMap() {
        mProvinceEntriesMap = new HashMap<>();
        ArrayList<BarEntry> totalConfirmedEntries = new ArrayList<>();
        ArrayList<BarEntry> totalDeathEntries = new ArrayList<>();
        ArrayList<BarEntry> totalCuredEntries = new ArrayList<>();
        ArrayList<BarEntry> existingConfirmedEntries = new ArrayList<>();
        try {
            for (int i = 0; i < mProvinces.length(); i++) {
                JSONObject province = mProvinces.getJSONObject(i);
                JSONObject provinceTotal = province.getJSONObject("total");
                int totalConfirmed = provinceTotal.getInt("confirm");
                int totalDeath = provinceTotal.getInt("dead");
                int totalCured = provinceTotal.getInt("heal");
                int existingConfirmed = totalConfirmed - totalCured - totalDeath;
                String provinceName = province.getString("name");
                totalConfirmedEntries.add(new BarEntry(i, totalConfirmed, provinceName));
                totalDeathEntries.add(new BarEntry(i, totalDeath, provinceName));
                totalCuredEntries.add(new BarEntry(i, totalCured, provinceName));
                existingConfirmedEntries.add(new BarEntry(i, existingConfirmed, provinceName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ChartComparator.sortBarEntries(totalConfirmedEntries);
        ChartComparator.sortBarEntries(totalDeathEntries);
        ChartComparator.sortBarEntries(totalCuredEntries);
        ChartComparator.sortBarEntries(existingConfirmedEntries);
        mProvinceEntriesMap.put("totalConfirmedEntries", totalConfirmedEntries);
        mProvinceEntriesMap.put("totalDeathEntries", totalDeathEntries);
        mProvinceEntriesMap.put("totalCuredEntries", totalCuredEntries);
        mProvinceEntriesMap.put("existingConfirmedEntries", existingConfirmedEntries);
    }

    /**
     * 简介：{@link #mProvinceEntriesMap}的get方法。
     * 调用：{@link ProvinceChartDrawer#update}
     * @return 返回成员变量mProvinceLists
     */
    Map<String, ArrayList<BarEntry>> getProvinceEntriesMap() {
        return mProvinceEntriesMap;
    }
}
