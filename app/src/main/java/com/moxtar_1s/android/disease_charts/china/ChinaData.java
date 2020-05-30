package com.moxtar_1s.android.disease_charts.china;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.moxtar_1s.android.disease_charts.china.distribution.ChinaDistributionBean;
import com.moxtar_1s.android.disease_charts.china.distribution.ChinaDistributionDrawer;
import com.moxtar_1s.android.disease_charts.china.introduction.ChinaIntroBean;
import com.moxtar_1s.android.disease_charts.china.introduction.ChinaIntroDrawer;
import com.moxtar_1s.android.disease_charts.china.province.ChinaProvinceBean;
import com.moxtar_1s.android.disease_charts.china.province.ChinaProvinceDrawer;
import com.moxtar_1s.android.disease_charts.china.trend.ChinaTrendBean;
import com.moxtar_1s.android.disease_charts.china.trend.ChinaTrendDrawer;
import com.moxtar_1s.android.disease_charts.utils.SortUtil;
import com.moxtar_1s.android.disease_charts.utils.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
public class ChinaData {
    // 封装了信息，被get方法返回的成员变量。
    protected final String mJSONString;
    protected final JSONObject mJSONObject;
    private JSONArray mProvinces;
    private ChinaIntroBean mChinaIntroBean;
    private ChinaDistributionBean mChinaDistributionBean;
    private ChinaProvinceBean mChinaProvinceBean;
    private ChinaTrendBean mChinaTrendBean;
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
    public ChinaData(String jsonString) throws JSONException {
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
        loadChinaIntroBean();
        loadChinaDistributionBean();
        loadChinaProvinceBean();
        loadChinaTrendBean();
    }

    /**
     * 简介：{@link #mJSONString}的get方法。
     * @return 返回成员变量mJSONString
     */
    public String getJSONString() {
        return mJSONString;
    }

    /**
     * 简介：{@link #mJSONObject}的get方法。
     * @return 返回成员变量mJSONObject
     */
    public JSONObject getJSONObject() {
        return mJSONObject;
    }

