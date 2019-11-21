package com.bluebud.activity.login.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.bluebud.activity.login.model.IBackListener;
import com.bluebud.activity.login.model.LoginModel;
import com.bluebud.activity.login.view.ILoginView;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ServerConnInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.service.IMLiteGuardianService;
import com.bluebud.utils.Constants;
import com.bluebud.utils.RegularUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.facebook.AccessToken;
import com.loopj.android.http.RequestParams;

/**
 * Created by Administrator on 2019/7/10.
 */

public class LoginPresenter {
    private ILoginView mView;
    private LoginModel mModel;
    private Context mContext;
    private String requestUrl;
    private ServerConnInfo serverConnInfo;//获取注册服务器返回对象

    public LoginPresenter(Context mContext, ILoginView loginView) {
        this.mContext = mContext;
        this.mView = loginView;
        this.mModel = new LoginModel(mContext);
    }

    /**
     * 获取登录服务器ip及端口号
     * index 0注册、1账号登录、2facebook登录
     */
    public void getLonginServiceIP(final int index, final String userName, final String password) {
        if (HttpParams.ISDEBUG) {//调试模式
            virtualAdress();
        }
        requestUrl = UserUtil.getServerUrl(mContext);
        mView.showLoading();
        if (!TextUtils.isEmpty(requestUrl) && index != 0) {//有缓存ip端口号直接登录
            performOperations(index, userName, password);
            return;
        }
        String url = HttpParams.SERVER_URL_CENTER_HK;
        RequestParams params;
        if (index == 0 || index == 2) {//注册或facebook登录
            String sCurVersion = Utils.getVersionName(mContext);
            params = HttpParams.getServerConnInfo(sCurVersion);
            Constants.ISSERVICEIP_USERNAME = false;//获取注册列表查询
        } else {
            params = HttpParams.getServerConnInfoByUser(userName);//通过账号查询
            Constants.ISSERVICEIP_USERNAME = true;
        }
        mModel.getServiceIP(url, params, new IBackListener() {
            @Override
            public void successBack(Object info) {
                serverConnInfo = (ServerConnInfo) info;
                requestUrl = Utils.getUrl(serverConnInfo.conn_name, serverConnInfo.conn_port);
                UserUtil.saveServerUrl(mContext, requestUrl);//注册服务器地址
                performOperations(index, userName, password);
            }

            @Override
            public void failedBack(String error) {
                mView.loginFail(error);
                mView.hideLoading();
            }
        });
    }

    /**
     * 执行对应的操作
     */
    private void performOperations(int index, String userName, String password) {
        switch (index) {
            case 0:
                if (!registConditional(userName, password))/*校验账号密码符合要求*/
                    goRegister(userName, password);
                break;
            case 1:
                if (!loginConditional(userName, password))
                    goLogin(userName, password);
                break;
            case 2:
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                goFacebookLogin(accessToken, userName, password);
                break;
        }
    }

    /**
     * 注册新用户
     */
    private void goRegister(final String sUserName, final String sPwd) {
        RequestParams params = HttpParams.userRegister(sUserName, sPwd, serverConnInfo.connid, Utils.getTimeZone(mContext));
        mModel.goregister(requestUrl, params, new IBackListener() {
            @Override
            public void successBack(Object devices) {
                UserUtil.saveServerUrl(mContext, requestUrl);//注册服务器地址
                AppSP.getInstance().saveRegisterAddressDNS(sUserName, serverConnInfo.conn_dns);
                goLogin(sUserName, sPwd);
            }

            @Override
            public void failedBack(String result) {
                mView.hideLoading();
                mView.loginFail(result);
            }
        });
    }


    /**
     * 账号登陆
     */
    private void goLogin(String sUserName, final String sPwd) {
        mView.showLoading();
        UserSP.getInstance().saveAutologin(mContext, false);/*更新登录状态*/
        RequestParams params = HttpParams.userLoginCN(sUserName, sPwd);
        mModel.goLogin(requestUrl, params, new IBackListener() {
            @Override
            public void successBack(Object devices) {
                UserSP.getInstance().savePWD(mContext, sPwd);//保存登录密码
                mView.hideLoading();
                mView.loginSuccess((int) devices);
                startIMService();
            }

            @Override
            public void failedBack(final String result) {
                mView.hideLoading();
                mView.loginFail(result);
            }
        });
    }

