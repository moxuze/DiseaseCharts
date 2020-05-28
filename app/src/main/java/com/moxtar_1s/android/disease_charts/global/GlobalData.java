package com.moxtar_1s.android.disease_charts.global;

import android.annotation.SuppressLint;

import com.github.mikephil.charting.data.BarEntry;
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
import java.util.Objects;

class GlobalData {
    protected final String mJSONString;
    protected final JSONObject mJSONObject;
    private Map<String, String> mIntroductionMap;
    private Map<String, ArrayList<PieEntry>> mDistributionEntriesMap;
    private Map<String, ArrayList<BarEntry>> mCountryEntriesMap;
    private float mTotalConfirmed = 0;
    private float mTotalDead = 0;
    private float mTotalCured = 0;
    private float mExistingConfirmed = 0;
    private static float DISTRIBUTiON_LIMIT_RATE = 0.015f;
    private static float COUNTRY_LIMIT_RATE = 0.005f;

    GlobalData(String jsonString) throws JSONException {
        mJSONString = jsonString;
        mJSONObject = new JSONObject(jsonString);
        loadIntroductionMap();
        loadDistributionEntriesMap();
        loadCountryEntriesMap();
    }

    String getJSONString() {
        return mJSONString;
    }

    JSONObject getJSONObject() {
        return mJSONObject;
    }

