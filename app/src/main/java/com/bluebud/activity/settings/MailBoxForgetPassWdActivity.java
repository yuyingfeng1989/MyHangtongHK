package com.bluebud.activity.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bluebud.activity.BaseActivity;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.ServerConnInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.TextChangeUtils;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class MailBoxForgetPassWdActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener {
    private EditText m_EmaiEditText;
    private Button m_CommitBtn;
    private Dialog loadingDialog;
    private String strAccount = "";
    private String sUrl = "";
    private RequestHandle requestHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailbox_forget_passwd);

        strAccount = getIntent().getStringExtra("ACCOUNT");
        init();
    }

    /**
     * 初始化控件
     */
    public void init() {
        loadingDialog = new Dialog(this, R.style.Transparent_Dialog);
        loadingDialog.setContentView(R.layout.loading_dialog);

        m_EmaiEditText = (EditText) findViewById(R.id.et_email);
        m_CommitBtn = (Button) findViewById(R.id.btn_commit);

        m_CommitBtn.setOnClickListener(this);
        findViewById(R.id.findpassword_backimage).setOnClickListener(this);

        m_EmaiEditText.setText(strAccount);
        // 帐号和密码监听
        TextChangeUtils tc = new TextChangeUtils();
        tc.addEditText(m_EmaiEditText);
        tc.setButton(m_CommitBtn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                submit();
                break;
            case R.id.findpassword_backimage:
                finish();
                break;
        }
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    /**
     * 提交
     */
    private void submit() {
        strAccount = m_EmaiEditText.getText().toString();
        if (strAccount == null || strAccount.trim().equals("")) {
            ToastUtil.show(this, R.string.no_email);
            return;
        }
        if (!Utils.isCorrectEmail(strAccount)) {
            ToastUtil.show(this, R.string.email_error);
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
        RequestParams params = HttpParams.forgotPassword(strAccount);

        requestHandle = HttpClientUsage.getInstance().post(this, sUrl, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        ProgressDialogUtil.show(MailBoxForgetPassWdActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            DialogUtil.show(MailBoxForgetPassWdActivity.this,
                                    R.string.prompt, R.string.notice_passwd,
                                    R.string.check_email,
                                    new OnClickListener() {
                                        @Override
                                        public void onClick(View arg0) {
                                            DialogUtil.dismiss();
                                            Utils.openUrl(MailBoxForgetPassWdActivity.this, strAccount);
                                            onBackPressed();// 返回登录页
                                        }
                                    }, R.string.cancel, new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            DialogUtil.dismiss();
                                        }
                                    });
                            return;
                        }
                        ToastUtil.show(MailBoxForgetPassWdActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(MailBoxForgetPassWdActivity.this,
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
                                MailBoxForgetPassWdActivity.this, null,
                                MailBoxForgetPassWdActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (null == obj) {
                            ToastUtil.show(MailBoxForgetPassWdActivity.this,
                                    R.string.net_exception);
                            return;
                        }
                        if (obj.code == 0) {
                            ServerConnInfo serverConnInfo = GsonParse
                                    .serverConnInfoByUserParse(new String(
                                            response));
                            if (null == serverConnInfo) {
                                ToastUtil.show(MailBoxForgetPassWdActivity.this,
                                        R.string.server_no);
                            } else {
                                sUrl = Utils.getUrl(serverConnInfo.conn_name, serverConnInfo.conn_port);
                                UserUtil.saveServerUrl(MailBoxForgetPassWdActivity.this,sUrl);
//                                AppSP.getInstance().saveRegisterAddressCountry(
//                                        MailBoxForgetPassWdActivity.this, strAccount,
//                                        serverConnInfo.conn_country);

                                forgotPassword();
                            }
                        } else {
                            ToastUtil.show(MailBoxForgetPassWdActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(MailBoxForgetPassWdActivity.this,
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
