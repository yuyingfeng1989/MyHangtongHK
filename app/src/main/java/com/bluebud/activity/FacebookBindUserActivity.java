package com.bluebud.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bluebud.activity.login.presenter.LoginPresenter;
import com.bluebud.activity.login.view.ILoginView;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.AdvertisingPage;
import com.bluebud.utils.Constants;
import com.bluebud.utils.FaceBookPlatform;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.facebook.AccessToken;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/8/8.
 */

public class FacebookBindUserActivity extends BaseActivity implements View.OnClickListener, FaceBookPlatform.FacebookListener, ILoginView, ProgressDialogUtil.OnProgressDialogClickListener {

    private final int LOGIN_SUCCESS_UNBIND = 1;
    private final int LOGIN_SUCCESS_BIND = 2;
    private final int LOGIN_SUCCESS_BINDS = 3;
    private final int LOGIN_ERROR = -1;
    private FacebookBindUserActivity mContext;
    private EditText user_name;
    private EditText et_pwd;
    private FaceBookPlatform facebook;
    //    private LoginUtil loginUtil;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        WeakReference<FacebookBindUserActivity> reference = new WeakReference<>(this);
        mContext = reference.get();
        initView();
        loginPresenter = new LoginPresenter(mContext, this);
        facebook = new FaceBookPlatform((Activity) mContext);
//        loginUtil = new LoginUtil(mContext, mHandler, true);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        findViewById(R.id.regist_backImage).setVisibility(View.GONE);
        findViewById(R.id.text_title_regist).setVisibility(View.GONE);
        findViewById(R.id.password_select).setVisibility(View.GONE);
        findViewById(R.id.tv_user_agreement).setVisibility(View.GONE);
        findViewById(R.id.ll_fb_title).setVisibility(View.VISIBLE);
        user_name = (EditText) findViewById(R.id.et_user_name);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        Button btn_register = (Button) findViewById(R.id.btn_register);
        findViewById(R.id.ll_user_agreement).setOnClickListener(this);
        TextView text_regist_pro = (TextView) findViewById(R.id.text_regist_pro);
        btn_register.setText(getString(R.string.bind_facebook));
        text_regist_pro.setText(getString(R.string.skip_enter_directly));
        text_regist_pro.setTextColor(getResources().getColor(R.color.color_52bbd3));

        btn_register.setOnClickListener(this);
//        text_regist_pro.setOnClickListener(this);
//        findViewById(R.id.ll_user_policy).setOnClickListener(this);
//        findViewById(R.id.ll_user_agreement).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                bindUserLogin();
                break;
//            case R.id.ll_user_agreement://用户协议
//                DialogUtil.showUserAgreement(mContext, R.string.register_tips2,false);
//                break;
//            case R.id.ll_user_policy://保密协议
//                DialogUtil.showUserAgreement(mContext, R.string.register_policy,true);
//                break;
            case R.id.ll_user_agreement:
                facebook.login(false);
                break;
        }
    }

    /**
     * 绑定账号登录
     */
    private void bindUserLogin() {
        String sUserName = user_name.getText().toString();
        String sPassWord = et_pwd.getText().toString();
        if (TextUtils.isEmpty(sUserName) || TextUtils.isEmpty(sPassWord)) {
            ToastUtil.show(mContext, R.string.no_username_or_passwd);
            return;
        }

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
//            loginUtil.facebooLoginOrBind(accessToken, sUserName, sPassWord, 1, 6);
            Constants.FACEBOOK_BIND_EMAIL = 1;
            loginPresenter.getLonginServiceIP(2, sUserName, sPassWord);
        } else {
            facebook.login(false);
        }
    }

    /**
     * 登录结果回调处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ProgressDialogUtil.dismiss();
            LogUtil.e("msg=" + msg.what);
            switch (msg.what) {
                case LOGIN_SUCCESS_UNBIND://没有设备
                    LogUtil.i("enter bindActivity");
                    intentClass(0);
                    break;
                case LOGIN_SUCCESS_BIND://只有一个设备
                    intentClass(1);
                    break;
                case LOGIN_SUCCESS_BINDS://大于两个设备则跳转到综合首页
                    intentClass(2);
                    break;
                case LOGIN_ERROR://登录出错
                    break;
            }
        }
    };

    /**
     * 根据不同需要跳转界面
     */
    private void intentClass(int size) {
        if (!Constants.isQrcode) {
            new AdvertisingPage(mContext, size).getAdvertisingPageInfo("10", 20);
        } else {
            if (!TextUtils.isEmpty(Constants.qrcodeUrl)) {
                Intent intent = new Intent(mContext, ShoppingActivity.class);
                intent.putExtra("url", Constants.qrcodeUrl);
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * 第三方登录成功回调函数
     */
    @Override
    public void facebookLoginSuccess(AccessToken token) {
//        if (loginUtil == null)
//            loginUtil = new LoginUtil(mContext, mHandler, true);
//        loginUtil.facebooLoginOrBind(token, null, null, 0, 6);
        Constants.FACEBOOK_BIND_EMAIL = 0;
        loginPresenter.getLonginServiceIP(2, null, null);
    }

    @Override
    public void facebookLoginFail(String message) {
        ToastUtil.show(mContext, message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        mContext = null;
        user_name = null;
        et_pwd = null;
    }

    @Override
    public void loginSuccess(int devices) {
        if (devices < 1)//没有绑定设备
            intentClass(0);
        else if (devices > 1)//大于两个设备则跳转到综合首页
            intentClass(2);
        else//只有一个设备
            intentClass(1);
    }

    @Override
    public void loginFail(String result) {
        ToastUtil.show(mContext, result);
    }

    @Override
    public void showLoading() {
        ProgressDialogUtil.showNoCanceled(mContext, null, mContext);
    }

    @Override
    public void hideLoading() {
        ProgressDialogUtil.dismiss();
    }

    @Override
    public void onProgressDialogBack() {
    }
}
