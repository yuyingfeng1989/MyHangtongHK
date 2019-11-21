package com.bluebud.activity.login.model;

import com.loopj.android.http.RequestParams;

/**
 * Created by Administrator on 2019/7/11.
 */

public interface ILoginMethod {
    /**
     * 获取注册服务器地址接口
     *
     * */
    void getServiceIP(String url, RequestParams params, IBackListener iLoginListener);
    /**
     * 账号登陆接口
     *
     * */
    void goLogin(String url,RequestParams params,IBackListener iLoginListener);
    /**
     * facebook登录接口
     *
     * */
    void goFacebookLogin(String url,RequestParams params,IBackListener iLoginListener);
    /**
     * 注册账号接口
     *
     * */
    void goregister(String url,RequestParams params,IBackListener iLoginListener);
}
