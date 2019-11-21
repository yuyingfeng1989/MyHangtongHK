package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.chat.ChatInfoCardEditActivity;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.Tracker;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.PopupWindowCheckBoxUtils;
import com.bluebud.utils.PopupWindowWheelViewUtils;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;

import java.util.Calendar;

/**
 * Created by user on 2017/11/27.
 */

public class MedicineSetActivity extends BaseActivity implements View.OnClickListener, PopupWindowCheckBoxUtils.OnCheckBoxTime, PopupWindowWheelViewUtils.OnWheeClicked {

    private TextView tvTime;
    private RelativeLayout rlWeek;
    private TextView tvWeek;
    private Tracker mTracker;

    private PopupWindowCheckBoxUtils checkBoxUtils;
    private String sTime;
    private PopupWindowWheelViewUtils wheelViewUtils;
    private String deviceNo;

    private String days;
    private String time = "00:00";
    private RelativeLayout rlTime;
    private String week;
    private int index = 1;
    private RelativeLayout rl_hint;
    private TextView tv_hint2;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_add_alarm_clock);
        checkBoxUtils = new PopupWindowCheckBoxUtils(this, this);
        wheelViewUtils = new PopupWindowWheelViewUtils(this, this);
        getData();
        init();
    }

    /**
     * 获取数据
     */
    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            time = intent.getStringExtra("time");
            week = intent.getStringExtra("week");
            index = intent.getIntExtra("index", 1);
            message = intent.getStringExtra("message");
            LogUtil.i("time:" + time + ",week:" + week + ",index:" + index);
        }
    }

    /**
     * 初始化控件和监听
     */
    private void init() {
        setBaseTitleText(R.string.take_medicine_set);
        super.setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        super.setBaseTitleRightText(R.string.submit1);
        super.setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        mTracker = UserUtil.getCurrentTracker(this);
        if (mTracker != null) {
            deviceNo = mTracker.device_sn;
        }
        tvTime = (TextView) findViewById(R.id.tv_time);//时间
        rlWeek = (RelativeLayout) findViewById(R.id.rl_week);//星期
        tvWeek = (TextView) findViewById(R.id.tv_week);//星期
        rlTime = (RelativeLayout) findViewById(R.id.rl_time);//时间
        rl_hint = (RelativeLayout) findViewById(R.id.rl_hint);//提示语
        tv_hint2 = (TextView) findViewById(R.id.tv_hint2);//提示语
        rl_hint.setVisibility(View.VISIBLE);
        rl_hint.setOnClickListener(this);
        rlTime.setOnClickListener(this);
        rlWeek.setOnClickListener(this);
        tvTime.setText(time);
        tv_hint2.setText(message);//提示语
        tvWeek.setText(Utils.strDaylongToWeek(MedicineSetActivity.this, Utils.strDayToWeek1(MedicineSetActivity.this, week)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.rl_time:
                if (Utils.isEmpty(sTime)) {
                    Calendar calendar = Calendar.getInstance();
                    wheelViewUtils.ShowTime(Utils.getDate(calendar), true);
                } else {
                    wheelViewUtils.ShowTime(sTime, true);
                }
                break;
            case R.id.rl_week://选择周中的某一天
                if (Utils.isEmpty(week)) {
                    checkBoxUtils.ShowCheckBox(getString(R.string.repeat), "1,2,3,4,5");
                } else {
                    checkBoxUtils.ShowCheckBox(getString(R.string.repeat), week);
                }
                break;
            case R.id.rl_title_right_text:
                setDeviceRemind();
                break;
            case R.id.rl_hint:
                Intent intent = new Intent(MedicineSetActivity.this, ChatInfoCardEditActivity.class);
                intent.putExtra("isChatInfo", true);
                intent.putExtra("hint", message);
                startActivityForResult(intent, 0);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 0 && resultCode == RESULT_OK) {
            message = data.getStringExtra("hint");
            if (message != null)
                tv_hint2.setText(message);
        }
    }

    //周时间
    @Override
    public void getCheckBoxTime(String Time) {
        LogUtil.i("time=" + Time);
        week = Time;
        tvWeek.setText(Utils.strDaylongToWeek(MedicineSetActivity.this, Utils.strDayToWeek1(MedicineSetActivity.this, Time)));
    }

    //时间
    @Override
    public void getWheelTime(String sTime, Boolean ison) {
        this.sTime = sTime;
        time = sTime;
        tvTime.setText(sTime);
    }

    /**
     * 设置吃药提醒
     */
    public void setDeviceRemind() {
        if (mTracker == null)
            return;
        //01:20-1-3-0110110;
        LogUtil.i("week666:" + week);
        days = Utils.strDayToWeek1(this, week);
        time = tvTime.getText().toString().trim();
        final String times = time + "-" + "1" + "-" + "3" + "-" + days;
        ChatHttpParams.getInstallSigle(this).chatHttpRequest(15, times, deviceNo, null, null, message, String.valueOf(index), null, null, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(MedicineSetActivity.this);
            }

            @Override
            public void callBackResult(String result) {
                LogUtil.e("resultset==" + result);
                ProgressDialogUtil.dismiss();
                Intent intent = new Intent();
                intent.putExtra("data", times);
                intent.putExtra("message", message);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void callBackFailResult(String result) {
                ToastUtil.show(MedicineSetActivity.this, result);
                ProgressDialogUtil.dismiss();
            }
        });
    }
}
