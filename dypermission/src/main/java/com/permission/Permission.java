package com.permission;

import android.Manifest;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * @author：SelfZhangTQ
 */
public final class Permission {

    public static final String[] CALENDAR;   // 读写日历。
    public static final String[] CAMERA;     // 相机。
    public static final String[] CONTACTS;   // 读写联系人。
    public static final String[] LOCATION;   // 读位置信息。
    public static final String[] MICROPHONE; // 使用麦克风。
    public static final String[] PHONE;      // 读电话状态、打电话、读写电话记录。
    public static final String[] SENSORS;    // 传感器。
    public static final String[] SMS;        // 读写短信、收发短信。
    public static final String[] STORAGE;    // 读写存储卡。

    public static final Map<String, String> permissionArrMap;

    public static final Map<String, String[]> permissionMap;

    public static final Map<String, Integer> permissionNameMap;

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            CALENDAR = new String[]{};
            CAMERA = new String[]{};
            CONTACTS = new String[]{};
            LOCATION = new String[]{};
            MICROPHONE = new String[]{};
            PHONE = new String[]{};
            SENSORS = new String[]{};
            SMS = new String[]{};
            STORAGE = new String[]{};
        } else {
            CALENDAR = new String[]{
                    Manifest.permission.READ_CALENDAR,
            };
//            Manifest.permission.WRITE_CALENDAR

            CAMERA = new String[]{
                    Manifest.permission.CAMERA};

            CONTACTS = new String[]{
//                    Manifest.permission.READ_CONTACTS,
//                    Manifest.permission.WRITE_CONTACTS,
//                    Manifest.permission.GET_ACCOUNTS
                    };

            LOCATION = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            MICROPHONE = new String[]{
                    Manifest.permission.RECORD_AUDIO};

            PHONE = new String[]{
//                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.CALL_PHONE,
//                    Manifest.permission.READ_CALL_LOG,
//                    Manifest.permission.WRITE_CALL_LOG,
//                    Manifest.permission.PROCESS_OUTGOING_CALLS
            };
//            Manifest.permission.USE_SIP,

            SENSORS = new String[]{};
//            Manifest.permission.BODY_SENSORS

            SMS = new String[]{
//                    Manifest.permission.SEND_SMS,
//                    Manifest.permission.RECEIVE_SMS,
//                    Manifest.permission.READ_SMS,
//                    Manifest.permission.RECEIVE_WAP_PUSH,
            };
//            Manifest.permission.RECEIVE_MMS

            STORAGE = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        permissionNameMap = new HashMap<>();
        permissionNameMap.put("CALENDAR", R.string.title_calendar);
        permissionNameMap.put("CAMERA", R.string.title_camera);
        permissionNameMap.put("CONTACTS", R.string.title_contacts);

        permissionNameMap.put("LOCATION", R.string.title_location);
        permissionNameMap.put("MICROPHONE", R.string.title_microphone);
        permissionNameMap.put("PHONE", R.string.title_phone);

        permissionNameMap.put("SENSORS", R.string.title_sensors);
        permissionNameMap.put("SMS", R.string.title_sms);
        permissionNameMap.put("STORAGE", R.string.title_storage);


        permissionMap = new HashMap<>();

        permissionMap.put("CALENDAR", CALENDAR);
        permissionMap.put("CAMERA", CAMERA);
        permissionMap.put("CONTACTS", CONTACTS);

        permissionMap.put("LOCATION", LOCATION);
        permissionMap.put("MICROPHONE", MICROPHONE);
        permissionMap.put("PHONE", PHONE);

        permissionMap.put("SENSORS", SENSORS);
        permissionMap.put("SMS", SMS);
        permissionMap.put("STORAGE", STORAGE);

        permissionArrMap = new HashMap<>();

        permissionArrMap.put("android.permission.READ_EXTERNAL_STORAGE", "STORAGE");//读文件权限
        permissionArrMap.put("android.permission.WRITE_EXTERNAL_STORAGE", "STORAGE");//写文件权限


//        permissionArrMap.put("android.permission.SEND_SMS", "SMS");//允许程序发送短信
//        permissionArrMap.put("android.permission.RECEIVE_SMS", "SMS");//允许程序接收短信
//        permissionArrMap.put("android.permission.READ_SMS", "SMS");//允许程序读取短信内容
//        permissionArrMap.put("android.permission.RECEIVE_WAP_PUSH", "SMS");//允许程序接收WAP PUSH信息
//        permissionArrMap.put("android.permission.RECEIVE_MMS", "SMS");//彩信权限

//        permissionArrMap.put("android.permission.BODY_SENSORS", "SENSORS");//传感器权限

//        permissionArrMap.put("android.permission.READ_PHONE_STATE", "PHONE");//允许程序访问电话状态
//        permissionArrMap.put("android.permission.CALL_PHONE", "PHONE");//允许程序从非系统拨号器里拨打电话
//        permissionArrMap.put("android.permission.READ_CALL_LOG", "PHONE");//读取通话记录
//        permissionArrMap.put("android.permission.WRITE_CALL_LOG", "PHONE");//允许程序写入（但是不能读）用户的联系人数据
//        permissionArrMap.put("android.permission.USE_SIP", "PHONE");//SIP视频服务
//        permissionArrMap.put("android.permission.PROCESS_OUTGOING_CALLS", "PHONE");//允许程序监视，修改或放弃播出电话

        permissionArrMap.put("android.permission.READ_CALENDAR", "CALENDAR");//允许程序读取用户的日程信息
//        permissionArrMap.put("android.permission.WRITE_CALENDAR", "CALENDAR");//允许程序写入日程，但不可读取

        permissionArrMap.put("android.permission.CAMERA", "CAMERA");//允许程序访问摄像头进行拍照

//        permissionArrMap.put("android.permission.READ_CONTACTS", "CONTACTS");//允许程序访问联系人通讯录信息
//        permissionArrMap.put("android.permission.WRITE_CONTACTS", "CONTACTS");//写入联系人,但不可读取
//        permissionArrMap.put("android.permission.GET_ACCOUNTS", "CONTACTS");//允许程序访问账户Gmail列表

        permissionArrMap.put("android.permission.ACCESS_FINE_LOCATION", "LOCATION");//允许程序通过GPS芯片接收卫星的定位信息
        permissionArrMap.put("android.permission.ACCESS_COARSE_LOCATION", "LOCATION");//允许程序通过WiFi或移动基站的方式获取用户错略的经纬度信息

        permissionArrMap.put("android.permission.RECORD_AUDIO", "MICROPHONE");//允许程序录制声音通过手机或耳机的麦克

    }
}