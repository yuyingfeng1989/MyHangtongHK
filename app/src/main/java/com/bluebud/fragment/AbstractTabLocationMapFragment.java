package com.bluebud.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.activity.CommonTrackActivity;
import com.bluebud.activity.FenceSettingListActivity;
import com.bluebud.activity.MainActivity;
import com.bluebud.activity.MapFenceEditActivity;
import com.bluebud.activity.MyCarTrackActivity;
import com.bluebud.activity.MyPetWalkActivity;
import com.bluebud.activity.ObdCarDetectActivity;
import com.bluebud.app.App;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.Tracker;
import com.bluebud.listener.IHomeFragment;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.OnLocationListener;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.map.model.MyMapPresenter;
import com.bluebud.obd_optimize.ObdDriverActivity;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DeviceExpiredUtil;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.OBDDashboardUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.utils.ViewUtil;
import com.bluebud.utils.request.HomeRequestUtil;
import com.bluebud.utils.request.RequestLocationUtil;
import com.bluebud.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

import static com.bluebud.constant.TrackerConstant.PROTOCOL_TYPE_770;
import static com.bluebud.constant.TrackerConstant.PROTOCOL_TYPE_790;
import static com.bluebud.constant.TrackerConstant.PROTOCOL_TYPE_990;
import static com.bluebud.constant.TrackerConstant.VALUE_PRODUCT_TYPE_790S;
import static com.bluebud.constant.TrackerConstant.VALUE_PRODUCT_TYPE_HT_790;
import static com.bluebud.constant.TrackerConstant.VALUE_PRODUCT_TYPE_K1;
import static com.bluebud.constant.TrackerConstant.VALUE_RANGE_BLUETOOTH_WATCH;
import static com.bluebud.constant.TrackerConstant.VALUE_RANGE_CAR;
import static com.bluebud.constant.TrackerConstant.VALUE_RANGE_MOTO;
import static com.bluebud.constant.TrackerConstant.VALUE_RANGE_OBD;
import static com.bluebud.constant.TrackerConstant.VALUE_RANGE_PERSON;
import static com.bluebud.constant.TrackerConstant.VALUE_RANGE_PET;
import static com.bluebud.constant.TrackerConstant.VALUE_RANGE_WATCH;

