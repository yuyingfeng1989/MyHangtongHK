package com.bluebud.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.IMMessage;
import com.bluebud.info.LitemallInfo;
import com.bluebud.utils.Constants;
import com.google.gson.Gson;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

public class IMLiteGuardianService extends Service {

    //    private SharedPreferences shared;
    private ServiceIMUtil serviceIMUtil;
    private static final long HEART_BEAT_RATE = 5 * 1000;//每隔10秒进行一次查询消息
    private static final String requestIm = "http://www.litemall.hk/mobile/index.php?m=chat&a=GetNeedList";
    private Handler handler = new Handler();
    private LitemallInfo liteMall;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
//        shared = getSharedPreferences("userInfo", 0); //文件缓存类
        serviceIMUtil = new ServiceIMUtil(this);
        serviceIMUtil.startForegroundNF();
        HttpPostIM();//启动服务获取消息
        handler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启定时轮询
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isNetCome = false;
        if (intent != null)
            isNetCome = intent.getBooleanExtra("START_IM", false);
        if (isNetCome)
            return START_STICKY;
        paramJson();
        return START_STICKY;
    }

    /**
     * 定时轮询执行请求
     */
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            HttpPostIM();
            handler.postDelayed(this, HEART_BEAT_RATE); //每隔一定的时间，对长连接进行一次心跳检测
        }
    };

    /**
     * 轮询客服和订单消息
     */
    RequestHandle post;

    private void HttpPostIM() {
        if (liteMall == null) {
            paramJson();
        }
        if (liteMall == null)
            return;
        if (post != null && !post.isFinished())
            post.cancel(true);
        RequestParams params = HttpParams.setLiteMallIm(liteMall);
        Log.e("TAG", requestIm + "/" + params.toString());
        post = HttpClientUsage.getInstance().post(this, requestIm, params,
                new AsyncHttpResponseHandlerReset() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                        super.onSuccess(statusCode, headers, response);
                        String result = new String(response);
                        Log.e("TAG", "result=" + result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String code = jsonObject.optString("code");
                            if (!code.equals("1"))//没有未读消息
                                return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        IMMessage imMessage = new Gson().fromJson(result, IMMessage.class);
                        List<IMMessage.ImBean> imBeans = imMessage.im;
                        Constants.imCountMessage = Constants.imCountMessage + imBeans.size();
                        ShortcutBadger.applyCount(IMLiteGuardianService.this, Constants.imCountMessage);
                        for (IMMessage.ImBean imBean : imBeans) {
                            serviceIMUtil.showNotification(imBean);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
//                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        Log.e("TAG", "ER=" + errorResponse);
                        Log.e("TAG", "ERstatusCode=" + statusCode);
                        Log.e("TAG", "throwable=" + throwable.getMessage());
                    }
                });
    }

    /**
     * 更新参数
     */
    private void paramJson() {
        liteMall = UserSP.getInstance().getLiteMall(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(heartBeatRunnable);
        }
    }
}
