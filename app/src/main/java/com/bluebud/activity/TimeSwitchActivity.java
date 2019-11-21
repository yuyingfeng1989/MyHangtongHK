package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.TimeSwitchInfo;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.PopupWindowWheelViewUtils;
import com.bluebud.utils.PopupWindowWheelViewUtils.OnWheeClicked;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.Calendar;

//定时开关机页面
public class TimeSwitchActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener, OnWheeClicked {
    private CheckBox sbSwitch;
    private LinearLayout llTime;
    private TextView tvTimeOn;
    private TextView tvTimeOff;

    private String sTime;

    private boolean bEnable = false;
    private boolean bFirst = true;
    private boolean isSubmit = true;

    private Tracker mTracker;
    private String sTrackerNo = "";
    private String sTrackerType = Constants.EXTRA_DEVICE_TYPE_720;

    private TimeSwitchInfo timeSwitchInfo;

    private RequestHandle requestHandle;

    private PopupWindowWheelViewUtils wheelViewUtils;
    private RelativeLayout rlOff;
    private RelativeLayout rlOn;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_time_switch);

        mTracker = UserUtil.getCurrentTracker(this);
        sTrackerNo = mTracker.device_sn;

        if (null != getIntent()) {
            sTrackerType = getIntent().getStringExtra(Constants.EXTRA_DEVICE_TYPE);
        }

        timeSwitchInfo = new TimeSwitchInfo();

        init();

        getTimeSwitch();
    }

    public void init() {
        setBaseTitleText(R.string.time_switch);
        setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);

        sbSwitch = (CheckBox) findViewById(R.id.switch_button);//定时开关机按扭

        rlOff = ((RelativeLayout) findViewById(R.id.rl_off));
        rlOff.setOnClickListener(this);//关机
        rlOn = ((RelativeLayout) findViewById(R.id.rl_on));
        rlOn.setOnClickListener(this);//开机
        llTime = (LinearLayout) findViewById(R.id.ll_time_set);
        tvTimeOn = (TextView) findViewById(R.id.tv_time_on);//开机时间
        tvTimeOff = (TextView) findViewById(R.id.tv_time_off);//关机时间
        wheelViewUtils = new PopupWindowWheelViewUtils(this, this);

        sbSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean flag) {
//				if (bFirst) {
//					bFirst = false;
//					return;
//				}
                LogUtil.i("定时开关机按钮的状态" + flag);
                //			if (Utils.isOperate(TimeSwitchActivity.this, mTracker)) {
                if (flag) {
                    llTime.setClickable(true);
                    rlOff.setClickable(true);
                    rlOn.setClickable(true);
                } else {
                    llTime.setClickable(false);
                    rlOff.setClickable(false);
                    rlOn.setClickable(false);
                }

                bEnable = flag;

                if (isSubmit) {//这个是防止提交失败时不进入死循环

                    if (bFirst) {
                        bFirst = false;
                    } else {
                        if (Utils.isSuperUser(mTracker, TimeSwitchActivity.this)) {
                            saveTimeSwitch();
                        }
                    }
                } else {
                    isSubmit = true;
                }

            }
            //}
        });
        sbSwitch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean checked = sbSwitch.isChecked();
                if (!Utils.isSuperUser(mTracker, TimeSwitchActivity.this)) {
                    if (checked) {
                        sbSwitch.setChecked(false);
                    } else {
                        sbSwitch.setChecked(true);
                    }
                }

            }
        });

		
		/*if (sTrackerType.equals(Constants.EXTRA_DEVICE_TYPE_719)) {
            setBaseTitleText(R.string.time_dormancy);
			tvTitle.setText(R.string.time_dormancy);
			tvTitleOn.setText(R.string.start);
			tvTitleOff.setText(R.string.end);

			tvPrompt.setVisibility(View.VISIBLE);
			tvPrompt.setText(R.string.dormancy_prompt);
		}*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back://返回
                finish();
                break;
            case R.id.rl_on://开机
                if (Utils.isSuperUser(mTracker, TimeSwitchActivity.this)) {
                    if (Utils.isEmpty(sTime)) {
                        Calendar calendar = Calendar.getInstance();
                        wheelViewUtils.ShowTime(Utils.getDate(calendar), true);
                    } else {
                        wheelViewUtils.ShowTime(sTime, true);
                    }

                }
                break;
            case R.id.rl_off://关机
                if (Utils.isSuperUser(mTracker, TimeSwitchActivity.this)) {
                    if (Utils.isEmpty(sTime)) {
                        Calendar calendar = Calendar.getInstance();
                        wheelViewUtils.ShowTime(Utils.getDate(calendar), false);
                    } else {
                        wheelViewUtils.ShowTime(sTime, false);
                    }
                }

                break;
/*		
        case R.id.ll_time_set:
			Intent intent = new Intent(TimeSwitchActivity.this,
					TimeSwitchSetActivity.class);
			intent.putExtra(Constants.EXTRA_DEVICE_TYPE, sTrackerType);
			intent.putExtra("TIME_SWITCH", timeSwitchInfo);
			startActivityForResult(intent, 1);
			break;*/
        }
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    /*
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
                timeSwitchInfo = (TimeSwitchInfo) data
                        .getSerializableExtra("TIME_SWITCH");

                tvTimeOn.setText(timeSwitchInfo.boottime);
                tvTimeOff.setText(timeSwitchInfo.shutdowntime);
            }
        };*/
