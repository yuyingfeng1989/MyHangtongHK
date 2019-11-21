package com.bluebud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.activity.settings.TrackerEditActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.PetWalkEndInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.petWalkInfo;
import com.bluebud.info.petWalkRecentGpsInfo;
import com.bluebud.info.petWalkStatusInfo;
import com.bluebud.info.unfinishWalkDogMapInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.model.AbstractMapModel;
import com.bluebud.map.walk.PetWalkManager;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DeviceExpiredUtil;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.List;



public class MyPetWalkActivity extends BaseActivity implements View.OnClickListener, AbstractMapModel.MyMapReadyCallback {//OnGetGeoCoderResultListener

    private PetWalkManager mWalkManager;
    private Tracker mCurTracker;
    private String sTrackerNo = "";
    private CurrentGPS mCurGPS;
    private LinearLayout llPetWalk;
    private RelativeLayout llPetWalkStart;

    private int mRange = 1;
    private String petWalkId;

    private TextView tvHourLong;
    private TextView tvMileage;
    private TextView tvCalorie;
    private int petWalkState = 0;//表示未开始
    private TextView mTvStart;

    private String lastDateTime;
    private String lastDateTime1;
    private List<CurrentGPS> walkDogMapGPS;
    private int markerStype = 0;//0表示普通点，1表示结束加的点

    private ImageView mNormal, mWave1, mWave2;
    private int i = 3;

    private AnimationSet mAnimationSet1, mAnimationSet2;
    private static final int OFFSET = 600;  //每个动画的播放时间间隔


    private final static int TIMING = 15 * 1000;
    private Handler timingHandler = new Handler();
    private Runnable timingRunnable = new Runnable() {

        @Override
        public void run() {
            recentGpsData();
            timingHandler.postDelayed(this, TIMING);
        }
    };
    private final static int TIMING1 = 1000;
    private Handler timingHandler1 = new Handler();
    private Runnable timingRunnable1 = new Runnable() {

        @Override
        public void run() {
            String diffTime = Utils.getDiffTime(null, Utils.getCurTime(), lastDateTime1);
            LogUtil.i("difftime=" + diffTime);
            tvHourLong.setText(diffTime);
            timingHandler1.postDelayed(this, TIMING1);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_my_pet_walk);
        init();
        getCurrentGPS();
        checkWhethePetWalkRecording();//检测是否有未结束的记录

        mWalkManager.getPresenter().onCreate(savedInstanceState);
    }

    public void init() {
        initData();
        initLayout();
        initListener();

    }

    private void initLayout() {
        llPetWalk = findViewById(R.id.rl_date_setting);
        tvHourLong = findViewById(R.id.tv_hour_long);
        tvMileage = findViewById(R.id.tv_mileage);
        tvCalorie = findViewById(R.id.tv_calorie);
        llPetWalkStart = findViewById(R.id.ll_pet_walk_start);
        mNormal = findViewById(R.id.normal);
        mWave1 = findViewById(R.id.wave1);
        mWave2 = findViewById(R.id.wave2);
        mTvStart = findViewById(R.id.tv_start);
        mWalkManager.addMapView(this, R.id.map, this);
    }

    private void initListener() {
        mNormal.setOnClickListener(this);
        getRight().setOnClickListener(this);
        getBaseTitleLeftBack().setOnClickListener(this);
    }

    private void initData() {
        mCurTracker = UserUtil.getCurrentTracker();
        if (mCurTracker == null) {
            finish();
        }
        LogUtil.d("getCurrentTracker success." + mCurTracker.ranges);
        sTrackerNo = mCurTracker.device_sn;
        mRange = mCurTracker.ranges;

        mWalkManager = PetWalkManager.newInstance(this);

        setBaseTitleText(R.string.pet_walk);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightSettingBackground(R.drawable.btn_pet_walk_edit_total_selector);
        setBaseTitleRightSettingVisible(View.VISIBLE);

        mAnimationSet1 = initAnimationSet();
        mAnimationSet2 = initAnimationSet();
    }




    @Override
    protected void onRestart() {
        super.onRestart();
        mCurTracker = UserUtil.getCurrentTracker();
    }