    /**
     * 简介：{@link #mChinaIntroBean}的load方法。
     * 调用：{@link #ChinaData}
     */
    private void loadChinaIntroBean() {
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
            mChinaIntroBean = new ChinaIntroBean();
            mChinaIntroBean.setExistingConfirmed(format(existingConfirmed, increaseExistingConfirmed));
            mChinaIntroBean.setNoSymptom(format(noSymptom, increaseNoSymptom));
            mChinaIntroBean.setInput(format(input, increaseInput));
            mChinaIntroBean.setConfirmed(format(totalConfirmed, increaseConfirmed));
            mChinaIntroBean.setDead(format(totalDead, increaseDead));
            mChinaIntroBean.setCured(format(totalCured, increaseCured));
            mChinaIntroBean.setDate(data.getString("lastUpdateTime"));
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
     * 简介：{@link #mChinaIntroBean}的get方法。
     * 调用：{@link ChinaIntroDrawer#update}
     * @return 返回成员变量mChinaIntroBean
     */
    public ChinaIntroBean getChinaIntroBean() {
        return mChinaIntroBean;
    }

    /**
     * 简介：{@link #mChinaDistributionBean}的load方法。
     * 调用：{@link #ChinaData}
     */
    private void loadChinaDistributionBean() {
        mChinaDistributionBean = new ChinaDistributionBean();
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
            SortUtil.sortPieEntries(totalConfirmedEntries);
            SortUtil.sortPieEntries(existingConfirmedEntries);
            if (otherTotalConfirmed > 0) {
                totalConfirmedEntries.add(new PieEntry(otherTotalConfirmed, "其他"));
            }
            if (otherExistingConfirmed > 0) {
                existingConfirmedEntries.add(new PieEntry(otherExistingConfirmed, "其他"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mChinaDistributionBean.setTotalConfirmedEntries(totalConfirmedEntries);
        mChinaDistributionBean.setExistingConfirmedEntries(existingConfirmedEntries);
    }

    /**
     * 简介：{@link #mChinaDistributionBean}的get方法。
     * 调用：{@link ChinaDistributionDrawer#update}
     * @return 返回成员变量mChinaDistributionBean
     */
    public ChinaDistributionBean getChinaDistributionBean() {
        return mChinaDistributionBean;
    }

    /**
     * 简介：{@link #mChinaProvinceBean}的load方法。
     * 调用：{@link #ChinaData}
     */
    private void loadChinaProvinceBean() {
        mChinaProvinceBean = new ChinaProvinceBean();
        ArrayList<BarEntry> totalConfirmedEntries = new ArrayList<>();
        ArrayList<BarEntry> totalDeadEntries = new ArrayList<>();
        ArrayList<BarEntry> totalCuredEntries = new ArrayList<>();
        ArrayList<BarEntry> existingConfirmedEntries = new ArrayList<>();
        try {
            for (int i = 0; i < mProvinces.length(); i++) {
                JSONObject province = mProvinces.getJSONObject(i);
                JSONObject provinceTotal = province.getJSONObject("total");
                int totalConfirmed = provinceTotal.getInt("confirm");
                int totalDead = provinceTotal.getInt("dead");
                int totalCured = provinceTotal.getInt("heal");
                int existingConfirmed = totalConfirmed - totalCured - totalDead;
                String provinceName = province.getString("name");
                totalConfirmedEntries.add(new BarEntry(i, totalConfirmed, provinceName));
                totalDeadEntries.add(new BarEntry(i, totalDead, provinceName));
                totalCuredEntries.add(new BarEntry(i, totalCured, provinceName));
                existingConfirmedEntries.add(new BarEntry(i, existingConfirmed, provinceName));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SortUtil.sortBarEntries(totalConfirmedEntries);
        SortUtil.sortBarEntries(totalDeadEntries);
        SortUtil.sortBarEntries(totalCuredEntries);
        SortUtil.sortBarEntries(existingConfirmedEntries);
        mChinaProvinceBean.setTotalConfirmedEntries(totalConfirmedEntries);
        mChinaProvinceBean.setTotalDeadEntries(totalDeadEntries);
        mChinaProvinceBean.setTotalCuredEntries(totalCuredEntries);
        mChinaProvinceBean.setExistingConfirmedEntries(existingConfirmedEntries);
    }

    /**
     * 简介：{@link #mChinaProvinceBean}的get方法。
     * 调用：{@link ChinaProvinceDrawer#update}
     * @return 返回成员变量mChinaProvinceBean
     */
    public ChinaProvinceBean getChinaProvinceBean() {
        return mChinaProvinceBean;
    }

    /**
     * 简介：{@link #mChinaTrendBean}的load方法。
     * 调用：{@link #ChinaData}
     */
    private void loadChinaTrendBean() {
        mChinaTrendBean = new ChinaTrendBean();
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
                float date = DateUtil.dateToFloat(chinaDay.getString("date"));
                confirmedEntries.add(new Entry(date, chinaDayTotal.getInt("confirm")));
                deadEntries.add(new Entry(date, chinaDayTotal.getInt("dead")));
                curedEntries.add(new Entry(date, chinaDayTotal.getInt("heal")));
                suspectedEntries.add(new Entry(date, chinaDayTotal.getInt("suspect")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mChinaTrendBean.setConfirmedEntries(confirmedEntries);
        mChinaTrendBean.setDeadEntries(deadEntries);
        mChinaTrendBean.setCuredEntries(curedEntries);
        mChinaTrendBean.setSuspectedEntries(suspectedEntries);
    }

    /**
     * 简介：{@link #mChinaTrendBean}的get方法。
     * 调用：{@link ChinaTrendDrawer#update}
     * @return 返回成员变量mChinaTrendBean
     */
    public ChinaTrendBean getChinaTrendBean(){
        return mChinaTrendBean;
    }
}
