package com.bluebud.utils.request;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.activity.settings.TrackerEditActivity;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.CarInfo;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.DeviceInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.listener.IHomeFragment;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DeviceExpiredUtil;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.Arrays;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;

/**
 * Created by Administrator on 2019/4/24.
 */

public class HomeRequestUtil implements ProgressDialogUtil.OnProgressDialogClickListener {
    private Context mContext;
    private IHomeFragment iHomeFragment;
    private RequestHandle requestHandle;
    private Tracker mCurTracker;
    private String url;

    public HomeRequestUtil(Context context, IHomeFragment iHomeFragment, Tracker mCurTracker, String url) {
        this.mContext = context;
        this.iHomeFragment = iHomeFragment;
        this.mCurTracker = mCurTracker;
        this.url = url;
    }

    public void setRefreshTrack(Tracker track) {
        this.mCurTracker = track;
    }

    /**
     * 是否是德国用户
     */
    String isDe;

    public void isDeChat(final TextView ibchat_textunread) {
        isDe = Utils.getHashMap().get(mCurTracker.device_sn);
        if (TextUtils.isEmpty(isDe)) {
            ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(18, null, mCurTracker.device_sn, null, null, null, null, null, null, new ChatCallbackResult() {
                @Override
                public void callBackResult(String result) {
                    String backString = (String) ChatHttpParams.getInstallSigle(mContext).getParseResult(18, result);
                    if (TextUtils.isEmpty(backString))
                        backString = "0";
                    Utils.getHashMap().put(mCurTracker.device_sn, backString);
                    intentChat(backString, ibchat_textunread);
                }

                @Override
                public void callBackFailResult(String result) {
                    ToastUtil.show(mContext, result);
                }
            });
        } else {
            intentChat(isDe, ibchat_textunread);
        }
    }

