package com.bluebud.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;

import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.service.IMLiteGuardianService;
import com.bluebud.utils.Constants;
import com.bluebud.utils.LogUtil;

import io.rong.imkit.RongIM;
import me.leolin.shortcutbadger.ShortcutBadger;

public class AppApplication extends Application {
    private static AppApplication mInstance = null;

    public static AppApplication getInstance() {
        return mInstance;
    }

    /**
     * 分包初始化数据，重写方法
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 初始化地图sdk、融云sdk、错误收集日志
     */
    @Override
    public void onCreate() {
        super.onCreate();
        String mainPackageName = ChatUtil.getCurProcessName(getApplicationContext());
        if (getApplicationInfo().packageName.equals(mainPackageName)) {//防止重复初始化
            mInstance = this;//必要的初始化资源操作
            // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
            CrashHandler crashHandler = CrashHandler.getInstance();//捕捉异常
            crashHandler.init(mInstance);
            initChat();//初始化微聊
            Constants.imCountMessage = 0;
            ShortcutBadger.removeCount(getApplicationContext());//清除小红点
            stopService(new Intent(this, IMLiteGuardianService.class));
        }
    }

    /**
     * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
     * io.rong.push 为融云 push 进程名称，不可修改。
     */
    private void initChat() {
        LogUtil.e("融云初始化");
        RongIM.init(this);// 初始化融云
        ChatUtil.initerListener();//初始化监听
    }

    /**
     * 网络连接
     * isWifiConnect true为wifi,false为所有网络
     */
    public boolean isNetworkConnected(boolean isWifiConnect) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            boolean available = mNetworkInfo.isAvailable();
            if (!isWifiConnect) {
                return available;
            } else if (mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return available;
            }
        }
        return false;
    }

    public static Context getContext(){
        return getInstance();
    }
}
