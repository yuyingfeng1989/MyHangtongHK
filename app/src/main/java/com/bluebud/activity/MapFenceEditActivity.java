package com.bluebud.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.app.App;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.constant.TrackerConstant;
import com.bluebud.info.GeofenceObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.map.model.MyMapPresenter;
import com.bluebud.utils.Constants;
import com.bluebud.utils.FenceRequestUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.PopupWindowUtils;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.CircleImageView;
import com.bluebud.view.LastInputEditText;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;


public class MapFenceEditActivity extends BaseFragmentActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, AbstractMapModel.MyMapReadyCallback {//, OnProgressDialogClickListener，OnGetSuggestionResultListener,OnWheeClicked,

    private MyMapPresenter mPresenter;

    private Tracker mCurTracker;
    private TextView tvFenceRange;//围栏范围
    private TextView tvFinishSetting;//提交
    private ImageView ivBack;//返回
    private CheckBox ibFenceSwitch;//开关
    private LastInputEditText etFenceName;//围栏名
    private int iRadius = 100;//围栏范围大小
    private MyLatLng mLocation;
    private MyLatLng mGeoFenceLocation; // 电子围栏定位点
    private View vTrackerLocationDot; // 在地图上的定位点视图
    private CircleImageView ivTrackerHeadIcon; // 定位点的设备头像
    private int iDefaultHeadIconDrawableID;//默认定位点头像
    private RelativeLayout mRlRangeSetting;
    private boolean hasFenceChanged; // 判断围栏是否有改动

