package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bluebud.app.App;
import com.bluebud.info.PeripherDetail;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyGeocodeCallback;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.map.model.MyMapPresenter;


public class MyAlarmDetailActivity extends BaseActivity implements
        View.OnClickListener, AbstractMapModel.MyMapReadyCallback {
    private String sTime;
    private double speed;
    private String mAddress;
    private MyLatLng mLocation;
    private MyMapPresenter mPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_alarm_detail);
        init();
        mPresenter.onCreate(savedInstanceState);
    }

    private void init() {
        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            sTime = intent.getStringExtra("dtime");
            double lat = intent.getDoubleExtra("lat", 0);
            double lng = intent.getDoubleExtra("lng", 0);
            mLocation = MyLatLng.from(lat, lng);
            speed = intent.getDoubleExtra("speed", 0);
            mAddress = intent.getStringExtra("address");
        }

        mPresenter = new MyMapPresenter(this, App.getMapType());
        initMap();
    }

    private void initMap() {

        if (App.getMapType() == App.MAP_TYPE_GMAP) {
            mPresenter.initMapView(this, R.id.map, this);
        } else {
            ViewGroup rootView = findViewById(R.id.map);
            View view = mPresenter.getMapView(this);

            if (rootView == null || view == null) {
                finish();
                return;
            }

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            rootView.addView(view, params);
            onMapReady();
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        }
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
        mPresenter.onDestroy();

    }

    @Override
    public void onMapReady() {

        if (!TextUtils.isEmpty(mAddress)) {
            mapAddMarker(mAddress);
            return;
        }
        mPresenter.setGeoSearchCallback(mLocation.latitude, mLocation.longitude, new MyGeocodeCallback() {
            @Override
            public void onGetAddressSucceed(String address) {
                mapAddMarker(address);
            }
        });
    }

    private void mapAddMarker(String address) {
        PeripherDetail detail = new PeripherDetail(mLocation);
        detail.name = sTime + " " + getString(R.string.speed_unit, speed + "");
        detail.address = address;

        mPresenter.addMarker(detail, null, null);
        mPresenter.changeLocation(mLocation);
    }
}
