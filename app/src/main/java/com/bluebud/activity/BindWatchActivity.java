package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bluebud.activity.settings.TrackerEditActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.User;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
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

public class BindWatchActivity extends BaseActivity implements OnClickListener,
        OnProgressDialogClickListener {
    private Button btnCommit;
    private EditText etTrackerNo;
    private EditText etPhone1;
    private EditText etPhone2;
    private EditText etPhone3;
    //    private Tracker mCurTracker;
    private Tracker mTracker;

    private String sTrackerNo = "";
    private String sSIM = "";
    private String sPhone1 = "";
    private String sPhone2 = "";
    private String sPhone3 = "";

    private RequestHandle requestHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_bind_watch);
        init();
    }

    public void init() {
        setBaseTitleText(R.string.contact_add);
        getBaseTitleLeftBack().setOnClickListener(this);

        sTrackerNo = getIntent().getStringExtra("TRACKER_NO");
        sSIM = getIntent().getStringExtra("SIM_NO");

//        mCurTracker = UserUtil.getCurrentTracker(this);

        etTrackerNo = (EditText) findViewById(R.id.et_tracker_no);
        etPhone1 = (EditText) findViewById(R.id.et_mobile1);
        etPhone2 = (EditText) findViewById(R.id.et_mobile2);
        etPhone3 = (EditText) findViewById(R.id.et_mobile3);
        btnCommit = (Button) findViewById(R.id.btn_commit);

        btnCommit.setOnClickListener(this);

        etTrackerNo.setText(sTrackerNo);
        // 帐号和密码监听
        TextChangeUtils tc = new TextChangeUtils();
        tc.addEditText(etTrackerNo);
        tc.setButton(btnCommit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (etTrackerNo.getText().toString().length() > 0) {
            ((Button) findViewById(R.id.btn_commit)).setEnabled(true);
            ((Button) findViewById(R.id.btn_commit)).setTextColor(this
                    .getResources().getColor(R.color.white));

        } else {
            ((Button) findViewById(R.id.btn_commit)).setEnabled(false);
            ((Button) findViewById(R.id.btn_commit)).setTextColor(this.getResources().getColor(R.color.text_theme3));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.btn_commit:
                if (UserUtil.isGuest(this)) {
                    ToastUtil.show(this, R.string.guest_no_set);
                    return;
                }
                bindingRegister();
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

    private void bindSuccess(String sResult) {
        User user = GsonParse.userParse(sResult);
        User user1 = UserUtil.getUserInfo(BindWatchActivity.this);
        user1.device_list = user.device_list;
        UserUtil.savaUserInfo(BindWatchActivity.this, user1);
        for (int i = 0; i < user1.device_list.size(); i++) {
            if (sTrackerNo.equals(user1.device_list.get(i).device_sn)) {
                mTracker = user1.device_list.get(i);
                break;
            }
        }

//		if (null == mCurTracker) {
//			UserUtil.saveCurrentTracker(this, user1.device_list.get(0));
//			sendBroadcast(new Intent(Constants.ACTION_TRACTER_CHANGE));
//		}
        UserUtil.saveCurrentTracker(this, mTracker);
        sendBroadcast(new Intent(Constants.ACTION_TRACTER_CHANGE));

        Intent intent1 = new Intent(BindWatchActivity.this,
                TrackerEditActivity.class);
        intent1.putExtra(Constants.EXTRA_TRACKER, mTracker);
        intent1.putExtra("fromwhere", Constants.BINDACTIVITY);
        startActivity(intent1);
        finish();
    }

    private void bindingRegister() {
        sPhone1 = etPhone1.getText().toString().trim();
        sPhone2 = etPhone2.getText().toString().trim();
        sPhone3 = etPhone3.getText().toString().trim();

        if (!Utils.isEmpty(sPhone1) && !Utils.isCorrectPhone(sPhone1)) {
            ToastUtil.show(this, R.string.input_tracker_contect);
            return;
        }

        if (!Utils.isEmpty(sPhone2) && !Utils.isCorrectPhone(sPhone2)) {
            ToastUtil.show(this, R.string.input_tracker_contect);
            return;
        }

        if (!Utils.isEmpty(sPhone3) && !Utils.isCorrectPhone(sPhone3)) {
            ToastUtil.show(this, R.string.input_tracker_contect);
            return;
        }

        trackerRegister();

    }

    private void trackerRegister() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.bindingDevice(sTrackerNo, sSIM,
                sPhone1, sPhone2, sPhone3, 5, 1);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                BindWatchActivity.this, null,
                                BindWatchActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj reBaseObj = GsonParse
                                .reBaseObjParse(new String(response));
                        if (reBaseObj == null) {
                            ToastUtil.show(BindWatchActivity.this,
                                    R.string.net_exception);
                            return;
                        }
                        if (reBaseObj.code == 0) {
                            bindSuccess(new String(response));
                        } else {
                            DialogUtil.show(BindWatchActivity.this,
                                    R.string.prompt, reBaseObj.what,
                                    R.string.confirm, new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            DialogUtil.dismiss();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(BindWatchActivity.this,
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
