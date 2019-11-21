package com.bluebud.activity.settings;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.adapter.AlarmAdapter;
import com.bluebud.adapter.AlarmAdapter.SBOnCheckedChangeListener;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.AlarmSwitch;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.PopupWindowOverValueTimeUtils;
import com.bluebud.utils.PopupWindowOverValueTimeUtils.RadiogroupValueTime;
import com.bluebud.utils.PopupWindowOverValueUtils;
import com.bluebud.utils.PopupWindowOverValueUtils.RadiogroupValue;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.MyListview;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.lang.ref.WeakReference;


public class AlarmSwitchActivity extends BaseActivity implements
        OnClickListener, SBOnCheckedChangeListener,
        OnProgressDialogClickListener, RadiogroupValue, RadiogroupValueTime {//CompoundButton.OnCheckedChangeListener,
    private MyListview lvAlarm;
    private View viewSpeed;
    private TextView tvValue;
    private TextView tvTime;
    private int[] titlesPeople = {R.string.alarm_sos, R.string.alarm_over,
            R.string.alarm_low_voltage};
    private int[] titles_k1 = {R.string.alarm_sos, R.string.alarm_over,
            R.string.alarm_low_voltage, R.string.alarm_fall_off};
    private int[] titlesbluebooth = {R.string.alarm_sos};
    private int[] titlesPet = {R.string.alarm_over, R.string.alarm_low_voltage};
    private int[] titlesCar = {R.string.alarm_over,
            R.string.alarm_low_voltage, R.string.alarm_tow,
            R.string.alarm_power_line, R.string.alarm_speed};
    private int[] titlesOBD = {R.string.alarm_blackout, R.string.alarm_over,
            R.string.alarm_low_voltage, R.string.alarm_tow,
            R.string.alarm_power_line,R.string.alarm_water, R.string.alarm_speed};
    private int[] titlesmoto = {R.string.alarm_over,
            R.string.alarm_low_voltage, R.string.alarm_shock,
            R.string.alarm_power_line, R.string.alarm_speed};
    private int[] titlesCurrent;
    private int[] titleLiteFamily = {R.string.alarm_sos, R.string.alarm_over, R.string.alarm_low_voltage};

    private int[] switchsPeople = {1, 1, 1};
    private int[] switchs_k1 = {1, 1, 1, 1};
    private int[] switchsbluetooth = {1};
    private int[] switchsbluetoothtrue = {1};
    private int[] switchsbluetoothflase = {0};
    private int[] switchsPet = {1, 1};
    private int[] switchsCar = {1, 1, 1, 1, 1};
    private int[] switchsOBD = {1, 1, 1, 1, 1, 1,1};
    private int[] switchsLiteFamily = {1, 1, 1};
    private int[] switchsCurrent;

    private int speedValue = 120;
    private int speedTime = 60;


    private Tracker mTrakcer;
    private String sTrackerNo = "";
    private int sTrackerType = 1;

    private AlarmAdapter mAdapter;
    private AlarmSwitch alarmSwitch;

    private RequestHandle requestHandle;
    private PopupWindowOverValueUtils overValueUtils;
    private PopupWindowOverValueTimeUtils overValueTimeUtils;

    private String product_type;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_alarm_switch);
        WeakReference<AlarmSwitchActivity> weakReference = new WeakReference<>(this);
        mContext = weakReference.get();
        mTrakcer = (Tracker) getIntent().getSerializableExtra(Constants.EXTRA_TRACKER);
        if (null != mTrakcer) {
            sTrackerType = mTrakcer.ranges;
            sTrackerNo = mTrakcer.device_sn;
            product_type = mTrakcer.product_type;
        }
        initeView();
    }

    /**
     * 初始化控件
     */
    private void initeView() {
        super.setBaseTitleText(R.string.alarm_setting);
        super.setBaseTitleVisible(View.VISIBLE);
        getBaseTitleRightBtn().setVisibility(View.GONE);
        setBaseTitleRightBtnBackground(R.drawable.btn_orange_selector);
        setBaseTitleRightBtnText(R.string.confirm);
        setBaseTitleRightBtnTextColor(getResources().getColor(R.color.white));
        lvAlarm = (MyListview) findViewById(R.id.lv_alarm);
        View footerView = LayoutInflater.from(mContext).inflate(R.layout.layout_over_speed, null);
        tvValue = (TextView) footerView.findViewById(R.id.tv_over_speed_value);//超速值
        tvTime = (TextView) footerView.findViewById(R.id.tv_over_speed_time);//时间
        viewSpeed = footerView.findViewById(R.id.ll_viewSpeed);
        viewSpeed.setVisibility(View.GONE);
        lvAlarm.addFooterView(footerView);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        getBaseTitleRightBtn().setOnClickListener(this);
        footerView.findViewById(R.id.ll_over_speed_value).setOnClickListener(this);//超速值
        LinearLayout ll_over_speed_time = footerView.findViewById(R.id.ll_over_speed_time);//时间
        ll_over_speed_time.setOnClickListener(this);
        if (sTrackerType == 6)//obd去掉持续时长
            ll_over_speed_time.setVisibility(View.GONE);
        initValue();
    }

    /**
     * 初始化控件
     */
    private void initValue() {
        alarmSwitch = new AlarmSwitch();
        alarmSwitch.sos = 1;
        alarmSwitch.boundary = 1;
        alarmSwitch.voltage = 1;
        alarmSwitch.tow = 1;
        alarmSwitch.clipping = 1;
        alarmSwitch.speed = 1;
        alarmSwitch.takeOff = 1;
        alarmSwitch.vibration = 1;
        alarmSwitch.outage = 1;//断电告警
        alarmSwitch.water = 1;//水温报警
        alarmSwitch.speedValue = 100;
        alarmSwitch.speedTime = 3;
        if (3 == sTrackerType) {// || 6 == sTrackerType
            LogUtil.i("sTrackerType22：" + sTrackerType);
            titlesCurrent = titlesCar;
            switchsCurrent = switchsCar;
            viewSpeed.setVisibility(View.VISIBLE);
            tvValue.setText(getString(R.string.over_speed_value, String.valueOf(alarmSwitch.speedValue)));
            tvTime.setText(getString(R.string.over_speed_time, String.valueOf(alarmSwitch.speedTime)));
        } else if (6 == sTrackerType) {
            titlesCurrent = titlesOBD;
            switchsCurrent = switchsOBD;
            viewSpeed.setVisibility(View.VISIBLE);
            tvValue.setText(getString(R.string.over_speed_value, String.valueOf(alarmSwitch.speedValue)));
            tvTime.setText(getString(R.string.over_speed_time, String.valueOf(alarmSwitch.speedTime)));
        } else if (2 == sTrackerType) {
            LogUtil.i("sTrackerType22：" + sTrackerType);
            titlesCurrent = titlesPet;
            switchsCurrent = switchsPet;
        } else if (7 == sTrackerType) {
            titlesCurrent = titlesbluebooth;
            switchsCurrent = switchsbluetooth;
        } else if (4 == sTrackerType) {
            if ("18".equals(product_type)) {//620新设备有震动报警
                titlesCurrent = titlesmoto;
            } else {
                titlesCurrent = titlesCar;
            }
            switchsCurrent = switchsCar;
            viewSpeed.setVisibility(View.VISIBLE);
            tvValue.setText(getString(R.string.over_speed_value, String.valueOf(alarmSwitch.speedValue)));
            tvTime.setText(getString(R.string.over_speed_time, String.valueOf(alarmSwitch.speedTime)));
        } else if (5 == sTrackerType && mTrakcer.protocol_type == 8) {//HT-891 3G智能手表
            titlesCurrent = titleLiteFamily;
            switchsCurrent = switchsLiteFamily;
        } else if (5 == sTrackerType && product_type.equals("22")) {//790 4G手表 ，(product_type.equals("30")||
            titlesCurrent = titles_k1;
            switchsCurrent = switchs_k1;
        } else {
            LogUtil.i("sTrackerType22：" + sTrackerType);
            titlesCurrent = titlesPeople;
            switchsCurrent = switchsPeople;
        }
        mAdapter = new AlarmAdapter(this, titlesCurrent, switchsCurrent, this, mTrakcer);
        lvAlarm.setAdapter(mAdapter);
        openTypeSwitch();
    }

    /**
     * 初始化不同设备开关状态
     */
    private void openTypeSwitch() {
            getAlarmSwitch();
        overValueUtils = new PopupWindowOverValueUtils(AlarmSwitchActivity.this, this);//超速值泡泡
        overValueTimeUtils = new PopupWindowOverValueTimeUtils(AlarmSwitchActivity.this, this);//超速 时间泡泡
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back://返回
                finish();
                break;
            case R.id.ll_over_speed_value://超速值
                overValueUtils.ShowRadiogroupValue(alarmSwitch.speedValue);
                break;
            case R.id.ll_over_speed_time://超速时间
                overValueTimeUtils.ShowRadiogroupValue(alarmSwitch.speedTime, sTrackerType);
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
    public void onCheckedChanged(int position, boolean flag) {
        LogUtil.i("position:" + position + " flag:" + flag);
        if (3 == sTrackerType) {
            if (0 == position) {
                alarmSwitch.boundary = flag ? 1 : 0;
            } else if (1 == position) {
                alarmSwitch.voltage = flag ? 1 : 0;
            } else if (2 == position) {
                alarmSwitch.tow = flag ? 1 : 0;
            } else if (3 == position) {
                alarmSwitch.clipping = flag ? 1 : 0;
            } else if (4 == position) {
                alarmSwitch.speed = flag ? 1 : 0;//是否有超速报警
                LogUtil.i("speed:" + alarmSwitch.speed);
                if (flag) {
                    viewSpeed.setVisibility(View.VISIBLE);
                } else {
                    viewSpeed.setVisibility(View.GONE);
                }
            }
        } else if (sTrackerType == 6) {
            if (0 == position) {
                alarmSwitch.outage = flag ? 1 : 0;
            } else if (1 == position) {
                alarmSwitch.boundary = flag ? 1 : 0;
            } else if (2 == position) {
                alarmSwitch.voltage = flag ? 1 : 0;
            } else if (3 == position) {
                alarmSwitch.tow = flag ? 1 : 0;
            } else if (4 == position) {
                alarmSwitch.clipping = flag ? 1 : 0;
            }
            else if(5 == position){//水温报警
                alarmSwitch.water = flag ? 1 : 0;
            }else if (6 == position) {
                alarmSwitch.speed = flag ? 1 : 0;//是否有超速报警
                LogUtil.i("speed:" + alarmSwitch.speed);
                if (flag) {
                    viewSpeed.setVisibility(View.VISIBLE);
                } else {
                    viewSpeed.setVisibility(View.GONE);
                }
            }

        } else if (4 == sTrackerType) {
            if ("18".equals(product_type)) {
                if (0 == position) {
                    alarmSwitch.boundary = flag ? 1 : 0;
                } else if (1 == position) {
                    alarmSwitch.voltage = flag ? 1 : 0;
                } else if (2 == position) {
                    alarmSwitch.vibration = flag ? 1 : 0;
                } else if (3 == position) {
                    alarmSwitch.clipping = flag ? 1 : 0;
                } else if (4 == position) {
                    alarmSwitch.speed = flag ? 1 : 0;//是否有超速报警
                    LogUtil.i("speed:" + alarmSwitch.speed);

                    if (flag) {
                        viewSpeed.setVisibility(View.VISIBLE);
                    } else {
                        viewSpeed.setVisibility(View.GONE);
                    }

                }
            } else {
                if (0 == position) {
                    alarmSwitch.boundary = flag ? 1 : 0;
                } else if (1 == position) {
                    alarmSwitch.voltage = flag ? 1 : 0;
                } else if (2 == position) {
                    alarmSwitch.tow = flag ? 1 : 0;
                } else if (3 == position) {
                    alarmSwitch.clipping = flag ? 1 : 0;
                } else if (4 == position) {
                    alarmSwitch.speed = flag ? 1 : 0;//是否有超速报警
                    LogUtil.i("speed:" + alarmSwitch.speed);
                    if (flag) {
                        viewSpeed.setVisibility(View.VISIBLE);
                    } else {
                        viewSpeed.setVisibility(View.GONE);
                    }
                }
            }
        } else if (2 == sTrackerType) {
            if (0 == position) {
                alarmSwitch.boundary = flag ? 1 : 0;
            } else if (1 == position) {
                alarmSwitch.voltage = flag ? 1 : 0;
            }
        }
        else if (5 == sTrackerType && mTrakcer.protocol_type == 8) {//HT-891 3G智能手表
            if (0 == position) {
                alarmSwitch.sos = flag ? 1 : 0;
            } else if (1 == position) {
                alarmSwitch.boundary = flag ? 1 : 0;
            } else if (2 == position) {
                alarmSwitch.voltage = flag ? 1 : 0;
            }
//            alarmSwitch.sos = flag ? 1 : 0;
        } else {
            if (0 == position) {
                alarmSwitch.sos = flag ? 1 : 0;
            } else if (1 == position) {
                alarmSwitch.boundary = flag ? 1 : 0;
            } else if (2 == position) {
                alarmSwitch.voltage = flag ? 1 : 0;
            } else if (3 == position) {
                alarmSwitch.takeOff = flag ? 1 : 0;
            }
        }

            if (Utils.isSuperUser(mTrakcer, AlarmSwitchActivity.this)) {//是超级用户
                refreshData();
                setAlarmSwitch();
            }

    }

    /**
     * 刷新开关适配器状态
     */
    private void refreshData() {//Boolean isFirst
        int power_off = alarmSwitch.outage;
        int sos = alarmSwitch.sos;
        int boundary = alarmSwitch.boundary;
        int voltage = alarmSwitch.voltage;
        int tow = alarmSwitch.tow;
        int takeOff = alarmSwitch.takeOff;
        int vibration = alarmSwitch.vibration;
        int clipping = alarmSwitch.clipping;
        int speed = alarmSwitch.speed;
        int water = alarmSwitch.water;
        speedValue = alarmSwitch.speedValue;
        speedTime = alarmSwitch.speedTime;
        if (3 == sTrackerType || 4 == sTrackerType || 6 == sTrackerType) {
            if (alarmSwitch.speed == 1)
                viewSpeed.setVisibility(View.VISIBLE);
            else
                viewSpeed.setVisibility(View.GONE);
        }

        if (3 == sTrackerType) {
            tvValue.setText(getString(R.string.over_speed_value, String.valueOf(speedValue)));
            tvTime.setText(getString(R.string.over_speed_time, String.valueOf(speedTime)));
            switchsCurrent = new int[]{boundary, voltage, tow, clipping, speed};
        }
        else if(6 == sTrackerType){
            tvValue.setText(getString(R.string.over_speed_value, String.valueOf(speedValue)));
            tvTime.setText(getString(R.string.over_speed_time, String.valueOf(speedTime)));
            switchsCurrent = new int[]{power_off,boundary, voltage, tow, clipping,water, speed};
        }
        else if (4 == sTrackerType) {
            if ("18".equals(product_type)) {
                switchsCurrent = new int[]{boundary, voltage, vibration, clipping, speed};
            } else {
                switchsCurrent = new int[]{boundary, voltage, tow, clipping, speed};
            }
            tvValue.setText(getString(R.string.over_speed_value, String.valueOf(speedValue)));
            tvTime.setText(getString(R.string.over_speed_time, String.valueOf(speedTime)));
        } else if (2 == sTrackerType) {
            switchsCurrent = new int[]{boundary, voltage};
        } else if (5 == sTrackerType && mTrakcer.protocol_type == 8) {//HT-891 3G智能手表
            switchsCurrent = new int[]{sos, boundary, voltage};
        } else if (5 == sTrackerType && product_type.equals("22")) {//(product_type.equals("30")||
            switchsCurrent = new int[]{sos, boundary, voltage, takeOff};
        } else {
            switchsCurrent = new int[]{sos, boundary, voltage};
        }
        LogUtil.i("speed:" + alarmSwitch.speed);
        mAdapter.setList(titlesCurrent, switchsCurrent);
        lvAlarm.setAdapter(mAdapter);
    }

    /**
     * 得到警情信息接口
     */
    private void getAlarmSwitch() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.getToggle(sTrackerNo);
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(AlarmSwitchActivity.this, null, AlarmSwitchActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            AlarmSwitch alarmSwitch1 = GsonParse.alarmSwitchParse(new String(response));
                            if (alarmSwitch1 == null) {
                                return;
                            }
                            alarmSwitch = alarmSwitch1;
                        } else {
                            ToastUtil.show(AlarmSwitchActivity.this, obj.what);
                        }
                        refreshData();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(AlarmSwitchActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    /**
     * 上传数据
     */
    private void setAlarmSwitch() {
        String url = UserUtil.getServerUrl(this);
        LogUtil.i("上传数据");
        String userName = UserSP.getInstance().getUserName(this);
        RequestParams params = HttpParams.setToggle(userName, sTrackerNo, sTrackerType, alarmSwitch, product_type);
        LogUtil.i("上传数据：" + alarmSwitch.toString());

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(AlarmSwitchActivity.this, null, AlarmSwitchActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;

                        if (600 == obj.code) {
                            ToastUtil.show(AlarmSwitchActivity.this, obj.what);
                        } else {
                            ToastUtil.show(AlarmSwitchActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(AlarmSwitchActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    /**
     * 得到超速值
     */
    @Override
    public void getRadiogroupValue(int value) {
        alarmSwitch.speedValue = value;
        LogUtil.i("超速值：" + value);
        if (Utils.isSuperUser(mTrakcer, AlarmSwitchActivity.this)) { //是超级用户
            refreshData();
            setAlarmSwitch();
        }
    }

    /**
     * 得到超速 时间
     */
    @Override
    public void getRadiogroupValueTime(int value) {
        alarmSwitch.speedTime = value;
        LogUtil.i("超速时间：" + value);
        if (Utils.isSuperUser(mTrakcer, AlarmSwitchActivity.this)) { //是超级用户
            refreshData();
            setAlarmSwitch();
        }
    }
}
