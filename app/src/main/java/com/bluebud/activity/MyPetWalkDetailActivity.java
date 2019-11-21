package com.bluebud.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bluebud.app.App;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.DogTrailMap;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.petWalkRecordingDetailInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.map.model.MyMapPresenter;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.List;



public class MyPetWalkDetailActivity extends BaseActivity implements View.OnClickListener, AbstractMapModel.MyMapReadyCallback {


    private MyMapPresenter mPresenter;
    private String sTrackerNo = "";

    private int mRange = 1;

    private List<CurrentGPS> walkDogMapGPS;
    private TextView tv_fraction;
    private TextView tv_point;
    private TextView tv_hour_long;
    private TextView tv_mileage;
    private TextView tv_calorie;
    private DogTrailMap trailMap;
    private String start_time;
    private String end_time;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_my_pet_walk_detail);
        getData();
        init();
        initMapView();
        mPresenter.onCreate(savedInstanceState);
        getWalkDogTrailDetail();


    }

    private void getData() {
        trailMap = (DogTrailMap) getIntent().getSerializableExtra("trailMap");
        if (trailMap != null) {
            start_time = trailMap.start_time;
            end_time = trailMap.end_time;
            id = trailMap.id;
        }
    }

    /**
     *
     */
    public void init() {
        initData();
        initLayout();
        initListener();
    }

    private void initData() {
        Tracker mCurTracker = UserUtil.getCurrentTracker();
        if (null == mCurTracker) {
            finish();
            return;
        }
        sTrackerNo = mCurTracker.device_sn;
        mRange = mCurTracker.ranges;
        mPresenter = new MyMapPresenter(this, App.getMapType());
    }

    private void initLayout() {
        setBaseTitleText(R.string.recording);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightSettingVisible(View.GONE);

        tv_fraction = findViewById(R.id.tv_fraction);
        tv_point = findViewById(R.id.tv_point);
        tv_hour_long = findViewById(R.id.tv_hour_long);
        tv_mileage = findViewById(R.id.tv_mileage);
        tv_calorie = findViewById(R.id.tv_calorie);
    }

    private void initListener() {
        getBaseTitleLeftBack().setOnClickListener(this);
        getRight().setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rl_title_back) {
            finish();
        }
    }


    private void initMapView() {

        if (App.getMapType() == App.MAP_TYPE_GMAP) {
            mPresenter.initMapView(this, R.id.map, this);
            return;
        }

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


    private void mapLocation() {
        mPresenter.mapLocation();
    }

    @Override
    public void onMapReady() {
        mapLocation();
        if (walkDogMapGPS != null && walkDogMapGPS.size() > 0) {
            mapAddRouteOverlay(walkDogMapGPS);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }


    private void mapAddRouteOverlay(List<CurrentGPS> walkDogMapGPS) {
        int size = walkDogMapGPS.size();
        LogUtil.i("size=" + size);
        mapClearOverlay();
        mPresenter.mapAddRouteOverlay(mRange, walkDogMapGPS.toArray(new CurrentGPS[walkDogMapGPS.size()]));

    }

    /**
     * 清除图层
     */
    public void mapClearOverlay() {
       mPresenter.mapClearOverlay();
    }


    private void getWalkDogTrailDetail() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.getwalkDogTrailDetail(sTrackerNo, start_time, end_time, id);
        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(MyPetWalkDetailActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            petWalkRecordingDetailInfo walkTrailDetail = GsonParse.getwalkDogTrailDetailParse(new String(
                                    response));
                            if (walkTrailDetail != null) {

                                if (walkTrailDetail.gps != null) {
                                    walkDogMapGPS = walkTrailDetail.gps;
                                    if (mPresenter.hasInitialized()) {
                                        mapAddRouteOverlay(walkDogMapGPS);
                                    }

                                    setData(walkTrailDetail);
                                }

                            }

                        } else {
                            ToastUtil.show(MyPetWalkDetailActivity.this, obj.what);
                        }
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });

    }


    private void setData(petWalkRecordingDetailInfo walkTrailDetail) {
        if (walkTrailDetail.spendtime != null) {
            tv_hour_long.setText(trailMap.spendtime);
        } else {
            tv_hour_long.setText("--");
        }
        if (walkTrailDetail.mileage != null) {
            tv_mileage.setText(trailMap.mileage + "km");
        } else {
            tv_mileage.setText("--km");
        }
        if (walkTrailDetail.calorie != null) {
            tv_calorie.setText(trailMap.calorie + "Cal");
        } else {
            tv_calorie.setText("--Cal");
        }
        tv_fraction.setText(walkTrailDetail.walkDogScore + "");

        if (walkTrailDetail.walkDogScore >= 100) {//100分
            tv_point.setText(getResources().getString(R.string.pet_walk_point));
        } else if (walkTrailDetail.walkDogScore >= 81 && walkTrailDetail.walkDogScore <= 99) {//81-99
            tv_point.setText(getResources().getString(R.string.pet_walk_point80));
        } else if (walkTrailDetail.walkDogScore >= 31 && walkTrailDetail.walkDogScore <= 80) {//31-80
            tv_point.setText(getResources().getString(R.string.pet_walk_point30));
        } else {
            tv_point.setText(getResources().getString(R.string.pet_walk_point29));
        }
    }
}

