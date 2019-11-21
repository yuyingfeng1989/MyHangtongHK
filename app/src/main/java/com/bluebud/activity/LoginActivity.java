package com.bluebud.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bluebud.activity.login.presenter.LoginPresenter;
import com.bluebud.activity.login.view.ILoginView;
import com.bluebud.activity.settings.MailBoxForgetPassWdActivity;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.AdvertisingPage;
import com.bluebud.utils.Constants;
import com.bluebud.utils.FaceBookPlatform;
import com.bluebud.utils.PopupWindowUtils;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.SystemUtil;
import com.bluebud.utils.TextChangeUtils;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.view.ClearEditText;
import com.facebook.AccessToken;
import com.permission.RequestPermission;
import com.permission.RequestPermissionCallback;
import com.permission.ShowDialogActivity;

import java.lang.ref.WeakReference;

public class LoginActivity extends BaseActivity implements OnClickListener, FaceBookPlatform.FacebookListener, RequestPermissionCallback, ILoginView, ProgressDialogUtil.OnProgressDialogClickListener {//OnProgressDialogClickListener
    private ClearEditText etUserName, etPwd;
    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvFindPasswd;
    private LoginActivity mContext;
    private FaceBookPlatform faceBookPlatform;
    private boolean isFacebookLogin;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_login);
        WeakReference<LoginActivity> weakReference = new WeakReference(this);
        mContext = weakReference.get();
        UserUtil.saveServerUrl(mContext, null);/*每次进入登录界面都清空缓存ip端口号*/
        faceBookPlatform = new FaceBookPlatform(mContext);
        initView();
        loginPresenter = new LoginPresenter(mContext, this);
    }

    /**
     * @Description: 初始化
     */
    public void initView() {
        super.setBaseTitleGone();
        etUserName = (ClearEditText) findViewById(R.id.et_username);
        etPwd = (ClearEditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        tvFindPasswd = (TextView) findViewById(R.id.tv_find_passwd);
        Button btn_facebook = (Button) findViewById(R.id.btn_facebook);

        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvFindPasswd.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);
        if ("zh".equals(SystemUtil.getSystemLanguage())) {
            btn_facebook.setText("Facebook" + getString(R.string.login));
        } else {
            btn_facebook.setText("Facebook");
        }
        String sUserName = UserSP.getInstance().getUserName(mContext);//用户名
        String sPwd = UserSP.getInstance().getPWD(mContext);//密码
        new PopupWindowUtils(mContext);
        TextChangeUtils tc = new TextChangeUtils();// 帐号和密码监听
        tc.addEditText(etUserName);
        tc.addEditText(etPwd);
        tc.setButton(btnLogin);
        btnLogin.requestFocus();
        int loginState = AppSP.getInstance().getLoginState();
        if (loginState == 2) {
            AppSP.getInstance().saveLoginState(0);
            return;
        }
        if (!TextUtils.isEmpty(sUserName) && loginState != 2)
            etUserName.setText(sUserName);
        if (!TextUtils.isEmpty(sPwd)) etPwd.setText(sPwd);
    }


    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(mContext, ShowDialogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                return;
            }
        }
        switch (v.getId()) {
            case R.id.btn_login:
                isFacebookLogin = false;
                RequestPermission.create(mContext, mContext).checkPermissionsUtil();
                break;
            case R.id.btn_facebook:
                isFacebookLogin = true;
                RequestPermission.create(mContext, mContext).checkPermissionsUtil();
                break;
            case R.id.tv_register://免费注册
                startActivityForResult(new Intent(mContext, RegisterActivity.class), 1);
                break;
            case R.id.tv_find_passwd:// 找回密码
                startActivity(new Intent(mContext, MailBoxForgetPassWdActivity.class));
                break;
        }
    }

    /**
     * 申请权限结果返回
     */
    @Override
    public void onPermissionSuccess() {
        if (!isFacebookLogin) {/*账号登录*/
            loginPresenter.getLonginServiceIP(1, etUserName.getText().toString().trim().toLowerCase(), etPwd.getText().toString().trim());
        } else {/*facebook登录*/
            faceBookPlatform.login(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            String sUserName = data.getStringExtra("USER_NAME");
            String sPwd = data.getStringExtra("PASSWORD");
            etUserName.setText(sUserName);
            etPwd.setText(sPwd);
        } else if (faceBookPlatform.getIssFaceBook() && resultCode == RESULT_OK) {
            faceBookPlatform.getCallbackManager().onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


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
            } else {
                new AdvertisingPage(mContext, size).getAdvertisingPageInfo("10", 20);
            }
        }
    }

    /**
     * 第三方登录成功回调函数
     */
    @Override
    public void facebookLoginSuccess(AccessToken token) {
        Constants.FACEBOOK_BIND_EMAIL = -1;
        loginPresenter.getLonginServiceIP(2, null, null);
    }

    @Override
    public void facebookLoginFail(String message) {
        ToastUtil.show(mContext, message);
    }

    @Override
    protected void onNewIntent(Intent intent) {//清空缓存ip
        super.onNewIntent(intent);
        UserUtil.saveServerUrl(mContext, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        etUserName = null;
        etPwd = null;
        btnLogin = null;
        tvRegister = null;
        tvFindPasswd = null;
        mContext = null;
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
        if (ProgressDialogUtil.isShow())
            ProgressDialogUtil.dismiss();
    }


    @Override
    public void onProgressDialogBack() {
    }
}
