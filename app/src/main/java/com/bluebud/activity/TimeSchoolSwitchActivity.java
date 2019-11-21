package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.TimeSwitchCourseInfo;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.PopupWindowCheckBoxUtils;
import com.bluebud.utils.PopupWindowCheckBoxUtils.OnCheckBoxTime;
import com.bluebud.utils.PopupWindowWheelViewUtils1;
import com.bluebud.utils.PopupWindowWheelViewUtils1.OnWheeClicked;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.Calendar;

//上课禁用
public class TimeSchoolSwitchActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener, OnWheeClicked, OnCheckBoxTime {
    private CheckBox sbSwitch;
    //    private LinearLayout llTime;
    private TextView tvTimeAmOn;
    private TextView tvTimeAmOff;
    private TextView tvTimePmOn;
    private TextView tvTimePmOff;
    private TextView tvRepeatDay;

    private boolean bEnable = false;
    private boolean bFirst = true;
    private boolean isSubmit = true;

    private Tracker mTracker;
    private String sTrackerNo = "";

    private TimeSwitchCourseInfo timeSwitchCourseInfo;

    private RequestHandle requestHandle;
    private RelativeLayout rlOn;
    private RelativeLayout rlPm;
    private RelativeLayout rlRepeat;
    private PopupWindowWheelViewUtils1 wheelViewUtils1;
    private PopupWindowCheckBoxUtils checkBoxUtils;
    private int protocol_type = 0;
    private String product_type = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_time_school_switch);
        mTracker = UserUtil.getCurrentTracker(this);
        if (mTracker != null) {
            sTrackerNo = mTracker.device_sn;
            protocol_type = mTracker.protocol_type;
            product_type = mTracker.product_type;
        }
        timeSwitchCourseInfo = new TimeSwitchCourseInfo();
        wheelViewUtils1 = new PopupWindowWheelViewUtils1(TimeSchoolSwitchActivity.this, this);
        checkBoxUtils = new PopupWindowCheckBoxUtils(this, this);
        init();
        getTimeCourse();
    }

    public void init() {
        TextView mtext = (TextView) findViewById(R.id.tv_switch_title);
//        if ("23".equals(product_type)) {//判断HT-772设备更改为免打扰
//            setBaseTitleText(R.string.no_disturbing);
//            mtext.setText(R.string.no_disturbing);
//        }else {
//        setBaseTitleText(R.string.class_disabled);
//        mtext.setText(R.string.class_disabled);
//        }
        if ("15".equals(product_type)) {//770s是免打扰
            setBaseTitleText(R.string.no_disturbing);
            mtext.setText(R.string.no_disturbing);
        } else {
            setBaseTitleText(R.string.class_disabled);
            mtext.setText(R.string.class_disabled);
        }
        setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);

        sbSwitch = (CheckBox) findViewById(R.id.switch_button);
//        llTime = (LinearLayout) findViewById(R.id.ll_time_set);
        rlOn = (RelativeLayout) findViewById(R.id.rl_am);
        rlPm = (RelativeLayout) findViewById(R.id.rl_pm);
        rlRepeat = (RelativeLayout) findViewById(R.id.rl_repeat);
        tvTimeAmOn = (TextView) findViewById(R.id.tv_time_am_on);
        tvTimeAmOff = (TextView) findViewById(R.id.tv_time_am_off);
        tvTimePmOn = (TextView) findViewById(R.id.tv_time_pm_on);
        tvTimePmOff = (TextView) findViewById(R.id.tv_time_pm_off);
        tvRepeatDay = (TextView) findViewById(R.id.tv_time_repeat);
        ImageView ivNext = (ImageView) findViewById(R.id.iv_next);
        if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {//770,771,772,790
            ivNext.setVisibility(View.INVISIBLE);
        } else {
            ivNext.setVisibility(View.VISIBLE);
        }
        rlOn.setOnClickListener(this);
        rlPm.setOnClickListener(this);
        rlRepeat.setOnClickListener(this);
        sbSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean flag) {
                // TODO Auto-generated method stub
//				if(bFirst){
//					bFirst = false;
//					return;
//				}
//				if(Utils.isOperate(TimeSchoolSwitchActivity.this, mTracker)){
                if (flag) {
                    rlOn.setClickable(true);
                    rlOn.setEnabled(true);
                    rlOn.setFocusable(true);
                    rlPm.setClickable(true);
                    rlPm.setEnabled(true);
                    rlPm.setFocusable(true);
                    rlRepeat.setClickable(true);
                    rlRepeat.setEnabled(true);
                    rlRepeat.setFocusable(true);

                } else {
                    rlOn.setClickable(false);
                    rlOn.setEnabled(false);
                    rlOn.setFocusable(false);
                    rlPm.setClickable(false);
                    rlPm.setEnabled(false);
                    rlPm.setFocusable(false);
                    rlRepeat.setClickable(false);
                    rlRepeat.setEnabled(false);
                    rlRepeat.setFocusable(false);
                }

                bEnable = flag;
                if (isSubmit) {
                    if (bFirst) {
                        bFirst = false;
                    } else {
                        if (Utils.isSuperUser(mTracker, TimeSchoolSwitchActivity.this)) {
                            confirm(true);
                        }
                    }
                } else {
                    isSubmit = true;
                }
            }
