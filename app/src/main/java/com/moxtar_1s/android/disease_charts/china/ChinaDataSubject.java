package com.moxtar_1s.android.disease_charts.china;

import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author MoXtar
 * @see Subject
 * 简介：被各个图表观测的实现了{@link Subject}的主体类。
 * 功能：用于管理{@link Observer}列表以及信息的获取或更新。
 * 注意：这个类需要申请联网权限，如果报错请检查AndroidManifest.xml的相关配置。
 */
class ChinaDataSubject implements Subject {
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final Request REQUEST = new Request.Builder()
            .url("https://c.m.163.com/ug/api/wuhan/app/data/list-total")
            .removeHeader("User-Agent")
            .addHeader("User-Agent", Objects.requireNonNull(System.getProperty("http.agent")))
            .build();
    private boolean isChanged;
    private ArrayList<Observer> mObserverList;
    protected ChinaData mChinaData;

    /**
     * 简介：构造方法。
     * 调用：{@link ChinaFragment#onCreateView}
     */
    ChinaDataSubject() {
        mObserverList = new ArrayList<>();
        isChanged = false;
    }

    /**
     * 简介：构造方法。
     * 调用：{@link ChinaFragment.RefreshRunnable#run}
     */
    void refreshData() throws IOException, JSONException {
        try (Response response = CLIENT.newCall(REQUEST).execute()) {
            mChinaData = new ChinaData(Objects.requireNonNull(response.body()).string());
            setChanged();
            notifyAllObservers();
        }
    }

    @Override
    public void addObserver(Observer oc) {
        mObserverList.add(oc);
        oc.initialize();
    }

    @Override
    public void deleteObserver(Observer oc) {
        int i = mObserverList.indexOf(oc);
        if (i >= 0) {
            mObserverList.remove(oc);
        }
    }

    @Override
    public void notifyAllObservers() {
        if (isChanged) {
            for (int i = 0; i < mObserverList.size(); i++) {
                mObserverList.get(i).update(this, mChinaData);
            }
            isChanged = false;
        }
    }

    @Override
    public void setChanged() {
        isChanged = true;
    }
}
