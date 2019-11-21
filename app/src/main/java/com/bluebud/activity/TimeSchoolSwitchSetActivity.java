package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.TimeSwitchCourseInfo;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
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

public class TimeSchoolSwitchSetActivity extends BaseActivity implements
        OnClickListener, OnWheelChangedListener1, OnProgressDialogClickListener {

    private WheelView1 wvHoursAmOn;
    private WheelView1 wvMinsAmOn;
    private WheelView1 wvHoursAmOff;
    private WheelView1 wvMinsAmOff;
    private WheelView1 wvHoursPmOn;
    private WheelView1 wvMinsPmOn;
    private WheelView1 wvHoursPmOff;
    private WheelView1 wvMinsPmOff;

    private TextView tvWeek;
    private TextView tvWeek1;
    private TextView tvWeek2;
    private TextView tvWeek3;
    private TextView tvWeek4;
    private TextView tvWeek5;
    private TextView tvWeek6;
    private TextView tvWeek7;

    private String sHoursAmOn = "";
    private String sMinsAmOn = "";
    private String sHoursAmOff = "";
    private String sMinsAmOff = "";

    private String sHoursPmOn = "";
    private String sMinsPmOn = "";
    private String sHoursPmOff = "";
    private String sMinsPmOff = "";

    private String[] arrWeeks = {"0", "0", "0", "0", "0", "0", "0"};

    private Tracker mTracker;
    private String sTrackerNo = "";

    private TimeSwitchCourseInfo timeSwitchCourseInfo;

    private RequestHandle requestHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_time_school_switch_set);

        mTracker = UserUtil.getCurrentTracker(this);
        sTrackerNo = mTracker.device_sn;

        timeSwitchCourseInfo = new TimeSwitchCourseInfo();

        init();
    }

    public void init() {
        setBaseTitleText(R.string.class_disabled);
        getBaseTitleLeftBack().setOnClickListener(this);
        setBaseTitleRightTextVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.save);
        getBaseTitleRightText().setOnClickListener(this);

        timeSwitchCourseInfo = (TimeSwitchCourseInfo) getIntent()
                .getSerializableExtra("TIME_SWITCH_COURSE");

        sHoursAmOn = timeSwitchCourseInfo.amstarttime.split(":")[0];
        sMinsAmOn = timeSwitchCourseInfo.amstarttime.split(":")[1];
        sHoursAmOff = timeSwitchCourseInfo.amendtime.split(":")[0];
        sMinsAmOff = timeSwitchCourseInfo.amendtime.split(":")[1];
        sHoursPmOn = timeSwitchCourseInfo.tmstarttime.split(":")[0];
        sMinsPmOn = timeSwitchCourseInfo.tmstarttime.split(":")[1];
        sHoursPmOff = timeSwitchCourseInfo.tmendtime.split(":")[0];
        sMinsPmOff = timeSwitchCourseInfo.tmendtime.split(":")[1];

        wvHoursAmOn = (WheelView1) findViewById(R.id.hour_am_on);
        wvMinsAmOn = (WheelView1) findViewById(R.id.mins_am_on);
        wvHoursAmOff = (WheelView1) findViewById(R.id.hour_am_off);
        wvMinsAmOff = (WheelView1) findViewById(R.id.mins_am_off);
        wvHoursPmOn = (WheelView1) findViewById(R.id.hour_pm_on);
        wvMinsPmOn = (WheelView1) findViewById(R.id.mins_pm_on);
        wvHoursPmOff = (WheelView1) findViewById(R.id.hour_pm_off);
        wvMinsPmOff = (WheelView1) findViewById(R.id.mins_pm_off);

        tvWeek = (TextView) findViewById(R.id.tv_week);
        tvWeek1 = (TextView) findViewById(R.id.tv_week_1);
        tvWeek2 = (TextView) findViewById(R.id.tv_week_2);
        tvWeek3 = (TextView) findViewById(R.id.tv_week_3);
        tvWeek4 = (TextView) findViewById(R.id.tv_week_4);
        tvWeek5 = (TextView) findViewById(R.id.tv_week_5);
        tvWeek6 = (TextView) findViewById(R.id.tv_week_6);
        tvWeek7 = (TextView) findViewById(R.id.tv_week_7);

        tvWeek1.setOnClickListener(this);
        tvWeek2.setOnClickListener(this);
        tvWeek3.setOnClickListener(this);
        tvWeek4.setOnClickListener(this);
        tvWeek5.setOnClickListener(this);
        tvWeek6.setOnClickListener(this);
        tvWeek7.setOnClickListener(this);

        initTime();

        if (!Utils.isChina()) {
            tvWeek.setVisibility(View.GONE);
        }

        String[] arrRepeat = timeSwitchCourseInfo.repeatday.split(",");
        for (int i = 0; i < arrRepeat.length; i++) {
            switch (Integer.parseInt(arrRepeat[i])) {
                case 1:
                    setTextViewBackground(0, tvWeek1);
                    break;
                case 2:
                    setTextViewBackground(1, tvWeek2);
                    break;
                case 3:
                    setTextViewBackground(2, tvWeek3);
                    break;
                case 4:
                    setTextViewBackground(3, tvWeek4);
                    break;
                case 5:
                    setTextViewBackground(4, tvWeek5);
                    break;
                case 6:
                    setTextViewBackground(5, tvWeek6);
                    break;
                case 7:
                    setTextViewBackground(6, tvWeek7);
                    break;
            }
        }
    }

    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Utils.hourString2Date(timeSwitchCourseInfo.amendtime));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 时
        wvHoursAmOff.setAdapter(new NumericWheelAdapter(0, 12));
        wvHoursAmOff.setCyclic(true);
        wvHoursAmOff.setLabel(getString(R.string.hour));// 添加文字
        wvHoursAmOff.setCurrentItem(hour);

        // 分
        wvMinsAmOff.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wvMinsAmOff.setCyclic(true);
        wvMinsAmOff.setLabel(getString(R.string.minute));// 添加文字
        wvMinsAmOff.setCurrentItem(minute);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(Utils
                .hourString2Date(timeSwitchCourseInfo.amstarttime));
        int hour1 = calendar1.get(Calendar.HOUR_OF_DAY);
        int minute1 = calendar1.get(Calendar.MINUTE);

        // 时
        wvHoursAmOn.setAdapter(new NumericWheelAdapter(0, 12));
        wvHoursAmOn.setCyclic(true);
        wvHoursAmOn.setLabel(getString(R.string.hour));// 添加文字
        wvHoursAmOn.setCurrentItem(hour1);

        // 分
        wvMinsAmOn.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wvMinsAmOn.setCyclic(true);
        wvMinsAmOn.setLabel(getString(R.string.minute));// 添加文字
        wvMinsAmOn.setCurrentItem(minute1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2
                .setTime(Utils.hourString2Date(timeSwitchCourseInfo.tmendtime));
        int hour2 = calendar2.get(Calendar.HOUR_OF_DAY) - 13;
        int minute2 = calendar2.get(Calendar.MINUTE);

        // 时
        wvHoursPmOff.setAdapter(new NumericWheelAdapter(13, 23));
        wvHoursPmOff.setCyclic(true);
        wvHoursPmOff.setLabel(getString(R.string.hour));// 添加文字
        wvHoursPmOff.setCurrentItem(hour2);

        // 分
        wvMinsPmOff.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wvMinsPmOff.setCyclic(true);
        wvMinsPmOff.setLabel(getString(R.string.minute));// 添加文字
        wvMinsPmOff.setCurrentItem(minute2);

        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(Utils
                .hourString2Date(timeSwitchCourseInfo.tmstarttime));
        int hour3 = calendar3.get(Calendar.HOUR_OF_DAY) - 13;
        int minute3 = calendar3.get(Calendar.MINUTE);

        // 时
        wvHoursPmOn.setAdapter(new NumericWheelAdapter(13, 23));
        wvHoursPmOn.setCyclic(true);
        wvHoursPmOn.setLabel(getString(R.string.hour));// 添加文字
        wvHoursPmOn.setCurrentItem(hour3);

        // 分
        wvMinsPmOn.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wvMinsPmOn.setCyclic(true);
        wvMinsPmOn.setLabel(getString(R.string.minute));// 添加文字
        wvMinsPmOn.setCurrentItem(minute3);

        wvHoursAmOff.addChangingListener(this);
        wvMinsAmOff.addChangingListener(this);
        wvHoursAmOn.addChangingListener(this);
        wvMinsAmOn.addChangingListener(this);
        wvHoursPmOff.addChangingListener(this);
        wvMinsPmOff.addChangingListener(this);
        wvHoursPmOn.addChangingListener(this);
        wvMinsPmOn.addChangingListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.tv_week_1:
                setTextViewBackground(0, tvWeek1);
                break;
            case R.id.tv_week_2:
                setTextViewBackground(1, tvWeek2);
                break;
            case R.id.tv_week_3:
                setTextViewBackground(2, tvWeek3);
                break;
            case R.id.tv_week_4:
                setTextViewBackground(3, tvWeek4);
                break;
            case R.id.tv_week_5:
                setTextViewBackground(4, tvWeek5);
                break;
            case R.id.tv_week_6:
                setTextViewBackground(5, tvWeek6);
                break;
            case R.id.tv_week_7:
                setTextViewBackground(6, tvWeek7);
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
        if (wvHoursAmOff.equals(wheel)) {
            sHoursAmOff = String.format("%02d", newValue);
        } else if (wvMinsAmOff.equals(wheel)) {
            sMinsAmOff = String.format("%02d", newValue);
        } else if (wvHoursAmOn.equals(wheel)) {
            sHoursAmOn = String.format("%02d", newValue);
        } else if (wvMinsAmOn.equals(wheel)) {
            sMinsAmOn = String.format("%02d", newValue);
        } else if (wvHoursPmOff.equals(wheel)) {
            sHoursPmOff = String.format("%02d", newValue + 13);
        } else if (wvMinsPmOff.equals(wheel)) {
            sMinsPmOff = String.format("%02d", newValue);
        } else if (wvHoursPmOn.equals(wheel)) {
            sHoursPmOn = String.format("%02d", newValue + 13);
        } else if (wvMinsPmOn.equals(wheel)) {
            sMinsPmOn = String.format("%02d", newValue);
        }

        LogUtil.i(sHoursAmOn + ":" + sMinsAmOn + " " + sHoursAmOff + ":"
                + sMinsAmOff);
        LogUtil.i(sHoursPmOn + ":" + sMinsPmOn + " " + sHoursPmOff + ":"
                + sMinsPmOff);
    }

    private void setTextViewBackground(int position, TextView tv) {
        if ("0".equals(arrWeeks[position])) {
            arrWeeks[position] = "1";
            tv.setBackgroundResource(R.drawable.bg_week1_pressed);
            tv.setTextColor(getResources().getColor(R.color.white));
        } else {
            arrWeeks[position] = "0";
            tv.setBackgroundResource(R.drawable.bg_week1_nor);
            tv.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void confirm() {
        timeSwitchCourseInfo.amstarttime = sHoursAmOn + ":" + sMinsAmOn;
        timeSwitchCourseInfo.amendtime = sHoursAmOff + ":" + sMinsAmOff;
        timeSwitchCourseInfo.tmstarttime = sHoursPmOn + ":" + sMinsPmOn;
        timeSwitchCourseInfo.tmendtime = sHoursPmOff + ":" + sMinsPmOff;

        timeSwitchCourseInfo.repeatday = Utils.arrDayToString(arrWeeks);

        LogUtil.i(timeSwitchCourseInfo.amstarttime + " "
                + timeSwitchCourseInfo.amendtime);
        LogUtil.i(timeSwitchCourseInfo.tmstarttime + " "
                + timeSwitchCourseInfo.tmendtime);

        int timeAM = Utils.compareTime(timeSwitchCourseInfo.amstarttime,
                timeSwitchCourseInfo.amendtime);
        int timePM = Utils.compareTime(timeSwitchCourseInfo.tmstarttime,
                timeSwitchCourseInfo.tmendtime);
        if (timeAM > 0) {
            ToastUtil.show(this, R.string.time_error);
            return;
        }
        if (timePM > 0) {
            ToastUtil.show(this, R.string.time_error);
            return;
        }

        if (Utils.isEmpty(timeSwitchCourseInfo.repeatday)) {
            ToastUtil.show(this, R.string.week_empty);
            return;
        }

        saveTimeCourse();
    }

    private void saveTimeCourse() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.saveTimeCourse(sTrackerNo, 1,
                timeSwitchCourseInfo.amstarttime,
                timeSwitchCourseInfo.amendtime,
                timeSwitchCourseInfo.tmstarttime,
                timeSwitchCourseInfo.tmendtime, Utils.arrDayToString(arrWeeks));

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TimeSchoolSwitchSetActivity.this, null,
                                TimeSchoolSwitchSetActivity.this);
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
                            intent.putExtra("TIME_SWITCH_COURSE",
                                    timeSwitchCourseInfo);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        ToastUtil.show(TimeSchoolSwitchSetActivity.this,
                                obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeSchoolSwitchSetActivity.this,
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
