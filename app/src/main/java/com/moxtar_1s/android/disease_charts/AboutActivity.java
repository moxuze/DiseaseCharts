package com.moxtar_1s.android.disease_charts;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    TextView tvAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("关于");

        String s = "作者：MoXtar" +
                "\n邮箱：moxtar-1s@outlook.com" +
                "\n仓库：https://github.com/MoXtar-1s/DiseaseCharts/\n" +
                "\n数据来源" +
                "\n国内、国际疫情：网易新闻APP" +
                "\nAPI：https://c.m.163.com/ug/api/wuhan/app/data/list-total" +
                "\n网易新闻：https://wp.m.163.com/163/page/news/virus_report/index.html\n" +
                "\n新闻、辟谣：2019新型冠状病毒疫情实时爬虫及API" +
                "\n作者：https://github.com/BlankerL" +
                "\nGitHub：https://github.com/BlankerL/DXY-COVID-19-Crawler" +
                "\nAPI：https://lab.isaaclin.cn/nCoV/\n" +
                "\n数据说明" +
                "\n1. 现存确诊=累计确诊-累计治愈-累计死亡。" +
                "\n2. “+”号开头的数据为较昨日的增长量。" +
                "\n3. 国内的增长量较为准确，而全球的只有部分国家能够提供，没有提供的数据按0计算。" +
                "\n4. 增长量累算到最后为“+0”时，有可能是数据没有更新或数据有缺损，显示“--”。" +
                "\n5. 每日中午12点左右为数据更新高峰，下午的数据往往更为准确。" +
                "\n6. 国内/国际分布概况图的“其他”部分为低于对应总数1.5%（考虑视觉效果）的数据的总和。" +
                "\n7. 国际详情柱状图的“其他”部分为低于对应总数0.5%（考虑精确度）的数据的总和。\n" +
                "\n注意！仅供学术交流使用，严禁用作商业用途。" +
                "\n数据来源皆为第三方，如果产生商业纠纷，本人概不负责。\n" +
                "\n使用OKHttp3以及MPAndroidChart制作" +
                "\n2020年5月28日";
        tvAbout = findViewById(R.id.tv_about);
        tvAbout.setText(s);
    }
}
