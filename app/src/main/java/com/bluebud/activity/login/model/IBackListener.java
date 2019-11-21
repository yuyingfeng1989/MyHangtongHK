package com.bluebud.activity.login.model;

/**
 * Created by Administrator on 2019/7/10.
 */

public interface IBackListener<T> {
    void successBack(T devices);
    void failedBack(String result);
}
