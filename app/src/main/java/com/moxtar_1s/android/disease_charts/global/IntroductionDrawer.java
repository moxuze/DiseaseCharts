package com.moxtar_1s.android.disease_charts.global;

import android.app.Activity;
import android.widget.TextView;

import com.moxtar_1s.android.disease_charts.R;
import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;

import java.util.Map;
import java.util.Objects;

class IntroductionDrawer implements Observer {
    private Activity mActivity;
    private Subject mSubject;
    private Map<String, String> mIntroductionMap;
    private Map<String, TextView> mTextViewMap;

    IntroductionDrawer(Map<String, TextView> textViewMap, Activity activity) {
        mActivity = activity;
        mTextViewMap = textViewMap;
    }

    @Override
    public void initialize() {}

    @Override
    public void update(Subject subject, Object data) {
        mSubject = subject;
        GlobalData globalData = (GlobalData) data;
        mIntroductionMap = globalData.getIntroductionMap();
        setData();
    }

    private void setData() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Objects.requireNonNull(mTextViewMap.get("tvIntroExistingConfirmed"))
                        .setText(String.format(mActivity.getString(R.string.intro_existing_confirmed),
                                mIntroductionMap.get("existingConfirmed")));
                Objects.requireNonNull(mTextViewMap.get("tvIntroConfirmed"))
                        .setText(String.format(mActivity.getString(R.string.intro_total_confirmed),
                                mIntroductionMap.get("confirmed")));
                Objects.requireNonNull(mTextViewMap.get("tvIntroDead"))
                        .setText(String.format(mActivity.getString(R.string.intro_total_dead),
                                mIntroductionMap.get("dead")));
                Objects.requireNonNull(mTextViewMap.get("tvIntroCured"))
                        .setText(String.format(mActivity.getString(R.string.intro_total_cured),
                                mIntroductionMap.get("cured")));
                Objects.requireNonNull(mTextViewMap.get("tvIntroDate"))
                        .setText(String.format(mActivity.getString(R.string.intro_date),
                                mIntroductionMap.get("date")));
            }
        });
    }

    void disableObserve() {
        if (mSubject != null) {
            mSubject.deleteObserver(this);
        }
    }
}