    // 退出当前页面
    private void exit() {
        DialogUtil.show(this, R.string.prompt,
                R.string.whether_end_dog_walk, R.string.confirm,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        endPetWalk();
                        DialogUtil.dismiss();
                        finish();
                    }
                }, R.string.cancel, new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (petWalkState == 1) {
                exit();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                if (petWalkState == 1) {
                    exit();
                } else {
                    finish();
                }

                break;
            case R.id.normal:
                if (mCurTracker == null) {
                    return;
                }
                if (petWalkState == 0) {//表示未开始
                    //判断是否需要收费
                    if (DeviceExpiredUtil.advancedFeatures(this, mCurTracker, true)) {
                        return;
                    }
                    if (mCurGPS == null) {
                        ToastUtil.show(this, getString(R.string.gps_empty));
                        return;
                    }

                    if (mCurGPS.onlinestatus != 1) {
                        ToastUtil.show(this, getString(R.string.noOnlinestatus));
                        return;
                    } else {
                        markerStype = 0;
                        lastDateTime = Utils.getCurTime();
                        lastDateTime1 = Utils.getCurTime();
                        startPetWalk();
                    }

                } else if (petWalkState == 1) {//表示已开始，是结束的状态
                    endPetWalk();
                    clearWaveAnimation();
                }
                break;
            case R.id.rl_title_right:
                if (DeviceExpiredUtil.advancedFeatures(this, mCurTracker, true)) {
                    return;
                }
                startActivity(new Intent(this, PetWalkRecordingActivity.class));
                break;
        }
    }


    private AnimationSet initAnimationSet() {
        AnimationSet set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1f, 2.3f,
                1f, 2.3f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(OFFSET * 3);
        scaleAnimation.setRepeatCount(Animation.INFINITE);// 设置循环
        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0.1f);
        alphaAnimation.setDuration(OFFSET * 3);
        alphaAnimation.setRepeatCount(Animation.INFINITE);//设置循环
        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);
        return set;
    }

    private void clearWaveAnimation() {
        mWave1.clearAnimation();
        mWave2.clearAnimation();
    }

    private void showWaveAnimation() {
        mWave1.startAnimation(mAnimationSet1);
        mAnimationSet2.setStartOffset(OFFSET);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mWalkManager.getPresenter().onPause();
    }

    @Override
    protected void onResume() {
        // MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
        super.onResume();
        mWalkManager.getPresenter().onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWalkManager.getPresenter().onDestroy();
        timingHandler.removeCallbacks(timingRunnable);
        timingHandler1.removeCallbacks(timingRunnable1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mWalkManager.getPresenter().onSaveInstanceState(outState);
    }

    private void mapAddRouteOverlay(List<CurrentGPS> walkDogMapGPS) {

        if (walkDogMapGPS == null || walkDogMapGPS.size() == 0) {
            return;
        }

        if (!mWalkManager.getPresenter().hasInitialized()) {
            return;
        }
        mWalkManager.getPresenter().mapClearOverlay();
        mWalkManager.getPresenter().mapAddRouteOverlay(mRange, walkDogMapGPS.toArray(new CurrentGPS[walkDogMapGPS.size()]));
    }

    //开始遛狗
    private void startPetWalk() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.startWalkDog(sTrackerNo);
        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            petWalkInfo petWalkParse = GsonParse.startPetWalkParse(new String(response));
                            if (petWalkParse != null) {
                                petWalkId = petWalkParse.id;
                                //表示已经开始了。状态变成结束
                                petWalkState = 1;//表示已开始
                                showWaveAnimation();
                                mNormal.setBackgroundResource(R.drawable.wave11);
                                mWave1.setBackgroundResource(R.drawable.wave22);
                                mWave2.setBackgroundResource(R.drawable.wave33);
                                mTvStart.setText(getString(R.string.end1));
                                mapClearOverlay();
                                if (walkDogMapGPS != null) {
                                    walkDogMapGPS.clear();
                                }
                                mapAddLocationOverlay();//把定位加回到地图上去
                                timingHandler.postDelayed(timingRunnable, TIMING);//开始轮询数据
                                timingHandler1.postDelayed(timingRunnable1, TIMING1);//开始更新时间
                            }
                        } else if (obj.code == 400) {
                            petWalkStatusInfo petWalkStatus = GsonParse.PetWalkParse(new String(response));
                            if (petWalkStatus == null) {
                                return;
                            }

                            int statusValue = petWalkStatus.statusValue;
                            if (statusValue == 1) {//设备信息不全，去完成信息卡
                                LogUtil.i("设备信息不全，去完成信息卡");
                                ToastUtil.show(MyPetWalkActivity.this, getString(R.string.finish_edit));
                                Intent trackerEditIntent = new Intent(MyPetWalkActivity.this,
                                        TrackerEditActivity.class);
                                trackerEditIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                                trackerEditIntent.putExtra("fromwhere", Constants.PETWALKACTIVITY);
                                startActivity(trackerEditIntent);

                                //new Intent(mContext, cls);
                            } else if (statusValue == 2) {//设备不在线，
                                LogUtil.i("设备不在线");
                                ToastUtil.show(MyPetWalkActivity.this, getString(R.string.no_online_no_pet_walk));
                            } else if (statusValue == 3) {//下发定位频率失败
                                LogUtil.i("下发定位频率失败");
                                ToastUtil.show(MyPetWalkActivity.this, getString(R.string.issued_positioning_failure_frequency));
                            }
                        } else {
                            ToastUtil.show(MyPetWalkActivity.this, obj.what);
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
                    }
                });
    }


    //结束遛狗
    private void endPetWalk() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.endWalkDog(sTrackerNo, petWalkId);
        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);

                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null) {

                            return;
                        }
                        if (obj.code == 0) {
                            PetWalkEndInfo endDataParse = GsonParse.petWalkEndDataParse(new String(response));
                            if (endDataParse != null) {
                                petWalkState = 0;
                                mNormal.setBackgroundResource(R.drawable.wave1);
                                mWave1.setBackgroundResource(R.drawable.wave2);
                                mWave2.setBackgroundResource(R.drawable.wave3);
                                mTvStart.setText(getString(R.string.start1));
                                timingHandler.removeCallbacks(timingRunnable);//结束轮询数据
                                timingHandler1.removeCallbacks(timingRunnable1);//结束轮询数据
                                llPetWalk.setVisibility(View.GONE);
                                llPetWalkStart.setVisibility(View.GONE);
                                String spendtime;
                                String mileage;
                                String calorie;
                                if (endDataParse.spendtime != null) {
                                    spendtime = endDataParse.spendtime;
                                } else {
                                    spendtime = "--";
                                }
                                if (endDataParse.mileage != null) {
                                    mileage = endDataParse.mileage + "km";
                                } else {
                                    mileage = "--km";
                                }
                                if (endDataParse.calorie != null) {
                                    calorie = endDataParse.calorie + "Cal";
                                } else {
                                    calorie = "--Cal";
                                }
                                tvHourLong.setText(spendtime);
                                tvCalorie.setText(calorie);
                                tvMileage.setText(mileage);

                                LogUtil.i("calorie=" + endDataParse.calorie + ",spendtime" + endDataParse.spendtime + ",mileage=" + endDataParse.mileage + ",walkDogScore" + endDataParse.walkDogScore);

                                DialogUtil.show(MyPetWalkActivity.this, endDataParse.walkDogScore, spendtime, mileage, calorie, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogUtil.dismiss();
                                        llPetWalk.setVisibility(View.VISIBLE);
                                        llPetWalkStart.setVisibility(View.VISIBLE);
                                    }
                                });

                                if (endDataParse.walkDogMap == null) {
                                    return;
                                }
                                if (walkDogMapGPS != null) {
                                    walkDogMapGPS.clear();
                                }

                                walkDogMapGPS = endDataParse.walkDogMap;
                                LogUtil.i("walkDogMapGPS size=" + walkDogMapGPS.size());
                                mapAddRouteOverlay(walkDogMapGPS);//加上遛狗轨迹图
                            }

                        } else if (obj.code == 400) {
                            petWalkStatusInfo petWalkStatus = GsonParse.PetWalkParse(new String(response));
                            if (petWalkStatus == null) {
                                return;
                            }
                            int statusValue = petWalkStatus.statusValue;
                            if (statusValue == 1) {//设备信息不全，去完成信息卡
                                LogUtil.e("设备信息不全，去完成信息卡");
                            } else if (statusValue == 2) {//设备不在线，
                                LogUtil.e("设备不在线");

                            } else if (statusValue == 3) {//下发定位频率失败
                                LogUtil.e("下发定位频率失败");
                            }
                        } else {
                            ToastUtil.show(MyPetWalkActivity.this, obj.what);
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
                    }
                });
    }


    //检测是否有未结束遛狗记录
    private void checkWhethePetWalkRecording() {
        if (mCurTracker == null) {
            return;
        }
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.unfinishWalkDog(sTrackerNo);
        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);

                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null) {
                            return;
                        }
                        if (obj.code != 0) {
                            ToastUtil.show(MyPetWalkActivity.this, obj.what);
                        }

                        final unfinishWalkDogMapInfo unfinishParse = GsonParse.petWalkUnfinishParse(new String(response));
                        if (unfinishParse == null) {
                            return;
                        }

                        if (unfinishParse.unfinishWalkDogMap != null) {
                            DialogUtil.show(MyPetWalkActivity.this, R.string.end_dog_walk,
                                    R.string.unfinish_pet_walk, R.string.end_dog_walk,
                                    new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {//结束上次遛狗
                                            LogUtil.i("id=" + unfinishParse.unfinishWalkDogMap.id + ",device_sn=" + unfinishParse.unfinishWalkDogMap.device_sn);
                                            petWalkId = unfinishParse.unfinishWalkDogMap.id;
                                            sTrackerNo = unfinishParse.unfinishWalkDogMap.device_sn;
                                            endPetWalk();

                                            DialogUtil.dismiss();
                                        }
                                    }, R.string.cancel, new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {//取消
                                            LogUtil.i("id=" + unfinishParse.unfinishWalkDogMap.id + ",device_sn=" + unfinishParse.unfinishWalkDogMap.device_sn);
                                            petWalkId = unfinishParse.unfinishWalkDogMap.id;
                                            sTrackerNo = unfinishParse.unfinishWalkDogMap.device_sn;
                                            //表示有未结束的遛狗记录，要先结束上次的遛狗
                                            petWalkState = 1;//表示已开始
                                            mNormal.setBackgroundResource(R.drawable.wave11);
                                            mWave1.setBackgroundResource(R.drawable.wave22);
                                            mWave2.setBackgroundResource(R.drawable.wave33);
                                            mTvStart.setText(getString(R.string.end1));
                                            showWaveAnimation();
                                            DialogUtil.dismiss();
                                        }
                                    });
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
                    }
                });
        ;
    }

    //轮询遛狗数据
    private void recentGpsData() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.recentGpsData(sTrackerNo, lastDateTime);
        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
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
                            petWalkRecentGpsInfo gpsDataParse = GsonParse.recentGpsDataParse(new String(response));
                            if (gpsDataParse != null) {
                                List<CurrentGPS> gpsDataMap = gpsDataParse.recentGpsDataMap;
                                if (null != gpsDataMap && gpsDataMap.size() > 0) {
                                    LogUtil.i("gpsdataMAP size=" + gpsDataMap.size());
                                    lastDateTime = gpsDataMap.get(gpsDataMap.size() - 1).collect_datetime;
                                    if (walkDogMapGPS == null) {
                                        walkDogMapGPS = gpsDataMap;
                                    } else {
                                        walkDogMapGPS.addAll(gpsDataMap);
                                    }

                                    mapAddRouteOverlay(walkDogMapGPS);


                                }
                            }
                        } else {
                            ToastUtil.show(MyPetWalkActivity.this, obj.what);
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
                    }
                });
        ;
    }


    /**
     * 清除图层
     */
    public void mapClearOverlay() {
        mWalkManager.getPresenter().mapClearOverlay();
    }

    /**
     * 实时定位
     */
    private void getCurrentGPS() {

        if (null == mCurTracker) {
            return;
        }
        if (Utils.serialNumberRange719(mCurTracker.ranges,
                mCurTracker.device_sn) && 4 == mCurTracker.onlinestatus) {
            ToastUtil.show(MyPetWalkActivity.this, R.string.dormancy_ing);
            return;
        }
        // TODO: 2019/7/15 需要去掉吗？
        mapClearOverlay();

        LogUtil.i("sTrackerNo is:" + sTrackerNo);
        String url = UserUtil.getServerUrl(MyPetWalkActivity.this);
        RequestParams params = HttpParams.currentGPS(sTrackerNo, Utils.getCurTime(MyPetWalkActivity.this));
        HttpClientUsage.getInstance().post(MyPetWalkActivity.this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(MyPetWalkActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null) {
                            return;
                        }
                        if (obj.code == 0) {
                            CurrentGPS currentGPS = GsonParse
                                    .currentGPSParse(new String(response));
                            if (currentGPS == null) {
                                return;
                            }
                            LogUtil.v("gps date: battery " + currentGPS.battery
                                    + " lat " + currentGPS.lat + " lng:"
                                    + currentGPS.lng + " mileage:"
                                    + currentGPS.mileage);
                            if (0 == currentGPS.lat && 0 == currentGPS.lng) {
                                ToastUtil.show(MyPetWalkActivity.this,
                                        R.string.nodate_location);
                                return;
                            }
                            mCurGPS = currentGPS;
                            mapAddLocationOverlay();
                        } else {
                            mapClearOverlay();
                            if (null != obj.what) {
                                ToastUtil.show(MyPetWalkActivity.this, obj.what);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(MyPetWalkActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    //加定位点
    private void mapAddLocationOverlay() {
        if (null == mCurGPS) {
            return;
        }
        double lat = mCurGPS.lat;
        double lng = mCurGPS.lng;
        LogUtil.i("WGS坐标：" + lat + "," + lng);

        mWalkManager.getPresenter().mapAddRouteOverlay(mRange, mCurGPS);
        mWalkManager.getPresenter().changeLocation(MyLatLng.from(mCurGPS.lat, mCurGPS.lng));
    }

    @Override
    public void onMapReady() {
        // TODO: 2019/7/23 这里怎么没有实现？
    }
}

