package com.bluebud.activity.settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.AlarmClockInfo1;
import com.bluebud.info.AlarmClockList;
import com.bluebud.info.ReBaseObj;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class AlarmClockListActivity extends BaseActivity implements
        OnClickListener, OnCheckedChangeListener, OnTouchListener {

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
    //    private boolean isFirst1 = true;
//    private boolean isFirst2 = true;
//    private boolean isFirst3 = true;
//    private boolean isback = false;
    private boolean ispoint = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_alarm_clock_list);
        init();
        getDeviceRemind();
    }

    private void init() {
        super.setBaseTitleText(R.string.alarm_clock);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        mTracker = UserUtil.getCurrentTracker(this);
        if (mTracker != null) {
            deviceNo = mTracker.device_sn;
//            product_type = mTracker.product_type;
        }
    /*	if ("15".equals(product_type)) {//970老人手表
            super.setBaseTitleText(R.string.smart_reminder);
		}else {//770其他手表
			super.setBaseTitleText(R.string.alarm_clock);
			
		}*/
        ((LinearLayout) findViewById(R.id.ll_alarm_clock1)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_alarm_clock2)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.ll_alarm_clock3)).setOnClickListener(this);
        tvTime1 = (TextView) findViewById(R.id.tv_time1);
        tvTime2 = (TextView) findViewById(R.id.tv_time2);
        tvTime3 = (TextView) findViewById(R.id.tv_time3);
        tvWeek1 = (TextView) findViewById(R.id.tv_week1);
        tvWeek2 = (TextView) findViewById(R.id.tv_week2);
        tvWeek3 = (TextView) findViewById(R.id.tv_week3);
        checkBox1 = (CheckBox) findViewById(R.id.shake_switch_button1);
        checkBox2 = (CheckBox) findViewById(R.id.shake_switch_button2);
        checkBox3 = (CheckBox) findViewById(R.id.shake_switch_button3);
        checkBox1.setChecked(true);
        checkBox2.setChecked(false);
        checkBox3.setChecked(true);
        checkBox1.setOnClickListener(this);
        checkBox2.setOnClickListener(this);
        checkBox3.setOnClickListener(this);
        checkBox1.setOnCheckedChangeListener(this);
        checkBox2.setOnCheckedChangeListener(this);
        checkBox3.setOnCheckedChangeListener(this);
        checkBox1.setOnTouchListener(this);
        checkBox2.setOnTouchListener(this);
        checkBox3.setOnTouchListener(this);
        //默认设置
        setData(tvTime1, tvWeek1, checkBox1, "00:00-0-3-0111110");
        setData(tvTime2, tvWeek2, checkBox2, "00:00-0-3-0111110");
        setData(tvTime3, tvWeek3, checkBox3, "00:00-0-3-0111110");

    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, AddAlarmClockActivity.class);
