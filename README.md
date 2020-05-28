# 疫情图表

这是一款能够显示部分新冠疫情信息的~~辣鸡~~简单安卓APP

## 项目简介

* APP具有四个标签：国内疫情、国际疫情、新闻、辟谣。

* 国内疫情具有四个模块：中国的每日疫情累计、增长概况，  
省市分布饼图（现存/累计确诊），具体省市分布柱状图，历史轨迹图。

* 国际疫情具有三个模块：全球的每日疫情累计、增长概况，  
全球分布饼图（现存/累计确诊），具体国家分布柱状图。

* 新闻：有关疫情的新闻速报，链接可点击进入原网页。

* 辟谣：有关疫情的谣言及其辟谣信息。

## 数据来源

* 国内疫情、国际疫情：网易新闻APP  
[网易新闻APP](https://wp.m.163.com/163/page/news/virus_report/index.html)
提供了JSON格式的[API](https://c.m.163.com/ug/api/wuhan/app/data/list-total)，点击左边的链接可查看JSON中信息的具体格式。

* 新闻、辟谣：2019新型冠状病毒疫情实时爬虫及API  
[2019新型冠状病毒疫情实时爬虫](https://github.com/BlankerL/DXY-COVID-19-Crawler)是[BlankerL](https://github.com/BlankerL)提供的抓取丁香园数据的项目，他提供了JSON格式的[API](https://lab.isaaclin.cn/nCoV/)。如果你对他的项目感兴趣，请到他的项目中浏览相关内容并支持他。

## 运行截图

![screenshot_1](https://raw.githubusercontent.com/MoXtar-1s/DiseaseCharts/master/screenshots/screenshot_1.png)
![screenshot_2](https://raw.githubusercontent.com/MoXtar-1s/DiseaseCharts/master/screenshots/screenshot_2.png)
![screenshot_3](https://raw.githubusercontent.com/MoXtar-1s/DiseaseCharts/master/screenshots/screenshot_3.png)
![screenshot_4](https://raw.githubusercontent.com/MoXtar-1s/DiseaseCharts/master/screenshots/screenshot_4.png)
![screenshot_5](https://raw.githubusercontent.com/MoXtar-1s/DiseaseCharts/master/screenshots/screenshot_5.png)

## 很多废话

* 在家上数学建模的网课，老师要求做一份关于预测疫情的数学建模作业。为了做出可视化的数据，我便找了相关的API生成Excel表格，后来便萌生了做成安卓APP的念头，用来与大家分享和交流。为了做出这个APP，我宕了[MPChart](https://github.com/PhilJay/MPAndroidChart)的源码研究了好长时间，毕竟没有什么开发安卓APP的经验XD。

* 很久之前为了研究洞窟物语进化版的源码我注册了GitHub，这个项目是我注册这么久以来提交第一个作品（虽然做得很辣鸡）。项目中用到了观察者模式，详情请参考部分源码的注释（虽然只写了关键部分的思路，但是写得还挺认真的，希望你能看懂）。我目前是个没什么开发经验的学生，代码一定存在某些设计不合理的缺陷，还恳请多多指教。如果有什么疑惑、意见或者建议，请敲我的邮箱，我会尽量在第一时间回复你的。

* 你也看到了吧~~那个新闻和疫情的页面做得超级简陋，item的布局只是几个拼在一起的TextView而已XD，因为我不大会布局设计啦，想做成TimeLine样式的却不大会。以后可能会更新的，当然你要是设计熟手，恳请你帮我改改啦。

* 感谢网易新闻、丁香园和BlankerL提供的数据，请不要商业化（我的作品肯定是没什么商业化价值了，请遵守数据来源的规则）！这个作品仅供学习交流，也算是我对这次新冠疫情爆发事件作出的一点贡献了。如果能帮到你一点点，是我的荣幸。

* 要戴口罩！勤洗手！少外出！不能松懈！祝愿一切都好。

* If you need English edition, e-mail me. I can update my project if I had time.
