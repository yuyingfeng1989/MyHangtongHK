package com.bluebud.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.bluebud.app.AppApplication;

import java.util.Locale;

/**
 * Created by Administrator on 2019/7/8.
 */

public class SystemUtil {

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return  语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取当前所在城市
     *
     * 返回城市标号
     */
    public static String getSystemCountry() {
        return Locale.getDefault().getCountry();
    }


    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return  手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

//    /**
//     * 获取手机厂商
//     *
//     * @return  手机厂商
//     */
//    public static String getDeviceBrand() {
//        return android.os.Build.BRAND;
//    }

    /**
     * 获取当前apk版本号
     * 返回版本号
     */
    public static String getApkVersion(){
        String version = null;
        try {
            version = AppApplication.getInstance().getPackageManager().getPackageInfo(AppApplication.getInstance().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "V"+version;
    }
}
