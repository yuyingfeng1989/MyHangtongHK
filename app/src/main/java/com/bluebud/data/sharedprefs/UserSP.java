package com.bluebud.data.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import com.bluebud.activity.MainActivity;
import com.bluebud.info.LitemallInfo;
import com.bluebud.app.App;
import com.bluebud.info.User;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.UserUtil;

public class UserSP {
    private final String FILE_NAME = "user_info3";
    private final String KEY_USER_INFO = "user_info3";
    private final String KEY_WISDOM_INFO = "wisdom_info";
    private final String KEY_USER_NAME = "user_name";
    private final String KEY_GUEST = "guest";
    private final String KEY_LATANDLNG = "lat and lng";
    //    private final String KEY_CARTEST = "car_test_data";
    private final String KEY_PWD = "user_pwd";
    private final String KEY_USER_NAME_LAST = "user_name_last";
    private final String KEY_PWD_LAST = "user_pwd_last";
    private final String KEY_SERVER_URL = "server_url";
    private final String KEY_AUTO_LOGIN = "auto_login";
    //    private final String KEY_REMEMBER_USER = "remember_user";
//    private final String KEY_LOGIN = "login";
    private final String KEY_CURRENT_TRACKER = "current_tracker";
    private final String KEY_CURRENT_IMEI = "current_imei";
    private final String KEY_CURRENT_ADDRESS = "current_address";
    private final String KEY_CURRENT_SPORT = "current_sport";
    private final String KEY_CURRENT_ALARM_VERSION = "alarm_version";
    private final String KEY_CURRENT_WISDOM = "current_wisdom";
    private final String KEY_CURRENT_TARGET_STEP = "current_target_step";
    private final String KEY_CURRENT_SLEEP = "current_sleep";
    private final String KEY_CURRENT_USERID = "current_userid";
    private final String KEY_CURRENT_HEADRATE = "current_headrate";
    private final String KEY_CURRENT_HEADRATE1 = "current_headrate_continuous";

    private final String KEY_CURRENT_FENCE = "current_fence";
    private final String KEY_MAP = "map";
    //    private final String KEY_ALARM_CLOCK = "alarm_clock";
//    private final String KEY_SOUND = "sound";
    private final String KEY_MANUAL_LOGIN = "manual_login_time";
    //    private final String KEY_SYSTEM_NOTICE_FLAG = "system_notice_flag";
//    private final String KEY_SYSTEM_NOTICE_SHOW = "system_notice_icon_show";
//    private final String KEY_SYSTEM_NOTICE_CODE = "system_notice_icon_code";
    private final String KEY_BLUETOOTH_FLAG_SOS = "bluetooth_flag_sos";
    //    private final String KEY_MALL = "mall_shop";//商城
    private final String KEY_MALL_OPENID = "key_openid";//litemall的openId
    private final String KEY_MALL_TOKEN = "key_token";//litemall的token
    private final String KEY_MALL_USERID = "key_userid";//litemall的UserId
    private final String KEY_MALL_WEBURL = "key_weburl";//litemall商城
    private static UserSP instance;

    public UserSP() {
    }

    public static UserSP getInstance() {
        if (null == instance) {
            instance = new UserSP();
        }
        return instance;
    }

    public String getLastManualLoginTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_MANUAL_LOGIN, "1900-00-00 00:00:00");
    }

    public boolean saveUserInfo(Context context, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_USER_INFO, value);
        return editor.commit();
    }

//    public boolean saveWisdomInfo(Context context, String value) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putString(KEY_WISDOM_INFO, value);
//        return editor.commit();
//    }

//    public WisdomSeoretaryList getWisdomInfo(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//
//        return GsonParse.json2object(sp.getString(KEY_WISDOM_INFO, " "),
//                WisdomSeoretaryList.class);
//    }

    public User getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        return GsonParse.json2object(sp.getString(KEY_USER_INFO, "{}"),User.class);
    }

    public boolean saveUserName(Context context, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_USER_NAME, value);
        return editor.commit();
    }

    public boolean savePWD(Context context, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_PWD, value);
        return editor.commit();
    }

    public String getPWD(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_PWD, "");
    }

//    public String getUserNameLast(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        return sp.getString(KEY_USER_NAME_LAST, "");
//    }

//    public boolean saveUserNameLast(Context context, String value) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putString(KEY_USER_NAME_LAST, value);
//        return editor.commit();
//    }

