package com.bluebud.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.kankan.wheelview.NumericWheelAdapter;
import com.kankan.wheelview.OnWheelChangedListener1;
import com.kankan.wheelview.WheelView1;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.Calendar;

public class TimeSwitchSetActivity extends BaseActivity implements
        OnClickListener, OnWheelChangedListener1, OnProgressDialogClickListener {
    private TextView tvTitleOn;
    private TextView tvTitleOff;
    private TextView tvPrompt;

    private WheelView1 wvHoursOff;
    private WheelView1 wvMinsOff;
    private WheelView1 wvHoursOn;
    private WheelView1 wvMinsOn;

    private String sHoursOff = "";
    private String sMinsOff = "";
    private String sHoursOn = "";
    private String sMinsOn = "";

    private Tracker mTracker;
    private String sTrackerNo = "";
    private String sTrackerType = Constants.EXTRA_DEVICE_TYPE_720;

    private TimeSwitchInfo timeSwitchInfo;

    private RequestHandle requestHandle;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_time_switch_set);

        mTracker = UserUtil.getCurrentTracker(this);
        sTrackerNo = mTracker.device_sn;

        if (null != getIntent()) {
            sTrackerType = getIntent().getExtras().getString(
                    Constants.EXTRA_DEVICE_TYPE,
                    Constants.EXTRA_DEVICE_TYPE_720);
        }

        timeSwitchInfo = new TimeSwitchInfo();

        init();
    }

    public void init() {
        setBaseTitleText(R.string.time_switch);
        getBaseTitleLeftBack().setOnClickListener(this);
        setBaseTitleRightTextVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.save);
        getBaseTitleRightText().setOnClickListener(this);

        timeSwitchInfo = (TimeSwitchInfo) getIntent().getSerializableExtra(
                "TIME_SWITCH");

        tvTitleOn = (TextView) findViewById(R.id.tv_title_on);
        tvTitleOff = (TextView) findViewById(R.id.tv_title_off);
        tvPrompt = (TextView) findViewById(R.id.tv_prompt);

        wvHoursOff = (WheelView1) findViewById(R.id.hour_off);
        wvMinsOff = (WheelView1) findViewById(R.id.mins_off);
        wvHoursOn = (WheelView1) findViewById(R.id.hour_on);
        wvMinsOn = (WheelView1) findViewById(R.id.mins_on);

        if (sTrackerType.equals(Constants.EXTRA_DEVICE_TYPE_719)) {
            setBaseTitleText(R.string.time_dormancy);
            tvTitleOn.setText(R.string.end);
            tvTitleOff.setText(R.string.start);

            tvPrompt.setVisibility(View.VISIBLE);
            tvPrompt.setText(R.string.dormancy_set_prompt);
        }

        sHoursOn = timeSwitchInfo.boottime.split(":")[0];
        sMinsOn = timeSwitchInfo.boottime.split(":")[1];
        sHoursOff = timeSwitchInfo.shutdowntime.split(":")[0];
        sMinsOff = timeSwitchInfo.shutdowntime.split(":")[1];

        initTime();
    }

    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Utils.hourString2Date(timeSwitchInfo.shutdowntime));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 时
        wvHoursOff.setAdapter(new NumericWheelAdapter(0, 23));
        wvHoursOff.setCyclic(true);
        wvHoursOff.setLabel(getString(R.string.hour));// 添加文字
        wvHoursOff.setCurrentItem(hour);

        // 分
        wvMinsOff.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wvMinsOff.setCyclic(true);
        wvMinsOff.setLabel(getString(R.string.minute));// 添加文字
        wvMinsOff.setCurrentItem(minute);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(Utils.hourString2Date(timeSwitchInfo.boottime));
        int hour1 = calendar1.get(Calendar.HOUR_OF_DAY);
        int minute1 = calendar1.get(Calendar.MINUTE);

        // 时
        wvHoursOn.setAdapter(new NumericWheelAdapter(0, 23));
        wvHoursOn.setCyclic(true);
        wvHoursOn.setLabel(getString(R.string.hour));// 添加文字
        wvHoursOn.setCurrentItem(hour1);

        // 分
        wvMinsOn.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wvMinsOn.setCyclic(true);
        wvMinsOn.setLabel(getString(R.string.minute));// 添加文字
        wvMinsOn.setCurrentItem(minute1);

        wvHoursOff.addChangingListener(this);
        wvMinsOff.addChangingListener(this);
        wvHoursOn.addChangingListener(this);
        wvMinsOn.addChangingListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.rl_title_right_text:
                if (Utils.isOperate(this, mTracker)) {
                    confirm();
                }
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

    @Override
    public void onChanged(WheelView1 wheel, int oldValue, int newValue) {
        LogUtil.i(oldValue + " " + newValue);
        if (wvHoursOff.equals(wheel)) {
            sHoursOff = String.format("%02d", newValue);
        } else if (wvMinsOff.equals(wheel)) {
            sMinsOff = String.format("%02d", newValue);
        } else if (wvHoursOn.equals(wheel)) {
            sHoursOn = String.format("%02d", newValue);
        } else if (wvMinsOn.equals(wheel)) {
            sMinsOn = String.format("%02d", newValue);
        }

        LogUtil.i(sHoursOn + ":" + sMinsOn + " " + sHoursOff + ":" + sMinsOff);
    }

    private void confirm() {
        timeSwitchInfo.boottime = sHoursOn + ":" + sMinsOn;
        timeSwitchInfo.shutdowntime = sHoursOff + ":" + sMinsOff;

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

        saveTimeSwitch();
    }

    private void saveTimeSwitch() {
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
                                TimeSwitchSetActivity.this, null,
                                TimeSwitchSetActivity.this);
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
                            finish();
                        }
                        ToastUtil.show(TimeSwitchSetActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeSwitchSetActivity.this,
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