    private void loadIntroductionMap() {
        mIntroductionMap = new HashMap<>();
        long nowFastTime = new Date().getTime();
        long todayFastTime = nowFastTime - nowFastTime % 86400000L;
        try {
            JSONObject data = mJSONObject.getJSONObject("data");
            JSONArray areaTree = data.getJSONArray("areaTree");
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int totalConfirmed = 0, totalDead = 0, totalCured = 0, existingConfirmed = 0;
            int increaseConfirmed = 0, increaseDead = 0, increaseCured = 0, increaseExistingConfirmed = 0;
            for (int i = 0; i < areaTree.length(); i++) {
                JSONObject country = areaTree.getJSONObject(i);
                JSONObject today = country.getJSONObject("today");
                JSONObject total = country.getJSONObject("total");

                totalConfirmed += getInt(total, "confirm");
                totalDead += getInt(total, "dead");
                totalCured += getInt(total, "heal");

                String date = country.getString("lastUpdateTime");
                long updateFastTime = Objects.requireNonNull(sdf.parse(date)).getTime();
                long updateDayFastTime = updateFastTime - updateFastTime % 86400000L;
                // “较昨日”数据的日期检查
                if (updateDayFastTime == todayFastTime) {
                    increaseConfirmed += getInt(today, "confirm");
                    increaseDead += getInt(today, "dead");
                    increaseCured += getInt(today, "heal");
                }
            }
            existingConfirmed = totalConfirmed - totalDead - totalCured;
            increaseExistingConfirmed = increaseConfirmed - increaseDead - increaseCured;
            mTotalConfirmed = totalConfirmed;
            mTotalDead = totalDead;
            mTotalCured = totalCured;
            mExistingConfirmed = existingConfirmed;
            mIntroductionMap.put("existingConfirmed", format(existingConfirmed, increaseExistingConfirmed));
            mIntroductionMap.put("confirmed", format(totalConfirmed, increaseConfirmed));
            mIntroductionMap.put("dead", format(totalDead, increaseDead));
            mIntroductionMap.put("cured", format(totalCured, increaseCured));
            mIntroductionMap.put("date", data.getString("lastUpdateTime"));
        } catch (JSONException | ParseException e) {
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

    Map<String, String> getIntroductionMap() {
        return mIntroductionMap;
    }

    private void loadDistributionEntriesMap() {
        mDistributionEntriesMap = new HashMap<>();
        ArrayList<PieEntry> totalConfirmedEntries = new ArrayList<>();
        ArrayList<PieEntry> existingConfirmedEntries = new ArrayList<>();
        try {
            JSONObject data = mJSONObject.getJSONObject("data");
            JSONArray areaTree = data.getJSONArray("areaTree");
            int otherTotalConfirmed = 0;
            int otherExistingConfirmed = 0;
            for (int i = 0; i < areaTree.length(); i++) {
                JSONObject country = areaTree.getJSONObject(i);
                String countryName = country.getString("name");
                JSONObject total = country.getJSONObject("total");
                int totalConfirmed = getInt(total, "confirm");
                int totalDead = getInt(total, "dead");
                int totalCured = getInt(total, "heal");
                int existingConfirmed = totalConfirmed - totalDead - totalCured;
                if (totalConfirmed >= mTotalConfirmed * DISTRIBUTiON_LIMIT_RATE) {
                    totalConfirmedEntries.add(new PieEntry(totalConfirmed, countryName));
                } else {
                    otherTotalConfirmed += totalConfirmed;
                }
                if (existingConfirmed >= mExistingConfirmed * DISTRIBUTiON_LIMIT_RATE) {
                    existingConfirmedEntries.add(new PieEntry(existingConfirmed, countryName));
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

    Map<String, ArrayList<PieEntry>> getDistributionEntriesMap() {
        return mDistributionEntriesMap;
    }

    private void loadCountryEntriesMap() {
        mCountryEntriesMap = new HashMap<>();
        ArrayList<BarEntry> totalConfirmedEntries = new ArrayList<>();
        ArrayList<BarEntry> totalDeathEntries = new ArrayList<>();
        ArrayList<BarEntry> totalCuredEntries = new ArrayList<>();
        ArrayList<BarEntry> existingConfirmedEntries = new ArrayList<>();
        try {
            JSONObject data = mJSONObject.getJSONObject("data");
            JSONArray areaTree = data.getJSONArray("areaTree");
            int i1 = 0, i2 = 0, i3 = 0, i4 = 0;
            int otherTotalConfirmed = 0, otherTotalDead = 0,
                    otherTotalCured = 0, otherExistingConfirmed = 0;
            for (int i = 0; i < areaTree.length(); i++) {
                JSONObject country = areaTree.getJSONObject(i);
                JSONObject countryTotal = country.getJSONObject("total");
                int totalCured = countryTotal.getInt("heal");
                int totalDeath = countryTotal.getInt("dead");
                int totalConfirmed = countryTotal.getInt("confirm");
                int existingConfirmed = totalConfirmed - totalCured - totalDeath;
                String countryName = country.getString("name");
                if (totalConfirmed >= mTotalConfirmed * COUNTRY_LIMIT_RATE) {
                    totalConfirmedEntries.add(new BarEntry(i1++, totalConfirmed, countryName));
                } else {
                    otherTotalConfirmed += totalConfirmed;
                }
                if (totalDeath >= mTotalDead * COUNTRY_LIMIT_RATE) {
                    totalDeathEntries.add(new BarEntry(i3++, totalDeath, countryName));
                } else {
                    otherTotalDead += totalDeath;
                }
                if (totalCured >= mTotalCured * COUNTRY_LIMIT_RATE) {
                    totalCuredEntries.add(new BarEntry(i2++, totalCured, countryName));
                } else {
                    otherTotalCured += totalCured;
                }
                if (existingConfirmed >= mExistingConfirmed * COUNTRY_LIMIT_RATE) {
                    existingConfirmedEntries.add(new BarEntry(i4++, existingConfirmed, countryName));
                } else {
                    otherExistingConfirmed += existingConfirmed;
                }
            }
            ChartComparator.sortBarEntries(totalConfirmedEntries);
            ChartComparator.sortBarEntries(totalDeathEntries);
            ChartComparator.sortBarEntries(totalCuredEntries);
            ChartComparator.sortBarEntries(existingConfirmedEntries);
            if (otherTotalConfirmed > 0) {
                totalConfirmedEntries.add(new BarEntry(i1, otherTotalConfirmed, "其他"));
            }
            if (otherTotalDead > 0) {
                totalDeathEntries.add(new BarEntry(i3, otherTotalDead, "其他"));
            }
            if (otherTotalCured > 0) {
                totalCuredEntries.add(new BarEntry(i2, otherTotalCured, "其他"));
            }
            if (otherExistingConfirmed > 0) {
                existingConfirmedEntries.add(new BarEntry(i4, otherExistingConfirmed, "其他"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mCountryEntriesMap.put("totalConfirmedEntries", totalConfirmedEntries);
        mCountryEntriesMap.put("totalDeathEntries", totalDeathEntries);
        mCountryEntriesMap.put("totalCuredEntries", totalCuredEntries);
        mCountryEntriesMap.put("existingConfirmedEntries", existingConfirmedEntries);
    }

    Map<String, ArrayList<BarEntry>> getCountryEntriesMap() {
        return mCountryEntriesMap;
    }
}
