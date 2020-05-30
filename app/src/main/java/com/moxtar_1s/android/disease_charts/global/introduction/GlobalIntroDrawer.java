package com.moxtar_1s.android.disease_charts.global.introduction;

import com.moxtar_1s.android.disease_charts.global.GlobalData;
import com.moxtar_1s.android.disease_charts.pattern.Observer;
import com.moxtar_1s.android.disease_charts.pattern.Subject;

public class GlobalIntroDrawer implements Observer {
    private Subject mSubject;
    private GlobalIntroBean mGlobalIntroBean;
    private GlobalIntroPrinter mGlobalIntroPrinter;

    public GlobalIntroDrawer(GlobalIntroPrinter printer) {
        mGlobalIntroPrinter = printer;
    }

    @Override
    public void initialize() {}

    @Override
    public void update(Subject subject, Object data) {
        mSubject = subject;
        mGlobalIntroBean = ((GlobalData) data).getGlobalIntroBean();
        setData();
    }

    private void setData() {
        mGlobalIntroPrinter.printGlobalIntro(
                mGlobalIntroBean.getExistingConfirmed(),
                mGlobalIntroBean.getConfirmed(),
                mGlobalIntroBean.getDead(),
                mGlobalIntroBean.getCured(),
                mGlobalIntroBean.getDate()
        );
    }

    public void disableObserve() {
        if (mSubject != null) {
            mSubject.deleteObserver(this);
        }
    }
}
