package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.bluebud.activity.BaseActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.OnlyUser;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.TextChangeUtils;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.view.ClearEditText;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


public class AuthActivity extends BaseActivity implements OnClickListener,
        OnProgressDialogClickListener, CompoundButton.OnCheckedChangeListener {
    private Button btnAdd;
    private ClearEditText etAccount;
    private ClearEditText etName;
    private RequestHandle requestHandle;
    private int position;
    private LinearLayout llPermissions;
    private Tracker mCurTracker;
    private int ranges = 1;
    private CheckBox cbLocation;
    private int is_gps = 0;
    private OnlyUser onlyUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_auth);
        mCurTracker = UserUtil.getCurrentTracker(this);
        if (mCurTracker != null)
            ranges = mCurTracker.ranges;
        Intent intent = getIntent();
        if (intent != null) {
            onlyUser = (OnlyUser) intent.getSerializableExtra("onlyUser");
            position = intent.getIntExtra("position", 0);
        }
        init();//初始化view
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (etAccount.getText().toString().length() > 0 || etName.getText().toString().length() > 0) {
            findViewById(R.id.btn_commit).setEnabled(true);
            ((Button) findViewById(R.id.btn_commit)).setTextColor(this
                    .getResources().getColor(R.color.white));

        } else {
            findViewById(R.id.btn_commit).setEnabled(false);
            ((Button) findViewById(R.id.btn_commit)).setTextColor(this
                    .getResources().getColor(R.color.text_theme3));

        }
    }

    public void init() {
        setBaseTitleText(R.string.add_members);
        getBaseTitleLeftBack().setOnClickListener(this);
        setBaseTitleVisible(View.VISIBLE);

        btnAdd = (Button) findViewById(R.id.btn_commit);
        etAccount = (ClearEditText) findViewById(R.id.et_username);
        etName = (ClearEditText) findViewById(R.id.et_password);
        etName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//限制昵称限制20个字符

        llPermissions = (LinearLayout) findViewById(R.id.ll_member_permissions);
        if (ranges == 7) {//只有蓝牙手表才会有是否上传定位
            llPermissions.setVisibility(View.VISIBLE);
        } else {
            llPermissions.setVisibility(View.GONE);
        }
        cbLocation = (CheckBox) findViewById(R.id.shake_switch_button);
        cbLocation.setOnCheckedChangeListener(this);

        if (onlyUser == null) {
            etAccount.setEnabled(true);
        } else {//修改备注时账号是不能修改的
            etAccount.setEnabled(false);
            etAccount.setTextColor(getResources().getColor(R.color.edit_hint));
            etAccount.setText(onlyUser.name);
            etName.setText(onlyUser.nickname);
            etName.setSelection(etName.length());
            btnAdd.setText(getResources().getString(R.string.ensure_modify));
            is_gps = onlyUser.isGps;
            LogUtil.i("is_gps:" + is_gps);
        }
        if (0 == is_gps) {//0表示开启，1表示不开启
            cbLocation.setChecked(true);
        } else {
            cbLocation.setChecked(false);
        }
        btnAdd.setOnClickListener(this);
        // 帐号和密码监听
        TextChangeUtils tc = new TextChangeUtils();
        tc.addEditText(etAccount);
        tc.addEditText(etName);
        tc.setButton(btnAdd);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                requestApiUtil();
                break;

            case R.id.rl_title_back:
                finish();
                break;
        }
    }

    @Override
    public void onProgressDialogBack() {
        if (null != requestHandle && !requestHandle.isFinished())
            requestHandle.cancel(true);
    }

    /**
     * 增加授权账号和修改授权账号
     */
    private void requestApiUtil() {
        final String email = etAccount.getText().toString();
        if (email == null || email.trim().equals("")) {
            ToastUtil.show(this, R.string.no_email);
            return;
        }
        final String nickName = etName.getText().toString();
        String url = UserUtil.getServerUrl(this);
        RequestParams params;
        if (onlyUser == null)
            params = HttpParams.authorizationBinding(mCurTracker.device_sn, email, nickName, is_gps);
        else
            params = HttpParams.modifyAccountRemark(mCurTracker.device_sn, email, nickName, is_gps);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(AuthActivity.this, null, AuthActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        ToastUtil.show(AuthActivity.this, obj.what);
                        if (obj.code == 0) {
                            if (onlyUser == null) {
                                setResult(RESULT_OK);
                            } else {
                                onlyUser.nickname = nickName;
                                onlyUser.isGps = is_gps;
                                Intent intent = new Intent();
                                intent.putExtra("onlyUser", onlyUser);
                                intent.putExtra("position", position);
                                setResult(RESULT_OK, intent);
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(AuthActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            is_gps = 0;
        } else {
            is_gps = 1;
        }
    }
}