//        intent.putExtra("profile", profile);
        switch (view.getId()) {
            case R.id.rl_title_back://返回
                finish();
                break;
            case R.id.ll_alarm_clock1://第一个闹钟
                intent.putExtra("time", tvTime1.getText().toString().trim());
                if (Utils.isEmpty(timeData1)) {
                    intent.putExtra("week", "1,2,3,4,5");
                } else {
                    LogUtil.i("timeData1:" + timeData1);
                    intent.putExtra("week", Utils.strDaylongToString(timeData1));
                }
                intent.putExtra("index", 1);
                startActivityForResult(intent, 1);
                break;
            case R.id.ll_alarm_clock2://第二个闹钟q
                intent.putExtra("time", tvTime2.getText().toString().trim());
                if (Utils.isEmpty(timeData2)) {
                    intent.putExtra("week", "1,2,3,4,5");
                } else {
                    LogUtil.i("timeData2:" + timeData2);
                    intent.putExtra("week", Utils.strDaylongToString(timeData2));
                }
                intent.putExtra("index", 2);
                startActivityForResult(intent, 2);
                break;
            case R.id.ll_alarm_clock3://第三个闹钟q
                intent.putExtra("time", tvTime3.getText().toString().trim());
                if (Utils.isEmpty(timeData3)) {
                    intent.putExtra("week", "1,2,3,4,5");
                } else {
                    LogUtil.i("timeData3:" + timeData1);
                    intent.putExtra("week", Utils.strDaylongToString(timeData3));
                }
                intent.putExtra("index", 3);
                startActivityForResult(intent, 3);
                break;
//            case R.id.shake_switch_button1://第一个闹钟q
//                LogUtil.i("第一个闹钟");
//                //ispoint = true;
//
//                break;
//            case R.id.shake_switch_button2://第二个闹钟q
//                LogUtil.i("第二个闹钟");
//                //ispoint = true;
//                break;
//            case R.id.shake_switch_button3://第三个闹钟q
//                LogUtil.i("第三个闹钟");
//                //ispoint = true;
//                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
//        isback = true;
        //String datas= data.getStringExtra("data");
        String datas = data.getStringExtra("data");
//        profile = data.getStringExtra("profile");
        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 1:
                setData(tvTime1, tvWeek1, checkBox1, datas);
                timeData1 = getTimeData(datas);
                break;
            case 2:
                setData(tvTime2, tvWeek2, checkBox2, datas);
                timeData2 = getTimeData(datas);
                break;
            case 3:
                setData(tvTime3, tvWeek3, checkBox3, datas);
                timeData3 = getTimeData(datas);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.shake_switch_button1:
                LogUtil.i("第一个闹钟状态改变：" + isChecked);
                if (ispoint) {
                    setDeviceRemind(tvTime1, isChecked, timeData1, 1, checkBox1);
                    ispoint = false;
                }

                break;
            case R.id.shake_switch_button2:
                LogUtil.i("第二个闹钟状态改变：" + isChecked);
                if (ispoint) {
                    setDeviceRemind(tvTime2, isChecked, timeData2, 2, checkBox2);
                    ispoint = false;
                }

                break;
            case R.id.shake_switch_button3:
                LogUtil.i("第三个闹钟状态改变：" + isChecked);
                if (ispoint) {
                    setDeviceRemind(tvTime3, isChecked, timeData3, 3, checkBox3);
                    ispoint = false;
                }
                break;

            default:
                break;
        }

    }


    public void setDeviceRemind(TextView tvTime, final boolean isopen, String week, int index, final CheckBox cb) {
        if (mTracker == null) {
            return;
        }
        String url = UserUtil.getServerUrl(this);
        //01:20-1-3-0110110;
        if (Utils.isEmpty(week)) {
            week = "0111110";
        }
        LogUtil.i("week66d6:" + week);
        String time = tvTime.getText().toString().trim();
        final String times;
        if (isopen) {
            times = time + "-" + "1" + "-" + "3" + "-" + week;
        } else {
            times = time + "-" + "0" + "-" + "3" + "-" + week;
        }
        RequestParams params = HttpParams.setDeviceRemind(deviceNo, index, times);
        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {


                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        ProgressDialogUtil.show(AlarmClockListActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null) {
                            if (isopen) {
                                cb.setChecked(false);
                            } else {
                                cb.setChecked(true);
                            }
                            return;
                        }
                        if (obj.code == 0) {
                            if (isopen) {
                                cb.setChecked(true);
                            } else {
                                cb.setChecked(false);
                            }
                            ToastUtil.show(AlarmClockListActivity.this, obj.what);
                        } else {
                            ToastUtil.show(AlarmClockListActivity.this, obj.what);
                            if (isopen) {
                                cb.setChecked(false);
                            } else {
                                cb.setChecked(true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(AlarmClockListActivity.this,
                                R.string.net_exception);
                        if (isopen) {
                            cb.setChecked(false);
                        } else {
                            cb.setChecked(true);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });


    }


    private void setData(TextView tvTime, TextView tvWeek, CheckBox cb, String data1) {
        if (Utils.isEmpty(data1)) {
            tvTime.setText("00:00");
        } else {
            //String data="01:20-1-3-0110110";
            String[] datasplit = data1.split("-");
            if (datasplit.length >= 4) {
                LogUtil.i("时间：" + datasplit[0] + ",关闭状态（0关闭，1打开）：" + datasplit[1] + ",频率：" + datasplit[2] + ",星期：" + datasplit[3]);
                tvTime.setText(datasplit[0]);
                if (datasplit[1].equalsIgnoreCase("1")) {//1打开
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }
                tvWeek.setText(Utils.strDaylongToWeek(AlarmClockListActivity.this, datasplit[3]));
            }
        }
    }

    private String getTimeData(String data1) {
        if (!Utils.isEmpty(data1)) {
            //String data="01:20-1-3-0110110";
            String[] datasplit = data1.split("-");
            if (datasplit.length >= 4) {
                LogUtil.i("6666666666666时间：" + datasplit[0] + ",关闭状态（0关闭，1打开）：" + datasplit[1] + ",频率：" + datasplit[2] + ",星期：" + datasplit[3]);
                //tvWeek.setText(Utils.strDaylongToWeek(AlarmClockListActivity.this, datasplit[3]));
                return datasplit[3];

            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    /**
     * 获取闹铃数据
     */
    public void getDeviceRemind() {
        if (mTracker == null) {
            return;
        }
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.getDeviceRemind(deviceNo);
        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(AlarmClockListActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null) {
                            return;
                        }
                        if (obj.code == 0) {
                            String ret = null;
                            try {
                                JSONObject job = new JSONObject(new String(response));
                                ret = job.get("ret").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            AlarmClockList alarmClock = GsonParse.alarmClockListParse(ret);
                            LogUtil.e("alrmclock=" + alarmClock.toString());
                            if (alarmClock != null) {
                                alarmClockList = alarmClock.remindMap;
                                setAlarmClockData(alarmClockList);
                            } else {
                                LogUtil.e("alarmClock is null");
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(AlarmClockListActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void setAlarmClockData(List<AlarmClockInfo1> alarmClockList) {
        if (alarmClockList != null && alarmClockList.size() > 0) {
            for (int i = 0; i < alarmClockList.size(); i++) {
                LogUtil.i("index:" + alarmClockList.get(i).index_value + ",time:" + alarmClockList.get(i).time);
                if (alarmClockList.get(i).index_value == 1) {
                    AlarmClockInfo1 alarm1 = alarmClockList.get(i);
                    setData(tvTime1, tvWeek1, checkBox1, alarm1.time);
                    timeData1 = getTimeData(alarm1.time);
                    LogUtil.e("timeData1:" + timeData1);
                } else if (alarmClockList.get(i).index_value == 2) {
                    AlarmClockInfo1 alarm2 = alarmClockList.get(i);
                    setData(tvTime2, tvWeek2, checkBox2, alarm2.time);
                    timeData2 = getTimeData(alarm2.time);
                    LogUtil.i("timeData2:" + timeData2);
                } else if (alarmClockList.get(i).index_value == 3) {
                    AlarmClockInfo1 alarm3 = alarmClockList.get(i);
                    setData(tvTime3, tvWeek3, checkBox3, alarm3.time);
                    timeData3 = getTimeData(alarm3.time);
                    LogUtil.i("timeData3:" + timeData3);
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
