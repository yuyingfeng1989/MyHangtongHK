package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.LoginActivity;
import com.bluebud.app.AppManager;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.FaceBookPlatform;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.RegularUtil;
import com.bluebud.utils.TextChangeUtils;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.ClearEditText;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import io.rong.imkit.RongIM;

//修改密码

public class ChangePassWdActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener {

    //    private String strAccount = "";
//    private String strPasswd;
    private ClearEditText mEdtOldPasswd;
    private ClearEditText mEdtnewPasswd;
    private ClearEditText mEdtAgainNewPasswd;
    private RequestHandle requestHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_change_passwd);

//        strAccount = getIntent().getStringExtra("ACCOUNT");

        init();
    }

    public void init() {
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleText(R.string.change_passwd);
        getBaseTitleLeftBack().setOnClickListener(this);

        mEdtOldPasswd = (ClearEditText) findViewById(R.id.et_input_old_passwd);// 请输入旧密码
        mEdtnewPasswd = (ClearEditText) findViewById(R.id.et_input_new_passwd);// 请输入新密码
        mEdtAgainNewPasswd = (ClearEditText) findViewById(R.id.et_input_again_new_passwd);// 请再次输入新密码
        Button btn_confire = ((Button) findViewById(R.id.btn_confirm));// 确认
        btn_confire.setOnClickListener(this);
        // 帐号和密码监听
        TextChangeUtils tc = new TextChangeUtils();
        tc.addEditText(mEdtOldPasswd);
        tc.addEditText(mEdtnewPasswd);
        tc.addEditText(mEdtAgainNewPasswd);
        tc.setButton(btn_confire);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mEdtOldPasswd.getText().toString().length() > 0
                && mEdtnewPasswd.getText().toString().length() > 0
                && mEdtAgainNewPasswd.getText().toString().length() > 0) {
            ((Button) findViewById(R.id.btn_confirm)).setEnabled(true);
            ((Button) findViewById(R.id.btn_confirm)).setTextColor(this
                    .getResources().getColor(R.color.white));

        } else {
            ((Button) findViewById(R.id.btn_confirm)).setEnabled(false);
            ((Button) findViewById(R.id.btn_confirm)).setTextColor(this.getResources().getColor(R.color.text_theme3));

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.btn_confirm:
                // if(UserUtil.isGuest(this)){
                // ToastUtil.show(this, R.string.guest_no_set);
                // return;
                // }
                modifyUserPassword();
                break;

        }
    }

    // 修改密码接口
    private void modifyUserPassword() {
        String oldPwd = mEdtOldPasswd.getText().toString();
        String newPwd = mEdtnewPasswd.getText().toString();
        String confirmPwd = mEdtAgainNewPasswd.getText().toString();
//        strPasswd = confirmPwd;
        if (oldPwd.trim().equals("") || newPwd.trim().equals("") || confirmPwd.trim().equals("")) {
            ToastUtil.show(this, R.string.passwd_is_empty);
            return;
        }
        if (!Utils.isCorrectPwd(newPwd) || !Utils.isCorrectPwd(confirmPwd)) {
            ToastUtil.show(this, R.string.passwd_error);
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            ToastUtil.show(this, R.string.passwd_not_same);
            return;
        }
        if (RegularUtil.limitCN(newPwd)) {//判读是否包行中文
            ToastUtil.show(this, R.string.register_limit);
            return;
        }
        if (!RegularUtil.limitInput(newPwd)) {//判断密码输入类型
            ToastUtil.show(this, R.string.register_limit);
            return;
        }

        String url = UserUtil.getServerUrl(this);
        String userName = UserSP.getInstance().getUserName(this);
        RequestParams params = HttpParams.modifyUserPassword(userName, oldPwd, newPwd);
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(ChangePassWdActivity.this, null, ChangePassWdActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        ToastUtil.show(ChangePassWdActivity.this, obj.what);
                        if (obj.code == 0) {
                            // 延时1秒 然后跳到主界面
                            UserSP.getInstance().savePWD(ChangePassWdActivity.this, "");
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    AppManager.getAppManager().finishAllActivity();
                                    Intent intent = new Intent(ChangePassWdActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    UserSP.getInstance().saveUserName(ChangePassWdActivity.this, null);//保存登录名
                                    new FaceBookPlatform(ChangePassWdActivity.this).logout();//登出facebook
                                    RongIM.getInstance().logout();
                                    finish();
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(ChangePassWdActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });

    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }

    }

}
