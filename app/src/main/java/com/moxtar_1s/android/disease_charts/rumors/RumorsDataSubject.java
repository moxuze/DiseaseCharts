package com.moxtar_1s.android.disease_charts.rumors;

import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RumorsDataSubject implements Subject {
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private int page;
    private boolean isChanged;
    private ArrayList<Observer> mObserverList;
    protected RumorsData mRumorsData;

    RumorsDataSubject() {
        mObserverList = new ArrayList<>();
        isChanged = false;
        page = 2;
    }

    void refreshData() throws IOException, JSONException {
        String url = "https://lab.isaaclin.cn/nCoV/api/rumors?page=1&num=10&lang=zh";
        Request request = new Request.Builder().url(url).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            mRumorsData = new RumorsData(Objects.requireNonNull(response.body()).string());
            mRumorsData.setDataUpdateMode(true);
            setChanged();
            notifyAllObservers();
            page = 2;
        }
    }

    void loadMoreData() throws IOException, JSONException {
        String url = "https://lab.isaaclin.cn/nCoV/api/rumors?page=" + page + "&num=10&lang=zh";
        Request request = new Request.Builder().url(url).build();
        try (Response response = CLIENT.newCall(request).execute()) {
            mRumorsData = new RumorsData(Objects.requireNonNull(response.body()).string());
            mRumorsData.setDataUpdateMode(false);
            setChanged();
            notifyAllObservers();
            page++;
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
                mObserverList.get(i).update(this, mRumorsData);
            }
            isChanged = false;
        }
    }

    @Override
    public void setChanged() {
        isChanged = true;
    }
}


