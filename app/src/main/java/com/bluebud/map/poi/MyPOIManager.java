package com.bluebud.map.poi;

import android.content.Context;
import android.text.TextUtils;

import com.bluebud.app.App;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.AroundStoreInfo;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.PeripherDetail;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.map.bean.MyLatLng;
import com.bluebud.map.model.MyMapPresenter;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyPOIManager{

    private Tracker mCurTracker;
    private MyMapPresenter mPresenter;
    private MyLatLng mCurrentLocation;
    private List<PeripherDetail> mDetailList;


    private MyPOIManager() {
        mCurTracker = UserUtil.getCurrentTracker();
        mPresenter = new MyMapPresenter(null, MyMapPresenter.MAP_TYPE_AMAP);
        mDetailList = new ArrayList<>();
    }

    public static MyPOIManager getNewInstance() {
        return new MyPOIManager();
    }

    public void setPoiSearchListener(Context context, final ResultCallback callback) {
        mPresenter.setPoiSearchListener(context, new MyGetPoiSearchResultListener() {
            @Override
            public void onGetPoiSucceed(boolean hasResult, List<PeripherDetail> searchList) {
                if (hasResult) {
                    sortData(searchList);//搜索回来的位置距离排序
                    sortData(mDetailList);
                    for (PeripherDetail info : searchList) {//排除
                        if (!mDetailList.contains(info))
                            mDetailList.add(info);
                    }

                }
                callback.handleResult(mDetailList);
            }
        });
    }


    /**
     * 获取当前设备的gps地址
     * @param context 上下文
     * @param themesName 周边名字
     * @param storeType 周边类型
     */
    public void getCurrentGPS(final Context context, final String themesName, final int storeType) {
        if (null == mCurTracker)
            return;

        String url = UserUtil.getServerUrl(context);
        RequestParams params = HttpParams.currentGPS(mCurTracker.device_sn, Utils.getCurTime(context));
        HttpClientUsage.getInstance().post(context, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(context);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj != null && obj.code == 0) {
                            CurrentGPS currentGPS = GsonParse.currentGPSParse(new String(response));
                            ProgressDialogUtil.show(context);
                            getAroundStore(currentGPS, themesName, storeType);
                        } else if (obj != null && null != obj.what)
                            ToastUtil.show(context, obj.what);
                        else ToastUtil.show(context, R.string.net_exception);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(context, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
//                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    public int getDistance(MyLatLng latLng) {
        return (int) mPresenter.getDistance(mCurrentLocation, latLng) / 1000;
    }


    /**
     * 获取周边信息
     */
    private void getAroundStore(final CurrentGPS currentGPS,final String themesName, int storeType) {
        if (TextUtils.isEmpty(themesName)) {
            return;
        }
        if (currentGPS == null) {
            ProgressDialogUtil.dismiss();
            ToastUtil.show(App.getContext(), App.getContext().getString(R.string.no_result));
            return;
        }
        double lng = currentGPS.lng;//经度
        double lat = currentGPS.lat;//纬度
        LogUtil.d("lng==" + lng + ";  lat==" + lat);
        mCurrentLocation = MyLatLng.from(currentGPS.lat, currentGPS.lng);
        ChatHttpParams.getInstallSingle().chatHttpRequest(17, String.valueOf(lng), mCurTracker.device_sn, null, null, String.valueOf(lat),
                String.valueOf(storeType), null, null, new ChatCallbackResult() {
                    @Override
                    public void callBackResult(String result) {
                        List<AroundStoreInfo> list = (List<AroundStoreInfo>) ChatHttpParams.getParseResult(17, result);
                        if (list != null) {
                            for (AroundStoreInfo info : list) {
                                PeripherDetail pd = new PeripherDetail();
                                MyLatLng dst = MyLatLng.from(info.lat, info.lng);
//                                LatLng ll = Utils.gpsConvert2BaiduPoint(CoordinateConverter.CoordType.GPS, new LatLng(info.lat, info.lng));//转为百度坐标
                                pd.address = info.address;
                                pd.name = info.name;
                                pd.latitude = dst.latitude;
                                pd.longitude = dst.longitude;
                                pd.distance = getDistance(dst);
//                                pd.distance = (int) DistanceUtil.getDistance(curPointLocation, ll);
                                mDetailList.add(pd);
                            }
                        }
                        mPresenter.searchNearby(mCurTracker.ranges == 2
                                ? App.getContext().getString(R.string.pet) + themesName : themesName, mCurrentLocation);
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        mPresenter.searchNearby(mCurTracker.ranges == 2
                                        ? App.getContext().getString(R.string.pet) + themesName : themesName,
                                mCurrentLocation);
                    }
                });
    }


    /**
     * 近到远排序升序排列
     */
    private void sortData(List<PeripherDetail> list) {
        if (list == null)
            return;
        Collections.sort(list, new Comparator<PeripherDetail>() {

            @Override
            public int compare(PeripherDetail lhs, PeripherDetail rhs) {
                if (lhs.distance > rhs.distance)
                    return 1;
                if (lhs.distance == rhs.distance)
                    return 0;
                return -1;
            }
        });
    }

    public MyLatLng getCurrentLocation() {
        return mCurrentLocation;
    }

    public interface ResultCallback {
        void handleResult(List<PeripherDetail> detailList);
    }


}
