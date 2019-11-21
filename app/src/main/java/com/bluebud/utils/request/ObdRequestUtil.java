package com.bluebud.utils.request;

import android.content.Context;
import android.util.Log;

import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.info.DriverDate;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;

import io.rong.eventbus.EventBus;

/**
 * Created by Administrator on 2019/4/17.
 */

public class ObdRequestUtil {
    private Context mContext;
    private String device_sn;
    private int type = 1;

    public ObdRequestUtil(Context context, String device_sn) {
        this.mContext = context;
        this.device_sn = device_sn;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void obdReuqestCallback(String startTime, String endTime) {
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(9, null, device_sn, null, startTime, endTime, String.valueOf(type), null, null, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(mContext);
            }

            @Override
            public void callBackResult(String result) {
                ProgressDialogUtil.dismiss();
                if (result == null)
                    return;
                Log.e("TAG","response=="+result);
//                StatisticsDataInfo carDrivedate = GsonParse.carDriveDataParse(result);
               DriverDate carDrivedate = GsonParse.carDriveTestDataParse(result);
                if (carDrivedate != null) {
                    EventBus.getDefault().post(carDrivedate);
                    Log.e("TAG", "carDricerdata=" + carDrivedate.toString());
                }
            }

            @Override
            public void callBackFailResult(String result) {
                ToastUtil.show(mContext, result);
                ProgressDialogUtil.dismiss();
            }
        });
    }

}
