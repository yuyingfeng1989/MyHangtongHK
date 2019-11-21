package com.bluebud.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bluebud.app.App;
import com.bluebud.constant.TrackerConstant;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.info.HomePageInfo;
import com.bluebud.info.PeripherDetail;
import com.bluebud.info.Tracker;
import com.bluebud.info.User;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.map.model.MyMapPresenter;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.RequestHomePageUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.eventbus.EventBus;


public class HomePageActivity extends BaseActivity implements
        ProgressDialogUtil.OnProgressDialogClickListener,
        View.OnClickListener,
        AbstractMapModel.MyMapReadyCallback{
    MyMapPresenter mPresenter;
    private List<HomePageInfo> listInfo;
    private Map<String, String> mMarkerIdList;
    private List<Tracker> device_list;//设备列表
    private int imageView;//头像
    private int backImageView;//背景图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        EventBus.getDefault().register(this);

        findViewById(R.id.text_skip).setOnClickListener(this);
        init();

        mPresenter.onCreate(savedInstanceState);
    }

    /**
     * 获取数据
     */
    public void onEventMainThread(List<HomePageInfo> listInfo) {// Message
        this.listInfo = listInfo;
        if (mPresenter.hasInitialized()) {
            showMapBorder();
        }
        if (listInfo != null)
            LogUtil.d("onEventMainThread==" + listInfo.toString());
    }

    private void init() {
        RequestHomePageUtil.requestHomePageData(this);//请求综合首页数据列表
        mPresenter = new MyMapPresenter(this, App.getMapType());
        User user = UserSP.getInstance().getUserInfo(this);
        device_list = user.device_list;

        if (App.getMapType() == App.MAP_TYPE_GMAP) {
            mPresenter.initMapView(this, R.id.map, this);
            return;
        }

        ViewGroup rootView = findViewById(R.id.map);
        View view = mPresenter.getMapView(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        rootView.addView(view, params);
        onMapReady();
    }


    /**
     * 收集经纬度及全显示在手机上
     */
    private void showMapBorder() {
        if (listInfo == null || listInfo.size() == 0) {
            return;
        }
        List<MyLatLng> latLngList = new ArrayList<>();
        mMarkerIdList = new HashMap<>();
        for (HomePageInfo info: listInfo) {
            latLngList.add(MyLatLng.from(info.lat, info.lng));
            imageLoad(info);
        }

        mPresenter.setBoundByLatlngs(latLngList, 400);
        setMapClickListener();

    }


    /**
     * 加载图片显示
     */
    private void imageLoad(final HomePageInfo info) {
        if (info == null) {
            return;
        }
        if (TextUtils.isEmpty(info.head_portrait)) {
            showImageView(info, null);
            return;
        }

        Glide.with(this)
                .load(info.head_portrait)
                .asBitmap()
                .error(R.drawable.remote_photo)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                if (bitmap != null) {
                    bitmap = Utils.createBitmap(bitmap, info.online);
                }

                showImageView(info, bitmap);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                showImageView(info, null);
            }
        }); //方法中设置asBitmap可以设置回调类型
    }

    /**
     * 添加market
     */
    private void showImageView(HomePageInfo info, Bitmap icon) {
        if (info == null) {
            return;
        }

        View view = LayoutInflater.from(this).inflate(R.layout.homepage_layout_item, null);
        ImageView ivBg = view.findViewById(R.id.imageview);
        CircleImageView ivIcon = view.findViewById(R.id.circle_image);


        int online = info.online;

        switch (info.ranges) {
            case TrackerConstant.VALUE_RANGE_PERSON:
                backImageView = R.drawable.homepage_watcher;
                imageView = R.drawable.image_preson_sos;
                break;
            case TrackerConstant.VALUE_RANGE_WATCH:
            case TrackerConstant.VALUE_RANGE_BLUETOOTH_WATCH:
                backImageView = R.drawable.homepage_watcher;
                imageView = R.drawable.image_watch;
                break;
            case TrackerConstant.VALUE_RANGE_MOTO:
                backImageView = R.drawable.homepage_obd;
                imageView = R.drawable.image_motorcycle;
                break;
            case TrackerConstant.VALUE_RANGE_CAR:
            case TrackerConstant.VALUE_RANGE_OBD:
                backImageView = R.drawable.homepage_obd;
                imageView = R.drawable.image_car;
                break;
            case TrackerConstant.VALUE_RANGE_PET:
                backImageView = R.drawable.homepage_pet;
                imageView = R.drawable.image_pet;
                break;
            default:
                break;
        }

        if (online == 1) {//头像背景
            ivBg.setImageResource(backImageView);
        } else {
            ivBg.setImageResource(R.drawable.homepage_default);
        }

        if (icon != null) {
            ivIcon.setImageBitmap(icon);
        } else {
            ivIcon.setImageBitmap(Utils.createBitmap(BitmapFactory.decodeResource(getResources(), imageView), info.online));
        }

        mMarkerIdList.put(mPresenter.addMarker(new PeripherDetail(
                MyLatLng.from(info.lat, info.lng)), view, null), info.device_sn);

    }

    /**
     * 监听地图事件
     */
    private void setMapClickListener() {
        mPresenter.setOnMarkerClickListener(new AbstractMapModel.MyMarkerClickListener() {
            @Override
            public void onMarkClick(String id) {
                if (mMarkerIdList == null || mMarkerIdList.size() == 0) {
                    return;
                }
                if (mMarkerIdList.containsKey(id)) {
                    String deviceId = mMarkerIdList.get(id);
                    for (Tracker tracker : device_list) {//保存当前设备显示
                        if (TextUtils.equals(tracker.device_sn, deviceId)) {
                            UserUtil.saveCurrentTracker(HomePageActivity.this, tracker);
                            break;
                        }
                    }
                    Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
        ProgressDialogUtil.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        mPresenter.onDestroy();

        if (listInfo != null) {
            listInfo.clear();
            listInfo = null;
        }
        if (device_list != null) {
            device_list.clear();//设备列表
            device_list = null;
        }
    }

    @Override
    public void onProgressDialogBack() {
        ProgressDialogUtil.dismiss();
    }


    @Override
    public void onMapReady() {
        showMapBorder(); //加载地图
    }
}