    /**
     * 跳转到微聊界面
     */
    private void intentChat(String isDe, TextView ibchat_textunread) {
        if ("3".equals(isDe) && DeviceExpiredUtil.advancedFeatures(mContext, mCurTracker, true))
            return;
        else if (RongIM.getInstance() != null) {
            RongIM.getInstance().startGroupChat(mContext, mCurTracker.device_sn, "微聊");
            ibchat_textunread.setText("");
            ibchat_textunread.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @Title: getCarDate
     * @Description: 获取汽车数据，只有odb设备才会调用到此方法
     */
    public void getCarDate() {
        RequestParams params = HttpParams.getCarData(mCurTracker.device_sn, Utils.getCurTime(mContext));
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null) {
                            return;
                        }
                        if (obj.code == 0) {
                            CarInfo carInfo = GsonParse.carInfoParse(new String(response));
                            if (carInfo == null) {
                                errorCar();
                                return;
                            }
                            EventBus.getDefault().post(carInfo);//传送到OBDDashboardUtil
                        } else {
                            errorCar();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        errorCar();
                    }
                });
    }

    /**
     * 请求出错时
     */
    private void errorCar() {
        CarInfo carInfo = new CarInfo();
        CarInfo.MileageAndFuel mf = new CarInfo.MileageAndFuel();
        mf.rotationRate = 0;
        mf.carStatus = 0;
        mf.fuel = 0;
        mf.mileage = 0;
        mf.speed = 0;
        mf.totalmileage = 0;
        carInfo.mileageAndFuel = mf;
        EventBus.getDefault().post(carInfo);
    }

    /**
     * 实时定位，点名
     *
     * @Description: 获取当前设备的gps地址
     */
    public void getCurrentGPS(int iType) {
        if (null == mCurTracker) {
            return;
        }
        if (Utils.serialNumberRange719(mCurTracker.ranges, mCurTracker.device_sn) && 4 == mCurTracker.onlinestatus) {
            ToastUtil.show(mContext, R.string.dormancy_ing);
            return;
        }

        // 如果是汽车类型，则在获取gps数据的同时也去获取汽车数据
        if (getTrackerType(iType) == Constants.CAR_TYPE) {
            getCarDate();
        }
        RequestParams params = HttpParams.currentGPS(mCurTracker.device_sn, Utils.getCurTime(mContext));
        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            CurrentGPS currentGPS = GsonParse.currentGPSParse(new String(response));
                            if (currentGPS == null || 0 == currentGPS.lat)
                                return;
                            iHomeFragment.callCurrentGPS(currentGPS);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    /**
     * 锁定车辆
     */
    public void lockVehicle() {
        RequestParams params = HttpParams.lockVehicle(mCurTracker.device_sn);
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;

                        if (obj.code == 0) {
                            iHomeFragment.callLockVehicleState(true);
                            mCurTracker.defensive = 1;
                            UserUtil.changeTrackerList(mContext, mCurTracker);
                        }
                        ToastUtil.show(mContext, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    /**
     * 解锁车辆
     */
    public void unlockVehicle() {
        RequestParams params = HttpParams.unlockVehicle(mCurTracker.device_sn);
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;

                        if (obj.code == 0) {
                            iHomeFragment.callLockVehicleState(false);
                            mCurTracker.defensive = 0;
                            UserUtil.changeTrackerList(mContext, mCurTracker);
                        }
                        ToastUtil.show(mContext, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    /**
     * 是否显示聊天按钮
     */
    public void isShowChatView(Tracker event, RelativeLayout ib_rl_chat) {
        if (event == null) {//无设备时不显示微聊
            ib_rl_chat.setVisibility(View.GONE);
            return;
        }
        String chatType = UserSP.getInstance().getChatType(mContext);
        if (TextUtils.isEmpty(chatType)) {//没有微聊功能的设备不显示
            ib_rl_chat.setVisibility(View.GONE);
            return;
        }

        List<String> list = Arrays.asList(chatType.split(","));//截取包含微聊设备类型集合
        String isExistGroup = event.isExistGroup;//是否显示微聊图标空不显示，否则显示
        LogUtil.e("chatType==" + chatType + "isExistGroup==" + isExistGroup);
        if (list == null || TextUtils.isEmpty(isExistGroup)) {
            ib_rl_chat.setVisibility(View.GONE);//隐藏微聊图标
            return;
        }

        if (list.contains(event.product_type) && mCurTracker.device_sn.equals(isExistGroup)) {// 判断是否是
            iHomeFragment.callChatState();//显示未读显示数
            ib_rl_chat.setVisibility(View.VISIBLE);
        } else
            ib_rl_chat.setVisibility(View.GONE);//隐藏微聊图标
    }

    /**
     * 设置设备信息
     */
    public void setDeviceInfo(CurrentGPS mCurGPS, Tracker mCurTracker) {
        DeviceInfo deviceInfo = new DeviceInfo();
        if (mCurGPS == null) {
            return;
        }
        deviceInfo.todayMileage = mCurGPS.mileage;
        deviceInfo.calorie = mCurGPS.calorie;
        deviceInfo.totalMileage = mCurGPS.totalMileage;
        deviceInfo.step = mCurGPS.step;
        deviceInfo.battery = mCurGPS.battery;
        mCurTracker.mDeviceInfo = deviceInfo;
        UserUtil.setTrackerDeviceInfo(mContext, mCurTracker);
    }

    /**
     * 注册广播接受者
     */
    public void regesterBroadcast(BroadcastReceiver broadcastReceiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_TRACTER_RANGES_CHANGE);//UserInfoActivity发出广播改变设备使用类型
        filter.addAction(Constants.ACTION_TRACTER_PICTURE_CHANGE);//TrackerEditActivity发过来的头像改变
        filter.addAction(Constants.ACTION_MAP_GOOGLE_ONDESTORY);
        filter.addAction(Constants.ACTION_BLUETOOTH_DATA);//蓝牙手表数据更新
        filter.addAction(Constants.ACTION_BLUETOOTH_SOS);////蓝牙手表发出sos
        filter.addAction(Constants.ACTION_BLUETOOTH_CONTINUOUS_HEART_RATE_SUCCESS);////连续心续
        filter.addAction(Constants.ACTION_TRACTER_NICKNAME_CHANGE);// TrackerEditActivity发出广播
        filter.addAction(Constants.BLUETOOTH_GET_0_NO_DATA);// 蓝牙手表get0无数据时
        mContext.registerReceiver(broadcastReceiver, filter);
    }

    /**
     * 获取Location Provider
     */
    public String getProvider(LocationManager locationManager) {
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 是否查询海拨：否
        criteria.setAltitudeRequired(false);
        // 是否查询方位角 : 否
        criteria.setBearingRequired(false);
        // 是否允许付费：是
        criteria.setCostAllowed(true);
        // 电量要求：低
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        // 返回最合适的符合条件的provider，第2个参数为true说明 , 如果只有一个provider是有效的,则返回当前provider
        return locationManager.getBestProvider(criteria, true);
    }

    /**
     * 设置电量
     */
    public void setTrackerBateray(CurrentGPS mCurGPS, Tracker mCurTracker, ImageView ivLocationWay, ImageView ivTrackElectric) {
        if (mCurGPS == null) {
            return;
        }
        float iTrackerBateray = mCurGPS.battery;
        LogUtil.e("电量：" + iTrackerBateray);
        ivLocationWay.setVisibility(View.VISIBLE);
        ivTrackElectric.setVisibility(View.VISIBLE);
        if (iTrackerBateray == 0) {
            ivTrackElectric.setImageResource(R.drawable.icon_0);
        } else if (iTrackerBateray > 0 && iTrackerBateray <= 10) {
            ivTrackElectric.setImageResource(R.drawable.icon_10);
        } else if (iTrackerBateray > 10 && iTrackerBateray <= 20) {
            ivTrackElectric.setImageResource(R.drawable.icon_20);
        } else if (iTrackerBateray > 20 && iTrackerBateray <= 30) {
            ivTrackElectric.setImageResource(R.drawable.icon_30);
        } else if (iTrackerBateray > 30 && iTrackerBateray <= 40) {
            ivTrackElectric.setImageResource(R.drawable.icon_40);
        } else if (iTrackerBateray > 40 && iTrackerBateray <= 50) {
            ivTrackElectric.setImageResource(R.drawable.icon_50);
        } else if (iTrackerBateray > 50 && iTrackerBateray <= 60) {
            ivTrackElectric.setImageResource(R.drawable.icon_60);
        } else if (iTrackerBateray > 60 && iTrackerBateray <= 70) {
            ivTrackElectric.setImageResource(R.drawable.icon_70);
        } else if (iTrackerBateray > 70 && iTrackerBateray <= 80) {
            ivTrackElectric.setImageResource(R.drawable.icon_80);
        } else if (iTrackerBateray > 80 && iTrackerBateray <= 90) {
            ivTrackElectric.setImageResource(R.drawable.icon_90);
        } else if (iTrackerBateray > 90) {
            ivTrackElectric.setImageResource(R.drawable.anchor_icon);
        }
        if (mCurTracker.ranges == 3 || mCurTracker.ranges == 4) {//隐藏电量显示
            ivTrackElectric.setVisibility(View.INVISIBLE);
            showLocationWay(mCurGPS,ivLocationWay);
        } else if (mCurTracker.ranges == 6) {//OBD显示汽车是否有警情标示
            if (mCurGPS.car_status == 2) {
                ivTrackElectric.setImageResource(R.drawable.ic_car_p_nor);
                ivTrackElectric.setVisibility(View.VISIBLE);
            } else if (mCurGPS.car_status == 0) {
                ivTrackElectric.setImageResource(R.drawable.ic_car_p);
                ivTrackElectric.setVisibility(View.VISIBLE);
            } else {
                ivTrackElectric.setVisibility(View.INVISIBLE);
            }
            ivLocationWay.setVisibility(View.GONE);
        } else {
            ivTrackElectric.setVisibility(View.VISIBLE);
            showLocationWay(mCurGPS,ivLocationWay);
        }
    }

    /**
     * 显示定位模式
     */
    private void showLocationWay(CurrentGPS mCurGPS, ImageView ivLocationWay){
        int gps_flag = mCurGPS.gps_flag;
        LogUtil.i("location gps flag:" + gps_flag);
        if (mCurTracker.protocol_type == 8) {
            ivLocationWay.setVisibility(View.GONE);//litefamily隐藏定位方式------------------------------------------------
        } else if (2 == gps_flag) {//基站
            ivLocationWay.setImageResource(R.drawable.icon_jizhan);
            ivLocationWay.setVisibility(View.VISIBLE);
        } else if (3 == gps_flag) {//是有效的GPS定位
            ivLocationWay.setImageResource(R.drawable.icon_gps);
            ivLocationWay.setVisibility(View.VISIBLE);
        } else if (10 == gps_flag) {
            ivLocationWay.setImageResource(R.drawable.icon_wifi);
            ivLocationWay.setVisibility(View.VISIBLE);
        } else {
            ivLocationWay.setVisibility(View.GONE);
        }
    }

    /**
     * @Description: 获取设备类型: CAR_TYPE: 汽车类型 PET_TYPE：宠物类型
     */
    // 使用范围 1.个人，2.宠物，3.汽车，4.摩托车,5.手表，6.OBD汽车
    public int getTrackerType(int iType) {
        LogUtil.v("iType is " + iType);
        if (iType == 6) {
            return Constants.CAR_TYPE;
        } else if (iType == 3 || iType == 4 || iType == 1 || iType == 5 || iType == 7) {
            return Constants.PERSON_TYPE;
        } else if (iType == 2) {
            return Constants.PET_TYPE;
        } else {
            return Constants.NO_TYPE;
        }
    }


    /**
     * 显示一键拨号
     */
    public void showPhoneDialog(final Tracker mTracker) {
        final String phoneNumber = mTracker.tracker_sim;
        String confirm;
        String title = null;
        String msg;
        if (TextUtils.isEmpty(phoneNumber)) {
            confirm = mContext.getString(R.string.phone_set);
            title = mContext.getString(R.string.phone_title);
            msg = mContext.getString(R.string.phone_guide);
        } else {
            confirm = mContext.getString(R.string.phone_call);
            msg = phoneNumber;
        }
        DialogUtil.callPhoneDialog(title, msg, confirm, mContext.getString(R.string.cancel),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (TextUtils.isEmpty(mTracker.tracker_sim)) {
                            Intent trackerEditIntent = new Intent(mContext, TrackerEditActivity.class);
                            trackerEditIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
                            trackerEditIntent.putExtra("fromwhere", Constants.MINEACTIVITY);
                            mContext.startActivity(trackerEditIntent);
                        } else {
//                            Intent intentCall = new Intent(Intent.ACTION_CALL);
//                            intentCall.setData(Uri.parse("tel:" + phoneNumber));
//                            if (intentCall.resolveActivity(mContext.getPackageManager()) != null) {
//                                mContext.startActivity(intentCall);
//                                return;
//                            }
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            Uri data = Uri.parse("tel:" + phoneNumber);
                            intent.setData(data);
                            mContext.startActivity(intent);
                        }
                        DialogUtil.dismiss();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                    }
                });
    }


    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    /**
     * 界面销毁时如果有请求，取消请求
     */
    public void ondestoryRelease() {
        if (null != requestHandle && !requestHandle.isFinished())
            requestHandle.cancel(true);
    }

}