    private PopupWindowUtils popupWindowUtils;
    private int iType;
    private FenceRequestUtil request;
    private GeofenceObj.DefenceList defenceList;//当前围栏信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_fence_edit);
        request = new FenceRequestUtil(this);
        defenceList = new GeofenceObj().new DefenceList();
        Intent intent = getIntent();
        if (intent != null) {
            mLocation = (MyLatLng) intent.getSerializableExtra(Constants.CURPOINTLOCATION);
            mGeoFenceLocation = mLocation;
        }
        init();
        mPresenter.onCreate(savedInstanceState);
        getGEOfence();
    }

    private void init() {
        initData();
        initLayout();
        initListener();
        initMapView();
    }

    private void initListener() {
        ivBack.setOnClickListener(this);

        tvFinishSetting.setOnClickListener(this);
        ibFenceSwitch.setOnClickListener(this);
        mRlRangeSetting.setOnClickListener(this);
    }

    private void initData() {
        mPresenter = new MyMapPresenter(this, App.getMapType());
        mCurTracker = UserUtil.getCurrentTracker(this);
        popupWindowUtils = new PopupWindowUtils(this);
    }

    private void initLayout() {
        ivBack = findViewById(R.id.iv_back);
        mRlRangeSetting = findViewById(R.id.rl_layout5);
        tvFenceRange = findViewById(R.id.tv_fence_range);
        tvFinishSetting = findViewById(R.id.tv_finish);
        ibFenceSwitch = findViewById(R.id.cb_switch_button);
        if (mCurTracker.ranges == TrackerConstant.VALUE_RANGE_OBD)
            findViewById(R.id.rl_title).setBackgroundColor(getResources().getColor(R.color.black));
        ibFenceSwitch.setOnCheckedChangeListener(this);
        switchMapFence(false);
        etFenceName = findViewById(R.id.et_fence_name);

    }

    private void initMapView() {

        FrameLayout llContent = findViewById(R.id.map);
        if (llContent == null) {
            finish();
            return;
        }

        if (App.getMapType() == App.MAP_TYPE_GMAP) {
            mPresenter.initMapView(this, R.id.map, this);
            return;
        }

        View mapView = mPresenter.getMapView(this);
        if (mapView == null) {
            finish();
            return;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        llContent.addView(mapView, params);
        onMapReady();

    }

    /**
     * 视图跳到设备所在地址
     */
    private void gotoCurrentGPS(boolean isShowLocation) {

        if (mGeoFenceLocation != null) {
            mPresenter.changeLocation(mLocation);
        }

        if (isShowLocation) {
            addCurrentDeviceOverlay();
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.rl_layout5:// 围栏范围值设置
                Utils.hiddenKeyboard(this, ivBack);
                popupWindowUtils.initPopupWindowRangeChoose(tvFenceRange.getText().toString(), new PopupWindowUtils.RangeChoose() {
                    @Override
                    public void click(String range) {
                        tvFenceRange.setText(range);
                        iRadius = Integer.parseInt(range);

                        mapClearOverlay();
                        mapAddLocationOverlay();
                        mapAddGeoFenceOverlay(mGeoFenceLocation);
                        switchMapFence(true);
                        popupWindowUtils.dismiss();
                    }
                });
                break;
            case R.id.tv_finish:// 完成
                Utils.hiddenKeyboard(this, ivBack);
                if (ibFenceSwitch.isChecked()) {
                    setFenceInfo();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    /**
     * 设置围栏信息到服务器
     */
    private void setFenceInfo() {
        if (!Utils.isOperate(this, mCurTracker)) {
            return;
        }
        setGEOfence(true);
    }

    /**
     * 初始化地图上设备显示点视图
     */
    private void initLocationMapInfoWindow() {
        vTrackerLocationDot = LayoutInflater.from(this).inflate(R.layout.layout_location_dot, null);
        ivTrackerHeadIcon = vTrackerLocationDot.findViewById(R.id.iv_tracker_headicon);
        if (mCurTracker != null) {
            iType = mCurTracker.ranges;
        }
        if (1 == iType) {
            iDefaultHeadIconDrawableID = R.drawable.image_preson_sos;
        } else if (2 == iType) {
            iDefaultHeadIconDrawableID = R.drawable.image_pet;
        } else if (3 == iType || 6 == iType) {
            iDefaultHeadIconDrawableID = R.drawable.image_car;
        } else if (4 == iType) {
            iDefaultHeadIconDrawableID = R.drawable.image_motorcycle;
        } else {
            iDefaultHeadIconDrawableID = R.drawable.image_watch;
        }

        ivTrackerHeadIcon.setImageResource(iDefaultHeadIconDrawableID);
    }


    /**
     * 加入当前设备所在位置标记
     */
    private void addCurrentDeviceOverlay() {

        if (mCurTracker == null || mLocation == null) {
            return;
        }
        LogUtil.d("设备定位点" + mLocation);
        initLocationMapInfoWindow();
        String sHeadIconUrl = Utils.getImageUrl(this) + mCurTracker.head_portrait;
        if (TextUtils.isEmpty(mCurTracker.head_portrait)) {
            ivTrackerHeadIcon.setImageDrawable(getResources().getDrawable(iDefaultHeadIconDrawableID));
            mapAddLocationOverlay();
            return;
        }
        Glide.with(this)
                .load(sHeadIconUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                        if (bitmap != null) {
                            ivTrackerHeadIcon.setImageBitmap(bitmap);
                        } else {
                            ivTrackerHeadIcon.setImageResource(iDefaultHeadIconDrawableID);// 下载失败，设置默认图片
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        ivTrackerHeadIcon.setImageResource(iDefaultHeadIconDrawableID);
                    }
                });
    }

    /**
     * 添加定位点
     */
    private void mapAddLocationOverlay() {
        initLocationMapInfoWindow();
        mPresenter.setMarker(mLocation, vTrackerLocationDot);
    }

    /**
     * 地图上加入围栏覆盖
     */
    private void mapAddGeoFenceOverlay(MyLatLng myLatLng) {
        if (myLatLng == null || mPresenter == null) {
            return;
        }
        LogUtil.d("mapAddGeoFenceOverlay " + myLatLng.latitude + " " + myLatLng.longitude + " iredius is " + iRadius);
        if (iRadius <= 0) {
            ToastUtil.show(this, getString(R.string.radius_error));
            return;
        }

        mPresenter.addCircleOverlay(myLatLng, iRadius);
        mPresenter.addCenterMarker(myLatLng);
    }

    /**
     * 开关打开状态
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Utils.hiddenKeyboard(this, ivBack);
        mapClearOverlay();
        mapAddLocationOverlay();
        if (isChecked) {// 如果打开开机，就以设备所在的地点为中心设置围栏，默认的半径为100
            mapAddGeoFenceOverlay(mGeoFenceLocation);
        } else {
            if (defenceList.defencestatus == 0) {
                return;
            }
            tvFenceRange.setTextColor(0xffbdbdbd);
            if (mCurTracker.product_type.equals(TrackerConstant.VALUE_PRODUCT_TYPE_HT_790)
                    || mCurTracker.product_type.equals(TrackerConstant.VALUE_PRODUCT_TYPE_K1)
                    || mCurTracker.product_type.equals(TrackerConstant.VALUE_PRODUCT_TYPE_790S)) {//调用设置围栏关闭接口
                setGEOfence(false);
            } else {//调用取消接口
                cancelGEOfence();
            }
        }
    }


    /**
     * 获取当前围栏信息接口
     */
    private void getGEOfence() {
        if (mCurTracker == null)
            return;
        if (mCurTracker.product_type.equals(TrackerConstant.VALUE_PRODUCT_TYPE_HT_790)
                || mCurTracker.product_type.equals(TrackerConstant.VALUE_PRODUCT_TYPE_K1)
                || mCurTracker.product_type.equals(TrackerConstant.VALUE_PRODUCT_TYPE_790S)) {
            defenceList = (GeofenceObj.DefenceList) getIntent().getSerializableExtra("DefenceList");
            if (mPresenter.hasInitialized()) {
                getFenceResult(defenceList);
            }
            return;
        }

        request.getGEOfence(mCurTracker.device_sn, new ChatCallbackResult() {
            @Override
            public void callBackResult(String result) {
                GeofenceObj mGeofenceObj = GsonParse.fenceSettingDataParse(result);
                if (mGeofenceObj == null) {
                    return;
                }
                int size = mGeofenceObj.defenceList.size() - 1;
                defenceList = mGeofenceObj.defenceList.get(size);
                if (mPresenter.hasInitialized()) {
                    getFenceResult(defenceList);
                }
            }

            @Override
            public void callBackFailResult(String result) {
                ToastUtil.show(MapFenceEditActivity.this, result);
                tvFenceRange.setText(String.valueOf(iRadius));
            }
        });
    }

    /**
     * 获取围栏信息设置结果
     */
    private void getFenceResult(GeofenceObj.DefenceList defenceList) {
        if (defenceList == null) {
            tvFenceRange.setText(String.valueOf(iRadius));
            return;
        }
        double cLat = defenceList.lat;
        double cLon = defenceList.lng;
        toGeoFenceLatLng(cLat, cLon);

        tvFenceRange.setText(String.valueOf(iRadius));
        tvFenceRange.setTextColor(getResources().getColor(R.color.black));
        etFenceName.setText(defenceList.defencename);
        switchMapFence(defenceList.defencestatus == 1);
    }


    /**
     * 设备围栏接口设置
     */
    private void setGEOfence(final boolean isCheck) {
        if (mGeoFenceLocation == null)
            return;
        defenceList.defencename = etFenceName.getText().toString().trim();
        boolean equals = (mCurTracker.product_type.equals("24") || mCurTracker.product_type.equals("30") || mCurTracker.product_type.equals("31"));
        MyLatLng myLatLng1 = mPresenter.gpsConvert2MapPoint(mLocation);
        int distance = (int) mPresenter.getDistance(mGeoFenceLocation, myLatLng1);
        boolean isOut = distance > iRadius;

        if (isOut && !equals) {
            ToastUtil.show(this, getString(R.string.radius_to_low));
            return;
        }
        MyLatLng myLatLng;
        myLatLng = mGeoFenceLocation;
        defenceList.lat = myLatLng.latitude;
        defenceList.lng = myLatLng.longitude;
        defenceList.radius = iRadius;//范围半径
        defenceList.defencestatus = isCheck ? 1 : 0;
        defenceList.isOut = isOut ? 1 : 0;
        if (!isCheck)
            defenceList.defencename = "";

        request.setGEOfence(mCurTracker.device_sn, defenceList, equals, new ChatCallbackResult() {
            @Override
            public void callBackResult(String result) {
                ToastUtil.show(MapFenceEditActivity.this, result);
                if (!isCheck) {
                    cancelGeoFenceSetting();
                    return;
                }
                switchMapFence(true);
                tvFenceRange.setTextColor(getResources().getColor(R.color.black));

//                mapAddGeoFenceOverlay(mGeoFenceLocation);//添加显示围栏
            }

            @Override
            public void callBackFailResult(String result) {
                ToastUtil.show(MapFenceEditActivity.this, result);
                if (!isCheck) {
                    switchMapFence(true);
                }
            }
        });
    }

    /**
     * 取消围栏设置接口
     */
    private void cancelGEOfence() {
        request.cancelGEOfence(mCurTracker.device_sn, new ChatCallbackResult() {
            @Override
            public void callBackResult(String result) {
                cancelGeoFenceSetting();
                ToastUtil.show(MapFenceEditActivity.this, result);
            }

            @Override
            public void callBackFailResult(String result) {
                switchMapFence(true);
                ToastUtil.show(MapFenceEditActivity.this, result);
            }
        });
    }

    /**
     * 取消围栏设置
     */
    private void cancelGeoFenceSetting() {
        mapClearOverlay();
        mapAddLocationOverlay();
        gotoCurrentGPS(false);
        switchMapFence(false);
        tvFenceRange.setText("");
        etFenceName.setText("");
        defenceList.defencename = "";
        defenceList.defencestatus = 0;
        iRadius = 100;
        tvFenceRange.setText(String.valueOf(iRadius));
        defenceList.radius = iRadius;
    }

    /**
     * 围栏坐标转换
     */
    private void toGeoFenceLatLng(double cLat, double cLon) {
        Log.e("TAG", "+cLat=" + cLat + "  +cLon=" + cLon);
        if (defenceList.radius != 0) {
            iRadius = defenceList.radius;
        }
        if ((cLat < 1 || cLon < 1)) {
            mGeoFenceLocation = mPresenter.gpsConvert2MapPoint(mLocation);
        } else {
            mGeoFenceLocation = MyLatLng.from(cLat, cLon);
        }
    }

    /**
     * 清除图层
     */
    public void mapClearOverlay() {
        mPresenter.mapClearOverlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ProgressDialogUtil.isShow()) {
            ProgressDialogUtil.dismiss();
        }
        mPresenter.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady() {
        initLocationMapInfoWindow();
        mPresenter.setMarker(mLocation, vTrackerLocationDot);
        gotoCurrentGPS(true);

        if (defenceList != null) {
            getFenceResult(defenceList);
        }

        mPresenter.setOnMapClickListener(new AbstractMapModel.MyMapClickListener() {
            @Override
            public void onClick(MyLatLng myLatLng) {
                if (!ibFenceSwitch.isChecked()) {
                    return;
                }
                mGeoFenceLocation = myLatLng;
                hasFenceChanged = true;
                // TODO: 2019/7/15 要抽出来
                mapClearOverlay();
                mapAddLocationOverlay();
                mapAddGeoFenceOverlay(mGeoFenceLocation);

            }
        });
        mPresenter.setOnMarkerClickListener(null);
    }

    private void switchMapFence(boolean status) {
        if (ibFenceSwitch != null) {
            ibFenceSwitch.setChecked(status);
        }
    }
}


