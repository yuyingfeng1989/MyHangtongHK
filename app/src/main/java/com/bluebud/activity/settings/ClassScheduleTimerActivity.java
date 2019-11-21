package com.bluebud.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.SchoolTimetableInfo;
import com.bluebud.info.Tracker;
import com.bluebud.utils.PopupWindowWheelViewUtils;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.request.RequestUtil;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/10/9.
 */

public class ClassScheduleTimerActivity extends BaseActivity implements PopupWindowWheelViewUtils.OnWheeClicked, View.OnClickListener, RequestUtil.ICallBack {

    private PopupWindowWheelViewUtils wheelViewUtils;
    private String TIMEINFO = "timeInfo";
    private SchoolTimetableInfo.SchoolHourMapBean timeInfo;
    private Context mContext;
    private String mDeviceSn;
    private RequestUtil request;
    private int indexStart;

    private TextView startTime1;
    private TextView startTime2;
    private TextView startTime3;
    private TextView startTime4;
    private TextView startTime5;
    private TextView startTime6;
    private TextView startTime7;
    private TextView startTime8;
    private TextView endTime1;
    private TextView endTime2;
    private TextView endTime3;
    private TextView endTime4;
    private TextView endTime5;
    private TextView endTime6;
    private TextView endTime7;
    private TextView endTime8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduletimer_activity);
        WeakReference<ClassScheduleTimerActivity> weakReference = new WeakReference<>(this);
        mContext = weakReference.get();
        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        startTime1 = (TextView) findViewById(R.id.startTime1);
        startTime2 = (TextView) findViewById(R.id.startTime2);
        startTime3 = (TextView) findViewById(R.id.startTime3);
        startTime4 = (TextView) findViewById(R.id.startTime4);
        startTime5 = (TextView) findViewById(R.id.startTime5);
        startTime6 = (TextView) findViewById(R.id.startTime6);
        startTime7 = (TextView) findViewById(R.id.startTime7);
        startTime8 = (TextView) findViewById(R.id.startTime8);

        endTime1 = (TextView) findViewById(R.id.endTime1);
        endTime2 = (TextView) findViewById(R.id.endTime2);
        endTime3 = (TextView) findViewById(R.id.endTime3);
        endTime4 = (TextView) findViewById(R.id.endTime4);
        endTime5 = (TextView) findViewById(R.id.endTime5);
        endTime6 = (TextView) findViewById(R.id.endTime6);
        endTime7 = (TextView) findViewById(R.id.endTime7);
        endTime8 = (TextView) findViewById(R.id.endTime8);

        findViewById(R.id.ll_time1).setOnClickListener(this);
        findViewById(R.id.ll_time2).setOnClickListener(this);
        findViewById(R.id.ll_time3).setOnClickListener(this);
        findViewById(R.id.ll_time4).setOnClickListener(this);
        findViewById(R.id.ll_time5).setOnClickListener(this);
        findViewById(R.id.ll_time6).setOnClickListener(this);
        findViewById(R.id.ll_time7).setOnClickListener(this);
        findViewById(R.id.ll_time8).setOnClickListener(this);

        findViewById(R.id.schedule_back).setOnClickListener(this);
        findViewById(R.id.schedule_commit).setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        wheelViewUtils = new PopupWindowWheelViewUtils(this, this);
        timeInfo = (SchoolTimetableInfo.SchoolHourMapBean) getIntent().getSerializableExtra(TIMEINFO);
        Tracker mCurTracker = UserUtil.getCurrentTracker(mContext);
        if (mCurTracker != null)
            mDeviceSn = mCurTracker.device_sn;
        request = new RequestUtil(mContext, mDeviceSn, this);
        if (timeInfo != null) initSettingTime();
        else timeInfo = new SchoolTimetableInfo.SchoolHourMapBean();
    }

    /**
     * 设置初始值
     */
    private void initSettingTime() {
        startTime1.setText(timeInfo.class1Start);
        startTime2.setText(timeInfo.class2Start);
        startTime3.setText(timeInfo.class3Start);
        startTime4.setText(timeInfo.class4Start);
        startTime5.setText(timeInfo.class5Start);
        startTime6.setText(timeInfo.class6Start);
        startTime7.setText(timeInfo.class7Start);
        startTime8.setText(timeInfo.class8Start);
        endTime1.setText(timeInfo.class1End);
        endTime2.setText(timeInfo.class2End);
        endTime3.setText(timeInfo.class3End);
        endTime4.setText(timeInfo.class4End);
        endTime5.setText(timeInfo.class5End);
        endTime6.setText(timeInfo.class6End);
        endTime7.setText(timeInfo.class7End);
        endTime8.setText(timeInfo.class8End);
    }

    /**
     * 设置时间返回值
     */
    @Override
    public void getWheelTime(String sTime, Boolean ison) {
        String[] times = sTime.split(",");
        if (times == null || times.length < 2) {
            ToastUtil.show(this, getString(R.string.set_failure));
            return;
        }
        switch (indexStart) {
            case 0:
                timeInfo.class1Start = times[0];
                timeInfo.class1End = times[1];
                startTime1.setText(times[0]);
                endTime1.setText(times[1]);
                break;
            case 1:
                timeInfo.class2Start = times[0];
                timeInfo.class2End = times[1];
                startTime2.setText(times[0]);
                endTime2.setText(times[1]);
                break;
            case 2:
                timeInfo.class3Start = times[0];
                timeInfo.class3End = times[1];
                startTime3.setText(times[0]);
                endTime3.setText(times[1]);
                break;
            case 3:
                timeInfo.class4Start = times[0];
                timeInfo.class4End = times[1];
                startTime4.setText(times[0]);
                endTime4.setText(times[1]);
                break;
            case 4:
                timeInfo.class5Start = times[0];
                timeInfo.class5End = times[1];
                startTime5.setText(times[0]);
                endTime5.setText(times[1]);
                break;
            case 5:
                timeInfo.class6Start = times[0];
                timeInfo.class6End = times[1];
                startTime6.setText(times[0]);
                endTime6.setText(times[1]);
                break;
            case 6:
                timeInfo.class7Start = times[0];
                timeInfo.class7End = times[1];
                startTime7.setText(times[0]);
                endTime7.setText(times[1]);
                break;
            case 7:
                timeInfo.class8Start = times[0];
                timeInfo.class8End = times[1];
                startTime8.setText(times[0]);
                endTime8.setText(times[1]);
                break;

        }
    }


    @Override
    public void onClick(View v) {
        if (timeInfo == null)
            return;
        switch (v.getId()) {
            case R.id.ll_time1:
                wheelViewUtils.ShowClassTime(timeInfo.class1Start, timeInfo.class1End);
                indexStart = 0;
                break;
            case R.id.ll_time2:
                wheelViewUtils.ShowClassTime(timeInfo.class2Start, timeInfo.class2End);
                indexStart = 1;
                break;
            case R.id.ll_time3:
                wheelViewUtils.ShowClassTime(timeInfo.class3Start, timeInfo.class3End);
                indexStart = 2;
                break;
            case R.id.ll_time4:
                wheelViewUtils.ShowClassTime(timeInfo.class4Start, timeInfo.class4End);
                indexStart = 3;
                break;
            case R.id.ll_time5:
                wheelViewUtils.ShowClassTime(timeInfo.class5Start, timeInfo.class5End);
                indexStart = 4;
                break;
            case R.id.ll_time6:
                wheelViewUtils.ShowClassTime(timeInfo.class6Start, timeInfo.class6End);
                indexStart = 5;
                break;
            case R.id.ll_time7:
                wheelViewUtils.ShowClassTime(timeInfo.class7Start, timeInfo.class7End);
                indexStart = 6;
                break;
            case R.id.ll_time8:
                wheelViewUtils.ShowClassTime(timeInfo.class8Start, timeInfo.class8End);
                indexStart = 7;
                break;
            case R.id.schedule_commit:
                commitTime();
                break;
            case R.id.schedule_back:
                finish();
                break;
        }
    }

    /**
     * 提交，需要补满秒数
     */
    private void commitTime() {
        if (timeInfo == null)
            return;

        SchoolTimetableInfo.SchoolHourMapBean oldTimeInfo = new SchoolTimetableInfo.SchoolHourMapBean();
        oldTimeInfo.class1Start = startTime1.getText() + ":00";
        oldTimeInfo.class2Start = startTime2.getText() + ":00";
        oldTimeInfo.class3Start = startTime3.getText() + ":00";
        oldTimeInfo.class4Start = startTime4.getText() + ":00";
        oldTimeInfo.class5Start = startTime5.getText() + ":00";
        oldTimeInfo.class6Start = startTime6.getText() + ":00";
        oldTimeInfo.class7Start = startTime7.getText() + ":00";
        oldTimeInfo.class8Start = startTime8.getText() + ":00";
        oldTimeInfo.class1End = endTime1.getText() + ":00";
        oldTimeInfo.class2End = endTime2.getText() + ":00";
        oldTimeInfo.class3End = endTime3.getText() + ":00";
        oldTimeInfo.class4End = endTime4.getText() + ":00";
        oldTimeInfo.class5End = endTime5.getText() + ":00";
        oldTimeInfo.class6End = endTime6.getText() + ":00";
        oldTimeInfo.class7End = endTime7.getText() + ":00";
        oldTimeInfo.class8End = endTime8.getText() + ":00";
        Gson gson = new Gson();
        request.setDeviceSchoolHours(1, 0, 0, null, gson.toJson(oldTimeInfo));
    }

    /**
     * 请求回调结果处理
     */
    @Override
    public void callBackData(String data, int position) {
        Intent intent = new Intent();
        intent.putExtra(TIMEINFO, timeInfo);
        setResult(1, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wheelViewUtils = null;
        timeInfo = null;
        startTime1 = null;
        startTime2 = null;
        startTime3 = null;
        startTime4 = null;
        startTime5 = null;
        startTime6 = null;
        startTime7 = null;
        startTime8 = null;
        endTime1 = null;
        endTime2 = null;
        endTime3 = null;
        endTime4 = null;
        endTime5 = null;
        endTime6 = null;
        endTime7 = null;
        endTime8 = null;
        mContext = null;
        mDeviceSn = null;
        request = null;
        timeInfo = null;
    }
}
