package com.moxtar_1s.android.disease_charts.global;

import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class GlobalDataSubject implements Subject {
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final Request REQUEST = new Request.Builder()
            .url("https://c.m.163.com/ug/api/wuhan/app/data/list-total")
            .removeHeader("User-Agent")
            .addHeader("User-Agent", Objects.requireNonNull(System.getProperty("http.agent")))
            .build();
    private boolean isChanged;
    private ArrayList<Observer> mObserverList;
    protected GlobalData mGlobalData;

    GlobalDataSubject() {
        mObserverList = new ArrayList<>();
        isChanged = false;
    }

    void refreshData() throws IOException, JSONException {
        try (Response response = CLIENT.newCall(REQUEST).execute()) {
            mGlobalData = new GlobalData(Objects.requireNonNull(response.body()).string());
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
                mObserverList.get(i).update(this, mGlobalData);
            }
            isChanged = false;
        }
    }

    @Override
    public void setChanged() {
        isChanged = true;
    }
}
