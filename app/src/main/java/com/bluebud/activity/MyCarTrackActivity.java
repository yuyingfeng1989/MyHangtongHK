package com.bluebud.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.adapter.TrackListAdapter;
import com.bluebud.info.CarTrackInfo;
import com.bluebud.info.CarTrackListInfo;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.HistoryGPSData;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.TrackManager;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.obd_optimize.ObdDiagramActivity;
import com.bluebud.utils.DeviceExpiredUtil;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ObdHistoryTrackUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.CalendarView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyCarTrackActivity extends BaseActivity implements View.OnClickListener,
        ProgressDialogUtil.OnProgressDialogClickListener,
        ObdHistoryTrackUtil.ObdCallbackTrackResult, AbstractMapModel.MyMapReadyCallback {

    private TrackManager mTrackManager;

    private Tracker mCurTracker;
    private List<CurrentGPS> mRouteGPSList;
    private String sTrackerNo = "";
    private RelativeLayout rlDateSetting;
    private LinearLayout llShowCalender;
    private CalendarView cvCalendar;
    private RelativeLayout rlToplay;
    private View vDateSelect;// 日历界面
    private View vTrackList; // 每天的汽車行車軌跡列表
    private TrackListAdapter mTrackListAdapter;
    private List<CarTrackListInfo> mCarTrackList = new ArrayList<CarTrackListInfo>();
    private ImageView ivLast;
    private ImageView ivNext;
    private TextView tvCurrentDay;
    private List<CarTrackInfo.DriveTrailData> mDrivetraildata;
    private LinearLayout llstatus;
    private TextView tvMileageDetails;
    private TextView tvTimeDetails;
    private TextView tvOil;
    private TextView map_start_time;//起始时间
    private TextView map_end_time;//结束时间
    private TextView map_start_address;//起始地址
    private TextView map_end_address;//结束地址
    private TextView tv_title;//标题
    private ImageView iv_report;//报表

    private final DecimalFormat mFloatFormat = new DecimalFormat("0.0");
    private TextView mTvMileage;
    private TextView mTvTime;
    private TextView mTvOil;
    private ObdHistoryTrackUtil trackUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_track_car);

        init();
        mTrackManager.getMapPresenter().onCreate(savedInstanceState);

    }

    /**
     * 展示某一天的轨迹数据
     */
    private void showDate() {
        if (mDrivetraildata == null) {
            LogUtil.v("parse json date errror");
            return;
        }
        CarTrackListInfo lf;
        mCarTrackList.clear();
        for (CarTrackInfo.DriveTrailData d : mDrivetraildata) {
            lf = new CarTrackListInfo();
            lf.sStartAddr = d.start_addr;
            lf.sEndAddr = d.end_addr;
            lf.sStartTime = d.start_time;
            lf.sEndTime = d.end_time;
            lf.sMileage = "" + d.mileage;
            lf.sTime = Utils.StringtoTime(d.spendtime + "");
            lf.sumFuelConsumption = d.sumFuelConsumption;
            lf.sumMileage = d.sumMileage;
            lf.sumSpendtime = Utils.StringtoTime(d.sumSpendtime);
            lf.sOil = mFloatFormat.format(d.fuel_consumption);
            lf.sCarbonEmission = "" + mFloatFormat.format(d.speed);
            mCarTrackList.add(lf);
        }

        if (mCarTrackList != null && mCarTrackList.size() > 0) {
            String sumFuelConsumption = mCarTrackList.get(0).sumFuelConsumption;
            String sumMileage = mCarTrackList.get(0).sumMileage;
            String sumSpendtime = mCarTrackList.get(0).sumSpendtime;
            mTvOil.setText(Utils.isEmpty(sumFuelConsumption) ? "--" : Utils.format1(sumFuelConsumption));//总油耗
            mTvMileage.setText(Utils.isEmpty(sumMileage) ? "--" : Utils.format1(sumMileage));//总里程
            mTvTime.setText(sumSpendtime);//总时间
        }

        mTrackListAdapter.notifyDataSetChanged();
        if (mDrivetraildata.size() > 0 && vTrackList.getVisibility() == View.GONE) {
            vTrackList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化，找到控件id并设置监听器
     */
    @SuppressLint("NewApi")
    public void init() {
        initData();
        initLayout();

        mTrackManager.addView(this, R.id.map, this);
        trackUtil.showCalenderDay(Utils.curDate2Day(), tvCurrentDay);
        initListener();

    }


    private void initData() {

        mCurTracker = UserUtil.getCurrentTracker();
        if (null != mCurTracker) {
            sTrackerNo = mCurTracker.device_sn;
        }
        mTrackManager = TrackManager.newInstance(this);
        trackUtil = new ObdHistoryTrackUtil(this, this);
        trackUtil.getOnedayTrack(Utils.getCurTime(), sTrackerNo);



    }

    private void initLayout() {
        rlToplay = findViewById(R.id.rl_toplay);
        llstatus = findViewById(R.id.ic_car_status);
        llstatus.setVisibility(View.GONE);
        tvMileageDetails = findViewById(R.id.tv_mileage_details);//一天中的一段的里程
        tvTimeDetails = findViewById(R.id.tv_time_details);//一天中的一段的里程时间
        tvOil = findViewById(R.id.tv_oil_details);//一天中的一段的里程油耗
        map_start_time = findViewById(R.id.map_start_time);//起始时间
        map_end_time = findViewById(R.id.map_end_time);//结束时间
        map_start_address = findViewById(R.id.map_start_address);//起始地址
        map_end_address = findViewById(R.id.map_end_address);//结束地址
        tv_title = findViewById(R.id.tv_title);//title标题


        vTrackList = createTrackListView();
        rlToplay.addView(vTrackList);
        vTrackList.setVisibility(View.GONE);

        vDateSelect = trackUtil.createDateSelectView();
        rlToplay.addView(vDateSelect);
        vDateSelect.setVisibility(View.GONE);

        ivLast = findViewById(R.id.iv_last_day);
        ivNext = findViewById(R.id.iv_next_day);
        tvCurrentDay = findViewById(R.id.tv_current_day);
        rlDateSetting = findViewById(R.id.rl_date_setting);
        llShowCalender = findViewById(R.id.rl_show_calender);
        iv_report = findViewById(R.id.iv_report);//报表


        cvCalendar = findViewById(R.id.cv_calender);
        cvCalendar.setSelectMore(false); // 单选
    }

    private void initListener() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        ivLast.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        rlDateSetting.setOnClickListener(this);
        llShowCalender.setOnClickListener(this);
        iv_report.setOnClickListener(this);
        findViewById(R.id.ll_empty).setOnClickListener(this);

        // 设置控件监听，可以监听到点击的每一天（大家也可以在控件中根据需求设定）
        cvCalendar.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate) {
                if (!cvCalendar.isSelectMore()) {
                    vDateSelect.setVisibility(View.GONE);// 日期点击
                    llstatus.setVisibility(View.GONE);
                    mapClearOverlay();
                    SimpleDateFormat mday = new SimpleDateFormat("yyyy-MM-dd");
                    String day = mday.format(downDate);
                    trackUtil.showCalenderDay(day, tvCurrentDay);
                    if (Utils.curDate2Day().equals(day)) {
                        trackUtil.getOnedayTrack(Utils.getCurTime(), sTrackerNo);
                    } else {
                        trackUtil.getOnedayTrack(day + " 23:59:59", sTrackerNo);
                    }
                }
            }
        });
    }


    /**
     * 当天数据回调
     */
    @Override
    public void callbackOnDayTrack(CarTrackInfo carTrackInfo) {
        rlDateSetting.setVisibility(View.VISIBLE);
        iv_report.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mCurTracker.nickname)) tv_title.setText(mCurTracker.nickname);
        else tv_title.setText(mCurTracker.device_sn);
        if (carTrackInfo == null) {
            mapLocation();
            mCarTrackList.clear();
            mTrackListAdapter.notifyDataSetChanged();
            vTrackList.setVisibility(View.GONE);
        } else {
            mDrivetraildata = carTrackInfo.driveTrailData;
            showDate();
        }
    }

    @Override
    public void callbackGpsTrack(HistoryGPSData gpsData) {
        if (gpsData == null) {
            mTrackManager.getMapPresenter().mapClearOverlay();
        } else {
            mRouteGPSList = gpsData.gps;
            drawTrack();
        }
    }

    /**
     * @Description: 在地图上定位到手机(也就是用户)所在的地理位置
     */
    private void mapLocation() {
        mTrackManager.getMapPresenter().mapLocation();
        mTrackManager.getMapPresenter().showMyLocation(true);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (vTrackList.getVisibility() == View.VISIBLE)
                finish();
            else {
                if (mCarTrackList.size() > 0) {
                    vTrackList.setVisibility(View.VISIBLE);
                    iv_report.setVisibility(View.VISIBLE);
                    rlDateSetting.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(mCurTracker.nickname))
                        tv_title.setText(mCurTracker.nickname);
                    else tv_title.setText(mCurTracker.device_sn);
                } else {
                    finish();
                }
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mMapView.onResume();
        mTrackManager.getMapPresenter().onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCurTracker = UserUtil.getCurrentTracker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTrackManager.getMapPresenter().onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if (vTrackList.getVisibility() == View.VISIBLE)
                    finish();
                else {
                    if (mCarTrackList.size() > 0) {
                        vTrackList.setVisibility(View.VISIBLE);
                        iv_report.setVisibility(View.VISIBLE);
                        rlDateSetting.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(mCurTracker.nickname))
                            tv_title.setText(mCurTracker.nickname);
                        else tv_title.setText(mCurTracker.device_sn);
                    } else
                        finish();
                }
                break;
            case R.id.rl_show_calender:
                if (DeviceExpiredUtil.advancedFeatures(this, mCurTracker, true)) {
                    return;
                }
                vDateSelect.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_last_day://上一天
                // 如果日历界面显示，则跳转到下一个月 ，如果日历控件没显示，则跳转到下一天
                if (DeviceExpiredUtil.advancedFeatures(this, mCurTracker, true)) {
                    return;
                }
                if (vDateSelect.getVisibility() == View.VISIBLE) {
                    trackUtil.calenderGoLastMouth(tvCurrentDay, cvCalendar);
                } else {
                    trackUtil.getLastDayCarTrack(tvCurrentDay, sTrackerNo);
                }
                llstatus.setVisibility(View.GONE);
                mapClearOverlay();
                break;
            case R.id.iv_next_day://下一天
                if (DeviceExpiredUtil.advancedFeatures(this, mCurTracker, true)) {
                    return;
                }
                if (vDateSelect.getVisibility() == View.VISIBLE) {
                    trackUtil.calenderGoNextMouth(tvCurrentDay, cvCalendar);
                } else {
                    trackUtil.getNextDayCarTrack(tvCurrentDay, sTrackerNo);
                }
                llstatus.setVisibility(View.GONE);
                mapClearOverlay();
                break;
            case R.id.iv_report://轨迹报表
                if (DeviceExpiredUtil.advancedFeatures(this, mCurTracker, true)) {
                    return;
                }
                Intent intent = new Intent(this, ObdDiagramActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_empty:
                if (vDateSelect != null && vDateSelect.isShown()) {
                    vDateSelect.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onProgressDialogBack() {
    }


    private View createTrackListView() {
        View view = trackUtil.createTrackListView();
        ListView lvCarTrackList = view.findViewById(R.id.lv_track_list);
        lvCarTrackList.setVerticalScrollBarEnabled(false);
        mTvMileage = view.findViewById(R.id.tv_mileage);// 总里程
        mTvTime = view.findViewById(R.id.tv_time);//总时间
        mTvOil = view.findViewById(R.id.tv_oil);//总油耗
        mTrackListAdapter = new TrackListAdapter(this, mCarTrackList);
        lvCarTrackList.setAdapter(mTrackListAdapter);
        lvCarTrackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                showOneTrack(arg2);
            }
        });
        return view;
    }

    /**
     * @Description: 显示某段时间的行驶路线
     */
    private void showOneTrack(int arg2) {
        llstatus.setVisibility(View.VISIBLE);
        iv_report.setVisibility(View.GONE);
        rlDateSetting.setVisibility(View.GONE);
        tv_title.setText(R.string.trail);
        trackUtil.getOneTrackerFromServer(arg2, mDrivetraildata, sTrackerNo);
        CarTrackInfo.DriveTrailData driveTrailData = mDrivetraildata.get(arg2);
        if (driveTrailData == null)
            return;
        String starttime = driveTrailData.start_time.split(" ")[1];
        String endtime = driveTrailData.end_time.split(" ")[1];
        tvMileageDetails.setText(mFloatFormat.format(driveTrailData.mileage) + "km");
        tvTimeDetails.setText(Utils.StringtoTime(driveTrailData.spendtime + ""));//时间
        tvOil.setText(mFloatFormat.format(driveTrailData.fuel_consumption) + "L");//油耗
        map_start_time.setText(starttime);
        map_end_time.setText(endtime);
        map_start_address.setText(driveTrailData.start_addr);
        map_end_address.setText(driveTrailData.end_addr);
        vTrackList.setVisibility(View.GONE);
    }

    /**
     *  在地图上画出汽车行驶轨迹
     */
    private void drawTrack() {

        mTrackManager.getMapPresenter().drawTrack(mRouteGPSList);
    }

    /**
     * 清除图层
     */
    public void mapClearOverlay() {
        mTrackManager.getMapPresenter().mapClearOverlay();
    }

    @Override
    public void onMapReady() {
        mTrackManager.getMapPresenter().mapLocation();
    }
}

