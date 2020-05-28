package com.moxtar_1s.android.disease_charts.pattern;

/**
 * @author MoXtar
 * 简介：观测{@link Subject}的观测者类接口。
 * 功能：用于将数据通过某种方法（图表等）表现出来，并能对其进行更新。
 * 注意：请参阅“观察者模式”的相关文献并参照相关例子来实现这个接口。
 * <p>
 * 如果想实现自己的Observer，按顺序做：
 * 1. 这个Observer应当被用于绘制某些控件（图表，TextView或ListView等），
 *    故其构造方法的参数以及成员变量应含有这些控件的引用。
 * 2. 实现{@link #initialize()}方法，完成控件的初始化，例如图表的样式设定等。
 * 3. 实现{@link #update(Subject, Object)}方法，在数据更新时重绘控件中的数据内容。
 * 4. 在Fragment或Activity中创建实例，用构造方法来绑定控件。
 */
public interface Observer {
    /**
     * 简介：初始化控件的方法。
     * 调用：{@link Subject#addObserver(Observer)}
     */
    void initialize();

    /**
     * 简介：更新控件中数据内容的方法。
     * 调用：{@link Subject#notifyAllObservers()}
     */
    void update(Subject subject, Object data);
}
