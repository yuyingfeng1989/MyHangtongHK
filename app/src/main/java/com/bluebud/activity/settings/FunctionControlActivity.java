package com.bluebud.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.bluebud.activity.BaseActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.FunctionControlInfo;
import com.bluebud.info.Tracker;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.request.RequestUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;

/**
 * Created by Administrator on 2018/10/15.
 */

public class FunctionControlActivity extends BaseActivity implements RequestUtil.ICallBack, View.OnClickListener {

    private CheckBox box_reset;
    private CheckBox box_showdown;
    private CheckBox box_restart;
    private Context mContext;
    private RequestUtil request;
    private String device_sn;
    private FunctionControlInfo dataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.functioncontrol_activity);
        WeakReference<FunctionControlActivity> wf = new WeakReference<>(this);
        mContext = wf.get();
        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    public void initView() {
        findViewById(R.id.funtion_imageview).setOnClickListener(this);
        box_showdown = (CheckBox) findViewById(R.id.function_switch_shutdown);
        box_restart = (CheckBox) findViewById(R.id.function_switch_restart);
        box_reset = (CheckBox) findViewById(R.id.function_switch_reset);
        box_showdown.setOnClickListener(this);
        box_restart.setOnClickListener(this);
        box_reset.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    public void initData() {
        Tracker mCurTracker = UserUtil.getCurrentTracker(mContext);
        if(mCurTracker==null)
            return;
        device_sn = mCurTracker.device_sn;
        request = new RequestUtil(mContext, device_sn, this);
        request.getDeviceEnableSwitch();
    }

    @Override
    public void callBackData(String data, int position) {
        if (position == 4 && data != null) {
            try {
                JSONObject job = new JSONObject(data);
                String ret = job.get("ret").toString();
                JSONObject jb = new JSONObject(ret);
                String jbString = jb.get("deviceEnableSwitch").toString();
                Type typeOfT = new TypeToken<FunctionControlInfo>() {}.getType();//获取当前状态设置
                dataSet = new Gson().fromJson(jbString, typeOfT);
                if (dataSet.reset == 0) box_reset.setChecked(true);//恢复出厂设置
                else box_reset.setChecked(false);
                if (dataSet.shutdown == 0) box_showdown.setChecked(true);//关机
                else box_showdown.setChecked(false);
                if (dataSet.restart == 0) box_restart.setChecked(true);//复位
                else box_restart.setChecked(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (position < 4 && data == null) {//设置失败的情况
            if (position == 0) {
                if (dataSet.shutdown == 0)
                    box_showdown.setChecked(false);
                else box_showdown.setChecked(true);
            } else if (position == 1) {
                if (dataSet.restart == 0)
                    box_restart.setChecked(false);
                else box_restart.setChecked(true);
            } else if (position == 2) {
                if (dataSet.reset == 0)
                    box_reset.setChecked(false);
                else box_reset.setChecked(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (dataSet == null)
            dataSet = new FunctionControlInfo();
        switch (v.getId()) {
            case R.id.funtion_imageview:
                finish();
                break;
            case R.id.function_switch_shutdown://关机
                if (box_showdown.isChecked()) dataSet.shutdown = 0;
                else dataSet.shutdown = 1;
                request.setDeviceEnableSwitch(dataSet, 0);
                break;
            case R.id.function_switch_restart://复位
                if (box_restart.isChecked()) dataSet.restart = 0;
                else dataSet.restart = 1;
                request.setDeviceEnableSwitch(dataSet, 1);
                break;
            case R.id.function_switch_reset://恢复出厂设置
                if (box_reset.isChecked()) dataSet.reset = 0;
                else dataSet.reset = 1;
                request.setDeviceEnableSwitch(dataSet, 2);
                break;
        }
    }
}
