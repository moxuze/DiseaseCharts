package com.moxtar_1s.android.disease_charts.china.introduction;

import com.moxtar_1s.android.disease_charts.china.ChinaData;
import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;


public class ChinaIntroDrawer implements Observer {
    private Subject mSubject;
    private ChinaIntroBean mChinaIntroBean;
    private ChinaIntroPrinter mChinaIntroPrinter;

    public ChinaIntroDrawer(ChinaIntroPrinter printer) {
        mChinaIntroPrinter = printer;
    }

    @Override
    public void initialize() {}

    @Override
    public void update(Subject subject, Object data) {
        mSubject = subject;
        mChinaIntroBean = ((ChinaData) data).getChinaIntroBean();
        setData();
    }

    private void setData() {
        mChinaIntroPrinter.printChinaIntro(
                mChinaIntroBean.getExistingConfirmed(),
                mChinaIntroBean.getNoSymptom(),
                mChinaIntroBean.getInput(),
                mChinaIntroBean.getConfirmed(),
                mChinaIntroBean.getDead(),
                mChinaIntroBean.getCured(),
                mChinaIntroBean.getDate());
    }

    public void disableObserve() {
        if (mSubject != null) {
            mSubject.deleteObserver(this);
        }
    }
}
