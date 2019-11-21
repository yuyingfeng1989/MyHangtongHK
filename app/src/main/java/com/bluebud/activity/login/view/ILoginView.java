package com.bluebud.activity.login.view;

/**
 * Created by Administrator on 2019/7/10.
 */

public interface ILoginView {
    void loginSuccess(int devices);
    void loginFail(String result);
    void showLoading();
    void hideLoading();
}
