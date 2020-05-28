package com.moxtar_1s.android.disease_charts.news;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author MoXtar
 * 简介：{@link NewsDataSubject}的数据实体部分。
 * 功能：将JSON字符串拆分并封装成{@link NewsItem}的List（只记录数据，不设计外观），
 *       以便于{@link com.moxtar_1s.android.disease_charts.pattern.Observer}更新数据时调用。
 * 注意：这个类与服务器端发来的信息高度耦合。
 */
public class NewsData {
    // 封装了信息，被get方法返回的成员变量。
    protected final String mJSONString;
    protected final JSONObject mJSONObject;
    private ArrayList<NewsItem> mNewsItemList;
    // true:刷新的数据;false:添加的数据
    private boolean isRefreshedData;

    @SuppressLint("SimpleDateFormat")
    private final static SimpleDateFormat SDF = new SimpleDateFormat("yy-MM-dd HH:mm");

    NewsData(String jsonString) throws JSONException {
        mJSONString = jsonString;
        mJSONObject = new JSONObject(jsonString);
        mNewsItemList = new ArrayList<>();
        isRefreshedData = true;
        loadNewsItemList();
    }

    void setDataUpdateMode(boolean isRefreshedData) {
        this.isRefreshedData = isRefreshedData;
    }

    boolean isRefreshedData() {
        return isRefreshedData;
    }
    
    String getJSONString() {
        return mJSONString;
    }

    JSONObject getJSONObject() {
        return mJSONObject;
    }

    private void loadNewsItemList() {
        try {
            JSONArray resultArray = mJSONObject.getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject result = resultArray.getJSONObject(i);
                long pubDate = result.getLong("pubDate");
                String date = SDF.format(new Date(pubDate));
                String title = result.getString("title");
                String summary = result.getString("summary");
                String infoSource = result.getString("infoSource");
                String sourceUrl = result.getString("sourceUrl");
                mNewsItemList.add(new NewsItem(date, title, summary, infoSource, sourceUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ArrayList<NewsItem> getNewsItemList() {
        return mNewsItemList;
    }

    public static class NewsItem {
        public final String date;
        public final String title;
        public final String summary;
        public final String infoSource;
        public final String sourceUrl;

        NewsItem(String date, String title, String summary,
                 String infoSource, String sourceUrl) {
            this.date = date;
            this.title = title;
            this.summary = summary;
            this.infoSource = infoSource;
            this.sourceUrl = sourceUrl;
        }
    }
}
