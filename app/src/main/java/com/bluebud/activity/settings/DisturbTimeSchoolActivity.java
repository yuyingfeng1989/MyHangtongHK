package com.bluebud.activity.settings;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.TimeSwitchCourseInfo;
import com.bluebud.info.Tracker;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.PopupWindowCheckBoxUtils;
import com.bluebud.utils.PopupWindowWheelViewUtils1;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/11/22 0022.
 */

public class DisturbTimeSchoolActivity extends BaseActivity implements
        View.OnClickListener, ProgressDialogUtil.OnProgressDialogClickListener,
        PopupWindowWheelViewUtils1.OnWheeClicked, PopupWindowCheckBoxUtils.OnCheckBoxTime,
        CompoundButton.OnCheckedChangeListener, View.OnTouchListener {
    private String sUserName = "";
    private String sTrackerNo = "";
    private boolean ispoint = false;

    private TimeSwitchCourseInfo timeSwitchCourseInfo;
    private PopupWindowWheelViewUtils1 wheelViewUtils1;

    private TextView disturb_tv_on1;
    private TextView disturb_tv_on2;
    private TextView disturb_tv_on3;
    private TextView disturb_tv_off1;
    private TextView disturb_tv_off2;
    private TextView disturb_tv_off3;
    private TextView tv_time_repeat;
    private CheckBox switch_button1;
    private CheckBox switch_button2;
    private CheckBox switch_button3;
    private String[] enables = {"0", "0", "0"};
    private DisturbTimeSchoolActivity mContext;
    private Tracker mTracker;
    private String repeatday = "1,2,3,4,5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.disturbtimeschool_activity);
        WeakReference<DisturbTimeSchoolActivity> weakReference = new WeakReference<DisturbTimeSchoolActivity>(DisturbTimeSchoolActivity.this);
        mContext = weakReference.get();
        mTracker = UserUtil.getCurrentTracker(mContext);
        if (mTracker != null) {
            sTrackerNo = mTracker.device_sn;
            sUserName = UserSP.getInstance().getUserName(mContext);
        }
        timeSwitchCourseInfo = new TimeSwitchCourseInfo();
        wheelViewUtils1 = new PopupWindowWheelViewUtils1(mContext, mContext);
        init();
        getTimeCourse();//初始化开关值
    }

    /**
     * 初始化控件
     */
    public void init() {
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleText(R.string.no_disturbing);//免打扰
        //开关
        switch_button1 = (CheckBox) findViewById(R.id.switch_button1);
        switch_button2 = (CheckBox) findViewById(R.id.switch_button2);
        switch_button3 = (CheckBox) findViewById(R.id.switch_button3);
        //开始时间
        disturb_tv_on1 = (TextView) findViewById(R.id.disturb_tv_on1);
        disturb_tv_on2 = (TextView) findViewById(R.id.disturb_tv_on2);
        disturb_tv_on3 = (TextView) findViewById(R.id.disturb_tv_on3);
        //结束时间
        disturb_tv_off1 = (TextView) findViewById(R.id.disturb_tv_off1);
        disturb_tv_off2 = (TextView) findViewById(R.id.disturb_tv_off2);
        disturb_tv_off3 = (TextView) findViewById(R.id.disturb_tv_off3);
        //重复
        tv_time_repeat = (TextView) findViewById(R.id.tv_time_repeat);
        initListener();
    }

    /**
     * 注册监听
     */
    private void initListener() {
        getBaseTitleLeftBack().setOnClickListener(mContext);
        findViewById(R.id.ll_disturb1).setOnClickListener(mContext);
        findViewById(R.id.ll_disturb2).setOnClickListener(mContext);
        findViewById(R.id.ll_disturb3).setOnClickListener(mContext);
        findViewById(R.id.rl_disturb_repeat).setOnClickListener(mContext);
        switch_button1.setOnCheckedChangeListener(mContext);
        switch_button2.setOnCheckedChangeListener(mContext);
        switch_button3.setOnCheckedChangeListener(mContext);
        switch_button1.setOnTouchListener(mContext);
        switch_button2.setOnTouchListener(mContext);
        switch_button3.setOnTouchListener(mContext);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.ll_disturb1://
                if (Utils.isSuperUser(mTracker, mContext))
                    setTimers(1);//设置时间

                break;
            case R.id.ll_disturb2://
                if (Utils.isSuperUser(mTracker, mContext))
                    setTimers(2);
                break;
            case R.id.ll_disturb3:
                if (Utils.isSuperUser(mTracker, mContext))
                    setTimers(3);
                break;

//            case R.id.rl_disturb_repeat://重复
//                LogUtil.i("重复时间：" + timeSwitchCourseInfo.repeatday);
//                if (Utils.isSuperUser(mTracker, mContext))
//                    return;
//                if (Utils.isEmpty(timeSwitchCourseInfo.repeatday))
//                    checkBoxUtils.ShowCheckBox(getString(R.string.repeat), "1");
//                else
//                    checkBoxUtils.ShowCheckBox(getString(R.string.repeat), timeSwitchCourseInfo.repeatday);
//                break;
        }
    }

    /**
     * 时间设置
     */
    private void setTimers(int position) {
        Calendar calendar = Calendar.getInstance();
        if (Utils.isEmpty(timeSwitchCourseInfo.tmstarttime) && Utils.isEmpty(timeSwitchCourseInfo.tmendtime)) {
            wheelViewUtils1.ShowTime(Utils.getDate(calendar), Utils.getDate(calendar), "", true, position);
        } else if (Utils.isEmpty(timeSwitchCourseInfo.tmstarttime) && !Utils.isEmpty(timeSwitchCourseInfo.tmendtime)) {
            wheelViewUtils1.ShowTime(Utils.getDate(calendar), timeSwitchCourseInfo.amendtime, "", true, position);
        } else if (!Utils.isEmpty(timeSwitchCourseInfo.tmstarttime) && Utils.isEmpty(timeSwitchCourseInfo.tmendtime)) {
            wheelViewUtils1.ShowTime(timeSwitchCourseInfo.tmstarttime, Utils.getDate(calendar), "", true, position);
        } else {
            wheelViewUtils1.ShowTime(timeSwitchCourseInfo.tmstarttime, timeSwitchCourseInfo.tmendtime, "", true, position);
        }
    }

    /**
     * 从服务器得到免打扰时间
     */
    private void getTimeCourse() {
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(16, sUserName, sTrackerNo, null, null, null, "-1", null, null, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(mContext);
            }

            @Override
            public void callBackResult(String result) {
                timeSwitchCourseInfo = GsonParse.getTimeSwitchCourse(result);
                initValue();
                ProgressDialogUtil.dismiss();
            }

            @Override
            public void callBackFailResult(String result) {
                ProgressDialogUtil.dismiss();
                ToastUtil.show(mContext, result);
            }
        });
    }

    /**
     * 设置免打扰时间
     */
    private void saveTimeCourse(final boolean isCheckbox, final CheckBox cb, final int position) {
        LogUtil.e("第一个闹钟状态改变==：" + position);
        StringBuffer buff = new StringBuffer();
        StringBuffer enableBuff = new StringBuffer();
        appendValue(buff, enableBuff);
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(16, sUserName, sTrackerNo, null, null, buff.toString(), enableBuff.toString(), repeatday, null, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(mContext);
            }

            @Override
            public void callBackResult(String result) {
                ProgressDialogUtil.dismiss();
                if ("1".equals(enables[position])) {
                    cb.setChecked(true);//开关是开则打开
                } else {
                    cb.setChecked(false);
                }
                ToastUtil.show(mContext, GsonParse.reBaseObjParse(result).what);
            }

            @Override
            public void callBackFailResult(String result) {
                ProgressDialogUtil.dismiss();
                ToastUtil.show(mContext, result);

                if (!isCheckbox && cb.isChecked()) {//点击时间设置请求
                    cb.setChecked(true);//开关是开则打开
                    enables[position] = "1";
                } else if (!isCheckbox && !cb.isChecked()) {
                    cb.setChecked(false);//开关是开则打开
                    enables[position] = "0";
                } else {
                    if ("1".equals(enables[position])) {
                        cb.setChecked(false);//开关是开则打开
                        enables[position] = "0";
                    } else {
                        cb.setChecked(true);//开关是开则打开
                        enables[position] = "1";
                    }
                }
            }
        });
    }

    /**
     * 拼接数据
     */
    private void appendValue(StringBuffer buff, StringBuffer enableBuff) {
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                buff.append(timeSwitchCourseInfo.amstarttime).append(",");
                enableBuff.append(enables[0]).append(",");
            } else if (i == 1) {
                buff.append(timeSwitchCourseInfo.amendtime).append(",");
                enableBuff.append(enables[1]).append(",");
            } else if (i == 2) {
                buff.append(timeSwitchCourseInfo.tmstarttime).append(",");
                enableBuff.append(enables[2]);
            } else if (i == 3) {
                buff.append(timeSwitchCourseInfo.tmendtime).append(",");
            } else if (i == 4) {
                buff.append(timeSwitchCourseInfo.starttime3).append(",");
            } else {
                buff.append(timeSwitchCourseInfo.endtime3);
            }
        }
    }

    /**
     * 初始化值
     */
    private void initValue() {
        enables = timeSwitchCourseInfo.enable.split(",");
        disturb_tv_on1.setText(timeSwitchCourseInfo.amstarttime);
        disturb_tv_off1.setText(timeSwitchCourseInfo.amendtime);
        disturb_tv_on2.setText(timeSwitchCourseInfo.tmstarttime);
        disturb_tv_off2.setText(timeSwitchCourseInfo.tmendtime);
        disturb_tv_on3.setText(timeSwitchCourseInfo.starttime3);
        disturb_tv_off3.setText(timeSwitchCourseInfo.endtime3);
        tv_time_repeat.setText(Utils.strDayToWeek(mContext, repeatday));
        for (int i = 0; i < enables.length; i++) {
            if (i == 0 && enables[i].equals("1"))
                switch_button1.setChecked(true);
            else if (i == 1 && enables[i].equals("1"))
                switch_button2.setChecked(true);
            else if (enables[i].equals("1"))
                switch_button3.setChecked(true);
        }
    }

    /**
     * 起始时间
     */
    @Override
    public void getWheelAmTime(String sOnTime, String sOffTime, int position) {
        LogUtil.i("getWheelAmTime");
        if (position == 1) {
            if (!confirm(sOnTime, sOffTime))//时间不对
                return;
            disturb_tv_on1.setText(sOnTime);
            disturb_tv_off1.setText(sOffTime);
            timeSwitchCourseInfo.amstarttime = sOnTime;
            timeSwitchCourseInfo.amendtime = sOffTime;
            enables[0] = "1";
            saveTimeCourse(false, switch_button1, 0);

        } else if (position == 2) {
            if (!confirm(sOnTime, sOffTime))
                return;
            disturb_tv_on2.setText(sOnTime);
            disturb_tv_off2.setText(sOffTime);
            timeSwitchCourseInfo.tmstarttime = sOnTime;
            timeSwitchCourseInfo.tmendtime = sOffTime;
            enables[1] = "1";
            saveTimeCourse(false, switch_button2, 1);
        } else if (position == 3) {
            if (!confirm(sOnTime, sOffTime))
                return;
            disturb_tv_on3.setText(sOnTime);
            disturb_tv_off3.setText(sOffTime);
            timeSwitchCourseInfo.starttime3 = sOnTime;
            timeSwitchCourseInfo.endtime3 = sOffTime;
            enables[2] = "1";
            saveTimeCourse(false, switch_button3, 2);
        }
    }

    @Override
    public void getWheelPmTime(String sOnTime, String sOffTime, int position) {
    }

    /**
     * 判断提交的数据是否正常
     */
    private boolean confirm(String startTime, String endTime) {
        int time = Utils.compareTime(startTime, endTime);
        if (time > 0) {
            ToastUtil.show(mContext, R.string.time_error);
            return false;
        }
        return true;
    }

    @Override
    public void getCheckBoxTime(String Time) {
    }

    @Override
    public void onProgressDialogBack() {
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.switch_button1://第一个闹钟q
                ispoint = true;

                break;
            case R.id.switch_button2://第二个闹钟q
                ispoint = true;
                break;
            case R.id.switch_button3://第三个闹钟q
                ispoint = true;
                break;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.switch_button1:
                LogUtil.e("第一个闹钟状态改变：" + isChecked);
                if (ispoint) {
                    if (isChecked) enables[0] = "1";
                    else enables[0] = "0";
                    saveTimeCourse(true, switch_button1, 0);
                    ispoint = false;
                }

                break;
            case R.id.switch_button2:
                if (ispoint) {
                    if (isChecked) enables[1] = "1";
                    else enables[1] = "0";
                    saveTimeCourse(true, switch_button2, 1);
                    ispoint = false;
                }

                break;
            case R.id.switch_button3:
                if (ispoint) {
                    if (isChecked) enables[2] = "1";
                    else enables[2] = "0";
                    saveTimeCourse(true, switch_button3, 2);
                    ispoint = false;
                }
                break;

            default:
                break;
        }
    }
}