    /**
     * facebook登录
     */
    private void goFacebookLogin(AccessToken token, String sUserName, String password) {
        UserSP.getInstance().saveAutologin(mContext, false);/*更新登录状态*/
        RequestParams params;
        if (Constants.FACEBOOK_BIND_EMAIL < 0) {//facebook直接登录平台
            params = HttpParams.facebookLogin(token.getToken(), token.getUserId());
        } else {//绑定界面来的
            params = HttpParams.facebookBindUser(token.getToken(), sUserName, password, Constants.FACEBOOK_BIND_EMAIL, token.getUserId(), Utils.getTimeZone(mContext), 6);
        }
        mModel.goFacebookLogin(requestUrl, params, new IBackListener() {
            @Override
            public void successBack(Object devices) {
                mView.hideLoading();
                mView.loginSuccess((int) devices);
                startIMService();
            }

            @Override
            public void failedBack(String result) {
                mView.hideLoading();
                mView.loginFail(result);
            }
        });

    }

    /**
     * 注册校验账号密码是否正确
     *
     * @param sUserName
     * @param sPassWord
     */
    private boolean registConditional(String sUserName, String sPassWord) {
        if (null == serverConnInfo) {
            mView.loginFail(mContext.getApplicationContext().getString(R.string.net_exception));
            mView.hideLoading();
            return true;
        }
        if (TextUtils.isEmpty(sUserName) || TextUtils.isEmpty(sPassWord)) {
            mView.loginFail(mContext.getApplicationContext().getString(R.string.no_username_or_passwd));
            mView.hideLoading();
            return true;
        }

        if (!Utils.isCorrectEmail(sUserName)) {//是不是邮箱格式
            mView.loginFail(mContext.getApplicationContext().getString(R.string.email_error));
            mView.hideLoading();
            return true;
        }
        if (!Utils.isCorrectUserName(sUserName)) {//6-80个字符以外
            mView.loginFail(mContext.getApplicationContext().getString(R.string.email_error));
            mView.hideLoading();
            return true;
        }
        if (!RegularUtil.limitInputLength(sPassWord)) {//判读密码输入长度范围
            mView.loginFail(mContext.getApplicationContext().getString(R.string.passwd_error));
            mView.hideLoading();
            return true;
        }
        if (RegularUtil.limitCN(sPassWord)) {//判读是否包行中文
            mView.loginFail(mContext.getApplicationContext().getString(R.string.register_limit));
            mView.hideLoading();
            return true;
        }
        if (!RegularUtil.limitInput(sPassWord)) {//判断密码输入类型
            mView.loginFail(mContext.getApplicationContext().getString(R.string.register_limit));
            mView.hideLoading();
            return true;
        }
        return false;
    }

    /**
     * 登录校验账号密码
     *
     * @param sUserName
     * @param sPwd
     */
    private boolean loginConditional(String sUserName, String sPwd) {
        if (TextUtils.isEmpty(sUserName) || TextUtils.isEmpty(sPwd)) {/*验证账号密码是否为空*/
            mView.loginFail(mContext.getApplicationContext().getString(R.string.no_username_or_passwd));
            mView.hideLoading();
            return true;
        }
        if (!Utils.isCorrectEmail(sUserName)) {/*验证账号是否是邮箱格式*/
            mView.loginFail(mContext.getApplicationContext().getString(R.string.email_error));
            mView.hideLoading();
            return true;
        }
        return false;
    }


    /**
     * 平台开发人员测试地址
     */
    private void virtualAdress() {
        String url = Utils.getUrl("172.18.11.100", 10230);//钱坤
//        String url = Utils.getUrl("172.18.11.150", 10230);
//		String url = Utils.getUrl("172.18.11.170", 8090);//王娟
        UserUtil.saveServerUrl(mContext, url);//注册服务器地址
    }

    /**
     * 启动客服消息服务
     */
    private void startIMService() {
//        Android4.3 - Android7.0，隐藏Notification上的图标
        Intent serviceIM = new Intent(mContext, IMLiteGuardianService.class);
        mContext.startService(serviceIM);
    }

}
