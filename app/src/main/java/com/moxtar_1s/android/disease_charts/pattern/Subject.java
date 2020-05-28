package com.moxtar_1s.android.disease_charts.pattern;

/**
 * @author MoXtar
 * 简介：被{@link Observer}观测的主体类接口。
 * 功能：用于管理{@link Observer}列表以及信息的获取或更新。
 * 注意：请参阅“观察者模式”的相关文献并参照相关例子来实现这个接口。
 * <p>
 * 如果想实现自己的Subject，按顺序做：
 * 1. 创建私有成员：Observer的列表，用于管理Observer。
 * 2. 实现{@link #addObserver(Observer)}与{@link #deleteObserver(Observer)}，即增删管理功能。
 *    {@link #addObserver(Observer)}中应当调用{@link Observer#initialize()}。
 * 3. 创建数据封装类或将数据作为成员变量，编写增删改以及获取方法来实现对其的更新、获取操作。
 *    在数据更新的方法中调用{@link #notifyAllObservers()}来通知所有的Observer数据已更新。
 * 4. 在{@link #notifyAllObservers()}中尽量使用无序遍历的方法来调用所有Observer的
 *    {@link Observer#update(Subject, Object)}方法。
 * 5. 出于安全等考虑，自行决定要不要实现{{@link #setChanged()}}方法。
 * 6. 在Fragment或者Activity中创建实例，将所有的Observer添加进去并更新数据。
 */
public interface Subject {
    /**
     * 简介：增加{@link Observer}进列表的方法。
     * @param oc 被增加的{@link Observer}对象
     */
    void addObserver(Observer oc);

    /**
     * 简介：删除{@link Observer}出列表的方法。
     * @param oc 被删除的{@link Observer}对象
     */
    void deleteObserver(Observer oc);

    /**
     * 简介：通知列表中所有{@link Observer}的方法。
     */
    void notifyAllObservers();

    /**
     * 简介：数据更新后设置标志的方法。
     */
    void setChanged();
}
