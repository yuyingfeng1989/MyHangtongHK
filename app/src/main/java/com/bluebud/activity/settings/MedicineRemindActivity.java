package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.AlarmClockInfo1;
import com.bluebud.info.AlarmClockList;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Administrator on 2017/11/22 0022.
 */

public class MedicineRemindActivity extends BaseActivity implements
        View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnTouchListener {//
    private TextView tvTime1;
    private TextView tvTime2;
    private TextView tvTime3;
    private TextView tvWeek1;
    private TextView tvWeek2;
    private TextView tvWeek3;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private Tracker mTracker;
    private String deviceNo;
    private List<AlarmClockInfo1> alarmClockList;
    private String timeData1;
    private String timeData2;
    private String timeData3;
    private boolean ispoint = false;
    private TextView tv_name1;
    private TextView tv_name2;
    private TextView tv_name3;
    private MedicineRemindActivity mContext;
    private String messageRemind1 = "";//提示语
    private String messageRemind2 = "";
    private String messageRemind3 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.medicremind_activity);
        WeakReference<MedicineRemindActivity> weakReference = new WeakReference<MedicineRemindActivity>(MedicineRemindActivity.this);
        mContext = weakReference.get();
        initView();
        initListener();
        getDeviceRemind();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleText(R.string.take_medicine);//吃药提醒
        super.getBaseTitleLeftBack().setOnClickListener(mContext);
        mTracker = UserUtil.getCurrentTracker(mContext);
        if (mTracker != null)
            deviceNo = mTracker.device_sn;

        tvTime1 = (TextView) findViewById(R.id.tv_time1);
        tvTime2 = (TextView) findViewById(R.id.tv_time2);
        tvTime3 = (TextView) findViewById(R.id.tv_time3);
        tvWeek1 = (TextView) findViewById(R.id.tv_week1);
        tvWeek2 = (TextView) findViewById(R.id.tv_week2);
        tvWeek3 = (TextView) findViewById(R.id.tv_week3);
        checkBox1 = (CheckBox) findViewById(R.id.shake_switch_button1);
        checkBox2 = (CheckBox) findViewById(R.id.shake_switch_button2);
        checkBox3 = (CheckBox) findViewById(R.id.shake_switch_button3);
        tv_name1 = (TextView) findViewById(R.id.tv_name1); //提示语
        tv_name2 = (TextView) findViewById(R.id.tv_name2);
        tv_name3 = (TextView) findViewById(R.id.tv_name3);

        //默认设置
        setData(tvTime1, tvWeek1, checkBox1, "00:00-0-3-0111110");//00:00时间 0开关 3固定值 0111110星期循环，1表示循环
        setData(tvTime2, tvWeek2, checkBox2, "00:00-0-3-0111110");
        setData(tvTime3, tvWeek3, checkBox3, "00:00-0-3-0111110");
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        checkBox1.setOnClickListener(mContext);
        checkBox2.setOnClickListener(mContext);
        checkBox3.setOnClickListener(mContext);
        checkBox1.setOnCheckedChangeListener(mContext);
        checkBox2.setOnCheckedChangeListener(mContext);
        checkBox3.setOnCheckedChangeListener(mContext);
        checkBox1.setOnTouchListener(mContext);
        checkBox2.setOnTouchListener(mContext);
        checkBox3.setOnTouchListener(mContext);
        findViewById(R.id.rl_medicremind_clock1).setOnClickListener(mContext);
        findViewById(R.id.rl_medicremind_clock2).setOnClickListener(mContext);
        findViewById(R.id.rl_medicremind_clock3).setOnClickListener(mContext);
    }


    /**
     * 点击事件
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back://返回
                finish();
                break;
            case R.id.rl_medicremind_clock1://第一个时间设置

                Intent intent1 = new Intent(mContext, MedicineSetActivity.class);
                intent1.putExtra("time", tvTime1.getText().toString().trim());
                if (Utils.isEmpty(timeData1)) {//时间为空时打开周一到周五
                    intent1.putExtra("week", "1,2,3,4,5");
                } else {//打开已经开启的星期日期
                    LogUtil.i("timeData1:" + timeData1);
                    intent1.putExtra("week", Utils.strDaylongToString(timeData1));
                }

                intent1.putExtra("index", 1);
                intent1.putExtra("message", messageRemind1);
                startActivityForResult(intent1, 1);
                break;

            case R.id.rl_medicremind_clock2://第二个时间设置
                Intent intent2 = new Intent(mContext, MedicineSetActivity.class);
                intent2.putExtra("time", tvTime2.getText().toString().trim());
                if (Utils.isEmpty(timeData2)) {
                    intent2.putExtra("week", "1,2,3,4,5");
                } else {
                    LogUtil.i("timeData1:" + timeData2);
                    intent2.putExtra("week", Utils.strDaylongToString(timeData2));
                }
                intent2.putExtra("index", 2);
                intent2.putExtra("message", messageRemind2);
                startActivityForResult(intent2, 2);
                break;

            case R.id.rl_medicremind_clock3://第三个时间设置
                Intent intent3 = new Intent(mContext, MedicineSetActivity.class);
                intent3.putExtra("time", tvTime3.getText().toString().trim());
                if (Utils.isEmpty(timeData3)) {
                    intent3.putExtra("week", "1,2,3,4,5");
                } else {
                    LogUtil.i("timeData1:" + timeData1);
                    intent3.putExtra("week", Utils.strDaylongToString(timeData3));
                }
                intent3.putExtra("index", 3);
                intent3.putExtra("message", messageRemind3);
                startActivityForResult(intent3, 3);
                break;
        }
    }

    /**
     * 设置信息返回结果
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || data == null)
            return;

        String datas = data.getStringExtra("data");
        String messages = data.getStringExtra("message");
        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 1:
                timeData1 = setData(tvTime1, tvWeek1, checkBox1, datas);
                messageRemind1 = messages;
                tv_name1.setText(messageRemind1);
                break;
            case 2:
                timeData1 = setData(tvTime2, tvWeek2, checkBox2, datas);
                messageRemind2 = messages;
                tv_name2.setText(messageRemind2);
                break;
            case 3:
                timeData1 = setData(tvTime3, tvWeek3, checkBox3, datas);
                messageRemind3 = messages;
                tv_name3.setText(messageRemind3);
                break;
            default:
                break;
        }
    }

    /**
     * 开关状态监听
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.shake_switch_button1:
                LogUtil.i("第一个闹钟状态改变：" + isChecked);
                if (ispoint) {
                    setDeviceRemind(tvTime1, isChecked, timeData1, 1, checkBox1, messageRemind1);
                    ispoint = false;
                }

                break;
            case R.id.shake_switch_button2:
                LogUtil.i("第二个闹钟状态改变：" + isChecked);
                if (ispoint) {
                    setDeviceRemind(tvTime2, isChecked, timeData2, 2, checkBox2, messageRemind2);
                    ispoint = false;
                }

                break;
            case R.id.shake_switch_button3:
                LogUtil.i("第三个闹钟状态改变：" + isChecked);
                if (ispoint) {
                    setDeviceRemind(tvTime3, isChecked, timeData3, 3, checkBox3, messageRemind3);
                    ispoint = false;
                }
                break;

            default:
                break;
        }

    }


    /**
     * 设置吃药提醒
     *
     * @param tvTime
     * @param isChecked
     * @param week
     * @param index
     * @param cb
     */
    public void setDeviceRemind(TextView tvTime, final boolean isChecked, String week, int index, final CheckBox cb, String message) {
        if (mTracker == null)
            return;
        //01:20-1-3-0110110;
        if (Utils.isEmpty(week)) week = "0111110";//默认周一到周五打开
        String time = tvTime.getText().toString().trim();//时间
        final String times;

        if (isChecked) times = time + "-" + "1" + "-" + "3" + "-" + week;//拼接字符串，开关开
        else times = time + "-" + "0" + "-" + "3" + "-" + week;//开关关

        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(15, times, deviceNo, null, null, message, String.valueOf(index), null, null, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(mContext);
            }

            @Override
            public void callBackResult(String result) {
                LogUtil.e("resultmind1111==" + result);
                ProgressDialogUtil.dismiss();
                ReBaseObj obj = GsonParse.reBaseObjParse(result);
                if (isChecked) cb.setChecked(true);//开关是开则打开
                else cb.setChecked(false);//开关是关则关闭
                ToastUtil.show(mContext, obj.what);
            }

            @Override
            public void callBackFailResult(String result) {
                ProgressDialogUtil.dismiss();
                ToastUtil.show(mContext, result);
                if (isChecked) cb.setChecked(false);
                else cb.setChecked(true);

            }
        });
    }


    /**
     * 设置开关状态及时间显示
     *
     * @param tvTime 时间控件
     * @param tvWeek 星期控件
     * @param cb     开关
     * @param data1  字符串数据
     *               关闭状态（0关闭，1打开）data="01:20-1-3-0110110";
     */
    private String setData(TextView tvTime, TextView tvWeek, CheckBox cb, String data1) {
        if (Utils.isEmpty(data1)) {
            tvTime.setText("00:00");
            return null;
        } else {
            String[] datasplit = data1.split("-");//截取数据
            if (datasplit.length >= 4) {
                tvTime.setText(datasplit[0]);//时间

                if (datasplit[1].equalsIgnoreCase("1")) cb.setChecked(true);//1打开，开关打开状态
                else cb.setChecked(false);

                tvWeek.setText(Utils.strDaylongToWeek(mContext, datasplit[3]));//设置循环周日期
                return datasplit[3];
            }
            return null;
        }
    }

    /**
     * 获取星期字符串
     *
     * @param data1
     */
    private String getTimeData(String data1) {
        if (!Utils.isEmpty(data1)) {
            String[] datasplit = data1.split("-");
            if (datasplit.length >= 4)
                return datasplit[3];
        } else return null;
        return null;
    }

    /**
     * 获取开关信息
     */
    public void getDeviceRemind() {
        if (mTracker == null)
            return;

        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(15, null, deviceNo, null, null, null, "0", null, null, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(mContext);
            }

            @Override
            public void callBackResult(String result) {
                LogUtil.e("resultmind==" + result);
                ProgressDialogUtil.dismiss();
                String ret = null;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    ret = jsonObject.get("ret").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AlarmClockList alarmClock = GsonParse.alarmClockListParse(ret);
                if (alarmClock != null) {
                    alarmClockList = alarmClock.remindMap;
                    setAlarmClockData(alarmClockList);
                }
            }

            @Override
            public void callBackFailResult(String result) {
                ToastUtil.show(mContext, result);
                ProgressDialogUtil.dismiss();
            }
        });
    }

    /**
     * 设置设备时间和提示语
     */
    private void setAlarmClockData(List<AlarmClockInfo1> alarmClockList) {
        if (alarmClockList != null && alarmClockList.size() > 0) {
            for (int i = 0; i < alarmClockList.size(); i++) {
                AlarmClockInfo1 alarmClockInfo1 = alarmClockList.get(i);
                if (alarmClockInfo1.index_value == 1) {
                    setData(tvTime1, tvWeek1, checkBox1, alarmClockInfo1.time);
                    messageRemind1 = alarmClockInfo1.message;
                    timeData1 = getTimeData(alarmClockInfo1.time);
                    tv_name1.setText(messageRemind1);

                } else if (alarmClockInfo1.index_value == 2) {
                    setData(tvTime2, tvWeek2, checkBox2, alarmClockInfo1.time);
                    timeData2 = getTimeData(alarmClockInfo1.time);
                    messageRemind2 = alarmClockInfo1.message;
                    tv_name2.setText(messageRemind2);

                } else if (alarmClockInfo1.index_value == 3) {
                    setData(tvTime3, tvWeek3, checkBox3, alarmClockInfo1.time);
                    timeData3 = getTimeData(alarmClockInfo1.time);
                    messageRemind3 = alarmClockInfo1.message;
                    tv_name3.setText(messageRemind3);
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.shake_switch_button1://第一个闹钟q
                LogUtil.i("第一个闹钟nnnnnnnnnnnnnnnnnnnnnnn");
                ispoint = true;

                break;
            case R.id.shake_switch_button2://第二个闹钟q
                LogUtil.i("第二个闹钟nnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
                ispoint = true;
                break;
            case R.id.shake_switch_button3://第三个闹钟q
                LogUtil.i("第三个闹钟nnnnnnnnnnnnnnnnnnnnnnn");
                ispoint = true;
                break;
        }
        return false;
    }
}
