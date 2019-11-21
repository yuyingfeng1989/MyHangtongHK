package com.bluebud.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.TextView;

import com.bluebud.activity.login.presenter.LoginPresenter;
import com.bluebud.activity.login.view.ILoginView;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.AdvertisingPage;
import com.bluebud.utils.Constants;
import com.bluebud.utils.FaceBookPlatform;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.facebook.AccessToken;
import com.permission.RequestPermission;
import com.permission.RequestPermissionCallback;

import java.lang.ref.WeakReference;

public class PageActivity extends BaseActivity implements FaceBookPlatform.FacebookListener, RequestPermissionCallback, ILoginView {
    private TextView tvVersion;
    private PageActivity mContext;
    private String scheme;
    private LoginPresenter loginPresenter;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 非第一次登陆，直接进登陆页
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        WeakReference<PageActivity> weakReference = new WeakReference(this);
        mContext = weakReference.get();
        getQrCode();//扫码启动的app

        tvVersion = (TextView) findViewById(R.id.tv_version);
        findViewById(R.id.image_advertising).setBackground(getResources().getDrawable(R.drawable.welcome_bg));//添加背景图
        String version = Utils.getVersionName(mContext);//当前版本
        String saveVersion = AppSP.getInstance().getVersion(mContext);
        if (!TextUtils.isEmpty(saveVersion) && !version.equals(saveVersion)) {//当版本发生变化清空sp中缓存数据
            UserUtil.saveServerUrl(mContext, null);
            getSharedPreferences("app_info", Context.MODE_PRIVATE).edit().clear().commit();
            getSharedPreferences("user_info3", Context.MODE_PRIVATE).edit().clear().commit();
        }
        tvVersion.setText("V" + version);
        loginPresenter = new LoginPresenter(mContext, this);
        RequestPermission.create(mContext, mContext).checkPageActivityPermissions();
    }

    /**
     * 权限检测回调
     */
    @Override
    public void onPermissionSuccess() {
        loginType();
    }

    /**
     * 自动登录那种类型
     */
    private void loginType() {
        String newtime = Utils.getCurTime(getApplicationContext());
        String oldtime = UserSP.getInstance().getLastManualLoginTime(getApplicationContext());
        long time = Utils.getDifferTime(oldtime, newtime) / 1000;
        String serverUrl = UserUtil.getServerUrl(mContext);
        int state = AppSP.getInstance().getLoginState();
        if (!TextUtils.isEmpty(serverUrl) && UserSP.getInstance().getAutologin(mContext) && time < Constants.AUTO_LOGIN_DIFFER_TIME) {//已经登录过或者登录时间没有过期
            if (state == 1) {//账号登录
                String sUserName = UserSP.getInstance().getUserName(mContext);//转小写
                String sPwd = UserSP.getInstance().getPWD(mContext);
                loginPresenter.getLonginServiceIP(1, sUserName.toLowerCase(), sPwd);
            } else if (state == 2) {//facebook登录
                new FaceBookPlatform(mContext).login(false);
            } else {//调转到登录界面
                new Handler().postDelayed(runnable, 2000);
            }
        } else {
            new Handler().postDelayed(runnable, 2000);// 2秒后关闭，并跳转到登陆
        }
    }

    /**
     * facebook登录回调
     */
    @Override
    public void facebookLoginSuccess(AccessToken token) {
        Constants.FACEBOOK_BIND_EMAIL = -1;
        loginPresenter.getLonginServiceIP(2, null, null);
    }

    @Override
    public void facebookLoginFail(String message) {
        ToastUtil.show(mContext, message);
        new Handler().postDelayed(runnable, 2000);// 2秒后关闭，并跳转到登陆
    }

    /**
     * 判断是否是扫码过来的操作
     */
    private void getQrCode() {
        Intent intentWeb = getIntent();
        scheme = intentWeb.getScheme();
        String dataString = intentWeb.getDataString();
        if (scheme != null && "htdzrun".equals(scheme) && !TextUtils.isEmpty(dataString) && dataString.contains("http")) {//香港版扫码启动调转到保险页
            Constants.isQrcode = true;
            Constants.qrcodeUrl = dataString.substring(10);
        } else
            Constants.isQrcode = false;
    }

    /**
     * 扫码获取地址跳转加载网页
     */
    private void intentLoadWebView(int deviceSize) {
        if (!Constants.isQrcode) {
            new AdvertisingPage(mContext, deviceSize).getAdvertisingPageInfo("10", 20);
        } else {
            if (!TextUtils.isEmpty(Constants.qrcodeUrl)) {
                Intent intent = new Intent(mContext, ShoppingActivity.class);
                intent.putExtra("url", Constants.qrcodeUrl);
                startActivity(intent);
                finish();
            } else {
                new AdvertisingPage(mContext, deviceSize).getAdvertisingPageInfo("10", 20);
            }
        }
    }

    @Override
    public void loginSuccess(int devices) {
        if (devices < 1)//没有绑定设备
            intentLoadWebView(0);
        else if (devices > 1)//大于两个设备则跳转到综合首页
            intentLoadWebView(2);
        else//只有一个设备
            intentLoadWebView(1);
    }

    @Override
    public void loginFail(String result) {
        ToastUtil.show(mContext, result);
        UserSP.getInstance().saveAutologin(mContext, false);
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tvVersion = null;
        mContext = null;
        scheme = null;
    }
}
