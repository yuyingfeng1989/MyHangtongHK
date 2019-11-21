package com.bluebud.activity.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.LoginActivity;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.ServerConnInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.TextChangeUtils;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.ClearEditText;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class ForgetPassWdActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener {
    private String strAccount = "";
    private String sUrl = "";
    private RequestHandle requestHandle;
    private ClearEditText etPasswd;
    private ClearEditText etConfirePasswd;
    private ImageView ivShowPasswd;
    private boolean bIsShowPasswd = false;
    private String passwd;
    private String confirePasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_forget_passwd);
        strAccount = getIntent().getStringExtra("ACCOUNT");
        init();
    }

    public void init() {
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleText(R.string.reset_assword);
        getBaseTitleLeftBack().setOnClickListener(this);

        etPasswd = (ClearEditText) findViewById(R.id.et_passwd);// 请输入密码
        etConfirePasswd = (ClearEditText) findViewById(R.id.et_confire_passwd);// 请再次输入密码
        ivShowPasswd = (ImageView) findViewById(R.id.iv_show_passwd);// 密码明暗文切换
        Button btn_confirm = ((Button) findViewById(R.id.btn_confirm));// 确认
        btn_confirm.setOnClickListener(this);
        ivShowPasswd.setOnClickListener(this);

        TextChangeUtils tc = new TextChangeUtils();
        tc.addEditText(etPasswd);
        tc.addEditText(etConfirePasswd);
        tc.setButton(btn_confirm);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.btn_confirm:// 完成
                submit();
                break;
            case R.id.iv_show_passwd:
                changePasswdShowMode();
                break;

        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: changePasswdShowMode
     * @Description: 改变密码明文或者密文的显示模式
     */
    @SuppressLint("NewApi")
    private void changePasswdShowMode() {
        // TODO Auto-generated method stub
        // 如果密码显示为※，则明文显示
        if (!bIsShowPasswd) {
            bIsShowPasswd = true;
            // ivShowPasswd.setBackground(getResources().getDrawable(
            // R.drawable.btn_show_passwd));

            ivShowPasswd.setImageResource(R.drawable.btn_show_passwd);
            etPasswd.setTransformationMethod(HideReturnsTransformationMethod
                    .getInstance());
            etConfirePasswd
                    .setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
        } else {
            // 如果密码明文显示，则变为显示※
            bIsShowPasswd = false;
            // ivShowPasswd.setBackground(getResources().getDrawable(R.drawable.btn_hide_passwd));
            ivShowPasswd.setImageResource(R.drawable.btn_hide_passwd);
            etPasswd.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
            etConfirePasswd
                    .setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
        }

        // 切换密码明文或者密文显示模式后，光标置后
        if (etPasswd.hasFocus()) {
            CharSequence charSequence = etPasswd.getText();
            if (charSequence instanceof Spannable) {
                Spannable spanText = (Spannable) charSequence;
                Selection.setSelection(spanText, charSequence.length());
            }
        }
        if (etConfirePasswd.hasFocus()) {
            CharSequence charSequence = etConfirePasswd.getText();
            if (charSequence instanceof Spannable) {
                Spannable spanText = (Spannable) charSequence;
                Selection.setSelection(spanText, charSequence.length());
            }
        }
    }

    @Override
    public void onProgressDialogBack() {
        // TODO Auto-generated method stub
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    private void submit() {
        passwd = etPasswd.getText().toString().trim();
        confirePasswd = etConfirePasswd.getText().toString().trim();
        if (passwd == null || passwd.equals("")) {// 密码不能为空
            ToastUtil.show(this, R.string.no_passwd);
            return;
        }
        if (confirePasswd == null || confirePasswd.equals("")) {// 确认密码不能为空
            ToastUtil.show(this, R.string.no_confarm_passwd);
            return;
        }
        sUrl = UserUtil.getServerUrl(this);
        if (Utils.isEmpty(sUrl)) {
            getServerConnInfo();
        } else {
            forgotPassword();
        }

    }

    private void forgotPassword() {
        LogUtil.i("URL:" + sUrl);
        String userName = strAccount;
        LogUtil.i("name:" + userName + ",passwd:" + passwd);
        RequestParams params = HttpParams.forgetPasswordCN(userName, passwd);

        requestHandle = HttpClientUsage.getInstance().post(this, sUrl, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(ForgetPassWdActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            ToastUtil.show(ForgetPassWdActivity.this, obj.what);
                            UserSP.getInstance().savePWD(ForgetPassWdActivity.this, "");
                            finish();
                            startActivity(new Intent(ForgetPassWdActivity.this, LoginActivity.class));
                        }
                        ToastUtil.show(ForgetPassWdActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(ForgetPassWdActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void getServerConnInfo() {
        String url;//域名解析ip拼接全址
        if (Utils.isChineseMainland()) //判断是否是大陆
            url = HttpParams.SERVER_URL_CENTER_CN;
        else
            url = HttpParams.SERVER_URL_CENTER_HK;
//		String url = HttpParams.SERVER_URL_CENTER;
        RequestParams params = HttpParams.getServerConnInfoByUser(strAccount);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                ForgetPassWdActivity.this, null,
                                ForgetPassWdActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (null == obj) {
                            ToastUtil.show(ForgetPassWdActivity.this,
                                    R.string.net_exception);
                            return;
                        }
                        if (obj.code == 0) {
                            ServerConnInfo serverConnInfo = GsonParse
                                    .serverConnInfoByUserParse(new String(
                                            response));
                            if (null == serverConnInfo) {
                                ToastUtil.show(ForgetPassWdActivity.this,
                                        R.string.server_no);
                            } else {
                                sUrl = Utils.getUrl(serverConnInfo.conn_name, serverConnInfo.conn_port);
                                UserUtil.saveServerUrl(ForgetPassWdActivity.this,sUrl);
//                                AppSP.getInstance().saveRegisterAddress(ForgetPassWdActivity.this, strAccount,
//                                        sUrl);
//								AppSP.getInstance().saveRegisterAddressCountry(
//										ForgetPassWdActivity.this, strAccount,
//										serverConnInfo.conn_country);

                                forgotPassword();
                            }
                        } else {
                            ToastUtil.show(ForgetPassWdActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(ForgetPassWdActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

}
