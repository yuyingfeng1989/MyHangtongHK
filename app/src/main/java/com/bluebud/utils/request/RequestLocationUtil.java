package com.bluebud.utils.request;

import android.content.Context;
import android.os.Handler;

import com.bluebud.app.AppApplication;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.ReBaseObj;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.Timer;
import java.util.TimerTask;

import io.rong.eventbus.EventBus;

/**
 * Created by Administrator on 2018/6/26.
 */

/**
 * 实现步骤
 * 调用timer.schedule(task, 1 * 1000, 1 * 1000); 方法进行设置轮询的间隔时间
 * 这三个参数：1 步定义TimerTask类，2 表示的意思是我们需要多长时间
 * 执行我们的TimerTask类中run方法，3 设置轮询间隔的时间
 */

public class RequestLocationUtil {
    private Context mContext;
    private String sTrackerNo;
    private String url;
    private static RequestLocationUtil requestLocation;
    private RequestHandle requestHandle;
    private Handler handler;
    private Timer timer;
    private Task task;

    public RequestLocationUtil(Context context) {
        this.mContext = context;
    }

    public static RequestLocationUtil getRequestLocation(Context context) {
        if (requestLocation != null)
            return requestLocation;
        requestLocation = new RequestLocationUtil(context);
        return requestLocation;
    }

    /**
     * 启动定时器轮询
     * isChangeTracker 是否时切换设备，false启动onstart方法，true是切换设备
     */
    public void startTimerPolling(String sTrackerNo, String url, boolean isChangeTracker) {
        LogUtil.e("启动定时器=" + sTrackerNo);
        this.sTrackerNo = sTrackerNo;
        this.url = url;
        if (isChangeTracker)
            return;
        if (handler == null)
            handler = new Handler();
        if (mContext == null)
            mContext = AppApplication.getInstance().getApplicationContext();
        if (timer == null)
            timer = new Timer();
        if (task == null)
            task = new Task();
        timer.schedule(task, 20 * 1000, 20 * 1000);
    }

    /**
     * 停止定时器轮询
     */
    public void stopTimerPolling() {
        LogUtil.e("停止轮询");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    /**
     * schedule 计划安排，时间表
     */
    class Task extends TimerTask {
        @Override
        public void run() {
            if (handler == null || mContext == null)
                return;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    getLasterGPS();
                }
            });
        }
    }


    /**
     * 获取当前最后一次gps数据
     */
    private void getLasterGPS() {
        if (null != requestHandle && !requestHandle.isFinished())
            requestHandle.cancel(true);
        RequestParams params = HttpParams.getLasterGPS(sTrackerNo, Utils.getCurTime(mContext));
        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null) {
                            return;
                        }
                        if (obj.code == 0) {
                            CurrentGPS currentGPS = GsonParse.currentGPSParse(new String(response));
                            if (currentGPS == null) {
                                return;
                            }
                            if (0 == currentGPS.lat && 0 == currentGPS.lng) {
                                return;
                            }
                            EventBus.getDefault().post(currentGPS);
                        }
                    }
                });
    }



    /**
     * 释放内存
     */
    public void releaseMemory() {
        sTrackerNo = null;
        requestLocation = null;
        url = null;
        mContext = null;
        stopTimerPolling();
        timer = null;
        task = null;
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
//        handler = null;
    }
}
