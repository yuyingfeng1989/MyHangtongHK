package com.bluebud.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.phoneNumberInfo;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by Administrator on 2018/6/9 0009.
 */

public class MineHttpRequestUtl implements DialogUtil.OnEditTextEditListener, DialogUtil.OnEditTextEditSOSListener {

    private Activity mContext;
    private String strTrackerNo;
    private final String url;

    public MineHttpRequestUtl(Activity mContext, String strTrackerNo) {
        this.mContext = mContext;
        this.strTrackerNo = strTrackerNo;
        url = UserUtil.getServerUrl(mContext);
    }

    /**
     * 刷新设备号
     */
    public void refreshValue(String strTrackerNo) {
        if (TextUtils.isEmpty(strTrackerNo))
            return;
        this.strTrackerNo = strTrackerNo;
    }

    /**
     * 请求数据
     */
    public void setMineHttpRequest(final int function, String string) {
        RequestParams params = null;
        switch (function) {
            case 1:
                params = HttpParams.setDeviceMonitoring(strTrackerNo, string);//设置远程监听
                break;
            case 2:
                params = HttpParams.setDeviceSearch(strTrackerNo);//找设备 找手表
                break;
            case 3:
                params = HttpParams.setDeviceShutdown(strTrackerNo);//设置远程关机
                break;
            case 4:
                params = HttpParams.getDeviceMonitoringPhone(strTrackerNo);//获取远程监护中心号码
                break;
            case 5:
                params = HttpParams.setDeviceRestart(strTrackerNo);//设置远程重启
                break;
            case 6:
                params = HttpParams.setRemoteRecoder(strTrackerNo);
                break;//远程录音
        }
        if (params == null)
            return;
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
                        LogUtil.e("返回数据=" + new String(response));
                        if (obj == null) {
                            if (function == 4)
                                remoteMonitoring("");
                            return;
                        }
                        if (function == 4) {
                            String mobile = "";
                            if (obj.code == 0) {
                                phoneNumberInfo numberInfo = GsonParse.phoneNumberParse(new String(response));
                                if (numberInfo != null)
                                    mobile = numberInfo.mobile;
                            }
                            remoteMonitoring(mobile);
                            return;
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
     * 添加sos号码
     */
    private void addPhoneBook(boolean isNewDevice, String sTrackerNo, String sTelephones, final AlertDialog mDialog) {
        RequestParams params;
        if (!isNewDevice) {  //旧设备
            params = HttpParams.addPhoneBook(sTrackerNo, sTelephones, ",,,,,,,,,", 1);
        } else {  // 新设备
            params = HttpParams.addNamePhonebook(sTrackerNo, sTelephones, ",,,,,,,,,", 1);
        }
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (mDialog != null && mDialog.isShowing())
                            mDialog.dismiss();
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

//    /**
//     * 弹出sos框
//     */
//    public void ShowSOSDialog() {
//        DialogUtil.showEditSOSDialog(mContext, strTrackerNo, this);
//    }

    /**
     * 远程关机弹框
     */
    public void remoteShutDowm(final int function) {
        int title;
        int msg;
        if (function == 3) {
            title = R.string.remote_shutdown;
            msg = R.string.remote_Monitoring_hint;
        } else {
            title = R.string.remote_restart;
            msg = R.string.remote_Restart_hint;
        }
        DialogUtil.show(mContext, title, msg, R.string.confirm,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        setMineHttpRequest(function, null);
                        DialogUtil.dismiss();
                    }
                }, R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                    }
                });
    }


    /**
     * 设置sos号码弹框回调
     */
    @Override
    public void editTextEditSOS(String str, AlertDialog mDialog, boolean isNewDevice) {
        addPhoneBook(isNewDevice, strTrackerNo, str, mDialog);
    }

    /**
     * 远程监护弹框
     */
    private void remoteMonitoring(String mobile) {
        DialogUtil.showEditDialog(mContext, R.string.remote_monitoring,
                R.string.confirm, R.string.cancel, this, mobile, mContext.getString(R.string.remote_monitoring_hint));
    }

    /**
     * 远程监听弹框回调设置
     */
    @Override
    public void editTextEdit(String str, AlertDialog mDialog) {
        LogUtil.i("监护人的手机号：" + str);
        setMineHttpRequest(1, str);
        mDialog.dismiss();
    }
}