//得到设备开关机时间接口
    private void getTimeSwitch() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = null;

        if (sTrackerType.equals(Constants.EXTRA_DEVICE_TYPE_719)) {
            params = HttpParams.getSleepInfo(sTrackerNo);
        } else {
            params = HttpParams.getTimeSwitch(sTrackerNo);
        }

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TimeSwitchActivity.this, null,
                                TimeSwitchActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {
                            timeSwitchInfo = GsonParse
                                    .getTimeSwitch(new String(response));
                            LogUtil.i("从服务器得到开关机状态：" + timeSwitchInfo.enable);
                            if (1 == timeSwitchInfo.enable) {
                                bEnable = true;
                                llTime.setClickable(true);
                                rlOff.setClickable(true);
                                rlOn.setClickable(true);
                            } else {
                                bEnable = false;
                                llTime.setClickable(false);
                                rlOff.setClickable(false);
                                rlOn.setClickable(false);
                            }

                            sbSwitch.setChecked(bEnable);

                            if (!bEnable) {
                                bFirst = false;
                            }

                            if (sTrackerType.equals(Constants.EXTRA_DEVICE_TYPE_719)) {
                                String sStart = timeSwitchInfo.boottime;
                                String sEnd = timeSwitchInfo.shutdowntime;
                                timeSwitchInfo.boottime = sEnd;
                                timeSwitchInfo.shutdowntime = sStart;
                            }

                            tvTimeOn.setText(timeSwitchInfo.boottime);
                            tvTimeOff.setText(timeSwitchInfo.shutdowntime);
                            isSubmit = true;
                        } else {
                            ToastUtil.show(TimeSwitchActivity.this, obj.what);
                            isSubmit = true;
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeSwitchActivity.this,
                                R.string.net_exception);
                        isSubmit = true;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }
    //定时开关机接口

    private void saveTimeSwitch() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = null;

        if (sTrackerType.equals(Constants.EXTRA_DEVICE_TYPE_719)) {
            params = HttpParams.setSleepInfo(sTrackerNo,
                    bEnable ? 1 : 0, timeSwitchInfo.shutdowntime,
                    timeSwitchInfo.boottime);
        } else {
//			params = HttpParams.saveTimeSwitch(sTrackerNo,
//					1, timeSwitchInfo.boottime,
//					timeSwitchInfo.shutdowntime);
            // guoqz add 20160314.
            params = HttpParams.saveTimeSwitch(sTrackerNo,
                    bEnable ? 1 : 0, timeSwitchInfo.boottime,
                    timeSwitchInfo.shutdowntime);
        }

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TimeSwitchActivity.this, null,
                                TimeSwitchActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {//设置成功
                            if (5 == mTracker.ranges) {
                                mTracker.bt_enable = bEnable ? 1 : 0;
                                UserUtil.saveCurTrackerChange(
                                        TimeSwitchActivity.this, mTracker);

                                Intent intent = new Intent(
                                        Constants.ACTION_TIME_SWITCH);
                                sendBroadcast(intent);
                                ToastUtil.show(TimeSwitchActivity.this, obj.what);
                            }
                        } else {//设置失败
                            ToastUtil.show(TimeSwitchActivity.this, obj.what);
                            isSubmit = false;
                            sbSwitch.setChecked(!bEnable);

                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeSwitchActivity.this,
                                R.string.net_exception);
                        isSubmit = false;
                        sbSwitch.setChecked(!bEnable);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    private void confirm() {


        LogUtil.i(timeSwitchInfo.boottime + " " + timeSwitchInfo.shutdowntime);

        if (sTrackerType.equals(Constants.EXTRA_DEVICE_TYPE_719)) {
            int result = Utils.compareTime(timeSwitchInfo.shutdowntime,
                    timeSwitchInfo.boottime);
            if (result > 0) {
                ToastUtil.show(this, R.string.time_error);
                return;
            }
        }

        if (timeSwitchInfo.boottime.equals(timeSwitchInfo.shutdowntime)) {
            ToastUtil.show(this, R.string.switch_time_same);
            return;
        }

        saveTimeSwitch1();
    }


    private void saveTimeSwitch1() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = null;

        if (sTrackerType.equals(Constants.EXTRA_DEVICE_TYPE_719)) {
            params = HttpParams.setSleepInfo(sTrackerNo, 1,
                    timeSwitchInfo.shutdowntime, timeSwitchInfo.boottime);
        } else {
            params = HttpParams.saveTimeSwitch(sTrackerNo, 1,
                    timeSwitchInfo.boottime, timeSwitchInfo.shutdowntime);
        }

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TimeSwitchActivity.this, null,
                                TimeSwitchActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {
                            Intent intent = new Intent();
                            intent.putExtra("TIME_SWITCH", timeSwitchInfo);
                            setResult(RESULT_OK, intent);
                            //finish();
                        }
                        ToastUtil.show(TimeSwitchActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeSwitchActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    @Override
    public void getWheelTime(String sTime, Boolean ison) {

        this.sTime = sTime;
        if (ison) {
            tvTimeOn.setText(sTime);
            timeSwitchInfo.boottime = sTime;
        } else {
            tvTimeOff.setText(sTime);
            timeSwitchInfo.shutdowntime = sTime;
        }
        confirm();
    }


}
