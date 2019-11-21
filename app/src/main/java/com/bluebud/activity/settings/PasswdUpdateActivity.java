package com.bluebud.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bluebud.activity.BaseActivity;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class PasswdUpdateActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener {
    private Button btnSubmitPwd;
    private EditText etOldPwd;
    private EditText etNewPwd;
    private EditText etConfirmNewPwd;
//	private RequestHandle requestHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_passwd_update);

        init();
    }

    public void init() {
        setBaseTitleText(R.string.change_passwd);
        getBaseTitleLeftBack().setOnClickListener(this);
        btnSubmitPwd = (Button) findViewById(R.id.btn_commit);
        etOldPwd = (EditText) findViewById(R.id.et_old_pwd);
        etNewPwd = (EditText) findViewById(R.id.et_new_pwd);
        etConfirmNewPwd = (EditText) findViewById(R.id.et_confirm_pwd);
        btnSubmitPwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                if (UserUtil.isGuest(this)) {
                    ToastUtil.show(this, R.string.guest_no_set);
                    return;
                }
                modifyUserPassword();
                break;
            case R.id.rl_title_back:
                finish();
                break;
        }
    }

    @Override
    public void onProgressDialogBack() {
//		LogUtil.i("onProgressDialogBack()");
//		if (null != requestHandle && !requestHandle.isFinished()) {
//			requestHandle.cancel(true);
//		}
    }

    private void modifyUserPassword() {
        String oldPwd = etOldPwd.getText().toString();
        String newPwd = etNewPwd.getText().toString();
        String confirmPwd = etConfirmNewPwd.getText().toString();
        if (oldPwd.trim().equals("") || newPwd.trim().equals("")
                || confirmPwd.trim().equals("")) {
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

        String url = UserUtil.getServerUrl(this);

        String userName = UserSP.getInstance().getUserName(this);

        RequestParams params = HttpParams.modifyUserPassword(userName, oldPwd,
                newPwd);

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                PasswdUpdateActivity.this, null,
                                PasswdUpdateActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        ToastUtil.show(PasswdUpdateActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(PasswdUpdateActivity.this,
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