//    public boolean savePWDLast(Context context, String value) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putString(KEY_PWD_LAST, value);
//        return editor.commit();
//    }

//    public String getPWDLast(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        return sp.getString(KEY_PWD_LAST, "");
//    }

    public String getUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_USER_NAME, "");
    }

    public boolean saveAutologin(Context context, boolean flag) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_AUTO_LOGIN, flag);
        return editor.commit();
    }

    public boolean getAutologin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_AUTO_LOGIN, false);
    }

//    public boolean saveRememberUser(Context context, boolean flag) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putBoolean(KEY_REMEMBER_USER, flag);
//        return editor.commit();
//    }
//
//    public boolean getRememberUser(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        return sp.getBoolean(KEY_REMEMBER_USER, false);
//    }
//
//    public boolean saveLogin(Context context, boolean flag) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putBoolean(KEY_LOGIN, flag);
//        return editor.commit();
//    }

//    public boolean getLogin(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        return sp.getBoolean(KEY_LOGIN, false);
//    }

    /**
     * 保存注册地址
     */
    public void saveServerUrl(Context context, String serverUrl) {
        LogUtil.v("serverurl is " + serverUrl);
        Editor editor = context.getApplicationContext()
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_SERVER_URL, serverUrl);
        editor.commit();
    }

    public String getServerUrl(Context context) {
        return context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).getString(KEY_SERVER_URL, null);
    }

    public boolean saveCurrentTracker(Context context, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CURRENT_TRACKER, value);
        return editor.commit();
    }

    public String getCurrentTracker(Context context) {
        if (context == null)
            context = MainActivity.mainActivity;
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_CURRENT_TRACKER, "");
    }

    public boolean saveCurrentIMEI(Context context, String mac, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CURRENT_IMEI + "_" + mac, value);
        return editor.commit();
    }

    public String getCurrentIMEI(Context context, String mac) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_CURRENT_IMEI + "_" + mac, "");
    }

    public boolean saveCurrentaddress(Context context, String imei, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CURRENT_ADDRESS + "_" + imei, value);
        return editor.commit();
    }

    public String getCurrentaddress(Context context, String imei) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_CURRENT_ADDRESS + "_" + imei, "");
    }

    public String getCurrentSportData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_CURRENT_SPORT, "");
    }

    public boolean saveCurrentSportData(Context context, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CURRENT_SPORT, value);
        return editor.commit();
    }

    public String getCurrentWisdomData(Context context, String device) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        LogUtil.i(KEY_CURRENT_WISDOM + device);
        return sp.getString(KEY_CURRENT_WISDOM + device, "");
    }

    public boolean saveAlarmClockVersion(Context context, String deviceNo,
                                         int value, int version) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_CURRENT_ALARM_VERSION + "_" + deviceNo + "_" + value,
                version);
        return editor.commit();
    }

    public int getAlarmClockVersion(Context context, String deviceNo, int value) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        LogUtil.i(KEY_CURRENT_ALARM_VERSION + "_" + deviceNo + "_" + value);
        return sp.getInt(KEY_CURRENT_ALARM_VERSION + "_" + deviceNo + "_"
                + value, 0);

    }

    public boolean saveCurrentWisdomData(Context context, String value,
                                         String device) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        LogUtil.i(KEY_CURRENT_WISDOM + device);
        editor.putString(KEY_CURRENT_WISDOM + device, value);
        return editor.commit();
    }

    public String getCurrenttargetStep(Context context, String device) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        LogUtil.i(KEY_CURRENT_TARGET_STEP + device);
        return sp.getString(KEY_CURRENT_TARGET_STEP + device, "");
    }

    public boolean saveCurrenttargetStep(Context context, String value,
                                         String device) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        LogUtil.i(KEY_CURRENT_TARGET_STEP + device);
        editor.putString(KEY_CURRENT_TARGET_STEP + device, value);
        return editor.commit();
    }

    public String getCurrentSleepData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        return sp.getString(KEY_CURRENT_SLEEP, "");
    }

    public boolean saveCurrentSleepData(Context context, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CURRENT_SLEEP, value);
        return editor.commit();
    }

    public String getCurrentuserIDData(Context context, String deviceNo) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);

        return sp.getString(KEY_CURRENT_USERID + "_" + deviceNo, "");
    }

    public boolean saveCurrentuserIDData(Context context, String deviceNo,
                                         String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CURRENT_USERID + "_" + deviceNo, value);
        return editor.commit();
    }

    public String getCurrentHeadrateData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_CURRENT_HEADRATE, "");
    }

    public boolean saveCurrentHeadrateData(Context context, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CURRENT_HEADRATE, value);
        return editor.commit();
    }

    public String getCurrentHeadratecontinuousData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_CURRENT_HEADRATE1, "");
    }

    public boolean saveCurrentHeadratecontinuousData(Context context,
                                                     String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CURRENT_HEADRATE1, value);
        return editor.commit();
    }

    public String getCurrentFence(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_CURRENT_FENCE, "");
    }

    public boolean saveCurrentFence(Context context, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_CURRENT_FENCE, value);
        return editor.commit();
    }

    /**
     * @param context
     * @param value   0:高德地图 1:Google地图，2:百度地图
     * @return
     */
    public boolean saveServerAndMap(Context context, int value) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_MAP, value);
        return editor.commit();
    }

    /**
     * @param context
     * @return 0:高德地图 1:Google地图，2:百度地图
     */
    public int getServerAndMap(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(KEY_MAP, App.MAP_TYPE_AMAP);
    }