@SuppressWarnings("ResourceType")
public abstract class AbstractTabLocationMapFragment extends Fragment implements
        View.OnClickListener,
        MainActivity.OnChangeListener,
        MainActivity.OnTabClickListener,
        OBDDashboardUtil.OnclickObdFunciton,
        IHomeFragment, OnLocationListener,
        AbstractMapModel.MyMapReadyCallback {//SwipeRefreshLayout.OnRefreshListener,

    protected MyMapPresenter mMapPresenter;

    protected MyLatLng curPointLocation;// 定时定位
    protected MyLatLng curPointCurLocation;// 定位

    private View contentView;
    // 追踪器
    private Tracker mCurTracker;
    protected CurrentGPS mCurGPS;
    private String sTrackerNo = "";
    protected int mRange = 1;
    private boolean mNeedRefresh = false;
    private int iTabPosition = 0;// 0:定位 1:导航 2:电子围栏3.表示轨道４.表示遛狗，
    private ImageButton ibMapSwitch;

//    private RelativeLayout llLocationInfoWindow;
    private FrameLayout mFlMapContent;
    protected View mInfoWindowContent;
    private TextView tvMapPopTitle;
    private TextView tvMapPopSpeed;
    private boolean isMapTypeNormal = true;
    private LocationManager locationManager;

    private ImageView ivTrackElectric;
    private ImageView ivLocationWay;

    //汽车部分
    private View mVsCarView;
    private View mViewMenu;
    //围栏，轨迹，遛狗.导航
    private ImageButton ibLockVehicle;
    private ImageButton ibLocation;
    private ImageButton ibLocus;
    private ImageButton ibFence;
    private ImageButton ibPetWalk;
    private ImageButton ibNavigation;
    private ImageButton ibchat;
    private ImageButton ib_phone;//一键拨号
    private int protocol_type = 1;
    protected View vTrackerLocationDot; // 在地图上的定位点视图
    protected CircleImageView ivTrackerHeadIcon; // 定位点的设备头像\
    private int iDefaultHeadIconDrawableID;
    private CircleImageView ivHeadImage;
    private TextView tvMapPopTime;
    private LocationListener listener;
    private RelativeLayout rlMap;
    private String url;
    private OBDDashboardUtil obdDashboard;//汽车仪表盘工具类
    protected HomeRequestUtil homeRequestUtil;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapPresenter = onCreateMapPresenter(getContext());
        mMapPresenter.needOBDOffset(true);
        mMapPresenter.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);// 注册一个消息传递控件
        getUnreadMessage();// 注册一个未读消息观察者
        url = UserUtil.getServerUrl(App.getContext());
        if (homeRequestUtil == null) {
            homeRequestUtil = new HomeRequestUtil(getActivity(), this, null, url);
        }
        obdDashboard = new OBDDashboardUtil(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fragment_tab_common_location_map, container, false);
            init();
        }
        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapPresenter.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).showRefreshTitle();
        }
        if (mCurTracker == null) {
            return;
        }
        //支付成功
        if (Constants.isPay) {
            mCurTracker = UserUtil.getCurrentTracker();
        }
        if (mRange != VALUE_RANGE_BLUETOOTH_WATCH) {
            RequestLocationUtil.getRequestLocation(App.getContext()).startTimerPolling(sTrackerNo, url, false);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mCurTracker != null && mRange != VALUE_RANGE_BLUETOOTH_WATCH) {
            RequestLocationUtil.getRequestLocation(App.getContext()).stopTimerPolling();
        }
    }

    /**
     * 退群和加群，更新微聊图标是否显示
     */
    public void onEventMainThread(Tracker event) {
        homeRequestUtil.isShowChatView(event, ib_rl_chat);
    }

    protected boolean needRefresh() {
        if (curPointLocation == null || mCurGPS == null) {
            return false;
        }
        return curPointLocation.latitude != mCurGPS.lat
                || curPointLocation.longitude != mCurGPS.lng;
    }

    /**
     * 定时位置轮询
     */
    public void onEventMainThread(CurrentGPS event) {
        if (event == null) {
            return;
        }
        if (!mMapPresenter.hasInitialized()) {
            return;
        }
        if (mRange == VALUE_RANGE_OBD) {
            homeRequestUtil.getCarDate();
        }
        if (!mCurGPS.equals(event)) {
            mCurGPS = event;
            mNeedRefresh = true;
        }
        if (mInfoWindowContent == null || !mInfoWindowContent.isShown()) {
            homeRequestUtil.setDeviceInfo(mCurGPS, mCurTracker);
        }
        setHeadIconAndAddLocationOverlay();
    }

    private void init() {
        initData();
        initView();//初始化控件
        initMap();//初始化地图
        initListener();
        onChangeTracker(0);//选取首个设备
    }

    private void initData() {
        mCurTracker = UserUtil.getCurrentTracker(getContext());
        if (mCurTracker != null) {
            sTrackerNo = mCurTracker.device_sn;
            setRange(mCurTracker.ranges);
            protocol_type = mCurTracker.protocol_type;
        }
        homeRequestUtil.setRefreshTrack(mCurTracker);//刷新设备列表当前选择的设备
    }

    /**
     * 初始化view
     */
    private void initView() {
        initFenceLocusView();
    }

    /**
     * @Description: 围栏　轨迹，导航，遛狗
     */
    private TextView ibchat_textunread;
    private RelativeLayout ib_rl_chat;

    /**
     * 初始化右下角悬浮按钮，没毛病
     * todo 界面初始化和监听器初始化需要分离
     */
    private void initFenceLocusView() {
        ibLockVehicle = contentView.findViewById(R.id.ib_lock_car);// 锁车
        ibLocation = contentView.findViewById(R.id.ib_location);// 定位，只有770在这里做点名功能
        ibLocus = contentView.findViewById(R.id.ib_locus);// 轨迹
        ibFence = contentView.findViewById(R.id.ib_fence);// 围栏
        ibPetWalk = contentView.findViewById(R.id.ib_pet_walk);// 遛狗
        ibNavigation = contentView.findViewById(R.id.ib_navigation);// 导航
        ibchat = contentView.findViewById(R.id.ib_chat);// 微聊
        ib_phone = contentView.findViewById(R.id.ib_phone);//一键拨号
        ib_rl_chat = contentView.findViewById(R.id.ib_rl_chat);
        ibchat_textunread = contentView.findViewById(R.id.chat_unread);// 未读消息数
        rlMap = contentView.findViewById(R.id.rl_map);//---------

        ibMapSwitch = contentView.findViewById(R.id.ib_map_swatch);// 卫星地图切换
        mFlMapContent = contentView.findViewById(R.id.fl_map_content);

        // 是宠物有逛狗功能
        ibPetWalk.setVisibility(mRange == VALUE_RANGE_PET ? View.VISIBLE : View.GONE);
        if (mCurTracker == null) {
            ibLocus.setVisibility(View.GONE);
            ibFence.setVisibility(View.GONE);
            ibPetWalk.setVisibility(View.GONE);
            ibNavigation.setVisibility(View.GONE);
            ibLockVehicle.setVisibility(View.GONE);
            return;
        }
        setLockVehicleVisibility();//显示锁车图标
    }

    private void initListener() {
        MainActivity.setClickListener(this, this);
        homeRequestUtil.regesterBroadcast(broadcastReceiver);//注册广播接收者
        ibMapSwitch.setOnClickListener(this);
        ibLockVehicle.setOnClickListener(this);
        ibLocus.setOnClickListener(this);
        ibFence.setOnClickListener(this);
        ibPetWalk.setOnClickListener(this);
        ibNavigation.setOnClickListener(this);
        ibLocation.setOnClickListener(this);
        ibchat.setOnClickListener(this);
        ib_phone.setOnClickListener(this);
    }

    /**
     * 弹出的pop窗口布局
     */
    private void initMapInfoWindow() {
        mInfoWindowContent = LayoutInflater.from(getContext()).inflate(R.layout.map_pop_info, null);
        ivHeadImage = mInfoWindowContent.findViewById(R.id.iv_tracker_image);
        tvMapPopTitle = mInfoWindowContent.findViewById(R.id.map_info_title);
        tvMapPopTime = mInfoWindowContent.findViewById(R.id.map_info_time);
        ivTrackElectric = mInfoWindowContent.findViewById(R.id.iv_power);
        ivLocationWay = mInfoWindowContent.findViewById(R.id.iv_location_way);
        tvMapPopSpeed = mInfoWindowContent.findViewById(R.id.map_info_speed);

        RelativeLayout llLocationInfoWindow = mInfoWindowContent.findViewById(R.id.ll_location_info_window);
//        llLocationInfoWindow.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
        llLocationInfoWindow.setOnClickListener(this);
    }


    /**
     * 改变锁车显示
     */
    private void tabLockChange(boolean bIsLock) {
        ibLockVehicle.setBackgroundResource(
                bIsLock ? R.drawable.btn_icon_suoche : R.drawable.btn_icon_jiesuo);
    }

    /**
     * @Description: 汽车主页特有的显示部分
     */
    private void initCarView() {
        ViewStub vsCar = contentView.findViewById(R.id.rl_car_view);//汽车中部整个布局
        mVsCarView = vsCar.inflate();
        obdDashboard.addView(mVsCarView);
        mViewMenu = contentView.findViewById(R.id.ll_pet_navigation);
    }


    /**
     * 初始化地图
     */
    // TODO: 2019/6/27 替换为抽象
    protected void initMap() {
        initLocationMapInfoPoint(); //无相关
        if (App.getMapType() == App.MAP_TYPE_GMAP) {/*创建google地图*/
            mMapPresenter.initMapView(getActivity(), R.id.map, this);
        } else {
            View view = mMapPresenter.getMapView(getActivity());/*创建高德地图*/
            if (view == null) {
                return;
            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            mFlMapContent.addView(view, 0, lp);//添加地图
        }
        //初始化泡泡
        initMapInfoWindow(); //无相关
    }

    @Override
    public void onMapReady() {
        initCurrentLocation(); // 相关
        setHeadIconAndAddLocationOverlay();
    }

    /**
     * 获取当前手机经纬度
     */
    private void initCurrentLocation() {
        if (locationManager == null)
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
                handleLocationChanged(location);
            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, listener);
        getCurrentLocation();
    }


    /**
     * 获取当前位置
     */
    private void getCurrentLocation() {
        if (locationManager == null) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(homeRequestUtil.getProvider(locationManager));//获取本地最后一次定位
        if (location != null) {
            // TODO: 2019/6/27 替换
            handleCurrentLocation(location);
        }
    }

    /**
     * @Description: 初始化地图上设备显示点视图
     */
    protected void initLocationMapInfoPoint() {
        vTrackerLocationDot = LayoutInflater.from(getContext()).inflate(R.layout.layout_location_dot, null);
        ivTrackerHeadIcon = vTrackerLocationDot.findViewById(R.id.iv_tracker_headicon);
    }


    /**
     * 在地图上绘制各种图标
     */
    private void mapAddLocationOverlay() {
        if (null == mCurGPS) {
            return;
        }
        double lat = mCurGPS.lat;
        double lng = mCurGPS.lng;
        curPointLocation = MyLatLng.from(lat, lng);

        mMapPresenter.changeLocation(curPointLocation);

        if (mNeedRefresh) {
            mMapPresenter.mapClearOverlay();
            mMapPresenter.setMarker(curPointLocation, vTrackerLocationDot);
            mapAddCircleLocation();
            mNeedRefresh = false;
        }
        showInfoWindow();
    }


    /**
     * 绘制定位点位置及误差半径
     */
    protected void mapAddCircleLocation() {
        mMapPresenter.addCircleOverlay(curPointLocation);
    }

    private boolean hasRealLocation() {
        return curPointLocation != null;
    }

    /**
     * 修改infoWindow的信息，不要在里面添加其他不相关的操作
     */
    private void resetInfoWindow(Bitmap bitmap) {
        // 状态没有变化，直接将镜头移到定位点即可
        if (!mNeedRefresh) {
            mapAddLocationOverlay();
            return;
        }

        if (mCurTracker == null) {
            return;
        }
        setDefaultHeadIcon();
        resetMarkerView();
        // 重置图片资源
        if (bitmap == null) {
            ivTrackerHeadIcon.setImageResource(iDefaultHeadIconDrawableID);
            ivHeadImage.setImageResource(iDefaultHeadIconDrawableID);
        } else {
            ivTrackerHeadIcon.setImageBitmap(bitmap);
            ivHeadImage.setImageBitmap(bitmap);
        }

        tvMapPopTitle.setText(mCurTracker.nickname);
        tvMapPopSpeed.setVisibility(View.GONE);
        if (mRange == VALUE_RANGE_MOTO) {//只有620设备时才显示速度
            if (mCurGPS != null) {
                tvMapPopSpeed.setText(getString(R.string.over_speed_value, String.valueOf(mCurGPS.speed)));
                tvMapPopSpeed.setVisibility(View.VISIBLE);
            }
        }
        if (mCurGPS != null) {
            setTvMapPopTime(mCurGPS.collect_datetime);
            setTrackerBateray();
            setPopHeadStatus(mCurGPS.onlinestatus == 1);
            changeImageStatue(mCurGPS.onlinestatus == 1);
        }
        mapAddLocationOverlay();
    }

    private void setTvMapPopTime(String time) {
        if (TextUtils.isEmpty(time)) {
            return;
        }
        tvMapPopTime.setText(getString(R.string.final_location_time) + time);
    }

    private void showInfoWindow() {
        mMapPresenter.showInfoWindow(mInfoWindowContent, curPointLocation);
    }

    private void hideInfoWindow() {
        mMapPresenter.hideInfoWindow();
    }


    /**
     * 设置电量
     */
    private void setTrackerBateray() {
        if (mCurGPS == null) {
            return;
        }
        homeRequestUtil.setTrackerBateray(mCurGPS, mCurTracker, ivLocationWay, ivTrackElectric);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_locus://轨迹
                if (protocol_type == 8) {
                    CommonTrackActivity.NavigateTo(getContext(), true);
                    return;
                }
                if (homeRequestUtil.getTrackerType(mRange) == Constants.CAR_TYPE) {
                    startActivity(new Intent(getActivity(), MyCarTrackActivity.class));
                } else {
                    CommonTrackActivity.NavigateTo(getContext());
                }
                break;
            case R.id.ib_fence://围栏
                if (DeviceExpiredUtil.advancedFeatures(getActivity(), mCurTracker, true))//判断是否需要付费
                    return;
                Intent intent = new Intent();
                if (mCurTracker.product_type.equals("24") || mCurTracker.product_type.equals("30") || mCurTracker.product_type.equals("31")) {
                    intent.setClass(getActivity(), FenceSettingListActivity.class);
                } else {
                    intent.setClass(getActivity(), MapFenceEditActivity.class);
                }
                intent = buildFenceSettingIntent(intent);
                startActivity(intent);
                break;
            case R.id.ib_pet_walk://遛狗
                if (mCurTracker != null) {
                    if (Utils.isSuperUser(mCurTracker, getContext())) {
                        startActivity(new Intent(getActivity(), MyPetWalkActivity.class));
                    }
                }
                break;
            case R.id.ib_navigation://导航
                // TODO: 2019/7/8 代码耦合性太高，暂时不做
                if (curPointLocation == null) {
                    ToastUtil.show(getActivity(), R.string.no_location_point);
                    return;
                }
                toNavigator();
                break;
            case R.id.ll_location_info_window://信息显示框
                hideInfoWindow();
                break;
            case R.id.ib_map_swatch://卫星地图切换
                isMapTypeNormal = mMapPresenter.changeMapType(isMapTypeNormal);
                break;
            case R.id.ib_location://770定位
                getCurrentGPS();
                break;
            case R.id.ib_lock_car://锁车，
                if (DeviceExpiredUtil.advancedFeatures(getActivity(), mCurTracker, true))//判断是否需要付费
                    return;
                if (mCurGPS != null && mCurGPS.onlinestatus == 1) {
                    if (Utils.isSuperUser(mCurTracker, getContext())) {
                        if (mCurTracker != null) {
                            // 撤防
                            if (0 == mCurTracker.defensive) {
                                homeRequestUtil.lockVehicle();//锁车
                            } else {
                                homeRequestUtil.unlockVehicle();//解锁车
                            }
                        }
                    }
                } else {
                    if (mCurGPS != null)
                        ToastUtil.show(getContext(), R.string.online_unlock_the_car);
                }
                break;

            case R.id.ib_chat:// 微聊
                homeRequestUtil.isDeChat(ibchat_textunread);
                break;

            case R.id.ib_phone:
                mCurTracker = UserUtil.getCurrentTracker(getContext());
                if (mCurTracker != null)
                    homeRequestUtil.showPhoneDialog(mCurTracker);
                break;
        }
    }


    /**
     * 仪表盘obd点击回调
     */
    @Override
    public void onclickObdCallback(int position) {
        Intent intent = new Intent();
        switch (position) {
            case 0://车辆检测
                int car_status = 0;
                int onlinestatus = 0;
                if (mCurGPS != null) {
                    car_status = mCurGPS.car_status;
                    onlinestatus = mCurGPS.onlinestatus;
                }
                intent.setClass(getActivity(), ObdCarDetectActivity.class);
                intent.putExtra(Constants.VEHICLE_STATUS, car_status);
                intent.putExtra(Constants.ONLINESTATUS, onlinestatus);
                startActivity(intent);
                break;
            case 1://驾驶数据
                intent.setClass(getActivity(), ObdDriverActivity.class);
                startActivity(intent);
                break;
            case 2://历史轨迹
                intent.setClass(getActivity(), MyCarTrackActivity.class);
                startActivity(intent);
                break;
            case 3://围栏
                if (DeviceExpiredUtil.advancedFeatures(getContext(), mCurTracker, true)) {
                    return;
                }
                if (mCurTracker.product_type.equals(VALUE_PRODUCT_TYPE_HT_790)
                        || mCurTracker.product_type.equals(VALUE_PRODUCT_TYPE_K1)
                        || mCurTracker.product_type.equals(VALUE_PRODUCT_TYPE_790S)) {
                    intent.setClass(getActivity(), FenceSettingListActivity.class);
                } else {
                    intent.setClass(getActivity(), MapFenceEditActivity.class);
                }
                intent = buildFenceSettingIntent(intent);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onLocationClick() {
        iTabPosition = 0;//定位
        if (null == mCurTracker) {
            DialogUtil.showAddDevice(getActivity());
            return;
        }
        mNeedRefresh = true;
        getCurrentGPS();
    }

    /**
     * 每次切换设备会调用
     */
    @Override
    public void onChangeTracker(int position) {
        trackerChange();
        homeRequestUtil.isShowChatView(mCurTracker, ib_rl_chat);//绑定和解绑，点击选择设备后更新数据是否显示微聊图标
        if (null == mCurTracker) {
            mMapPresenter.mapClearOverlay();
            ibLocus.setVisibility(View.GONE);
            ibFence.setVisibility(View.GONE);
            ibPetWalk.setVisibility(View.GONE);
            ibNavigation.setVisibility(View.GONE);
            ib_phone.setVisibility(View.GONE);
            rlMap.setVisibility(View.VISIBLE);
            return;
        }

        curPointLocation = null;
        if (iTabPosition == 0) {
            getCurrentGPS();
        }
    }

    @Override
    public void onChangeTrackerClear() {//解绑设备
        mCurTracker = UserUtil.getCurrentTracker(getContext());
        sTrackerNo = "";
        mRange = VALUE_RANGE_PERSON;
        mMapPresenter.mapClearOverlay();
    }

    private void trackerChange() {
        Tracker tracker = UserUtil.getCurrentTracker(getContext());
        if (null == tracker) {
            return;
        }
        Constants.isChangeDevice = true;
        mCurTracker = tracker;
        homeRequestUtil.setRefreshTrack(mCurTracker);
        sTrackerNo = mCurTracker.device_sn;
        setRange(mCurTracker.ranges);
        protocol_type = mCurTracker.protocol_type;
        RequestLocationUtil.getRequestLocation(getContext()).startTimerPolling(sTrackerNo, url, true);//切换设备时更换设备号
        LogUtil.d("当前设备类型" + mRange);
        if (mRange == VALUE_RANGE_PET) {//是宠物有逛狗功能
            ibPetWalk.setVisibility(View.VISIBLE);
        } else {
            ibPetWalk.setVisibility(View.GONE);
        }
        if (mRange == VALUE_RANGE_OBD) {
            if (mVsCarView == null) {
                initCarView();
            }
            ViewUtil.setVisible(mVsCarView);
            ViewUtil.setGone(mViewMenu);
        } else {
            ViewUtil.setGone(mVsCarView);
            ViewUtil.setVisible(mViewMenu);
        }
        if (mRange != VALUE_RANGE_WATCH) {
            ib_phone.setVisibility(View.GONE);
        } else {
            ib_phone.setVisibility(View.VISIBLE);
        }
        setLockVehicleVisibility();
    }

    /**
     * 是否显示锁车和本地地位按钮
     */
    private void setLockVehicleVisibility() {
        if (mRange == VALUE_RANGE_CAR
                || mRange == VALUE_RANGE_MOTO
                || mRange == VALUE_RANGE_OBD) {
            //如果是汽车或摩托车显示锁车项
            ibLockVehicle.setVisibility(View.VISIBLE);
            if (mCurTracker != null) {
                tabLockChange(mCurTracker.defensive == 1); // 是否显示锁车
            }
        } else {
            ibLockVehicle.setVisibility(View.GONE);
        }

        if (mRange == VALUE_RANGE_WATCH) {
            if (protocol_type == PROTOCOL_TYPE_770
                    || protocol_type == PROTOCOL_TYPE_790
                    || protocol_type == PROTOCOL_TYPE_990) {
                ibLocation.setVisibility(View.GONE);//待开放
            } else {
                ibLocation.setVisibility(View.GONE);
            }
        } else {
            ibLocation.setVisibility(View.GONE);
        }
    }

    /**
     * 广播接收者
     */
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            LogUtil.i(intent.getAction());
            if (TextUtils.isEmpty(intent.getAction())) {
                return;
            }

            switch (intent.getAction()) {
                case Constants.ACTION_MAP_BAIDU_ONDESTORY:
                    LogUtil.d("小二，百度地图onDestroy");
                    break;
                case Constants.ACTION_TRACTER_NICKNAME_CHANGE:
                    mCurTracker = UserUtil.getCurrentTracker(getContext());
                    if (tvMapPopTitle != null) {
                        tvMapPopTitle.setText(mCurTracker.nickname);
                    }
                    break;
                case Constants.ACTION_TRACTER_RANGES_CHANGE:
                    onChangeTracker(0);
                    break;
                case Constants.ACTION_TRACTER_PICTURE_CHANGE:
                    mCurTracker = UserUtil.getCurrentTracker(getContext());
                    setHeadIconAndAddLocationOverlay();
                    break;
            }
        }

    };

    /**
     * 实时定位，点名
     *
     * @Description: 获取当前设备的gps地址
     */
    private void getCurrentGPS() {
        if (null == mCurTracker) {
            return;
        }
        mMapPresenter.mapClearOverlay();
        homeRequestUtil.getCurrentGPS(mRange);
    }

    private void setDefaultHeadIcon() {
        switch (mRange) {
            case VALUE_RANGE_PERSON:
                iDefaultHeadIconDrawableID = R.drawable.image_preson_sos;
                break;
            case VALUE_RANGE_PET:
                iDefaultHeadIconDrawableID = R.drawable.image_pet;
                break;
            case VALUE_RANGE_CAR:
            case VALUE_RANGE_OBD:
                iDefaultHeadIconDrawableID = R.drawable.image_car;
                break;
            case VALUE_RANGE_MOTO:
                iDefaultHeadIconDrawableID = R.drawable.image_motorcycle;
                break;
            default:
                iDefaultHeadIconDrawableID = R.drawable.image_watch;
                break;
        }
    }


    /**
     * @Description: 设置定位点头像，
     */
    protected void setHeadIconAndAddLocationOverlay() {
        if (!mMapPresenter.hasInitialized()) {
            return;
        }

        ivTrackerHeadIcon.setVisibility(View.VISIBLE);
        String head_portraitUrl = mCurTracker.head_portrait;
        String appendUrl = Utils.getImageUrl(getContext());
        if (Utils.isEmpty(head_portraitUrl) || TextUtils.isEmpty(appendUrl)) {
            resetInfoWindow(null);
            return;
        }

        String url = appendUrl + mCurTracker.head_portrait;
//        String url = "http://218.17.161.66:30033/image/upload/img/20190910/5F14C3892DB92DA9DBBB912BB830FC08_1568105870080.png";
        LogUtil.d("头像URL==" + url);
        Glide.with(getContext())
                .load(url)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        resetInfoWindow(bitmap);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        resetInfoWindow(null);
                    }
                });
    }

    /**
     * 改变mainActivity设备显示的状态和保存当前设备是否在线状态
     */
    private void changeImageStatue(boolean isStatues) {
        AppSP.getInstance().saveDeviceOnLine(getContext(), isStatues);
        getContext().sendBroadcast(new Intent(Constants.ACTION_ONLINE_TO_CHANGE_MAIN));
    }


    /**
     * 显示Pop头像是否在线
     */
    private void setPopHeadStatus(boolean online) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(online ? 1 : 0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        if (ivHeadImage != null) {
            ivHeadImage.setColorFilter(filter);
        }
        if (ivTrackerHeadIcon != null) {
            ivTrackerHeadIcon.setColorFilter(filter);
        }
    }

    /**
     * 实时定位，点名
     */
    @Override
    public void callCurrentGPS(CurrentGPS currentGPS) {
        if (currentGPS == null) {
            mMapPresenter.mapClearOverlay();
            mMapPresenter.mapLocation();
            return;
        }
        mCurGPS = currentGPS;
        setHeadIconAndAddLocationOverlay();
        homeRequestUtil.setDeviceInfo(mCurGPS, mCurTracker);
    }

    /**
     * 显示微聊未读消息
     */
    @Override
    public void callChatState() {
        unReadTotal();//显示微聊图标上的未读信息条数
    }

    @Override
    public void callLockVehicleState(boolean isLock) {
        tabLockChange(isLock);
        mCurTracker.defensive = isLock ? 1 : 0;
    }

    /**
     * 注册一个消息观察者
     */
    private void getUnreadMessage() {
        RongIM.getInstance().addUnReadMessageCountChangedObserver(observer, Conversation.ConversationType.GROUP);
    }

    private IUnReadMessageObserver observer = new IUnReadMessageObserver() {// 消息条数监听
        @Override
        public void onCountChanged(int position) {
            if (position == 0) {
                return;
            }
            unReadTotal();
        }
    };

    /**
     * 获取全部未读信息
     */
    private void unReadTotal() {
        if (mCurTracker == null || mCurTracker.device_sn == null || mCurTracker.isExistGroup == null) {
            return;
        }
        if (!TextUtils.equals(mCurTracker.isExistGroup, mCurTracker.device_sn)) {
            return;
        }
        RongIM.getInstance().getUnreadCount(
                Conversation.ConversationType.GROUP,
                mCurTracker.device_sn,
                new RongIMClient.ResultCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer index) {
                        if (index > 0) {
                            ibchat_textunread.setVisibility(View.VISIBLE);
                            ibchat_textunread.setText(String.valueOf(index));
                        } else {
                            ibchat_textunread.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode arg0) {
                    }
                });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contentView = null;
        if (listener != null) {
            locationManager.removeUpdates(listener);
        }
    }

    @Override
    public void handleLocationChanged(Location location) {
        if (location != null) {
            LogUtil.i("onLocationChanged():" + location.getLatitude() + " " + location.getLongitude());
            curPointCurLocation = MyLatLng.from(location);
            mMapPresenter.changeLocation(curPointCurLocation);
            UserUtil.setLocationLatLng(getContext(), location.getLatitude(), location.getLongitude());
        }
    }

    protected abstract MyMapPresenter onCreateMapPresenter(Context context);

    protected abstract void resetMarkerView();

    protected abstract MyLatLng getCorrectCarLatLng(double lat, double lng);

    protected abstract void toNavigator();


    private void handleCurrentLocation(Location location) {
        curPointCurLocation = MyLatLng.from(location);
        mMapPresenter.changeLocation(curPointCurLocation);
        UserUtil.setLocationLatLng(getContext(), location.getLatitude(), location.getLongitude());
    }


    private Intent buildFenceSettingIntent(Intent intent) {
        intent.putExtra(Constants.CURPOINTLOCATION, curPointLocation);
        return intent;
    }

    private void setCurPointLocation(double lat, double lng) {
        curPointLocation = MyLatLng.from(lat, lng);
    }

    public void setRange(int range) {
        this.mRange = range;
        mNeedRefresh = true;
    }


    @Override
    public void onPause() {
        DialogUtil.dismiss();
        if (!getActivity().isFinishing() && !getActivity().isDestroyed()) {
            ProgressDialogUtil.dismiss();
        }
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
        }
        super.onPause();
        mMapPresenter.onPause();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);// 解除eventbus绑定
        obdDashboard.unRegistEventBus();//注销obd仪表盘中的Eventbus
        homeRequestUtil.ondestoryRelease();
        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
        }
        if (null != observer) {
            RongIM.getInstance().removeUnReadMessageCountChangedObserver(observer);// 注销未读消息观察者监听
        }
        observer = null;
        ProgressDialogUtil.dismiss();
        super.onDestroy();
        mMapPresenter.onDestroy();
    }

}
