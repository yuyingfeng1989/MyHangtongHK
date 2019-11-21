package com.bluebud.utils;

import android.app.Activity;
import android.content.Context;

import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.GeofenceObj;
import com.bluebud.info.ReBaseObj;
import com.bluebud.liteguardian_hk.R;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by user on 2018/6/12.
 */

public class FenceRequestUtil implements ProgressDialogUtil.OnProgressDialogClickListener {
    private Context mContext;
    private RequestHandle requestHandle;
    private GeofenceObj mGeofenceObj;
    private final String url;

    public FenceRequestUtil(Context context) {
        this.mContext = context;
        url = UserUtil.getServerUrl(mContext);
    }

    /**
     * 获取当前围栏信息接口
     */
    public void getGEOfence(String sTrackerNo, final ChatCallbackResult iCallback) {
        if (sTrackerNo == null)
            return;
        RequestParams params = HttpParams.getGeoFenceCN(sTrackerNo);
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
                        String result = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(result);
                        if (obj == null) {
                            iCallback.callBackFailResult(mContext.getString(R.string.net_exception));
                            return;
                        }
                        if (obj.code == 0) {
                            mGeofenceObj = GsonParse.fenceSettingDataParse(result);
                            if (mGeofenceObj == null || mGeofenceObj.defenceList.size() == 0) {
                                iCallback.callBackFailResult(obj.what);
                                return;
                            }
                            iCallback.callBackResult(result);
                        } else {
                            iCallback.callBackFailResult(obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        iCallback.callBackFailResult(mContext.getString(R.string.net_exception));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mContext instanceof Activity) {
                            Activity activity = (Activity) mContext;
                            if (!activity.isFinishing() && !activity.isDestroyed()) {
                                ProgressDialogUtil.dismiss();
                            }
                        }
                    }
                });
    }


    /**
     * 设置围栏接口
     *
     * @isMoreFence 不止一个围栏
     */
    public void setGEOfence(String sTrackerNo, GeofenceObj.DefenceList defenceList, boolean isMoreFence, final ChatCallbackResult iCallback) {
        if (sTrackerNo == null) {
            return;
        }
//        if (App.getMapType() != App.MAP_TYPE_GMAP) {
//            MyLatLng tempLatLng = MyLatLng.from(defenceList.lat, defenceList.lng).toAmapWgs84Point();
//            defenceList.lng = tempLatLng.longitude;
//            defenceList.lat = tempLatLng.latitude;
//        }
        RequestParams params;
        if (!isMoreFence)
            params = HttpParams.setGeofenceCN(sTrackerNo, defenceList);
        else
            params = HttpParams.setGeofence(defenceList, sTrackerNo);
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
                        if (obj == null) {
                            iCallback.callBackFailResult(mContext.getString(R.string.net_exception));
                            return;
                        }
                        // guoqz add 20160309.
                        if (0 != obj.code) {
                            iCallback.callBackFailResult(obj.what);
                        } else {
                            iCallback.callBackResult(obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        iCallback.callBackFailResult(mContext.getString(R.string.net_exception));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    /**
     * 取消围栏设置接口
     */
    public void cancelGEOfence(String sTrackerNo, final ChatCallbackResult iCallback) {
        if (sTrackerNo == null)
            return;
        RequestParams params;
//        if (TextUtils.isEmpty(areaid))
        params = HttpParams.cancelGEOfence(sTrackerNo);
//        else
//            params = HttpParams.deleteGeoFenceCN(sTrackerNo, areaid);

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
                        if (obj == null) {
                            iCallback.callBackFailResult(mContext.getString(R.string.net_exception));
                            return;
                        }
                        if (obj.code == 0) {
                            iCallback.callBackResult(obj.what);
                        } else {
                            iCallback.callBackFailResult(obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        iCallback.callBackFailResult(mContext.getString(R.string.net_exception));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    @Override
    public void onProgressDialogBack() {
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }
}
