package com.moxtar_1s.android.disease_charts.rumors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RumorsData {
    // 封装了信息，被get方法返回的成员变量。
    protected final String mJSONString;
    protected final JSONObject mJSONObject;
    private ArrayList<RumorsItem> mRumorsItemList;
    // true:刷新的数据;false:添加的数据
    private boolean isRefreshedData;

    RumorsData(String jsonString) throws JSONException {
        mJSONString = jsonString;
        mJSONObject = new JSONObject(jsonString);
        mRumorsItemList = new ArrayList<>();
        isRefreshedData = true;
        loadRumorsItemList();
    }

    void setDataUpdateMode(boolean isRefreshedData) {
        this.isRefreshedData = isRefreshedData;
    }

    boolean isRefreshedData() {
        return isRefreshedData;
    }

    public String getJSONString() {
        return mJSONString;
    }

    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    private void loadRumorsItemList() {
        try {
            JSONArray resultArray = mJSONObject.getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject result = resultArray.getJSONObject(i);
                String title = result.getString("title");
                String mainSummary = result.getString("mainSummary");
                String body = result.getString("body");
                mRumorsItemList.add(new RumorsItem(title, mainSummary, body));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ArrayList<RumorsItem> getRumorsItemList() {
        return mRumorsItemList;
    }

    public static class RumorsItem {
        public final String title;
        public final String mainSummary;
        public final String body;

        RumorsItem(String title, String mainSummary, String body) {
            this.title = title;
            this.mainSummary = mainSummary;
            this.body = body;
        }
    }
}