//    public boolean saveSound(Context context, int type) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putInt(KEY_SOUND, type);
//        return editor.commit();
//    }
//
//    public int getSound(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        return sp.getInt(KEY_SOUND, 1);
//    }

    public boolean saveTrackerExpire(Context context, String key, String value) {
        Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public String getTrackerExpire(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

//    public boolean saveGuest(Context context, boolean flag) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putBoolean(KEY_GUEST, flag);
//        return editor.commit();
//    }

    public boolean getGuest(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_GUEST, false);
    }

    /**
     * @param @param  context
     * @param @param  latAndlng :经度&纬度
     * @param @return
     * @return boolean
     * @throws
     * @Title: saveCurrentTrackerGps
     */
    public boolean saveCurrentTrackerGps(Context context, String latAndlng) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_LATANDLNG, latAndlng);
        return editor.commit();
    }

    public String getCurrentTrackerGps(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(KEY_LATANDLNG, "0&0");
    }

//    /**
//     * @param @param  context
//     * @param @param
//     * @param @return
//     * @return boolean
//     * @throws
//     * @Title: saveCurrentTrackerGps
//     * @Description: 保存车辆检测信息
//     */
//    public boolean saveCarTestDate(Context context, String latAndlng) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putString(KEY_CARTEST, latAndlng);
//        return editor.commit();
//    }
//
//    public String getCarTestDate(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        return sp.getString(KEY_CARTEST, null);
//    }

    /**
     * 保存obd车辆检测分数和时间
     */
    public boolean saveCarScoreAndTimer(Context context,String key,String value){
        Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        return editor.commit();
    }
//    public boolean saveCarTime(Context context,String key,String value){
//        Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
//        editor.putString(key, value);
//        return editor.commit();
//    }

    /**
     * 获取obd车辆检测分数和时间
     * @return
     */
//    public int getCarScore(Context context,String key) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
//        return sp.getInt(key, 0);
//    }
    public String getCarScoreAndTime(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }


//    public boolean cleanCarTestDate(Context context) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putString(KEY_CARTEST, null);
//        return editor.commit();
//    }

    // 记录登录时间
    public boolean saveLastManualLoginTime(Context context, String time) {
        Editor editor = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE).edit();
        editor.putString(KEY_MANUAL_LOGIN, time);
        return editor.commit();
    }

//    public boolean saveSysmsgCode(Context context, String code) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putString(KEY_SYSTEM_NOTICE_CODE, code);
//        return editor.commit();
//    }
//
//    public String getSysmsgCode(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        return sp.getString(KEY_SYSTEM_NOTICE_CODE, "");
//    }

//    public boolean setSysmsgIconShow(Context context, boolean value, String code) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putBoolean(KEY_SYSTEM_NOTICE_SHOW + "_" + code, value);
//        return editor.commit();
//    }
//
//    public boolean getSysmsgIconShow(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        String code = getSysmsgCode(context);
//        return sp.getBoolean(KEY_SYSTEM_NOTICE_SHOW + "_" + code, false);
//    }

//    public boolean saveSystemNoticeFlag(Context context, boolean value,
//                                        String trackNo) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putBoolean(KEY_SYSTEM_NOTICE_FLAG + "_" + trackNo, value);
//        return editor.commit();
//    }

