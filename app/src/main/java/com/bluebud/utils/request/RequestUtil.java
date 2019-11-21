package com.bluebud.utils.request;

import android.content.Context;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.FunctionControlInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by Administrator on 2018/10/9.
 */

public class RequestUtil {
    private Context mContext;
    private String url;
    private String mDeviceSn;
    private ICallBack iCallBack;

    public RequestUtil(Context mContext, String deviceSn, ICallBack iCallBack) {
        this.mContext = mContext;
        this.mDeviceSn = deviceSn;
        this.iCallBack = iCallBack;
        this.url = UserUtil.getServerUrl(mContext);
    }

    /**
     * 设置数据
     */
    public void setDeviceSchoolHours(int isSchoolHours, int dayOfWeek, int courseOfday, final String courseName, String schoolHour) {
        RequestParams params = HttpParams.setDeviceSchoolHours(isSchoolHours, dayOfWeek, courseOfday, courseName, mDeviceSn, schoolHour);//设置
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
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        ToastUtil.show(mContext, obj.what);
                        ProgressDialogUtil.dismiss();
                        if (obj.code == 0)
                            iCallBack.callBackData(courseName, 2);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    /**
     * 获取课程表接口
     */
    public void getDeviceSchoolHours() {
        RequestParams params = HttpParams.getDeviceSchoolHours(mDeviceSn);//设置远程监听
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
                        String string = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(string);
                        if (obj.code == 0) {
                            iCallBack.callBackData(string, 3);
                        } else if (obj.code == 1) {
                            getDeviceSchoolHours();
                        }
                        ToastUtil.show(mContext, obj.what);
                        ProgressDialogUtil.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    /**
     * 获取设备状态开关
     */
    public void getDeviceEnableSwitch(){
        RequestParams params = HttpParams.getDeviceEnableSwitch(mDeviceSn);
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
                        String string = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(string);
                        if (obj.code == 0) {
                            iCallBack.callBackData(string, 4);
                        }else {
                            iCallBack.callBackData(null,4);
                        }
                        ToastUtil.show(mContext, obj.what);
                        ProgressDialogUtil.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                        ProgressDialogUtil.dismiss();
                    }
                });
    }
    /**
     * 设置设备状态开关
     */
    public void setDeviceEnableSwitch(FunctionControlInfo reset, final int position){
        RequestParams params = HttpParams.setDeviceEnableSwitch(mDeviceSn,reset,position);
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
                        String string = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(string);
                        if (obj.code == 0) {
                            iCallBack.callBackData(string, 5);
                        }else {
                            iCallBack.callBackData(null, position);
                        }
                        ToastUtil.show(mContext, obj.what);
                        ProgressDialogUtil.dismiss();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    /**
     * 回调接口
     */
    public interface ICallBack {
        void callBackData(String data, int position);
    }

}
