package com.bluebud.activity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.app.App;
import com.bluebud.constant.TrackerConstant;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.HistoryGPSData;
import com.bluebud.info.Tracker;
import com.bluebud.listener.IMapCallback;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.WheelViewManager;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.map.model.MyMapPresenter;
import com.bluebud.utils.HistoricalTrackUtils;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.utils.ViewUtil;
import com.liteguardian.wheelview.WheelView;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class CommonTrackActivity extends BaseFragmentActivity implements View.OnClickListener, AbstractMapModel.MyMapReadyCallback {

    private static final String TAG = "CommonTrackActivity";

    public static final String EXTRA_KEY_ADD_LINE = "addLine";

    private LinearLayout mLlDaySetting;
    private LinearLayout mLlStartSetting;
    private LinearLayout mLlEndSetting;
    private TextView mTvDate;
    private TextView mTvStartTime;
    private TextView mTvEndTime;

    // 三个wheelView根据布局内位置采用123来区分
    private WheelView mWheelView1;
    private WheelView mWheelView2;
    private WheelView mWheelView3;
    private RelativeLayout mRlWheelContent;
    private LinearLayout mLlWatchLayout;
    private RelativeLayout mRlWheelEmpty;
    private Button mBtnConfirm;

    private MyMapPresenter mMapPresenter;
    private WheelViewManager mWheelViewManager;
    private String mDate, mTimeStart, mTimeEnd;
    private Tracker mCurTracker;
    private int mRange = 1;
    private String mTrackerNo;
    private int mprotocolType;
    private HistoricalTrackUtils trackUtils;
    private TextView mTvDuration, mTvMileage, mTvStep, mTvCalorie;
    private boolean mAddLine;

    private MyHandler myHandler;

    public static void NavigateTo(Context context) {
        NavigateTo(context, false);
    }

    public static void NavigateTo(Context context , boolean addLine) {
        Intent intent = new Intent(context, CommonTrackActivity.class);
        intent.putExtra(EXTRA_KEY_ADD_LINE, addLine);
        if (context instanceof Application) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_track);

        Intent intent = getIntent();
        if (intent != null) {
            mAddLine = intent.getBooleanExtra(EXTRA_KEY_ADD_LINE, false);
        }
        initMap();
        mMapPresenter.onCreate(savedInstanceState);

        initLayout();
        initData();
        initListener();

        getHistoricalGPSData();
    }

    private void initMap() {
        mMapPresenter = new MyMapPresenter(this, App.getMapType());
    }

    private void initLayout() {
        FrameLayout flMapContent = findViewById(R.id.map);

        mLlWatchLayout = findViewById(R.id.ll_car_status);
        mLlDaySetting = findViewById(R.id.ll_day_setting);
        mLlStartSetting = findViewById(R.id.ll_begin_time_setting);
        mLlEndSetting = findViewById(R.id.ll_end_time_setting);
        mTvDate = findViewById(R.id.tv_date_setting);
        mTvStartTime = findViewById(R.id.tv_begin_time);
        mTvEndTime = findViewById(R.id.tv_end_time);
        mTvDuration = findViewById(R.id.tv_duration);
        mTvStep = findViewById(R.id.tv_step);
        mTvMileage = findViewById(R.id.tv_mileage);
        mTvCalorie = findViewById(R.id.tv_calorie);

        if (App.getMapType() == App.MAP_TYPE_GMAP) {
            mMapPresenter.initMapView(this, R.id.map, this);
            return;
        }
        View mapView = mMapPresenter.getMapView(this);
        if (mapView == null) {
            LogUtil.error(TAG, "获取地图为空!");
            return;
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        flMapContent.addView(mapView, layoutParams);
        onMapReady();
    }


    @Override
    public void onMapReady() {
        mapLocation();
    }


    private void initData() {

        myHandler = new MyHandler(this);

        mWheelViewManager = WheelViewManager.getNewInstance();
        mDate = Utils.getDate(Calendar.getInstance());
        mTimeStart = "00:00";
        mTimeEnd = Utils.getTime(Calendar.getInstance());
        mTvDate.setText(mDate);
        mTvStartTime.setText(mTimeStart);
        mTvEndTime.setText(mTimeEnd);

        mCurTracker = UserUtil.getCurrentTracker();
        if (null != mCurTracker) {
            mTrackerNo = mCurTracker.device_sn;
            mRange = mCurTracker.ranges;
            mprotocolType = mCurTracker.protocol_type;
        } else {
            mapLocation();
            return;
        }
        trackUtils = new HistoricalTrackUtils(this, mTrackerNo);
    }

    private void initListener() {
        mLlDaySetting.setOnClickListener(this);
        mLlStartSetting.setOnClickListener(this);
        mLlEndSetting.setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
    }

    private void mapLocation() {
        mMapPresenter.mapLocation();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapPresenter.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapPresenter.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_day_setting:
                showTimeWheel();
                mWheelView2.setVisibility(View.VISIBLE);
                ViewUtil.setVisible(findViewById(R.id.img_date));
                initDateWheel();
                break;
            case R.id.ll_begin_time_setting:
                showTimeWheel();
                mWheelView2.setVisibility(View.GONE);
                ViewUtil.setVisible(findViewById(R.id.img_start));
                initTimeWheel(true);
                break;
            case R.id.ll_end_time_setting:
                showTimeWheel();
                mWheelView2.setVisibility(View.GONE);
                ViewUtil.setVisible(findViewById(R.id.img_end));
                initTimeWheel(false);
                break;
            case R.id.btn_confirm:
                saveDateAndTime(mTvDate.getText(), mTvStartTime.getText(), mTvEndTime.getText());
                hideTimeWheel();
                getHistoricalGPSData();
                break;
            case R.id.rl_empty:
                resetDateAndTime();
                hideTimeWheel();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void saveDateAndTime(CharSequence date, CharSequence startTime, CharSequence endTime) {
        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            return;
        }

        mDate = (String) date;
        mTimeStart = (String) startTime;
        mTimeEnd = (String) endTime;
        mWheelViewManager.saveTempTime();
    }

    private void resetDateAndTime() {
        mTvDate.setText(mDate);
        mTvStartTime.setText(mTimeStart);
        mTvEndTime.setText(mTimeEnd);
        mWheelViewManager.resetTempTime();
    }

    private void showTimeWheel() {
        if (mWheelView1 == null) {
            ViewStub mStubWheels = findViewById(R.id.stub_track_time);
            mStubWheels.inflate();

            mRlWheelContent = findViewById(R.id.rl_wheel_content);
            mWheelView1 = findViewById(R.id.wheel_view1);
            mWheelView2 = findViewById(R.id.wheel_view2);
            mWheelView3 = findViewById(R.id.wheel_view3);
            mBtnConfirm = findViewById(R.id.btn_confirm);
            mRlWheelEmpty = findViewById(R.id.rl_empty);

            mBtnConfirm.setOnClickListener(this);
            mRlWheelEmpty.setOnClickListener(this);
        }
        mRlWheelContent.setVisibility(View.VISIBLE);
        ViewUtil.setGone(findViewById(R.id.img_date));
        ViewUtil.setGone(findViewById(R.id.img_start));
        ViewUtil.setGone(findViewById(R.id.img_end));
    }


    private void hideTimeWheel() {
        // 如果转轮图还没有初始化，那么设置隐藏将没有任何意义
        if (mWheelView3 != null) {
            mRlWheelContent.setVisibility(View.GONE);
        }
    }

    private void initDateWheel() {
        mWheelViewManager.initYYMMDDWheel(mWheelView1, mWheelView2, mWheelView3, dateAndTimeWheelListener);
    }

    private void initTimeWheel(boolean isStart) {
        mWheelViewManager.initTimeWheel(mWheelView1, mWheelView3, isStart, dateAndTimeWheelListener);
    }


    private WheelViewManager.MyWheelChangedListener dateAndTimeWheelListener = new WheelViewManager.MyWheelChangedListener() {
        @Override
        public void onChanged(String date, String startTime, String endTime) {
            if (!TextUtils.isEmpty(date)) {
                mTvDate.setText(date);
            }
            if (!TextUtils.isEmpty(startTime)) {
                mTvStartTime.setText(startTime);
            }
            if (!TextUtils.isEmpty(endTime)) {
                mTvEndTime.setText(endTime);
            }
        }
    };


    /**
     * 历史轨迹接口
     */
    private void getHistoricalGPSData() {
        int result = Utils.compareTime(mTimeStart, mTimeEnd);
        if (result > 0) {
            ToastUtil.show(this, R.string.time_error);
            return;
        }
        trackUtils.getHistoricalGPSData(mDate, mTimeStart, mTimeEnd, new IMapCallback() {
            @Override
            public void mapCallBack(HistoryGPSData data) {
                mMapPresenter.showMyLocation(false);
                mMapPresenter.mapClearOverlay();
                if (data == null) {
                    mMapPresenter.showMyLocation(true);
                    ToastUtil.show(CommonTrackActivity.this, R.string.trail_no);
                    return;
                }
                mMapPresenter.mapAddRouteOverlayWithLine(mRange, mAddLine, data.gps.toArray(new CurrentGPS[data.gps.size()]));
                if (mRange != TrackerConstant.VALUE_RANGE_WATCH) {//只有手表才需要
                    return;
                }
                if (mprotocolType != 5 && mprotocolType != 6 && mprotocolType != 7 && mprotocolType != 8) {
                    Message message = new Message();
                    message.what = 1;
                    message.obj = data;
                    myHandler.sendMessage(message);
                }
            }
        }, null);
    }


    /**
     * 设置手表的数据
     */
    protected void setWatchView(HistoryGPSData data) {
        if (data == null) {
            return;
        }
        mLlWatchLayout.setVisibility(View.VISIBLE);
        int timelong = data.timeLong;
        int steps = data.steps;
        int mileage = data.mileage;
        int calorie = data.calorie;
        mTvDuration.setText(new StringBuilder().append(timelong > 0 ? timelong : "--").append(" h"));
        mTvStep.setText(new StringBuilder().append(steps > 0 ? steps : "--"));
        mTvMileage.setText(new StringBuilder().append(mileage > 0 ? mileage : "--").append(" m"));
        mTvCalorie.setText(new StringBuilder().append(calorie > 0 ? calorie : "--").append(" cal"));


    }

    private static class MyHandler extends Handler {
        private WeakReference<CommonTrackActivity> mActivty;
        public MyHandler(CommonTrackActivity activity){
            mActivty = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CommonTrackActivity activity = mActivty.get();
            if (activity == null) {
                return;
            }
            if (msg.what == 1) {
                HistoryGPSData data = (HistoryGPSData) msg.obj;
                activity.setWatchView(data);
            }

        }
    }

}