//    public boolean getSystemNoticeFlag(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        String code = getSysmsgCode(context);
//        return sp.getBoolean(KEY_SYSTEM_NOTICE_FLAG + "_" + code, false);
//    }

//    private void saveDeviceName(Context context, String address, String name) {
//        LogUtil.i("saveDeviceName" + address + " " + name);
//
//        SharedPreferences prefs = context.getSharedPreferences("device_name",
//                Context.MODE_PRIVATE);
//        Editor editor = prefs.edit();
//        editor.putString(address, name);
//        editor.commit();
//    }

//    public boolean saveBluetoothFlag(Context context, boolean value,
//                                     String trackNo) {
//        Editor editor = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE).edit();
//        editor.putBoolean(KEY_BLUETOOTH_FLAG_SOS + "_" + trackNo, value);
//        return editor.commit();
//    }
//
//    public boolean getBluetoothFlag(Context context, String trackNo) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE);
//        return sp.getBoolean(KEY_BLUETOOTH_FLAG_SOS + "_" + trackNo, true);
//    }

    /**
     * 微聊保存token,nickName,用户头像
     */
    public void saveChatValue(Context context, String token, String userPhoto,String nickName, String type) {
        Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
        if (!TextUtils.isEmpty(token))
            editor.putString("token", token);
        String url = UserUtil.getServerIP(context);
        LogUtil.e("url==" + url + userPhoto);
        if (!TextUtils.isEmpty(userPhoto) && !TextUtils.isEmpty(url)) {
            editor.putString("userPhoto", url + userPhoto);
        }
        if (!TextUtils.isEmpty(nickName))
            editor.putString("nickName", nickName);
        if (!TextUtils.isEmpty(type))
            editor.putString("chatType", type);
        editor.commit();
    }


//    /**
//     * 保存注册账号ip端口号
//     */
//    public void saveUserUrl(Context context, String connIP, int connPort, String userName) {
//        if (TextUtils.isEmpty(connIP))
//            return;
//        Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
//        if (connPort > 0) {
//            String userUrl = "http://" + connIP + ":" + connPort;
//            editor.putString(userName, userUrl);
//        } else {
//            editor.putString(userName, null);
//        }
//        editor.commit();
//    }
//
//    /**
//     * 获取注册账号ip端口号
//     */
//    public String getUserUrl(Context context, String userName) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
//        if (TextUtils.isEmpty(userName))
//            return null;
//        return sp.getString(userName, null);
//    }

    /**
     * 清空微聊信息
     *
     * @param context
     */
    public void clearChatValue(Context context) {
        Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putString("token", null);
        editor.putString("userPhoto", null);
        editor.putString("nickName", null);
        editor.putString("chatType", null);
        editor.commit();
    }

    public String getToken(Context context) {//获取微聊token
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString("token", "");
    }

    public String getUserPhoto(Context context) {//获取用户微聊头像
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString("userPhoto", "");
    }

    public String getNickName(Context context) {//获取用户昵称
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString("nickName", "");
    }

    public String getChatType(Context context) {//判断该用户是否拥有微聊功能
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString("chatType", "");
    }

    /**
     * 保存大商串
     */
    public void saveLiteMall(Context context,String openId,String token,String userId,String webUrl){
        Log.e("TAG","openId="+openId);
        Log.e("TAG","token="+token);
        Log.e("TAG","userId="+userId);
        Log.e("TAG","webUrl="+webUrl);
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(KEY_MALL_OPENID,openId);
        editor.putString(KEY_MALL_TOKEN,token);
        editor.putString(KEY_MALL_USERID,userId);
        editor.putString(KEY_MALL_WEBURL,webUrl);
        editor.commit();
    }
    public LitemallInfo getLiteMall(Context context){
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String openId = sp.getString(KEY_MALL_OPENID,"");
        String token = sp.getString(KEY_MALL_TOKEN,"");
        String userId = sp.getString(KEY_MALL_USERID,"");
        String weburl = sp.getString(KEY_MALL_WEBURL,"");
        return new LitemallInfo(weburl,userId,openId,token);
    }

//    public void saveMallShop(Context context, String mallUrl) {
//        Editor editor = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE).edit();
//        editor.putString(KEY_MALL, mallUrl);
//        editor.commit();
//
//    }
//
//    public String getMallShop(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
//        return sp.getString(KEY_MALL, null);
//    }
}
