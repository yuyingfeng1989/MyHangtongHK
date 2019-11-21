package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.info.PeripherDetail;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.map.model.MyMapPresenter;

import java.util.List;


public class PoiDetailActivity extends BaseActivity{
    public static final String EXTRA_KEY_POI_LIST = "poiDetailList";
    public static final String EXTRA_KEY_MY_LOCATION = "myLocation";

    private MyMapPresenter mPresenter;
    private MyLatLng mLocation;

    private View mInfoWindowContent;
    private TextView tvMapPopTitle;
    private TextView tvMapPopSnippet;
    private RelativeLayout mRlContent;

    private List<PeripherDetail> mPoiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_poi_detail);
        init();
        mPresenter.onCreate(savedInstanceState);
    }

    private void init() {
        handleIntent();
        mPresenter  = new MyMapPresenter(this, MyMapPresenter.MAP_TYPE_AMAP);
        initMap();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mPoiList = (List<PeripherDetail>) intent.getSerializableExtra(EXTRA_KEY_POI_LIST);
            if (mPoiList == null || mPoiList.size() == 0) {
                finish();
            }
            mLocation = (MyLatLng) getIntent().getSerializableExtra(EXTRA_KEY_MY_LOCATION);

        }
    }

    private void initMap() {
        mRlContent = findViewById(R.id.rl_map_content);
        mInfoWindowContent = LayoutInflater.from(this).inflate(
                R.layout.map_pop_info1, null);
        tvMapPopTitle = mInfoWindowContent
                .findViewById(R.id.map_info_title);
        tvMapPopSnippet = mInfoWindowContent
                .findViewById(R.id.map_info_snippet);

        View mapView = mPresenter.getMapView(null);
        if (mapView == null) {
            return;
        }

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mRlContent.addView(mapView, lp);

        View poiView = LayoutInflater.from(this).inflate(R.layout.layout_poi_marker, null);

        mPresenter.setMarker(mLocation, null);

        AbstractMapModel.OnInfoWindowChangedListener listener = new AbstractMapModel.OnInfoWindowChangedListener() {
            @Override
            public View OnInfoWindowChanged(PeripherDetail detail) {
                tvMapPopTitle.setText(detail.name);
                tvMapPopSnippet.setText(detail.address);
                return mInfoWindowContent;
            }
        };

        if (mPoiList.size() == 1) {

            mPresenter.addMarker(mPoiList.get(0), poiView, listener);
            mPresenter.changeLocationByBound(mPoiList.get(0).toLatLng());
        } else {
            mPresenter.addMarkers(mPoiList, poiView, listener);
            mPresenter.changeLocationByBound(mLocation);

        }

    }

    @Override
    protected void onPause() {
        mPresenter.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mPresenter.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}
