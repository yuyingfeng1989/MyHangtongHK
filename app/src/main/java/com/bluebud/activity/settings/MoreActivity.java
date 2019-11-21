package com.bluebud.activity.settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.bluebud.activity.BaseActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

//更多页面
public class MoreActivity extends BaseActivity implements OnClickListener, OnProgressDialogClickListener {
    private Tracker mTracker;
    private RequestHandle requestHandle;
    private int ranges = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_more);
        mTracker = UserUtil.getCurrentTracker(this);
        if (mTracker != null) {
            ranges = mTracker.ranges;
        }
        init();
    }

    private void init() {
        super.setBaseTitleText(R.string.advanced_settings);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        LinearLayout ll_apn_setting = ((LinearLayout) findViewById(R.id.ll_apn_setting));
        LinearLayout ll_location_frequency = ((LinearLayout) findViewById(R.id.ll_location_frequency));
        LinearLayout ll_reset = ((LinearLayout) findViewById(R.id.ll_reset));
        ll_apn_setting.setOnClickListener(this);
        ll_location_frequency.setOnClickListener(this);
        ll_reset.setOnClickListener(this);
        if (ranges == 5) {
            ((LinearLayout) findViewById(R.id.ll_reset)).setVisibility(View.GONE);
        } else if(ranges == 6){//obd设备取消定位频率设置，默认是30s
            ll_location_frequency.setVisibility(View.GONE);
        } else {
            ((LinearLayout) findViewById(R.id.ll_reset)).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back://返回
                finish();
                break;
            case R.id.ll_apn_setting://APN设置
                Intent apnIntent = new Intent(this, ApnActivity.class);
                apnIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivity(apnIntent);
                break;
            case R.id.ll_location_frequency://定位频率
                if (null == mTracker) {
                    DialogUtil.showAddDevice(this);
                    return;
                }
                Intent positionIntent = new Intent(this, TimePositionActivity.class);
                positionIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                startActivity(positionIntent);
                break;
            case R.id.ll_reset://恢复出厂设置
                if (null == mTracker) {
                    DialogUtil.showAddDevice(this);
                    return;
                }
                if (!Utils.isSuperUser(mTracker, this)) {
                    return;
                }

                DialogUtil.show(this, R.string.prompt_reset,
                        R.string.notice_reset, R.string.confirm,
                        new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                DialogUtil.dismiss();

                                if (Utils.isOperate(MoreActivity.this,
                                        mTracker)) {
                                    resetTracker();
                                }
                            }
                        }, R.string.cancel, new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                DialogUtil.dismiss();
                            }
                        });
                break;
        }
    }


    /**
     * 恢复出厂设置
     */
    private void resetTracker() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.reset(UserUtil.getCurrentTracker(this).device_sn);
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(MoreActivity.this, null, MoreActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        /**
                         if(600 == obj.code){
                         ToastUtil.show(SettingActivity.this, R.string.offline_factory_setting);
                         }else{
                         ToastUtil.show(SettingActivity.this, obj.what);
                         }*/

                        // guoqz add 20160309.
                        if (0 != obj.code) {
                            ToastUtil.show(MoreActivity.this, R.string.offline_factory_setting);
                        } else {
                            ToastUtil.show(MoreActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(MoreActivity.this, R.string.net_exception);
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