//			}
        });
        sbSwitch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean checked = sbSwitch.isChecked();
                if (!Utils.isSuperUser(mTracker, TimeSchoolSwitchActivity.this)) {
                    if (checked) {
                        sbSwitch.setChecked(false);
                    } else {
                        sbSwitch.setChecked(true);
                    }
                }
            }
        });

        tvTimeAmOn.setText(timeSwitchCourseInfo.amstarttime);
        tvTimeAmOff.setText(timeSwitchCourseInfo.amendtime);
        tvTimePmOn.setText(timeSwitchCourseInfo.tmstarttime);
        tvTimePmOff.setText(timeSwitchCourseInfo.tmendtime);
        tvRepeatDay.setText(Utils.strDayToWeek(this, timeSwitchCourseInfo.repeatday));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.rl_am://上午
                if (Utils.isSuperUser(mTracker, TimeSchoolSwitchActivity.this)) {
                    Calendar calendar1 = Calendar.getInstance();
                    if (Utils.isEmpty(timeSwitchCourseInfo.amstarttime) && Utils.isEmpty(timeSwitchCourseInfo.amendtime)) {
                        wheelViewUtils1.ShowTime(Utils.getDate(calendar1), Utils.getDate(calendar1), getString(R.string.am), true, -1);
                    } else if (Utils.isEmpty(timeSwitchCourseInfo.amstarttime) && !Utils.isEmpty(timeSwitchCourseInfo.amendtime)) {
                        wheelViewUtils1.ShowTime(Utils.getDate(calendar1), timeSwitchCourseInfo.amendtime, getString(R.string.am), true, -1);
                    } else if (!Utils.isEmpty(timeSwitchCourseInfo.amstarttime) && Utils.isEmpty(timeSwitchCourseInfo.amendtime)) {
                        wheelViewUtils1.ShowTime(timeSwitchCourseInfo.amstarttime, Utils.getDate(calendar1), getString(R.string.am), true, -1);
                    } else {
                        wheelViewUtils1.ShowTime(timeSwitchCourseInfo.amstarttime, timeSwitchCourseInfo.amendtime, getString(R.string.am), true, -1);
                    }
                }


                break;
            case R.id.rl_pm://下午
                if (Utils.isSuperUser(mTracker, TimeSchoolSwitchActivity.this)) {
                    Calendar calendar = Calendar.getInstance();
                    if (Utils.isEmpty(timeSwitchCourseInfo.tmstarttime) && Utils.isEmpty(timeSwitchCourseInfo.tmendtime)) {
                        wheelViewUtils1.ShowTime(Utils.getDate(calendar), Utils.getDate(calendar), getString(R.string.pm), false, -1);
                    } else if (Utils.isEmpty(timeSwitchCourseInfo.tmstarttime) && !Utils.isEmpty(timeSwitchCourseInfo.tmendtime)) {
                        wheelViewUtils1.ShowTime(Utils.getDate(calendar), timeSwitchCourseInfo.amendtime, getString(R.string.pm), false, -1);
                    } else if (!Utils.isEmpty(timeSwitchCourseInfo.tmstarttime) && Utils.isEmpty(timeSwitchCourseInfo.tmendtime)) {
                        wheelViewUtils1.ShowTime(timeSwitchCourseInfo.tmstarttime, Utils.getDate(calendar), getString(R.string.pm), false, -1);
                    } else {
                        wheelViewUtils1.ShowTime(timeSwitchCourseInfo.tmstarttime, timeSwitchCourseInfo.tmendtime, getString(R.string.pm), false, -1);
                    }
                }
                break;
            case R.id.rl_repeat://重复
                if (!(protocol_type == 5 || protocol_type == 6 || protocol_type == 7)) {//只有720时才可点
                    if (Utils.isSuperUser(mTracker, TimeSchoolSwitchActivity.this)) {
                        LogUtil.i("重复时间：" + timeSwitchCourseInfo.repeatday);
                        if (Utils.isEmpty(timeSwitchCourseInfo.repeatday)) {
                            checkBoxUtils.ShowCheckBox(getString(R.string.repeat), "1");
                        } else {
                            checkBoxUtils.ShowCheckBox(getString(R.string.repeat), timeSwitchCourseInfo.repeatday);
                        }
                    }
                }

                break;

            case R.id.ll_time_set:
                Intent intent = new Intent(TimeSchoolSwitchActivity.this,
                        TimeSchoolSwitchSetActivity.class);
                intent.putExtra("TIME_SWITCH_COURSE", timeSwitchCourseInfo);
                startActivityForResult(intent, 1);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            timeSwitchCourseInfo = (TimeSwitchCourseInfo) data
                    .getSerializableExtra("TIME_SWITCH_COURSE");

            tvTimeAmOn.setText(timeSwitchCourseInfo.amstarttime);
            tvTimeAmOff.setText(timeSwitchCourseInfo.amendtime);
            tvTimePmOn.setText(timeSwitchCourseInfo.tmstarttime);
            tvTimePmOff.setText(timeSwitchCourseInfo.tmendtime);

            tvRepeatDay.setText(Utils.strDayToWeek(
                    TimeSchoolSwitchActivity.this,
                    timeSwitchCourseInfo.repeatday));
        }
    }

    //从服务器得到上课禁用时间
    private void getTimeCourse() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.getTimeCourse(sTrackerNo);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TimeSchoolSwitchActivity.this, null,
                                TimeSchoolSwitchActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {
                            timeSwitchCourseInfo = GsonParse.getTimeSwitchCourse(new String(response));

                            if ("1".equals(timeSwitchCourseInfo.enable)) {
                                bEnable = true;

                                rlOn.setClickable(true);
                                rlOn.setEnabled(true);
                                rlOn.setFocusable(true);
                                rlPm.setClickable(true);
                                rlPm.setEnabled(true);
                                rlPm.setFocusable(true);
                                rlRepeat.setClickable(true);
                                rlRepeat.setEnabled(true);
                                rlRepeat.setFocusable(true);
                            } else {
                                bEnable = false;
                                rlOn.setClickable(false);
                                rlOn.setEnabled(false);
                                rlOn.setFocusable(false);
                                rlPm.setClickable(false);
                                rlPm.setEnabled(false);
                                rlPm.setFocusable(false);
                                rlRepeat.setClickable(false);
                                rlRepeat.setEnabled(false);
                                rlRepeat.setFocusable(false);
                            }

                            sbSwitch.setChecked(bEnable);

                            if (!bEnable) {
                                bFirst = false;
                            }

                            tvTimeAmOn.setText(timeSwitchCourseInfo.amstarttime);
                            tvTimeAmOff.setText(timeSwitchCourseInfo.amendtime);
                            tvTimePmOn
                                    .setText(timeSwitchCourseInfo.tmstarttime);
                            tvTimePmOff.setText(timeSwitchCourseInfo.tmendtime);

                            tvRepeatDay.setText(Utils.strDayToWeek(
                                    TimeSchoolSwitchActivity.this,
                                    timeSwitchCourseInfo.repeatday));
                        } else {
                            ToastUtil.show(TimeSchoolSwitchActivity.this,
                                    obj.what);
                        }
                        isSubmit = true;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeSchoolSwitchActivity.this,
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

    //设置上课禁用开机接口
    private void saveTimeCourse(final boolean isSuswitch) {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.saveTimeCourse(sTrackerNo,
                bEnable ? 1 : 0, timeSwitchCourseInfo.amstarttime,
                timeSwitchCourseInfo.amendtime,
                timeSwitchCourseInfo.tmstarttime,
                timeSwitchCourseInfo.tmendtime, timeSwitchCourseInfo.repeatday);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TimeSchoolSwitchActivity.this, null,
                                TimeSchoolSwitchActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {
                            // mTracker.cdt_enable = bEnable ? 1 : 0;
                            UserUtil.saveCurTrackerChange(
                                    TimeSchoolSwitchActivity.this, mTracker);

                            Intent intent = new Intent(
                                    Constants.ACTION_TIME_SWITCH);
                            sendBroadcast(intent);
                            ToastUtil.show(TimeSchoolSwitchActivity.this, obj.what);
                            isSubmit = true;
                        } else {
                            ToastUtil.show(TimeSchoolSwitchActivity.this, obj.what);
                            if (isSuswitch) {
                                isSubmit = false;
                                sbSwitch.setChecked(!bEnable);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeSchoolSwitchActivity.this,
                                R.string.net_exception);
                        if (isSuswitch) {
                            isSubmit = false;
                            sbSwitch.setChecked(!bEnable);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    @Override
    public void getWheelAmTime(String sOnTime, String sOffTime, int position) {
        LogUtil.i("getWheelAmTime");
        tvTimeAmOn.setText(sOnTime);
        tvTimeAmOff.setText(sOffTime);
        timeSwitchCourseInfo.amstarttime = sOnTime;
        timeSwitchCourseInfo.amendtime = sOffTime;
        confirm(false);

    }

    @Override
    public void getWheelPmTime(String sOnTime, String sOffTime, int position) {
        LogUtil.i("getWheelPmTime");
        tvTimePmOn.setText(sOnTime);
        tvTimePmOff.setText(sOffTime);
        timeSwitchCourseInfo.tmstarttime = sOnTime;
        timeSwitchCourseInfo.tmendtime = sOffTime;
        confirm(false);
    }

    private void confirm(boolean isSuswitch) {


        //timeSwitchCourseInfo.repeatday = Utils.arrDayToString(arrWeeks);

        LogUtil.i(timeSwitchCourseInfo.amstarttime + " "
                + timeSwitchCourseInfo.amendtime);
        LogUtil.i(timeSwitchCourseInfo.tmstarttime + " "
                + timeSwitchCourseInfo.tmendtime);

        int timeAM = Utils.compareTime(timeSwitchCourseInfo.amstarttime,
                timeSwitchCourseInfo.amendtime);
        int timePM = Utils.compareTime(timeSwitchCourseInfo.tmstarttime,
                timeSwitchCourseInfo.tmendtime);
//		int timeAmendMax = Utils.compareTime(timeSwitchCourseInfo.amendtime,
//				"12:01");
//		int timeAmstartMax = Utils.compareTime(timeSwitchCourseInfo.amstarttime,
//				"12:01");
//		int timePmendMax = Utils.compareTime(timeSwitchCourseInfo.tmendtime,
//				"12:00");
//		int timePmstartMax = Utils.compareTime(timeSwitchCourseInfo.tmstarttime,
//				"12:00");

//		if (timeAmstartMax>0||timeAmendMax>0) {
//			ToastUtil.show(this, R.string.am_time_error);
//			return;
//		}
//		if (timePmstartMax<0||timePmendMax<0) {
//			ToastUtil.show(this, R.string.pm_time_error);
//			return;
//		}
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

        saveTimeCourse(isSuswitch);
    }


    private void saveTimeCourse1() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.saveTimeCourse(sTrackerNo, 1,
                timeSwitchCourseInfo.amstarttime,
                timeSwitchCourseInfo.amendtime,
                timeSwitchCourseInfo.tmstarttime,
                timeSwitchCourseInfo.tmendtime, timeSwitchCourseInfo.repeatday);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TimeSchoolSwitchActivity.this, null,
                                TimeSchoolSwitchActivity.this);
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
                            //finish();
                        }
                        ToastUtil.show(TimeSchoolSwitchActivity.this,
                                obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeSchoolSwitchActivity.this,
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
    public void getCheckBoxTime(String Time) {
        timeSwitchCourseInfo.repeatday = Time;
        tvRepeatDay.setText(Utils.strDayToWeek(this,
                timeSwitchCourseInfo.repeatday));
        confirm(false);
    }


}
