package com.bluebud.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.bluebud.activity.BaseActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.StepInfo;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

//计步开关
public class StepSettingActivity extends BaseActivity implements
        OnClickListener, CompoundButton.OnCheckedChangeListener {
    private CheckBox mStepSwatch;
    private Tracker mTrakcer;
    private Context mContext;
    private String device_sn;
    private int step = 0;
    private int lastStep = 0;
    private boolean isGetStepChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_step_setting);
        mContext = this;
        init();
        getDeviceStep();
    }

    public void init() {
        setBaseTitleText(R.string.step_setting);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.submit1);
        setBaseTitleRightTextVisible(View.GONE);
        getBaseTitleRightText().setOnClickListener(this);
        getBaseTitleLeftBack().setOnClickListener(this);
        mTrakcer = UserUtil.getCurrentTracker(this);
        if (mTrakcer != null) {
            device_sn = mTrakcer.device_sn;
        }
        mStepSwatch = (CheckBox) findViewById(R.id.ring_switch_button);
        mStepSwatch.setOnCheckedChangeListener(this);
        mStepSwatch.setOnClickListener(this);
        mStepSwatch.setChecked(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rl_title_back:
                finish();

                break;
            // case R.id.rl_title_right_text://提交
            //
            // break;
            case R.id.ring_switch_button://
                LogUtil.i("开关点击");
                if (!Utils.isSuperUser(mTrakcer, StepSettingActivity.this)) {
                    // 不是超级用户不让他点击
                    boolean checked = mStepSwatch.isChecked();
                    if (checked) {
                        mStepSwatch.setChecked(false);
                    } else {
                        mStepSwatch.setChecked(true);
                    }
                }

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.ring_switch_button:// 铃声
                LogUtil.i("计步开关1" + isChecked);
                if (isGetStepChange) {
                    isGetStepChange = false;
                    return;
                }
                if (Utils.isSuperUser(mTrakcer, StepSettingActivity.this)) {
                    // 不是超级用户不让他点击
                    if (isChecked) {
                        step = 1;
                    } else {
                        step = 0;
                    }
                    setDeviceStep();
                }

                break;

            default:
                break;
        }

    }

    // 设置步数开关接口
    public void setDeviceStep() {

        String url = UserUtil.getServerUrl(mContext);

        RequestParams params = HttpParams.setDeviceStep(device_sn, step);
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null) {
                            setStepSwatch(lastStep);
                            return;
                        }
                        if (obj.code == 0) {
                            setStepSwatch(step);
                            lastStep = step;
                        } else {
                            setStepSwatch(lastStep);
                        }
                        ToastUtil.show(mContext, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });

    }

    // 设置步数开关接口
    public void getDeviceStep() {

        String url = UserUtil.getServerUrl(mContext);

        RequestParams params = HttpParams.getDeviceStep(device_sn);
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null) {
                            return;
                        }
                        if (obj.code == 0) {
                            StepInfo stepInfo = GsonParse.getStepPrase(new String(response));
                            if (stepInfo != null) {
                                step = stepInfo.step;
                                if (step == 1) {
                                    isGetStepChange = true;
                                }
                                lastStep = stepInfo.step;
                                setStepSwatch(step);
                            }
                        }

                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void setStepSwatch(int step) {
        if (step == 1) {
            mStepSwatch.setChecked(true);
        } else {
            mStepSwatch.setChecked(false);
        }
    }
}
