package com.bluebud.data.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.bluebud.app.AppApplication;

public class AppSP {
    private final String FILE_NAME = "app_info";
    private final String KEY_FIRST_START = "first_start";
    private final String KEY_FIRST_MAIN_ACTIVITY = "first_main_activity";
    private final String KEY_IS_DEVICE_ONLINE = "is_device_online";
    private final String KEY_FIRST_SAVE_MAP = "first_save_map";
    private final String KEY_SCREEN_HEIGHT = "screen_height";
    private final String KEY_SCREEN_WIDTH = "screen_width";
    //    private final String KEY_COUNTRY = "country";
    private final String KEY_DNS = "dns";
    private final String KEY_LOGIN_STATE = "login_state";//登录状态，1账号登录，2facebook登录

    private static AppSP instance;
    private final SharedPreferences sp;

    public AppSP() {
        sp = AppApplication.getInstance().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static AppSP getInstance() {
        if (null == instance) {
            instance = new AppSP();
        }
        return instance;
    }

    public boolean saveFirstStart(Context context, boolean flag) {
        Editor editor = sp.edit();
        editor.putBoolean(KEY_FIRST_START, flag);
        return editor.commit();
    }

    public boolean getFirstStart(Context context) {
        return sp.getBoolean(KEY_FIRST_START, true);
    }

    public boolean saveFirstMainActivity(Context context, boolean flag) {
        Editor editor = sp.edit();
        editor.putBoolean(KEY_FIRST_MAIN_ACTIVITY, flag);
        return editor.commit();
    }

    public boolean getFirstMainActivity(Context context) {
        return sp.getBoolean(KEY_FIRST_MAIN_ACTIVITY, true);
    }

    public boolean saveDeviceOnLine(Context context, boolean flag) {
        Editor editor = sp.edit();
        editor.putBoolean(KEY_IS_DEVICE_ONLINE, flag);
        return editor.commit();
    }

    public boolean isDeviceOnLine(Context context) {
        return sp.getBoolean(KEY_IS_DEVICE_ONLINE, true);
    }


    public void saveAdHeight(Context context, int screenHeight) {
        Editor editor = sp.edit();
        editor.putInt(KEY_SCREEN_HEIGHT, screenHeight);
        editor.commit();
    }

    public void saveScreenWidth(Context context, int screenWidth) {
        Editor editor = sp.edit();
        editor.putInt(KEY_SCREEN_WIDTH, screenWidth);
        editor.commit();
    }

    public int getScreenWidth(Context context) {
        return sp.getInt(KEY_SCREEN_WIDTH, 0);
    }

    public int getAdHeight(Context context) {
        return sp.getInt(KEY_SCREEN_HEIGHT, 0);
    }

//    /**
//     * 保存账号注册服务器全拼地址
//     */
//    public void saveRegisterAddress(Context context, String sUserName, String iAddress) {
//        Editor editor = sp.edit();
//        editor.putString(sUserName, iAddress);
//        editor.commit();
//    }

//    /**
//     * 获取账号注册服务器全拼地址
//     */
//    public String getRegisterAddress(Context context, String sUserName) {
//        if (TextUtils.isEmpty(sUserName))
//            return null;
//        return sp.getString(sUserName, "");
//    }

    public boolean saveFirstSaveMap(Context context, boolean flag) {
        Editor editor = sp.edit();
        editor.putBoolean(KEY_FIRST_SAVE_MAP, flag);
        return editor.commit();
    }

    public boolean getFirstSaveMap(Context context) {
        return sp.getBoolean(KEY_FIRST_SAVE_MAP, true);
    }

//    public void saveRegisterAddressCountry(Context context, String sUserName,
//                                           String country) {
//        Editor editor = sp.edit();
//        editor.putString(sUserName + KEY_COUNTRY, country);
//        editor.commit();
//    }
//
//    public String getRegisterAddressCountry(Context context, String sUserName) {
//        return sp.getString(sUserName + KEY_COUNTRY, "");
//    }

    public void saveRegisterAddressDNS(String sUserName, String country) {
	        Editor editor = sp.edit();
        editor.putString(sUserName + KEY_DNS, country);
        editor.commit();
    }

    public String getRegisterAddressDNS(String sUserName) {
        return sp.getString(sUserName + KEY_DNS, "");
    }

    /**
     * 保存当前版本号
     */
    public void saveVersion(Context context, String version) {
        Editor editor = sp.edit();
        editor.putString("version", version);
        editor.commit();
    }

    /**
     * 获取当前保存的版本号
     */
    public String getVersion(Context context) {
        return sp.getString("version", "");
}

    /**
     * 保存登录类型状态
     */
    public void saveLoginState(int state) {
        sp.edit().putInt(KEY_LOGIN_STATE, state).commit();
    }

    /**
     * 获取保存登录类型状态
     */
    public int getLoginState() {
        return sp.getInt(KEY_LOGIN_STATE, 0);
    }


//    /**
//     * 保存当前gps数据对象
//     */
//    public void saveCacheCurrentGPS(String keyName, CurrentGPS currentGps) {
//        if (currentGps instanceof Serializable) {
//            Editor editor = sp.edit();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            try {
//                ObjectOutputStream oos = new ObjectOutputStream(baos);
//                oos.writeObject(currentGps);//把对象写到流里
//                String temp = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
//                editor.putString(keyName, temp);
//                editor.commit();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * 获取缓存gps数据
//     */
//    public CurrentGPS getCacheCurrentGPS(String keyName) {
//
//        CurrentGPS currentGps = null;
//        String temp = sp.getString(keyName, null);
//        if (temp == null)
//            return currentGps;
//        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
//        try {
//            ObjectInputStream ois = new ObjectInputStream(bais);
//            currentGps = (CurrentGPS) ois.readObject();
//        } catch (Exception e) {
//        }
//        return currentGps;
//    }

    /**
     * 保存缓存广告url
     */
    public void saveAdvertising(String url, boolean isCache) {
        sp.edit().putBoolean(url, isCache).commit();
    }

    public boolean getAdvertisingIsCache(String url) {
        return sp.getBoolean(url, false);
    }
}
