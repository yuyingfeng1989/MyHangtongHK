package com.bluebud.activity.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.view.HomeArcView;

public class targetSettingActivity extends BaseActivity implements OnClickListener {


    //    private TextView tvData;
//    private ListView lvHeartRate;
//    private List<HeadRateBluetoothInfo> headRateList;
//    private HeartRateAdapter adapter;
    private TextView tvNum;
    private Context mContext;
    //    private TextView tvTargetStep;
    private LinearLayout llSmallSport;
    private LinearLayout llProperSport;
    private LinearLayout llHighSport;

    private Tracker mCurTracker;
    private String strTrackerNo;
    //    private int targetStep = 5000;
    private String goal = 5000 + "";
    private String heiget = 160 + "";
    private String weight = 50 + "";
    private String msg;
    private int length;
    private String msgs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_target_setting);
        mContext = this;
        init();
//        Utils.sendBooBluetoothBroadcast(mContext, Constants.BLUETOOTH_PS_GET);//获取个人信息
        regesterBroadcast();
    }

    private void init() {
        setBaseTitleText(R.string.target_setting);
        super.setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        mCurTracker = UserUtil.getCurrentTracker(this);
        if (null != mCurTracker) {
            strTrackerNo = mCurTracker.device_sn;

        }
        tvNum = (TextView) findViewById(R.id.tv_num);
        LinearLayout doughnutView = (LinearLayout) findViewById(R.id.doughnutView);
        doughnutView.removeAllViews();
        doughnutView.addView(new HomeArcView(mContext, 83, 1));
        llSmallSport = (LinearLayout) findViewById(R.id.ll_small_sport);
        llProperSport = (LinearLayout) findViewById(R.id.ll_proper_sport);
        llHighSport = (LinearLayout) findViewById(R.id.ll_high_sport);
        llSmallSport.setOnClickListener(this);
        llProperSport.setOnClickListener(this);
        llHighSport.setOnClickListener(this);

    }


    //PS,GET,10000|170|60
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
//            case R.id.ll_small_sport://少量运动者
//                goal = 5000 + "";
////			heiget=170+"";
////			weight=50+"";
//                msg = "PS,SET," + goal + "|" + heiget + "|" + weight;
//                length = msg.length();
//                msgs = Constants.BLUETOOTH_GET + length + " " + msg;
//                Utils.sendBooBluetoothBroadcast(mContext, msgs);
//                break;
//            case R.id.ll_proper_sport://适量运动者
//                goal = 10000 + "";
////			heiget=170+"";
////			weight=50+"";
//                msg = "PS,SET," + goal + "|" + heiget + "|" + weight;
//                length = msg.length();
//                msgs = Constants.BLUETOOTH_GET + length + " " + msg;
//                Utils.sendBooBluetoothBroadcast(mContext, msgs);
//                break;
//            case R.id.ll_high_sport://高强度运动者
//                goal = 20000 + "";
////			heiget=170+"";
////			weight=50+"";
//                msg = "PS,SET," + goal + "|" + heiget + "|" + weight;
//                length = msg.length();
//                msgs = Constants.BLUETOOTH_GET + length + " " + msg;
//                Utils.sendBooBluetoothBroadcast(mContext, msgs);
//                break;


            case R.id.rl_title_right_text:
                if (UserUtil.isSuperUser(mContext, strTrackerNo)) {

                } else {
                    ToastUtil.show(mContext, R.string.no_super_user);
                }
                break;
        }
    }

    private void regesterBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_BLUETOOTH_PS);
        filter.addAction(Constants.ACTION_BLUETOOTH_PS_RET_SUCCESS);

        registerReceiver(broadcastReceiver, filter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context arg0, Intent intent) {
            LogUtil.i(intent.getAction());
            if (intent.getAction().equals(Constants.ACTION_BLUETOOTH_PS)) {//个人信息获取成功
                goal = intent.getStringExtra("goal");
                heiget = intent.getStringExtra("heiget");
                weight = intent.getStringExtra("weight");
                tvNum.setText(goal);
                UserSP.getInstance().saveCurrenttargetStep(mContext, goal, strTrackerNo);
            } else if (intent.getAction().equals(Constants.ACTION_BLUETOOTH_PS_RET_SUCCESS)) {//个人信息设置成功
                ToastUtil.show(mContext, R.string.set_success);
                tvNum.setText(goal);
                UserSP.getInstance().saveCurrenttargetStep(mContext, goal, strTrackerNo);
            }
        }
    };
}
