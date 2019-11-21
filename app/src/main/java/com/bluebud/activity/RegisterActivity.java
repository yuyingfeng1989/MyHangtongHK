package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bluebud.activity.login.presenter.LoginPresenter;
import com.bluebud.activity.login.view.ILoginView;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.AdvertisingPage;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.SystemUtil;
import com.bluebud.utils.TextChangeUtils;
import com.bluebud.utils.ToastUtil;
import com.permission.RequestPermission;
import com.permission.RequestPermissionCallback;

import java.lang.ref.WeakReference;

public class RegisterActivity extends BaseActivity implements OnClickListener, CompoundButton.OnCheckedChangeListener, RequestPermissionCallback, ILoginView, ProgressDialogUtil.OnProgressDialogClickListener {//,OnProgressDialogClickListener
    private Button btnRegister;
    private EditText etUserName;
    private EditText etPwd;
    private CheckBox password_select;
    private RegisterActivity mContext;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        WeakReference<RegisterActivity> reference = new WeakReference(this);
        mContext = reference.get();
        loginPresenter = new LoginPresenter(mContext, this);
        init();
    }

    /**
     * 初始化控件
     */
    public void init() {
        btnRegister = (Button) findViewById(R.id.btn_register);//注册
        etUserName = (EditText) findViewById(R.id.et_user_name);//用户名
        etPwd = (EditText) findViewById(R.id.et_pwd);//密码
        password_select = (CheckBox) findViewById(R.id.password_select);//切换密码为明文还是暗文
        password_select.setOnCheckedChangeListener(this);
        btnRegister.setOnClickListener(this);
        findViewById(R.id.regist_backImage).setOnClickListener(this);//返回
        LinearLayout ll_user_agreement = (LinearLayout) findViewById(R.id.ll_user_agreement);
        findViewById(R.id.tv_privacy_policy).setOnClickListener(this);
        findViewById(R.id.tv_terms_service).setOnClickListener(this);
        ll_user_agreement.setOnClickListener(this);
        if ("en".equals(SystemUtil.getSystemLanguage())) {
            ll_user_agreement.setVisibility(View.GONE);
            findViewById(R.id.tv_en_declare).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_user_policy).setVisibility(View.VISIBLE);
        }
        TextChangeUtils tc = new TextChangeUtils();// 帐号和密码监听
        tc.addEditText(etUserName);
        tc.addEditText(etPwd);
        tc.setButton(btnRegister);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register://注册
                RequestPermission.create(mContext, mContext).checkPermissionsUtil();
                break;
            case R.id.ll_user_agreement://用户协议
                DialogUtil.showUserAgreement(mContext, R.string.register_tips2, false);
                break;
            case R.id.tv_privacy_policy://保密协议
                DialogUtil.showUserAgreement(mContext, R.string.register_policy, true);
                break;
            case R.id.tv_terms_service:
                DialogUtil.showUserAgreement(mContext, R.string.register_tips2, false);
                break;
            case R.id.regist_backImage:
                finish();
                break;
        }
    }

    /**
     * 明文暗文切换
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);//选择状态 显示明文--设置为可见的密码
        } else {
            etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);//默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
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
            }
        }
    }

    /**
     * 权限申请返回
     */
    @Override
    public void onPermissionSuccess() {
        String sUserName = etUserName.getText().toString();
        String pwd = etPwd.getText().toString();
        loginPresenter.getLonginServiceIP(0, sUserName, pwd);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btnRegister = null;
        etUserName = null;
        etPwd = null;
        password_select = null;
        mContext = null;
        loginPresenter = null;
    }
}
