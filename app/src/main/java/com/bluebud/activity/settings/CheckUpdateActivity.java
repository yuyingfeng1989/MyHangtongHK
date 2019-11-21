package com.bluebud.activity.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.UpgradProgressInfo;
import com.bluebud.info.VersionObj;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.MyProgressBar;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


public class CheckUpdateActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener {
    private TextView tvCurVer;
    private TextView tvNewVer;
    private TextView tvCurDeviceVer;
    private TextView tvNewDeviceVer;
    private MyProgressBar progressBar;
    private Button btnUpdateDevice;
    private Button btnUpdateAPP;
    private LinearLayout llDevice;

    private Tracker mCurTracker;
    private String strTrackerNo = "";
    private int ranges = 1;
    private String strCurVer;
    private String strUrlApp;
    private String lastFirmwareVersion;
    private String progress = "";

    private RequestHandle requestHandle;

    // 定时器
    private final static int TIME = 3000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            getUpgradProgress();

            handler.postDelayed(this, TIME);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_check_update);

        init();

        checkForUpdate();

    }

    public void init() {
        setBaseTitleText(R.string.check_update);
        setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);

        mCurTracker = UserUtil.getCurrentTracker(this);
        if (null != mCurTracker) {
            strTrackerNo = mCurTracker.device_sn;
            ranges = mCurTracker.ranges;
        }

        tvCurVer = (TextView) findViewById(R.id.tv_current_version);
        tvNewVer = (TextView) findViewById(R.id.tv_newest_version);
        tvCurDeviceVer = (TextView) findViewById(R.id.tv_device_current_version);
        tvNewDeviceVer = (TextView) findViewById(R.id.tv_device_newest_version);
        progressBar = (MyProgressBar) findViewById(R.id.pb_upgrade);
        btnUpdateAPP = (Button) findViewById(R.id.btn_update_app);
        btnUpdateDevice = (Button) findViewById(R.id.btn_update_obd);
        llDevice = (LinearLayout) findViewById(R.id.ll_device);

        if (5 == ranges) {
            llDevice.setVisibility(View.GONE);
        }

        try {
            strCurVer = Utils.getVersionName(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvCurVer.setText(strCurVer);

        btnUpdateAPP.setOnClickListener(this);
        btnUpdateDevice.setOnClickListener(this);
        btnUpdateAPP.setClickable(false);
        btnUpdateDevice.setClickable(false);
    }

    private void setVersionData(VersionObj obj) {
        tvNewVer.setText(obj.appVersion);
        int iCompare = Utils.verCompare(obj.appVersion, strCurVer);
        if (0 < iCompare) {
            btnUpdateAPP.setClickable(true);
            btnUpdateAPP.setBackgroundResource(R.drawable.btn_theme_selector);
            strUrlApp = obj.appUrl;
        } else {
            btnUpdateAPP.setClickable(false);
        }

        LogUtil.i(obj.currentFirmwareVersion);
        LogUtil.i(obj.lastFirmwareVersion);

        if (!Utils.isEmpty(obj.currentFirmwareVersion)
                && !Utils.isEmpty(obj.lastFirmwareVersion)) {
            tvCurDeviceVer.setText(obj.currentFirmwareVersion);
            tvNewDeviceVer.setText(obj.lastFirmwareVersion);
            if (!obj.currentFirmwareVersion.equals(obj.lastFirmwareVersion)) {
                lastFirmwareVersion = obj.lastFirmwareVersion;
                btnUpdateDevice.setClickable(true);
                btnUpdateDevice
                        .setBackgroundResource(R.drawable.btn_theme_selector);

                handler.postDelayed(runnable, 100);
            }
        }
    }

    private void upgrading() {
        btnUpdateDevice.setClickable(false);
        btnUpdateDevice
                .setBackgroundResource(R.drawable.btn_grey_selector);

        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacks(runnable);

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.btn_update_app:
                Uri uri = Uri.parse(strUrlApp);
                Intent netIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(netIntent);
                break;
            case R.id.btn_update_obd:
                if (!mCurTracker.super_user.equals(UserSP.getInstance()
                        .getUserName(this))) {
                    ToastUtil.show(this, R.string.no_super_user);
                    return;
                }

                upgradDeviceSoftware();
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

    private void upgradeSuccessPrompt() {
        DialogUtil.show(this, R.string.prompt, R.string.upgrade_success_prompt,
                R.string.confirm, new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                        finish();
                    }
                }, R.string.cancel, new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                    }
                });
    }

    private void checkForUpdate() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.checkForUpdate(
                strTrackerNo, UserSP.getInstance().getUserName(this));

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                CheckUpdateActivity.this, null,
                                CheckUpdateActivity.this);
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
                            VersionObj versionObj = GsonParse
                                    .versionObjParse(new String(response));
                            if (versionObj == null)
                                return;
                            setVersionData(versionObj);
                        } else {
                            ToastUtil.show(CheckUpdateActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(CheckUpdateActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void upgradDeviceSoftware() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.upgradDeviceSoftware(strTrackerNo,
                lastFirmwareVersion);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                CheckUpdateActivity.this, null,
                                CheckUpdateActivity.this);
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
                            upgrading();

                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setProgress(0);
                        } else {
                            ToastUtil.show(CheckUpdateActivity.this, obj.what);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(CheckUpdateActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void getUpgradProgress() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.getUpgradProgress(strTrackerNo);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);

                        progress = progress + new String(response);

                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (null != obj && obj.code == 0) {
                            UpgradProgressInfo progressInfo = GsonParse
                                    .getUpgradProgress(new String(response));
                            if (0 == progressInfo.upgradstatus) {
                                progressBar.setVisibility(View.VISIBLE);
                                progressBar.setProgress(100);

                                upgradeSuccessPrompt();
                            } else if (1 == progressInfo.upgradstatus) {
                                progressBar.setVisibility(View.VISIBLE);
                            } else if (2 == progressInfo.upgradstatus) {
                                upgrading();

                                progressBar.setVisibility(View.VISIBLE);
                                progressBar.setProgress(progressInfo.upgradvalue);
                            } else if (3 == progressInfo.upgradstatus) {

                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

}
