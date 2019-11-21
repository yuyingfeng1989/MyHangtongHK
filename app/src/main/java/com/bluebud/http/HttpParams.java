package com.bluebud.http;

import com.bluebud.info.AlarmClockInfo;
import com.bluebud.info.AlarmSwitch;
import com.bluebud.info.FunctionControlInfo;
import com.bluebud.info.GeofenceObj;
import com.bluebud.info.LitemallInfo;
import com.bluebud.info.LostCard2Car;
import com.bluebud.info.LostCard2People;
import com.bluebud.info.LostCard2Pet;
import com.bluebud.utils.SystemUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;

public class HttpParams {
    public static final boolean ISDEBUG = false;
    // 测试环境
//    public static final String URL_CENTER_ADDRESS_CN = "http://218.17.161.66:10230";
//    public static final String URL_CENTER_ADDRESS_HK = "http://218.17.161.66:10230";

//    public static final String URL_CENTER_ADDRESS_CN = "http://218.17.161.66:30033";
//    public static final String URL_CENTER_ADDRESS_HK = "http://218.17.161.66:30033";
//    public static final String URL_CENTER_ADDRESS_CN = "http://218.17.161.66:30043";
//    public static final String URL_CENTER_ADDRESS_HK = "http://218.17.161.66:30043";

    //     香港正式环境
//	 public static final String URL_CENTER_ADDRESS_HK =
//	 "http://47.75.81.174:10000";
//	 public static final String URL_CENTER_ADDRESS_CN =
//	 "http://47.75.81.174:10000";

    public static final String URL_CENTER_ADDRESS_HK =
            "http://hkgw.litguardian.com:10000";
    public static final String URL_CENTER_ADDRESS_CN =
            "http://hkgw.litguardian.com:10000";

    /**
     * 外网商用测试
     */
//    public static final String URL_CENTER_ADDRESS_HK =
//            "http://showgw.litguardian.com:10000";
//    public static final String URL_CENTER_ADDRESS_CN =
//            "http://showgw.litguardian.com:10000";


//正式环境ip和端口号
//	 public static final String URL_CENTER_ADDRESS_HK =
//	 "http://54.179.149.239:10000";
//	 public static final String URL_CENTER_ADDRESS_CN =
//	 "http://54.179.149.239:10000";


    // 测试环境（王娟电脑IP ）
//	public static final String URL_CENTER_ADDRESS =
//	 "http://172.18.11.170:8090";
//	 public static final String URL_GUEST_ADDRESS =
//	 "http://172.18.11.170:8080";


    public static final String URL_CENTER_ACTION = "/SyncData/RemotingAPI";
    public static final String URL_ACTION = "/WebApi2d/WebAPIVersion3";

    public static final String SERVER_URL_CENTER_HK = URL_CENTER_ADDRESS_HK
            + URL_CENTER_ACTION;
    public static final String SERVER_URL_CENTER_CN = URL_CENTER_ADDRESS_CN
            + URL_CENTER_ACTION;

//    public static final String SERVER_URL_GUEST = URL_GUEST_ADDRESS
//            + URL_ACTION;

    public static final String HTTP_FUNCTION = "function";
    public static final String FUNCTION_GET_SERVER_CONN_INFO = "getServerConnInfo";
    public static final String FUNCTION_GET_SERVER_CONN_INFO_BY_USER = "getServerConnInfoByUser";
    public static final String FUNCTION_USER_LOGIN = "userLoginCN";// 登录
    public static final String FUNCTION_VERIFY_EMAIL = "againsendmail";// 邮箱验证
    public static final String FUNCTION_REGISTER = "registerCN";// 注册
    public static final String FUNCTION_FORGOT_PASSWORD = "forgetPassword";// 忘记密码
    public static final String FUNCTION_BINDING = "binding";// 追踪器绑定注册
    public static final String FUNCTION_UPDATE_DEVICE_AROUNDRANGES = "updateDeviceAroundRanges";//更新范围（周边）

    public static final String FUNCTION_EXIT = "logout";// 退出
    public static final String FUNCTION_GET_CURRENT_GPS = "getCurrentGPS";// 实时定位
    public static final String FUNCTION_GET_LASTER_GPS = "getLasterGPS";//  获取最后的GPS数据
    public static final String FUNCTION_GET_CAR_TRACK = "getDriveTrail";// 某一天的汽车轨迹
    public static final String FUNCTION_SET_GEOFENCE = "setGeoFenceCN";// 设置围栏大小
    public static final String FUNCTION_GET_GEOFENCE = "getGeoFenceCN";// 获取围栏信息
    public static final String FUNCTION_GET_GEOFENCE_CN = "getGeoFenceCN";// 获取围栏信息
    public static final String FUNCTION_CANCEL_GEOFENCE = "deleteGeoFence";// 取消围栏设置
    public static final String FUNCTION_GET_TRACKER_USER = "getTrackerUser";// 获取追踪器下的所有用户
    public static final String FUNCTION_GET_BLUETOOTH_WATCH_REMIND = "getBluetoothWatchRemind";// 获取蓝牙手表闹铃
    public static final String FUNCTION_DELETE_BLUETOOTH_WATCH_REMIND = "deleteBluetoothWatchRemind";// 获取蓝牙手表闹铃
    public static final String FUNCTION_SET_BLUETOOTH_WATCH_REMIND = "setBluetoothWatchRemind";// 设置蓝牙手表闹铃
    public static final String FUNCTION_UPDATE_BLUETOOTH_WATCH_REMIND = "updateBluetoothWatchRemind";// 设置蓝牙手表闹铃
    public static final String FUNCTION_CANCEL_AUTHORIZATION = "cancelAuthorization";// 取消绑定
    public static final String FUNCTION_AUTHORIZATION_BINDING = "authorizationBinding";// 增加授权账号
    public static final String FUNCTION_MODIFY_ACCOUNT_REMARK = "modifyAccountRemark";// 修改授权账号备注
    public static final String FUNCTION_SET_TRACKER_INFO_BY_SUPERUSER = "setTrackerInfoBySuperUser";// 超级用户保存追踪器设置
    public static final String FUNCTION_SET_LOSTCARD = "setInfo";
    public static final String FUNCTION_GET_LOSTCARD = "getInfo";
    public static final String FUNCTION_SET_HEAD_PORTRAIT = "setHeadPortrait";// 上传追踪器头像
    public static final String FUNCTION_MODIFY_USER_PASSWORD = "modifyuserpassword";// 密码修改
    public static final String FUNCTION_MODIFY_SIM = "modifysim";
    public static final String FUNCTION_CHANGE_DEVICE_RANGES = "changeDeviceRanges";
    public static final String FUNCTION_SAVE_IDEA = "saveidea";// 意见反馈
    public static final String FUNCTION_CHECK_FOR_UPDATE = "checkForUpdate";// 检查更新
    public static final String FUNCTION_UPGRAD_DEVICE_SOFTWARE = "upgradDeviceSoftware";
    public static final String FUNCTION_GET_ALARM_INFO = "getalarminfo";// 获取警情信息
    public static final String FUNCTION_GET_GPS_INTERVAL = "getGpsInterval";// 得到定位频率
    public static final String FUNCTION_SET_GPS_INTERVAL = "setGpsInterval";// 设置定位频率
    public static final String FUNCTION_RESET = "reset";// 恢复出厂设置
    public static final String FUNCTION_SET_ALERTWAY = "setalertway";// 恢复出厂设置
    public static final String FUNCTION_LOCK_VEHICLE = "lockVehicle";// 锁定车辆
    public static final String FUNCTION_UNLOCK_VEHICLE = "unlockVehicle";// 解锁车辆
    public static final String FUNCTION_GET_TOGGLE = "gettoggle";// 解锁车辆
    public static final String FUNCTION_SET_TOGGLE = "settoggle";// 解锁车辆
    public static final String FUNCTION_GET_HISTORICAL_GPS_DATA = "getHistoricalGPSData";// 历史轨迹
    public static final String FUNCTION_SET_TIMEZONE = "settimezone";// 历史轨迹
    public static final String FUNCTION_TIMEZONE = "timezone";// 历史轨迹
    public static final String FUNCTION_SAVE_CLOCK = "setRemind";
    public static final String FUNCTION_GET_CLOCK = "getRemind";
    public static final String FUNCTION_DELETE_CLOCK = "deleteRemind";
    public static final String FUNCTION_REMIND_ID = "remindID";
    public static final String FUNCTION_SAVE_LOG = "savelog";
    public static final String FUNCTION_GET_ORDER_INFO = "getorderinfo";
    public static final String FUNCTION_GET_ORDER_WX = "getorderwx";
    public static final String FUNCTION_GET_ORDER_PP = "getorderpp";
    public static final String FUNCTION_GET_ORDER_PACKAGE = "getOrderPackage";
    public static final String FUNCTION_GET_UPGRAD_PROGRESS = "getupgradprogress";
    public static final String FUNCTION_SAVE_ORDER = "saveOrder";

    // guoqz add 20160302.
    public static final String FUNCTION_ADD_NICK_PHONE_BOOK = "addNamePhonebook";// 添加昵称及电话本号码

    public static final String FUNCTION_ADD_PHONE_BOOK = "addphonebook";// 添加电话本号码
    public static final String FUNCTION_GET_PHONE_BOOK = "getphonebook";// 获取电话本数据
    public static final String FUNCTION_SAVE_TIME_BOOT = "savetimeboot";// 保存开关机设置
    public static final String FUNCTION_GET_TIME_BOOT = "gettimeboot";// 获取开关机时间设定
    public static final String FUNCTION_SAVE_COURSE_DISABLE_TIME = "savecoursedisabletime";// 保存课程禁用设置
    public static final String FUNCTION_GET_COURSE_DISABLE_TIME = "getcoursedisabletime";// 获取课程禁用设置
    public static final String FUNCTION_SET_DEFENSIVE_STATUS = "setdefensivestatus";// 设防/撤防
    public static final String FUNCTION_SEND_DEFENSIVE_ORDER = "senddefensiveorder";// 下发设防指令
    public static final String FUNCTION_SET_DEVICE_TIMEZONE = "setdevicetimezone";
    public static final String FUNCTION_GET_DEVICE_TIMEZONE = "getdevicetimezone";
    public static final String FUNCTION_GET_SLEEP_INFO = "getSleepInfo";
    public static final String FUNCTION_SET_SLEEP_INFO = "setSleepInfo";
    public static final String FUNCTION_GET_ON_LINE_STATUS = "getOnlineStatus";
    public static final String FUNCTION_USER_LOGIN_CN = "userLoginCN";
    public static final String FUNCTION_REGISTER_CN = "registerCN";
    public static final String FUNCTION_FORGET_PASSWD_CN = "forgetPasswordCN";
    public static final String FUNCTION_GET_CAR_DATA = "getCarData";
    public static final String FUNCTION_UP_LOAD_GPS_DATA = "uploadGpsData";
    public static final String FUNCTION_PUSH_BLUETOOTH_WATCH_ALARM_DATA_TO_SERVICE = "pushBluetoothWatchAlarmDataToApp";
    public static final String FUNCTION_GET_CURRENT_BLUETOOTH_WATCH_GPSDATA = "getCurrentBluetoothWatchGpsData";
    public static final String FUNCTION_GET_SAFE_DRIVE_DATE = "getSafeDriveData";
    public static final String FUNCTION_GET_ECONOMICALDRIVEDATE = "getEconomicalDriveData";
    public static final String FUNCTION_DELETE_GEO_FENCE_CN = "deleteGeoFenceCN";
    public static final String FUNCTION_GET_DRIVETRAIL_DETAIL = "getDriveTrailDetail";
    public static final String FUNCTION_START_CAR_INSPECTION = "startCarInspection";
    public static final String FUNCTION_SET_GEOFENCE_CN = "setGeoFenceCN";
    public static final String FUNCTION_SET_DEVICESSTEP = "getDeviceSteps";
    public static final String FUNCITON_SET_GETDEVICELASTSTEP = "getDeviceLastStep";//今天的步数
    public static final String FUNCTION_SET_MOREGEOFENCE = "setGeoFence"; //设置多围栏
    public static final String FUNCTION_OPENUSERLOGINISBINDING = "openUserLoginIsBinding";//facebook绑定原账号
    public static final String FUNTCION_OPENUSERLOGIN = "openUserLogin";//facebook登录
    public static final String FUNCTION_SETDEVICESCHOOLHOURS = "setDeviceSchoolHours";//设置上课时间和课程名
    public static final String FUNCTION_GETDEVICESCHOOLHOURS = "getDeviceSchoolHours";//获取上课时间和课程名
    public static final String FUNCTION_GETDEVICEENABLESWITCH = "getDeviceEnableSwitch";//获取设备开关
    public static final String FUNCTION_SETDEVICEENABLESWITCH = "setDeviceEnableSwitch";//设置设备开关

    //参数
    public static final String PARAMS_ID = "id";
    public static final String PARAMS_USER_NAME = "username";
    public static final String PARAMS_PASSWORD = "password";
    public static final String PARAMS_TRACKER_NO = "deviceSn";
    public static final String PARAMS_USER_ID = "user_id";
    public static final String PARAMS_WEEK = "week";
    public static final String PARAMS_TIME = "time";
    public static final String PARAMS_PROFILE = "profile";
    public static final String PARAMS_RING = "ring";
    public static final String PARAMS_ALERT_TYPE = "alert_type";
    public static final String PARAMS_FLAG = "flag";
    public static final String PARAMS_TYPE1 = "type";
    public static final String PARAMS_TITLE_LEN = "title_len";
    public static final String PARAMS_TITLE1 = "title";
    public static final String PARAMS_IMAGE_LEN = "image_len";
    public static final String PARAMS_IMAGE_NAME = "image_name";
    public static final String PARAMS_VERSION = "version";
    public static final String PARAMS_SIM_INFO = "simNo";//手表sim卡号
    public static final String PARAMS_COUNTRY = "customCountry";//区分地区

    public static final String PARAMS_COLLECT_DATETIME = "collect_datetime";
    public static final String PARAMS_GPS_TIME = "gpstime";

    public static final String ALARMIDS = "alarmIDS";

    public static final String PARAMS_SIM_NO = "simNo";
    public static final String PARAMS_AROUND_RANGES = "aroundRanges";
    public static final String PARAMS_DEVICE_RANGES = "deviceRanges";
    public static final String PARAMS_RANGES = "ranges";
    public static final String PARAMS_LAG = "lat";
    public static final String PARAMS_LNG = "lng";
    public static final String PARAMS_RADIUS = "radius";
    public static final String PARAMS_NICKNAME = "nickName";
    public static final String PARAMS_NICKNAME1 = "nickname";
    public static final String PARAMS_ISGPS = "isGps";
    public static final String PARAMS_HUMAN_FEATURE = "humanFeature";
    public static final String PARAMS_MOBILE1 = "mobile1";
    public static final String PARAMS_MOBILE2 = "mobile2";
    public static final String PARAMS_MOBILE3 = "mobile3";
    public static final String PARAMS_HUMAN_HEIGHT = "humanHeight";
    public static final String PARAMS_HUMAN_WEIGHT = "humanWeight";
    public static final String PARAMS_HUMAN_AGE = "humanAge";
    public static final String PARAMS_HUMAN_SEX = "humanSex";
    public static final String PARAMS_HUMAN_STEP = "humanStep";
    public static final String PARAMS_STEP = "step";
    public static final String PARAMS_HUMAN_ADDR = "humanAddr";
    public static final String PARAMS_HUMAN_LOST_ADDR = "humanLostAddr";
    public static final String PARAMS_PET_SEX = "pet_sex";
    public static final String PARAMS_PET_BREED = "pet_breed";
    public static final String PARAMS_PET_WEIGHT = "pet_weight";
    public static final String PARAMS_PET_AGE = "pet_age";
    public static final String PARAMS_PET_FEATURE = "pet_feature";
    public static final String PARAMS_PET_ADDR = "pet_addr";
    public static final String PARAMS_PET_LOSTADDR = "pet_lost_addr";
    public static final String PARAMS_MOTORBIKE_CODE = "motor_no";
    public static final String PARAMS_MOTORBIKE_TYPE = "moto_type";
    public static final String PARAMS_MOTORBIKE_CC = "motor_cc";
    public static final String PARAMS_MOTORBIKE_BRAND = "motor_trademark";
    public static final String PARAMS_MOTORBIKE_SET = "motor_set";
    public static final String PARAMS_MOTORBIKE_YEAR = "motor_year";
    public static final String PARAMS_CAR_CODE = "car_no";
    public static final String PARAMS_OBD_CODE = "obd_no";
    public static final String PARAMS_CAR_VIN = "car_vin";
    public static final String PARAMS_CAR_ENGINE = "car_engin";
    public static final String PARAMS_CAR_SET = "car_set";
    public static final String PARAMS_CAR_BRAND = "car_brand";
    public static final String PARAMS_CAR_YEAR = "car_year";
    public static final String PARAMS_CAR_TYPE = "car_type";
    public static final String PARAMS_OBD_TYPE = "obd_type";
    public static final String PARAMS_OBD_VIN = "car_vin";
    public static final String PARAMS_CAR_GASOLINE = "car_oil_type";
    public static final String PARAMS_CAR_MILEAGE = "car_mileage";
    public static final String PARAMS_CAR_AUDITTIME = "car_check_time";
    public static final String PARAMS_SIM = "sim";
    public static final String PARAMS_HEAD_PORTRAIT = "headPortrait";
    public static final String PARAMS_CURRENT_PASSWORD = "currentpassword";
    public static final String PARAMS_NEW_PASSWORD = "newpassword";
    public static final String PARAMS_TITLE = "title";
    public static final String PARAMS_CONTENT = "content";
    public static final String PARAMS_PHONE_TYPE = "phone_type";
    public static final String PARAMS_PHONE_VERSION = "phone_version";
    public static final String PARAMS_OS = "phoneOS";
    public static final String PARAMS_START = "start";
    public static final String PARAMS_END = "end";
    public static final String PARAMS_INTERVAL = "interval";
    public static final String PARAMS_ALERTWAY = "alertway";
    public static final String PARAMS_SOS = "sos";
    public static final String PARAMS_BOUNDARY = "boundary";
    public static final String PARAMS_VOLTAGE = "voltage";
    public static final String PARAMS_TAKEOFF = "takeOff";//脱落报警
    public static final String PARAMS_OUTAGE = "outage";//断电报警
    public static final String PARAMS_WATER = "water";//水温报警
    public static final String PARAMS_TOW = "tow";
    public static final String PARAMS_VIBRATION = "vibration";
    public static final String PARAMS_CLIPPING = "clipping";
    public static final String PARAMS_SPEED = "speed";
    public static final String PARAMS_SPEED_VALUE = "speedValue";
    public static final String PARAMS_SPEED_TIME = "speedTime";
    public static final String PARAMS_TYPE = "type";
    public static final String PARAMS_START_TIME = "startTime";
    public static final String PARAMS_END_TIME = "endTime";
    public static final String PARAMS_DATE_TIME = "datetime";
    public static final String PARAMS_TIMEZONE = "timezone";
    public static final String PARAMS_TIMEZONE_ID = "timezoneId";
    public static final String PARAMS_LANGUAGE = "language";
    public static final String PARAMS_TIMEZONE_CHECK = "timezoneCheck";
    public static final String PARAMS_WEEKLY = "weekly";
    public static final String PARAMS_MONTHLY = "monthly";
    public static final String PARAMS_YEARLY = "yearly";
    public static final String PARAMS_ONE = "monday";
    public static final String PARAMS_TWO = "tuesday";
    public static final String PARAMS_THREE = "wednesday";
    public static final String PARAMS_FOUR = "thursday";
    public static final String PARAMS_FIVE = "friday";
    public static final String PARAMS_SIX = "saturday";
    public static final String PARAMS_SEVEN = "sunday";
    public static final String PARAMS_SPECIFICYEAR = "specificYear";
    public static final String PARAMS_SPECIFICMONTH = "specificMonth";
    public static final String PARAMS_SPECIFICDAY = "specificDay";
    public static final String PARAMS_ISEND = "isEnd";
    public static final String PARAMS_DIABOLO = "diabolo";
    public static final String PARAMS_LOG = "log";
    public static final String PARAMS_SERVERNO = "serverNo";
    public static final String PARAMS_SUBJECT = "subject";
    public static final String PARAMS_BODY = "body";

    public static final String PARAMS_TARGET_VERSION = "targetVersion";
    public static final String PARAMS_UNIT = "unit";
    public static final String PARAMS_PHONE = "phone";
    public static final String PARAMS_PHOTO = "photo";
    public static final String PARAMS_ADMIN_INDEX = "adminindex";
    public static final String PARAMS_ENABLE = "enable";
    public static final String PARAMS_BOOT_TIME = "boottime";
    public static final String PARAMS_SHUT_DOWN_TIME = "shutdowntime";
    public static final String PARAMS_REPEAT_DAY = "repeatday";
    public static final String PARAMS_AM_START_TIME = "amstarttime";
    public static final String PARAMS_AM_END_TIME = "amendtime";
    public static final String PARAMS_TM_START_TIME = "tmstarttime";
    public static final String PARAMS_TM_END_TIME = "tmendtime";
    public static final String PARAMS_DEFENSIVE_STATUS = "defensivestatus";
    public static final String PARAMS_PROTOCOL_TYPE = "protocoltype";
    public static final String PARAMS_START_TIME1 = "starttime";
    public static final String PARAMS_END_TIME1 = "endtime";
    public static final String PARAMS_AREAID = "areaid";
    public static final String PARAMS_FENCE_NAME = "defencename";
    public static final String PARAMS_AREO_ID = "areaid";
    public static final String PARAMS_DEFENCESTATUS = "defencestatus";//开关状态
    public static final String PARAMS_ISOUT = "isOut";//围栏状态
    public static final String PARAMS_START_DATE_TIME = "startdatetime";
    public static final String PARAMS_END_DATE_TIME = "enddatetime";
    public static final String HUMAN_BIRTHDAY = "humanBirthday";
    public static final String PET_BIRTHDAY = "pet_birthday";
    public static final String CAR_BUYTIME = "car_buytime";
    public static final String OBD_BUYTIME = "obd_buytime";
    public static final String MOTOR_BUYTIME = "motor_buytime";
    public static final String DELETEALARMINFO = "deleteAlarmInfo";
    public static final String SETALARMSTATUS = "setAlarmStatus";
    public static final String FUNCTION_GET_SYSTEM_NOTICE = "getSystemNotice";
    public static final String FUNCTION_GET_GOODS_INFO = "getGoodsInfo";
    public static final String FUNCTION_START_WALK_DOG = "startWalkDog";
    public static final String FUNCTION_END_WALK_DOG = "endWalkDog";
    public static final String FUNCTION_ID = "id";
    public static final String FUNCTION_WALK_DOG_TRAIL = "walkDogTrail";
    public static final String FUNCTION_UNFINISH_WALK_DOG = "unfinishWalkDog";
    public static final String FUNCTION_RECENT_GPS_DATA = "recentGpsData";
    public static final String FUNCTION_WALK_DOG_TRAIL_DETAIL = "walkDogTrailDetail";
    public static final String PARAMS_AUTHORIZED_NAME = "authorizedname";

    public static final String PARAMS_PET_INSUR_CODE = "insur_code";
    public static final String FUNCTION_ISEXIST_DEVICE_CODE = "isExistDeviceCode";
    public static final String FUNCTION_SET_PET_INSURANCE = "setPetInsurance";
    public static final String FUNCTION_GET_PET_INSURANCE = "getPetInsurance";
    public static final String FUNCTION_SET_DEVICE_REMIND = "setDeviceRemind";
    public static final String FUNCTION_GET_DEVICE_REMIND = "getDeviceRemind";
    public static final String FUNCTION_SET_DEVICE_MONITORING = "setDeviceMonitoring";
    public static final String FUNCTION_SET_DEVICE_SHUT_DOWN = "setDeviceShutdown";
    public static final String FUCTION_SET_DEVICE_RESATRT = "setDeviceRestart";
    public static final String FUNCTION_SET_DEVICE_SEARCH = "setDeviceSearch";
    public static final String FUNCTION_SET_ROMETERECODER = "setDeviceRecord";
    public static final String FUNCTION_GET_DEVICE_MONITORING_PHONE = "getDeviceMonitoringPhone";

    public static final String FUNCTION_SET_DEVICE_STEP = "setDeviceStep";
    public static final String FUNCTION_GET_DEVICE_STEP = "getDeviceStep";

    public static final String PARAMS_USERNAME = "user_name";
    public static final String PARAMS_REAL_NAME = "real_name";
    public static final String PARAMS_MOBILE = "mobile";
    public static final String PARAMS_DOG_NAME = "dog_name";
    public static final String PARAMS_COLOUR = "colour";
    public static final String PARAMS_TAIL_SHAPE = "tail_shape";
    public static final String PARAMS_AGE = "age";
    public static final String PARAMS_SEX = "sex";
    public static final String PARAMS_IS_CUSTOMIZED_APP = "isCustomizedApp";
    public static final String PARAMS_INDEX = "index";
    public static final String PARAMS_NAME = "name";
    public static final String PARAMS_USER_DATA = "user_data";
    public static final String FUNCTION_SET_DEVICE_APN = "setDeviceAPN";// 设置APN
    public static final String FUNCTION_GET_DEVICE_APN = "getDeviceAPN";// 获取APN
    public static final String FUNCTION_GET_DEVICE_WIFI = "getDeviceWifi";// 获取wifi
    public static final String FUNCTION_SET_DEVICE_WIFI = "setDeviceWifi";// 设置wifi
    // wifi密码设置
    public static final String PARAMS_PASSWORD_STATE = "isSetGw";
    public static final String PARAMS_PRICE = "price";
    public static final String PARAMS_ORDER_PACKAGE_ID = "orderPackageId";
    public static final String PARAMS_PAYTYPE = "payType";
    public static final String PARAMS_CURRENCYUNIT = "currencyUnit";
    public static final String PARAMS_TRADENO = "tradeNo";

    public static final String PARAMS_STARTTIME = "startDate";
    public static final String PARAMS_ENDTIME = "endDate";

    public static String PARAMS_ISBINDING = "isBinding";//绑定facebook账号
    public static String PARAMS_ACCESSTOKEN = "accessToken";//facebook Token
    public static String PARAMS_OPENTYPE = "openType";//用户类型
    public static String PARAMS_OPENID = "openID";//facebook userId

    /**
     * 课程表参数
     */
    public static String PARAMS_isSchoolHours = "isSchoolHours";
    public static String PARAMS_dayOfWeek = "dayOfWeek";
    public static String PARAMS_courseOfday = "courseOfday";
    public static String PARAMS_courseName = "courseName";
    public static String PARAMS_schoolHour = "schoolHour";

    public static String PARAMS_RESET = "reset";//恢复出厂设置
    public static String PARAMS_SHUTDOWN = "shutdown";//关机
    public static String PARAMS_RESTART = "restart";//复位

    public static String PARAMS_USERAPPNUMBER = "userAppNumber";//App当前版本号
    public static String PARAMS_USERAPPVERSION = "userAppVersion";//App版本 通用版 0，国内版 1，香港版  2
    public static String PARAMS_PHONEVERSION = "phoneVersion";//App机型


    /**
     * 注册
     */
    public static RequestParams userRegister(String username, String password, int iServerNo, int iTimeZoneId) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_REGISTER);
        params.put(PARAMS_USER_NAME, username);
        params.put(PARAMS_PASSWORD, password);
        params.put(PARAMS_SERVERNO, iServerNo);
        params.put(PARAMS_TIMEZONE, iTimeZoneId);
        params.put(PARAMS_IS_CUSTOMIZED_APP, 4);
        return params;
    }

    /**
     * 登陆
     *
     * @param username
     * @param password
     * @return
     */
    public static RequestParams userLogin(String username, String password) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_USER_LOGIN);
        params.put(PARAMS_USER_NAME, username);
        params.put(PARAMS_PASSWORD, password);

        return params;
    }

    /**
     * 邮箱验证
     *
     * @return
     */
    public static RequestParams verifyEmail() {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_VERIFY_EMAIL);

        return params;
    }

    /**
     * 设备绑定
     *
     * @param trackerID
     * @param simNo
     * @param phone1
     * @param phone2
     * @param phone3
     * @param trackerType
     * @param protocolType 默认值为0，追踪器设备=0，手表=1，OBD=2
     * @return
     */
    public static RequestParams bindingDevice(String trackerID, String simNo,
                                              String phone1, String phone2, String phone3, int trackerType,
                                              int protocolType) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_BINDING);
        params.put(PARAMS_TRACKER_NO, trackerID);
        params.put(PARAMS_SIM_NO, simNo);
        params.put(PARAMS_RANGES, trackerType);
        params.put(PARAMS_PROTOCOL_TYPE, protocolType);
        params.put(PARAMS_MOBILE1, phone1);
        params.put(PARAMS_MOBILE2, phone2);
        params.put(PARAMS_MOBILE3, phone3);

        return params;
    }

    /**
     * 设备绑定
     *
     * @param trackerID
     * @param simNo     默认值为0，追踪器设备=0，手表=1，OBD=2
     * @return
     */
    public static RequestParams bindingDevice(String trackerID, String simNo, int aroundRanges) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_BINDING);
        params.put(PARAMS_TRACKER_NO, trackerID);
        params.put(PARAMS_SIM_NO, simNo);
        params.put(PARAMS_AROUND_RANGES, aroundRanges);

        return params;
    }

    public static RequestParams updateDeviceAroundRanges(String trackerID, String simNo, int aroundRanges) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_UPDATE_DEVICE_AROUNDRANGES);
        params.put(PARAMS_TRACKER_NO, trackerID);
        params.put(PARAMS_SIM_NO, simNo);
        params.put(PARAMS_AROUND_RANGES, aroundRanges);

        return params;
    }


    /**
     * 设备绑定蓝牙手表
     *
     * @param trackerID
     * @param simNo     默认值为0，追踪器设备=0，手表=1，OBD=2
     * @return
     */
    public static RequestParams bindingDevice(String trackerID, String simNo, int ranges, String mobile1, String mobile2, String mobile3, int aroundRanges) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_BINDING);
        params.put(PARAMS_TRACKER_NO, trackerID);
        params.put(PARAMS_SIM_NO, simNo);
        params.put(PARAMS_RANGES, ranges);
        params.put(PARAMS_MOBILE1, mobile1);
        params.put(PARAMS_MOBILE2, mobile2);
        params.put(PARAMS_MOBILE3, mobile3);
        params.put(PARAMS_PROTOCOL_TYPE, 4);

        params.put(PARAMS_AROUND_RANGES, aroundRanges);

        return params;
    }


    /**
     * 密码修改
     *
     * @param email
     * @return
     */
    public static RequestParams forgotPassword(String email) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_FORGOT_PASSWORD);
        params.put(PARAMS_USER_NAME, email);

        return params;
    }

    /**
     * 退出
     *
     * @return
     */
    public static RequestParams exit() {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_EXIT);

        return params;
    }

    public static RequestParams currentGPS(String trackerNo, String endTime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_CURRENT_GPS);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_END_TIME, endTime);

        return params;
    }


    public static RequestParams getLasterGPS(String trackerNo, String endTime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_LASTER_GPS);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_END_TIME, endTime);

        return params;
    }

    public static RequestParams getCarTrack(String trackerNo, String dateTime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_CAR_TRACK);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_DATE_TIME, dateTime);
        return params;
    }

    public static RequestParams getGEOfence(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_GEOFENCE);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams setGEOfence(String trackerNo, double lat,
                                            double lng, int radius) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_GEOFENCE);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_LAG, lat);
        params.put(PARAMS_LNG, lng);
        params.put(PARAMS_RADIUS, radius);

        return params;
    }

    public static RequestParams cancelGEOfence(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_CANCEL_GEOFENCE);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams getHistoricalGPSData(String trackerNo,
                                                     String sTim1, String sTime2) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_HISTORICAL_GPS_DATA);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_START_TIME, sTim1);
        params.put(PARAMS_END_TIME, sTime2);

        return params;
    }

    public static RequestParams getTrackerUser(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_TRACKER_USER);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams cancelAuthorization(String trackerNo,
                                                    String userName) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_CANCEL_AUTHORIZATION);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_USER_NAME, userName);

        return params;
    }

    public static RequestParams authorizationBinding(String trackerNo,
                                                     String userName, String nickName, int isGps) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_AUTHORIZATION_BINDING);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_USER_NAME, userName);
        params.put(PARAMS_NICKNAME1, nickName);
        params.put(PARAMS_ISGPS, isGps);

        return params;
    }

    public static RequestParams getLostCard(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_LOSTCARD);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams trackerPicture(String trackerNo, File file)
            throws FileNotFoundException {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_HEAD_PORTRAIT);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_HEAD_PORTRAIT, file);

        return params;
    }

    public static RequestParams setLostCard2People(String trackerNo,
                                                   LostCard2People lostCard) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_LOSTCARD);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        if (!Utils.isEmpty(lostCard.nickname)) {
            params.put(PARAMS_NICKNAME, lostCard.nickname);
        }
        if (!Utils.isEmpty(lostCard.human_feature)) {
            params.put(PARAMS_HUMAN_FEATURE, lostCard.human_feature);
        }
        if (!Utils.isEmpty(lostCard.human_height)) {
            params.put(PARAMS_HUMAN_HEIGHT, lostCard.human_height.replaceAll("cm", ""));
        }
        if (!Utils.isEmpty(lostCard.human_weight)) {
            params.put(PARAMS_HUMAN_WEIGHT, lostCard.human_weight.replaceAll("kg", ""));
        }
        if (!Utils.isEmpty(lostCard.human_step)) {
            params.put(PARAMS_HUMAN_STEP, lostCard.human_step);
        }
        if (!Utils.isEmpty(lostCard.human_addr)) {
            params.put(PARAMS_HUMAN_ADDR, lostCard.human_addr);
        }
        // if (!Utils.isEmpty(lostCard.human_lost_addr)) {
        // params.put(PARAMS_HUMAN_LOST_ADDR, lostCard.human_lost_addr);
        // }
        if (!Utils.isEmpty(lostCard.human_sex)) {
            params.put(PARAMS_HUMAN_SEX, lostCard.human_sex);
        }
        if (!Utils.isEmpty(lostCard.human_age)) {
            params.put(PARAMS_HUMAN_AGE, lostCard.human_age);
        }
        if (!Utils.isEmpty(lostCard.mobile1)) {
            params.put(PARAMS_MOBILE1, lostCard.mobile1);
        }

        if (!Utils.isEmpty(lostCard.human_birthday)) {
            params.put(HUMAN_BIRTHDAY, lostCard.human_birthday);
        }

        return params;
    }

    /**
     * 手表sim卡号设置
     */
    public static RequestParams setSimCard(String trackerNo, String simNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_LOSTCARD);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_SIM_INFO, simNo);
        return params;
    }

    public static RequestParams setLostCard(String trackerNo, int code,
                                            int type, String content) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_LOSTCARD);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        String markName = "";
        switch (code) {
            case 3:// 昵称//昵称
                markName = PARAMS_NICKNAME;
                break;
            case 4:// 身高
                markName = PARAMS_HUMAN_HEIGHT;
                break;
            case 5:// 体重
                markName = PARAMS_HUMAN_WEIGHT;

                break;
            case 6:// 步长
                markName = PARAMS_HUMAN_STEP;
                break;
            case 7:// 宠物的品种
                markName = PARAMS_PET_BREED;
                break;
            case 8:// 宠物的体重
                markName = PARAMS_PET_WEIGHT;
                break;
            case 9:// 外貌特证

                if (type == 2) {
                    markName = PARAMS_PET_FEATURE;
                } else {
                    markName = PARAMS_HUMAN_FEATURE;
                }
                break;
            case 10:// 紧急联络人
                markName = PARAMS_MOBILE1;

                break;
            case 11:// 车牌号
                if (type == 4) {
                    markName = PARAMS_MOTORBIKE_CODE;
                } else {
                    markName = PARAMS_CAR_CODE;
                }
                break;
            case 12:// 车的型号
                if (type == 4) {
                    markName = PARAMS_MOTORBIKE_TYPE;
                } else {
                    markName = PARAMS_CAR_TYPE;
                }
                break;
            case 13:// 车的紧急联络人
                markName = PARAMS_MOBILE1;
                break;
            default:
                break;
        }
        params.put(markName, content);

        return params;
    }

    public static RequestParams setLostCard2Pet(String trackerNo,
                                                LostCard2Pet lostCard) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_LOSTCARD);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        if (!Utils.isEmpty(lostCard.nickname)) {
            params.put(PARAMS_NICKNAME, lostCard.nickname);
        }
        if (!Utils.isEmpty(lostCard.pet_feature)) {
            params.put(PARAMS_PET_FEATURE, lostCard.pet_feature);
        }
        if (!Utils.isEmpty(lostCard.pet_weight)) {
            params.put(PARAMS_PET_WEIGHT, lostCard.pet_weight.replaceAll("kg", ""));
        }
        if (!Utils.isEmpty(lostCard.pet_addr)) {
            params.put(PARAMS_PET_ADDR, lostCard.pet_addr);
        }
        // if (!Utils.isEmpty(lostCard.pet_lost_addr)) {
        // params.put(PARAMS_PET_LOSTADDR, lostCard.pet_lost_addr);
        // }
        if (!Utils.isEmpty(lostCard.pet_sex)) {
            params.put(PARAMS_PET_SEX, lostCard.pet_sex);
        }
        if (!Utils.isEmpty(lostCard.pet_age)) {
            params.put(PARAMS_PET_AGE, lostCard.pet_age);
        }
        if (!Utils.isEmpty(lostCard.pet_breed)) {
            params.put(PARAMS_PET_BREED, lostCard.pet_breed);
        }
        if (!Utils.isEmpty(lostCard.mobile1)) {
            params.put(PARAMS_MOBILE1, lostCard.mobile1);
        }
        if (!Utils.isEmpty(lostCard.mobile2)) {
            params.put(PARAMS_MOBILE2, lostCard.mobile2);
        }
        if (!Utils.isEmpty(lostCard.mobile3)) {
            params.put(PARAMS_MOBILE3, lostCard.mobile3);
        }
        if (!Utils.isEmpty(lostCard.pet_birthday)) {
            params.put(PET_BIRTHDAY, lostCard.pet_birthday);
        }

        return params;
    }

    public static RequestParams setLostCard2Motor(String trackerNo,
                                                  LostCard2Car lostCard) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_LOSTCARD);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        if (!Utils.isEmpty(lostCard.nickname)) {
            params.put(PARAMS_NICKNAME, lostCard.nickname);
        }
    /*	if (!Utils.isEmpty(lostCard.motor_no)) {
            params.put(PARAMS_MOTORBIKE_CODE, lostCard.motor_no);
		}*/
        if (!Utils.isEmpty(lostCard.moto_type)) {
            params.put(PARAMS_MOTORBIKE_TYPE, lostCard.moto_type);
        }
        if (!Utils.isEmpty(lostCard.mobile1)) {
            params.put(PARAMS_MOBILE1, lostCard.mobile1);
        }
        if (!Utils.isEmpty(lostCard.motor_buytime)) {
            params.put(MOTOR_BUYTIME, lostCard.motor_buytime);
        }

        // if (!Utils.isEmpty(lostCard.nickname)) {
        // params.put(PARAMS_NICKNAME, lostCard.nickname);
        // }
        //
        // if (!Utils.isEmpty(lostCard.motor_cc)) {
        // params.put(PARAMS_MOTORBIKE_CC, lostCard.motor_cc);
        // }
        // if (!Utils.isEmpty(lostCard.motor_trademark)) {
        // params.put(PARAMS_MOTORBIKE_BRAND, lostCard.motor_trademark);
        // }
        // if (!Utils.isEmpty(lostCard.motor_set)) {
        // params.put(PARAMS_MOTORBIKE_SET, lostCard.motor_set);
        // }
        // if (!Utils.isEmpty(lostCard.motor_year)) {
        // params.put(PARAMS_MOTORBIKE_YEAR, lostCard.motor_year);
        // }

        // if (!Utils.isEmpty(lostCard.mobile2)) {
        // params.put(PARAMS_MOBILE2, lostCard.mobile2);
        // }
        // if (!Utils.isEmpty(lostCard.mobile3)) {
        // params.put(PARAMS_MOBILE3, lostCard.mobile3);
        // }
        //

        return params;
    }

    public static RequestParams setLostCard2Car(String trackerNo,
                                                LostCard2Car lostCard) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_LOSTCARD);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        if (!Utils.isEmpty(lostCard.nickname)) {
            params.put(PARAMS_NICKNAME, lostCard.nickname);
        }
        /*if (!Utils.isEmpty(lostCard.car_no)) {
            params.put(PARAMS_CAR_CODE, lostCard.car_no);
		}*/
        if (!Utils.isEmpty(lostCard.car_type)) {
            params.put(PARAMS_CAR_TYPE, lostCard.car_type);
        }
        if (!Utils.isEmpty(lostCard.car_buytime)) {
            params.put(CAR_BUYTIME, lostCard.car_buytime);
        }
        if (!Utils.isEmpty(lostCard.mobile1)) {
            params.put(PARAMS_MOBILE1, lostCard.mobile1);
        }

        // if (!Utils.isEmpty(lostCard.nickname)) {
        // params.put(PARAMS_NICKNAME, lostCard.nickname);
        // }

        // if (!Utils.isEmpty(lostCard.car_vin)) {
        // params.put(PARAMS_CAR_VIN, lostCard.car_vin);
        // }
        // if (!Utils.isEmpty(lostCard.car_engine)) {
        // params.put(PARAMS_CAR_ENGINE, lostCard.car_engine);
        // }
        // if (!Utils.isEmpty(lostCard.car_set)) {
        // params.put(PARAMS_CAR_SET, lostCard.car_set);
        // }
        // if (!Utils.isEmpty(lostCard.car_brand)) {
        // params.put(PARAMS_CAR_BRAND, lostCard.car_brand);
        // }
        // if (!Utils.isEmpty(lostCard.car_year)) {
        // params.put(PARAMS_CAR_YEAR, lostCard.car_year);
        // }

        // if (!Utils.isEmpty(lostCard.car_oil_type)) {
        // params.put(PARAMS_CAR_GASOLINE, lostCard.car_oil_type);
        // }
        // if (!Utils.isEmpty(lostCard.car_mileage)) {
        // params.put(PARAMS_CAR_MILEAGE, lostCard.car_mileage);
        // }
        // if (!Utils.isEmpty(lostCard.car_check_time)) {
        // params.put(PARAMS_CAR_AUDITTIME, lostCard.car_check_time);
        // }

        // if (!Utils.isEmpty(lostCard.mobile2)) {
        // params.put(PARAMS_MOBILE2, lostCard.mobile2);
        // }
        // if (!Utils.isEmpty(lostCard.mobile3)) {
        // params.put(PARAMS_MOBILE3, lostCard.mobile3);
        // }

        return params;
    }

    public static RequestParams setLostCard2Obd(String trackerNo,
                                                LostCard2Car lostCard) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_LOSTCARD);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        if (!Utils.isEmpty(lostCard.nickname)) {
            params.put(PARAMS_NICKNAME, lostCard.nickname);
        }
        /*if (!Utils.isEmpty(lostCard.obd_no)) {
            params.put(PARAMS_OBD_CODE, lostCard.obd_no);
		}*/
        if (!Utils.isEmpty(lostCard.obd_type)) {
            params.put(PARAMS_OBD_TYPE, lostCard.obd_type);
        }
        if (!Utils.isEmpty(lostCard.mobile1)) {
            params.put(PARAMS_MOBILE1, lostCard.mobile1);
        }
        if (!Utils.isEmpty(lostCard.obd_buytime)) {
            params.put(OBD_BUYTIME, lostCard.obd_buytime);
        }
        if (!Utils.isEmpty(lostCard.car_vin)) {
            params.put(PARAMS_OBD_VIN, lostCard.car_vin);
        }

        return params;
    }

    public static RequestParams modifyUserPassword(String userName,
                                                   String currentPassword, String newPassword) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_MODIFY_USER_PASSWORD);
        params.put(PARAMS_CURRENT_PASSWORD, currentPassword);
        params.put(PARAMS_NEW_PASSWORD, newPassword);

        return params;
    }

    public static RequestParams saveIdea(String trackerNo, String title,
                                         String content, String phone_type, String phone_version) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SAVE_IDEA);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_TITLE, title);
        params.put(PARAMS_PHONE_TYPE, phone_type);//机型
        params.put(PARAMS_PHONE_VERSION, phone_version);//系统版本
        params.put(PARAMS_CONTENT, content);

        return params;
    }

    public static RequestParams checkForUpdate(String trackerNo, String userName) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_CHECK_FOR_UPDATE);
        params.put(PARAMS_OS, "android_3");
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_USER_NAME, userName);
        params.put(PARAMS_IS_CUSTOMIZED_APP, "4_0");
        return params;
    }

    public static RequestParams upgradDeviceSoftware(String trackerNo,
                                                     String targetVersion) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_UPGRAD_DEVICE_SOFTWARE);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_TARGET_VERSION, targetVersion);

        return params;
    }

    public static RequestParams getUpgradProgress(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_UPGRAD_PROGRESS);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams getAlarmInfo(String trackerNo,
                                             String sTimeStart, String sTimeEnd, int type) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_ALARM_INFO);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_START, sTimeStart);
        params.put(PARAMS_END, sTimeEnd);
        params.put(PARAMS_TYPE, type);

        return params;
    }

    // 删除警情
    public static RequestParams deleteAlarmInfo(String username,
                                                String trackerNo, String alarmIDS) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, DELETEALARMINFO);
        params.put(PARAMS_USER_NAME, username);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(ALARMIDS, alarmIDS);
        return params;
    }

    // 设置未读，已读状态
    public static RequestParams setAlarmStatus(String trackerNo, String alarmIDS) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, SETALARMSTATUS);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(ALARMIDS, alarmIDS);
        return params;
    }

    public static RequestParams getGPSInterval(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_GPS_INTERVAL);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams setGPSInterval(String trackerNo, int interval) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_GPS_INTERVAL);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_INTERVAL, interval);

        return params;
    }

    public static RequestParams reset(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_RESET);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    /**
     * 1双开，2双关，3震动开，响铃关，4震动关响铃开
     *
     * @param type
     * @return
     */
    public static RequestParams setAlertway(int type) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_ALERTWAY);
        params.put(PARAMS_ALERTWAY, type);

        return params;
    }

    public static RequestParams lockVehicle(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_LOCK_VEHICLE);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams unlockVehicle(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_UNLOCK_VEHICLE);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams getToggle(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_TOGGLE);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams setToggle(String userName, String trackerNo,
                                          int type, AlarmSwitch alarmSwitch, String product_type) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_TOGGLE);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        if (3 == type || 6 == type) {
            params.put(PARAMS_SOS, 0);
            params.put(PARAMS_BOUNDARY, alarmSwitch.boundary);
            params.put(PARAMS_VOLTAGE, alarmSwitch.voltage);
            params.put(PARAMS_TOW, alarmSwitch.tow);
            params.put(PARAMS_CLIPPING, alarmSwitch.clipping);
            params.put(PARAMS_SPEED, alarmSwitch.speed);
            params.put(PARAMS_SPEED_VALUE, alarmSwitch.speedValue);
            params.put(PARAMS_SPEED_TIME, alarmSwitch.speedTime);
            if (6 == type) {
                params.put(PARAMS_OUTAGE, alarmSwitch.outage);
                params.put(PARAMS_WATER, alarmSwitch.water);
            }
        } else if (2 == type) {
            params.put(PARAMS_SOS, 0);
            params.put(PARAMS_BOUNDARY, alarmSwitch.boundary);
            params.put(PARAMS_VOLTAGE, alarmSwitch.voltage);
            params.put(PARAMS_TOW, 0);
            params.put(PARAMS_CLIPPING, 0);
            params.put(PARAMS_SPEED, 0);
        } else if (4 == type) {
            if ("18".equals(product_type)) {
                params.put(PARAMS_SOS, 0);
                params.put(PARAMS_BOUNDARY, alarmSwitch.boundary);
                params.put(PARAMS_VOLTAGE, alarmSwitch.voltage);
                params.put(PARAMS_VIBRATION, alarmSwitch.vibration);
                params.put(PARAMS_CLIPPING, alarmSwitch.clipping);
                params.put(PARAMS_SPEED, alarmSwitch.speed);
                params.put(PARAMS_SPEED_VALUE, alarmSwitch.speedValue);
                params.put(PARAMS_SPEED_TIME, alarmSwitch.speedTime);
            } else {
                params.put(PARAMS_SOS, 0);
                params.put(PARAMS_BOUNDARY, alarmSwitch.boundary);
                params.put(PARAMS_VOLTAGE, alarmSwitch.voltage);
                params.put(PARAMS_TOW, alarmSwitch.tow);
                params.put(PARAMS_CLIPPING, alarmSwitch.clipping);
                params.put(PARAMS_SPEED, alarmSwitch.speed);
                params.put(PARAMS_SPEED_VALUE, alarmSwitch.speedValue);
                params.put(PARAMS_SPEED_TIME, alarmSwitch.speedTime);
            }
        } else if (type == 5 && product_type.equals("27")) {
            params.put(PARAMS_SOS, alarmSwitch.sos);
//			params.put(PARAMS_BOUNDARY, alarmSwitch.boundary);
//			params.put(PARAMS_VOLTAGE, alarmSwitch.voltage);
//			params.put(PARAMS_TOW, 0);
//			params.put(PARAMS_CLIPPING, 0);
//			params.put(PARAMS_SPEED, 0);
        }
//        else if (type == 5 && product_type.equals("30")) {//k1
//            params.put(PARAMS_SOS, alarmSwitch.sos);
//            params.put(PARAMS_BOUNDARY, alarmSwitch.boundary);
//            params.put(PARAMS_VOLTAGE, alarmSwitch.voltage);
//            params.put(PARAMS_TAKEOFF, alarmSwitch.takeOff);
//			}
        else if (type == 5 && product_type.equals("22")) {
            params.put(PARAMS_SOS, alarmSwitch.sos);
            params.put(PARAMS_BOUNDARY, alarmSwitch.boundary);
            params.put(PARAMS_VOLTAGE, alarmSwitch.voltage);
            params.put(PARAMS_TAKEOFF, alarmSwitch.takeOff);
        } else {
            params.put(PARAMS_SOS, alarmSwitch.sos);
            params.put(PARAMS_BOUNDARY, alarmSwitch.boundary);
            params.put(PARAMS_VOLTAGE, alarmSwitch.voltage);
            params.put(PARAMS_TOW, 0);
            params.put(PARAMS_CLIPPING, 0);
            params.put(PARAMS_SPEED, 0);
        }

        return params;
    }

    public static RequestParams modifySIM(String userName, String trackerNo,
                                          String simNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_MODIFY_SIM);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_SIM_NO, simNo);

        return params;
    }

    public static RequestParams changeDeviceRanges(String trackerNo, int range) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_CHANGE_DEVICE_RANGES);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_DEVICE_RANGES, range);

        return params;
    }

    public static RequestParams modifyRange(String trackerNo, String simNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_CHANGE_DEVICE_RANGES);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_SIM_NO, simNo);

        return params;
    }

    public static RequestParams setTimeZone(long timezone, int timezoneId,
                                            long timezoneCheck) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_TIMEZONE);
        params.put(PARAMS_TIMEZONE, timezone);
        params.put(PARAMS_TIMEZONE_ID, timezoneId);
        params.put(PARAMS_TIMEZONE_CHECK, timezoneCheck);

        return params;
    }

    public static RequestParams saveClock(int id, AlarmClockInfo alarmClockInfo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SAVE_CLOCK);

        params.put(FUNCTION_REMIND_ID, id);

        params.put(PARAMS_TITLE, alarmClockInfo.title);
        StringBuffer sbTime = new StringBuffer();
        for (int i = 0; i < alarmClockInfo.times.size(); i++) {
            sbTime.append(alarmClockInfo.times.get(i));
            if (i != alarmClockInfo.times.size() - 1) {
                sbTime.append(",");
            }
        }
        params.put(PARAMS_DIABOLO, sbTime.toString());
        if (0 == alarmClockInfo.iType) {
            params.put(PARAMS_YEARLY, 1);
            params.put(PARAMS_SPECIFICYEAR, alarmClockInfo.repeat_year);
            params.put(PARAMS_SPECIFICMONTH, alarmClockInfo.repeat_month);
            params.put(PARAMS_SPECIFICDAY, alarmClockInfo.repeat_day);

            params.put(PARAMS_MONTHLY, 0);
            params.put(PARAMS_ISEND, 0);
            params.put(PARAMS_WEEKLY, 0);
            params.put(PARAMS_ONE, 0);
            params.put(PARAMS_TWO, 0);
            params.put(PARAMS_THREE, 0);
            params.put(PARAMS_FOUR, 0);
            params.put(PARAMS_FIVE, 0);
            params.put(PARAMS_SIX, 0);
            params.put(PARAMS_SEVEN, 0);
        } else if (1 == alarmClockInfo.iType) {
            params.put(PARAMS_MONTHLY, 1);
            params.put(PARAMS_SPECIFICYEAR, alarmClockInfo.repeat_year);
            params.put(PARAMS_SPECIFICMONTH, alarmClockInfo.repeat_month);
            params.put(PARAMS_SPECIFICDAY, alarmClockInfo.repeat_day);

            if (alarmClockInfo.isEnd) {
                params.put(PARAMS_ISEND, 1);
            } else {
                params.put(PARAMS_ISEND, 0);
            }
            params.put(PARAMS_YEARLY, 0);
            params.put(PARAMS_WEEKLY, 0);
            params.put(PARAMS_ONE, 0);
            params.put(PARAMS_TWO, 0);
            params.put(PARAMS_THREE, 0);
            params.put(PARAMS_FOUR, 0);
            params.put(PARAMS_FIVE, 0);
            params.put(PARAMS_SIX, 0);
            params.put(PARAMS_SEVEN, 0);
        } else if (2 == alarmClockInfo.iType) {
            params.put(PARAMS_WEEKLY, 1);
            if ("1".equals(alarmClockInfo.arrWeeks[0])) {
                params.put(PARAMS_ONE, 1);
            } else {
                params.put(PARAMS_ONE, 0);
            }
            if ("1".equals(alarmClockInfo.arrWeeks[1])) {
                params.put(PARAMS_TWO, 1);
            } else {
                params.put(PARAMS_TWO, 0);
            }
            if ("1".equals(alarmClockInfo.arrWeeks[2])) {
                params.put(PARAMS_THREE, 1);
            } else {
                params.put(PARAMS_THREE, 0);
            }
            if ("1".equals(alarmClockInfo.arrWeeks[3])) {
                params.put(PARAMS_FOUR, 1);
            } else {
                params.put(PARAMS_FOUR, 0);
            }
            if ("1".equals(alarmClockInfo.arrWeeks[4])) {
                params.put(PARAMS_FIVE, 1);
            } else {
                params.put(PARAMS_FIVE, 0);
            }
            if ("1".equals(alarmClockInfo.arrWeeks[5])) {
                params.put(PARAMS_SIX, 1);
            } else {
                params.put(PARAMS_SIX, 0);
            }
            if ("1".equals(alarmClockInfo.arrWeeks[6])) {
                params.put(PARAMS_SEVEN, 1);
            } else {
                params.put(PARAMS_SEVEN, 0);
            }

            params.put(PARAMS_ISEND, 0);
            params.put(PARAMS_YEARLY, 0);
            params.put(PARAMS_MONTHLY, 0);
            params.put(PARAMS_SPECIFICYEAR, alarmClockInfo.repeat_year);
            params.put(PARAMS_SPECIFICMONTH, alarmClockInfo.repeat_month);
            params.put(PARAMS_SPECIFICDAY, alarmClockInfo.repeat_day);
        }

        return params;
    }

    public static RequestParams getClock() {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_CLOCK);

        return params;
    }

    public static RequestParams deleteClock(int id) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_DELETE_CLOCK);
        params.put(FUNCTION_REMIND_ID, id);

        return params;
    }

    public static RequestParams savaLog(String str) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SAVE_LOG);
        params.put(PARAMS_LOG, str);

        return params;
    }

    public static RequestParams getServerConnInfoByUser(String sUserName) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_SERVER_CONN_INFO_BY_USER);
        params.put(PARAMS_USER_NAME, sUserName);

        return params;
    }

    public static RequestParams getServerConnInfo(String version) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_SERVER_CONN_INFO);
        params.put(PARAMS_VERSION, version);
        params.put(PARAMS_COUNTRY, "HK");
        return params;
    }

    public static RequestParams getOrderInfo(String subject, String body,
                                             String price, String trackerId, String orderPackageId) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_ORDER_INFO);
        params.put(PARAMS_SUBJECT, subject);
        params.put(PARAMS_BODY, body);
        params.put(PARAMS_PRICE, price);
        params.put(PARAMS_TRACKER_NO, trackerId);
        params.put(PARAMS_ORDER_PACKAGE_ID, orderPackageId);
        params.put(PARAMS_UNIT, "RMB");

        return params;
    }

    public static RequestParams getOrderInfoWX(String body, String price,
                                               String trackerId, String orderPackageId) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_ORDER_WX);
        params.put(PARAMS_BODY, body);
        params.put(PARAMS_PRICE, price);
        params.put(PARAMS_TRACKER_NO, trackerId);
        params.put(PARAMS_ORDER_PACKAGE_ID, orderPackageId);
        params.put(PARAMS_UNIT, "RMB");

        return params;
    }

    public static RequestParams setOrderInfoPP(String body, String price,
                                               String trackerId, String orderId, String orderPackageId) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_ORDER_PP);
        params.put(PARAMS_BODY, body);
        params.put(PARAMS_PRICE, price);
        params.put(PARAMS_TRACKER_NO, trackerId);
        params.put(PARAMS_ID, orderId);
        params.put(PARAMS_ORDER_PACKAGE_ID, orderPackageId);
        params.put(PARAMS_UNIT, "USD");

        return params;
    }

    public static RequestParams getOrderPackage() {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_ORDER_PACKAGE);

        return params;
    }

    public static RequestParams addPhoneBook(String trackerNo, String phone, String photoData,
                                             int position) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_ADD_PHONE_BOOK);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_PHONE, phone);
        params.put(PARAMS_PHOTO, photoData);
        params.put(PARAMS_ADMIN_INDEX, 1);

        return params;
    }

    // guoqz add 20160302.

    /**
     * 保存通讯录
     *
     * @param trackerNo 追踪器ID
     * @param phoneName 昵称+电话
     * @param position  紧急联系人系列号
     * @return
     */
    public static RequestParams addNamePhonebook(String trackerNo,
                                                 String phoneName, String photoData, int position) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_ADD_NICK_PHONE_BOOK);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_PHONE, phoneName);
        params.put(PARAMS_PHOTO, photoData);
        params.put(PARAMS_ADMIN_INDEX, 1);

        return params;
    }

    public static RequestParams getPhoneBook(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_PHONE_BOOK);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams saveTimeSwitch(String trackerNo, int enable,
                                               String timeON, String timeOFF) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SAVE_TIME_BOOT);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_ENABLE, enable);
        params.put(PARAMS_BOOT_TIME, timeON);
        params.put(PARAMS_SHUT_DOWN_TIME, timeOFF);

        return params;
    }

    public static RequestParams getTimeSwitch(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_TIME_BOOT);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams saveTimeCourse(String trackerNo, int enable,
                                               String timeAmON, String timeAmOFF, String timePmON,
                                               String timePmOFF, String repeatDay) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SAVE_COURSE_DISABLE_TIME);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_ENABLE, enable);
        params.put(PARAMS_AM_START_TIME, timeAmON);
        params.put(PARAMS_AM_END_TIME, timeAmOFF);
        params.put(PARAMS_TM_START_TIME, timePmON);
        params.put(PARAMS_TM_END_TIME, timePmOFF);
        params.put(PARAMS_REPEAT_DAY, repeatDay);

        return params;
    }

    public static RequestParams getTimeCourse(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_COURSE_DISABLE_TIME);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams setDefensiveStatus(String trackerNo, int status) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_DEFENSIVE_STATUS);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_DEFENSIVE_STATUS, status);

        return params;
    }

    public static RequestParams sendDefensiveOrder(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SEND_DEFENSIVE_ORDER);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams setTimeZoneWatch(String trackerNo,
                                                 long timezone, int timezoneId, int language) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_DEVICE_TIMEZONE);
        params.put(PARAMS_TIMEZONE, timezone);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_TIMEZONE_ID, timezoneId);
        params.put(PARAMS_LANGUAGE, language);


        return params;
    }

    public static RequestParams getTimeZoneWatch(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_DEVICE_TIMEZONE);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams getSleepInfo(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_SLEEP_INFO);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    public static RequestParams setSleepInfo(String trackerNo, int enable,
                                             String startTime, String endTime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_SLEEP_INFO);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_ENABLE, enable);
        params.put(PARAMS_BOOT_TIME, startTime);
        params.put(PARAMS_SHUT_DOWN_TIME, endTime);

        return params;
    }

    public static RequestParams getOnLineStatus(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_ON_LINE_STATUS);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    /**
     * @param @param  username 用户名
     * @param @param  pwd 密码
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: userLoginCN
     * @Description: 航通3.0 用户登录接口
     */
    public static RequestParams userLoginCN(String username, String pwd) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_USER_LOGIN_CN);
        params.put(PARAMS_USER_NAME, username);
        params.put(PARAMS_PASSWORD, pwd);
        params.put(PARAMS_IS_CUSTOMIZED_APP, 4);
        params.put(PARAMS_USERAPPNUMBER, SystemUtil.getApkVersion());
        params.put(PARAMS_USERAPPVERSION, "2");
        params.put(PARAMS_PHONEVERSION, SystemUtil.getSystemModel());
        return params;
    }

    /**
     * @param @param  username 用户名
     * @param @param  pwd 密码
     * @param @param  serverno 服务器编号
     * @param @param  timezone 时区
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: registerCN
     * @Description: 用户注册
     */
    public static RequestParams registerCN(String username, String pwd,
                                           int serverno, int timezone) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_REGISTER_CN);
        params.put(PARAMS_USER_NAME, username);
        params.put(PARAMS_PASSWORD, pwd);
        params.put(PARAMS_SERVERNO, serverno);
        params.put(FUNCTION_TIMEZONE, timezone);
        return params;
    }

    /**
     * @param @param  username
     * @param @param  pwd
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: forgetPasswordCN
     * @Description: 忘记密码
     */
    public static RequestParams forgetPasswordCN(String username, String pwd) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_FORGET_PASSWD_CN);
        params.put(PARAMS_USER_NAME, username);
        params.put(PARAMS_PASSWORD, pwd);
        return params;
    }

    /**
     * @param @param  trackerNo
     * @param @param  lat
     * @param @param  lng
     * @param @param  radius
     * @param @param  areoid
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: setGEOfence
     * @Description: TODO
     */
    public static RequestParams setGEOfence(String trackerNo, double lat,
                                            double lng, int radius, int areoid) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_GEOFENCE);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_LAG, lat);
        params.put(PARAMS_LNG, lng);
        params.put(PARAMS_RADIUS, radius);
        params.put(PARAMS_AREO_ID, areoid);
        return params;
    }

    /**
     * @Description: 设置围栏 3.0 版本
     * 设置单围栏
     */
    public static RequestParams setGeofenceCN(String trackerNo, GeofenceObj.DefenceList defenceList) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_GEOFENCE_CN);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_LAG, defenceList.lat);
        params.put(PARAMS_LNG, defenceList.lng);
        params.put(PARAMS_RADIUS, defenceList.radius);
        params.put(PARAMS_AREO_ID, defenceList.areaid);
        params.put(PARAMS_FENCE_NAME, defenceList.defencename);
        params.put(PARAMS_DEFENCESTATUS, defenceList.defencestatus);
        params.put(PARAMS_ISOUT, defenceList.isOut);
        return params;
    }

    /**
     * 设置多围栏
     */
    public static RequestParams setGeofence(GeofenceObj.DefenceList fenceInfo, String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_MOREGEOFENCE);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_LAG, fenceInfo.lat);
        params.put(PARAMS_LNG, fenceInfo.lng);
        params.put(PARAMS_RADIUS, fenceInfo.radius);
        params.put(PARAMS_AREO_ID, fenceInfo.areaid);
        params.put(PARAMS_FENCE_NAME, fenceInfo.defencename);
        params.put(PARAMS_DEFENCESTATUS, fenceInfo.defencestatus);
        params.put(PARAMS_ISOUT, fenceInfo.isOut);
        return params;
    }

    /**
     * @param @param  trackerNo
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: getGeoFenceCN
     * @Description: 获取围栏
     */
    public static RequestParams getGeoFenceCN(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_GEOFENCE_CN);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    /**
     * @Description: 删除围栏
     */
    public static RequestParams deleteGeoFenceCN(String trackerNo, String areaid) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_DELETE_GEO_FENCE_CN);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_AREAID, areaid);
        return params;
    }


    /**
     * @param @param  trackerNo
     * @param @param  datetime
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: getEconomicalDriveData
     * @Description: 获取经济驾驶数据
     */
    public static RequestParams getEconomicalDriveData(String trackerNo, String starttime,
                                                       String datetime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_ECONOMICALDRIVEDATE);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_START_TIME, starttime);
        params.put(PARAMS_END_TIME, datetime);
        return params;
    }

    /**
     * @param @param  trackerNo
     * @param @param  datetime
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: getSafeDriveData
     * @Description: 获取安全驾驶数据
     */
    public static RequestParams getSafeDriveData(String trackerNo, String starttime,
                                                 String datetime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_SAFE_DRIVE_DATE);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_START_TIME, starttime);
        params.put(PARAMS_END_TIME, datetime);
        return params;
    }

    /**
     * @param @param  trackerNo
     * @param @param  datetime
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: getCarData
     * @Description: 获取车辆检测信息
     */
    public static RequestParams getCarData(String trackerNo, String datetime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_CAR_DATA);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_DATE_TIME, datetime);
        return params;
    }

    /**
     * 蓝牙手表上传定位数据
     */
    public static RequestParams uploadGpsData(String trackerNo, String datetime, double lat, double lng) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_UP_LOAD_GPS_DATA);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_COLLECT_DATETIME, datetime);
        params.put(PARAMS_LAG, lat);
        params.put(PARAMS_LNG, lng);

        return params;
    }

    /**
     * 蓝牙手表推送警情信息上传到服务器接口
     */
    public static RequestParams pushBluetoothWatchAlarmDataToService(String trackerNo, String datetime, double lat, double lng) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_PUSH_BLUETOOTH_WATCH_ALARM_DATA_TO_SERVICE);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_GPS_TIME, datetime);
        params.put(PARAMS_LAG, lat);
        params.put(PARAMS_LNG, lng);

        return params;
    }

    /**
     * 获取蓝牙手表最后定位位置
     */
    public static RequestParams getCurrentBluetoothWatchGpsData(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_CURRENT_BLUETOOTH_WATCH_GPSDATA);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }


    /**
     * @param @param  trackerNo
     * @param @param  datetime
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: getDriveTrail
     * @Description: 获取某一天的轨迹
     */
    public static RequestParams getDriveTrail(String trackerNo, String datetime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_CAR_TRACK);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_DATE_TIME, datetime);
        return params;
    }

    /**
     * @param @param  trackerNo
     * @param @param  startdatetime
     * @param @param  enddatetime
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: getDriveTrailDetail
     * @Description: 获取某一段时间的轨迹详情
     */
    public static RequestParams getDriveTrailDetail(String trackerNo,
                                                    String startdatetime, String enddatetime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_DRIVETRAIL_DETAIL);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_START_DATE_TIME, startdatetime);
        params.put(PARAMS_END_DATE_TIME, enddatetime);
        return params;
    }

    /**
     * @param @param  trackerNo
     * @param @param  datetime
     * @param @return
     * @return RequestParams
     * @throws
     * @Title: startCarInspection
     * @Description: 开始汽车检测
     */
    public static RequestParams startCarInspection(String trackerNo, String datetime, int flag) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_START_CAR_INSPECTION);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_DATE_TIME, datetime);
        params.put(PARAMS_FLAG, flag);
        return params;
    }

    // 获取系统消息远程接口
    public static RequestParams geSystemNotice() {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_SYSTEM_NOTICE);

        return params;
    }

    // 获取商品信息接口
    public static RequestParams getGoodsInformation() {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_GOODS_INFO);

        return params;
    }

    // 开始遛狗
    public static RequestParams startWalkDog(String trackerID) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_START_WALK_DOG);
        params.put(PARAMS_TRACKER_NO, trackerID);
        return params;
    }

    //未结束的遛狗记录
    public static RequestParams unfinishWalkDog(String trackerID) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_UNFINISH_WALK_DOG);
        params.put(PARAMS_TRACKER_NO, trackerID);
        return params;
    }

    // 轮询遛狗数据
    public static RequestParams recentGpsData(String trackerID, String dateTime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_RECENT_GPS_DATA);
        params.put(PARAMS_TRACKER_NO, trackerID);
        params.put(PARAMS_DATE_TIME, dateTime);
        return params;
    }

    // 结束遛狗
    public static RequestParams endWalkDog(String trackerID, String id) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_END_WALK_DOG);
        params.put(PARAMS_TRACKER_NO, trackerID);
        params.put(PARAMS_ID, id);
        return params;
    }

    // 得到遛狗记录
    public static RequestParams getWalkDogTrail(String trackerID, String dateTime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_WALK_DOG_TRAIL);
        params.put(PARAMS_TRACKER_NO, trackerID);
        params.put(PARAMS_DATE_TIME, dateTime);
        return params;
    }

    // 遛狗轨迹详情
    public static RequestParams getwalkDogTrailDetail(String trackerID, String startTime, String endTime, String id) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_WALK_DOG_TRAIL_DETAIL);
        params.put(PARAMS_TRACKER_NO, trackerID);
        params.put(PARAMS_START_TIME, startTime);
        params.put(PARAMS_END_TIME, endTime);
        params.put(PARAMS_ID, id);
        return params;
    }


    public static RequestParams modifyAccountRemark(String trackerNo,
                                                    String userName, String nickName, int isGps) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_MODIFY_ACCOUNT_REMARK);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_AUTHORIZED_NAME, userName);
        params.put(PARAMS_NICKNAME1, nickName);
        params.put(PARAMS_ISGPS, isGps);

        return params;
    }

    //得到蓝牙
    public static RequestParams getBluetoothWatchRemind(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_BLUETOOTH_WATCH_REMIND);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    //设置蓝牙手表闹铃提示
    public static RequestParams setBluetoothWatchRemind(String trackerNo, String user_id,
                                                        String week, String time, int ring, int alarm_type, int flag, int type, int title_len,
                                                        String Title, int image_len, String image_name) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_BLUETOOTH_WATCH_REMIND);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_USER_ID, user_id);
        params.put(PARAMS_WEEK, week);
        params.put(PARAMS_TIME, time);
        params.put(PARAMS_RING, ring);
        params.put(PARAMS_ALERT_TYPE, alarm_type);
        params.put(PARAMS_FLAG, flag);
        params.put(PARAMS_TYPE, type);
        params.put(PARAMS_TITLE_LEN, title_len);
        params.put(PARAMS_TITLE, Title);
        params.put(PARAMS_IMAGE_LEN, image_len);
        params.put(PARAMS_IMAGE_NAME, image_name);
        return params;
    }

    //修改蓝牙手表闹铃提示
    public static RequestParams updateBluetoothWatchRemind(String trackerNo, String user_id, int id,
                                                           String week, String time, int ring, int alarm_type, int flag, int type, int title_len,
                                                           String Title, int image_len, String image_name, int version) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_UPDATE_BLUETOOTH_WATCH_REMIND);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_USER_ID, user_id);
        params.put(PARAMS_ID, id);
        params.put(PARAMS_WEEK, week);
        params.put(PARAMS_TIME, time);
        params.put(PARAMS_RING, ring);
        params.put(PARAMS_ALERT_TYPE, alarm_type);
        params.put(PARAMS_FLAG, flag);
        params.put(PARAMS_TYPE1, type);
        params.put(PARAMS_TITLE_LEN, title_len);
        params.put(PARAMS_TITLE1, Title);
        params.put(PARAMS_IMAGE_LEN, image_len);
        params.put(PARAMS_IMAGE_NAME, image_name);
        params.put(PARAMS_VERSION, version);
        return params;
    }

    //删除蓝牙闹钟
    public static RequestParams deleteBluetoothWatchRemind(String trackerNo, int id) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_DELETE_BLUETOOTH_WATCH_REMIND);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_ID, id);

        return params;
    }

    //验证宠物保险
    public static RequestParams isExistDeviceCode(String trackerNo,
                                                  String insurCode) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_ISEXIST_DEVICE_CODE);
        params.put(PARAMS_PET_INSUR_CODE, insurCode);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }

    //新增宠物保险订单
                /*deviceSn：设备编号
                user_name：app帐号
				real_name：真实姓名
				mobile：电话号码
				dog_name：宠物昵称
				type：宠物品种
				colour：宠物颜色
				tail_shape：尾巴颜色：1直，2卷，3曲
				age：年龄
				sex：性别：0 公/男 1母/女*/
    public static RequestParams setPetInsurance(String trackerNo,
                                                String user_name, String real_name, String mobile, String dog_name, String type, String colour, int tail_shape,
                                                String age, int sex) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_PET_INSURANCE);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_USERNAME, user_name);
        params.put(PARAMS_REAL_NAME, real_name);
        params.put(PARAMS_MOBILE, mobile);
        params.put(PARAMS_DOG_NAME, dog_name);
        params.put(PARAMS_TYPE, type);
        params.put(PARAMS_COLOUR, colour);
        params.put(PARAMS_TAIL_SHAPE, tail_shape);
        params.put(PARAMS_AGE, age);
        params.put(PARAMS_SEX, sex);
        return params;
    }


    //查询宠物保险订单
    public static RequestParams getPetInsurance(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_PET_INSURANCE);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    //设置闹钟
/*				输入参数	deviceSn：设备号
                id:提醒id唯一标识
				index:表示第几个闹钟，从1开始（1,2,3,4,5等）
				time：格式：08:10-1-3-0111110
				        08:10 时间格式hh24:mi 
				        1     0:关闭,1:打开
				        3     自定义频率（响一次）
				        0111110  周期：星期一到星期五*/
    public static RequestParams setDeviceRemind(String trackerNo, int index, String time) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_DEVICE_REMIND);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_INDEX, index);
        params.put(PARAMS_TIME, time);
        params.put(PARAMS_PROFILE, 1);
        return params;
    }

    //获取闹钟
    public static RequestParams getDeviceRemind(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_DEVICE_REMIND);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }

    //设置监听
    public static RequestParams setDeviceMonitoring(String trackerNo, String mobile) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_DEVICE_MONITORING);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_MOBILE, mobile);

        return params;
    }

    //设置远程关机
    public static RequestParams setDeviceShutdown(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_DEVICE_SHUT_DOWN);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }

    //设置手表远程重启
    public static RequestParams setDeviceRestart(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUCTION_SET_DEVICE_RESATRT);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }

    //设置远程录音
    public static RequestParams setRemoteRecoder(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_ROMETERECODER);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }

    //找设备 找手表
    public static RequestParams setDeviceSearch(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_DEVICE_SEARCH);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }

    //获取中心号码
    public static RequestParams getDeviceMonitoringPhone(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_DEVICE_MONITORING_PHONE);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }
    //设置770设备计步开关
    //deviceSn：设备号
    //step:计步开关 0：关 1：开

    public static RequestParams setDeviceStep(String trackerNo, int step) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_DEVICE_STEP);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_STEP, step);

        return params;
    }

    public static RequestParams getDeviceStep(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_DEVICE_STEP);
        params.put(PARAMS_TRACKER_NO, trackerNo);


        return params;
    }

    public static RequestParams setDeviceAPN(String trackerNo, String nameApn, String userName, String pwd, String userData) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_DEVICE_APN);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_NAME, nameApn);
        params.put(PARAMS_USERNAME, userName);
        params.put(PARAMS_PASSWORD, pwd);
        params.put(PARAMS_USER_DATA, userData);


        return params;
    }

    public static RequestParams getDeviceAPN(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_DEVICE_APN);
        params.put(PARAMS_TRACKER_NO, trackerNo);

        return params;
    }

    // wifi获取
    public static RequestParams getDeviceWifiNameAndPassword(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_DEVICE_WIFI);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }


    public static RequestParams setDeviceWifiNameAndPassword(String trackerNo,
                                                             String name, String password, String state) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_DEVICE_WIFI);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_NAME, name);
        params.put(PARAMS_PASSWORD, password);
        params.put(PARAMS_PASSWORD_STATE, state);

        return params;
    }

    public static RequestParams getOrderPackage(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GET_ORDER_PACKAGE);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }

    public static RequestParams saveOrder(String trackerNo, int payType, String price, String currencyUnit, String orderPackageId, String tradeNo) {

        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SAVE_ORDER);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_PAYTYPE, payType);
        params.put(PARAMS_PRICE, price);
        params.put(PARAMS_CURRENCYUNIT, currencyUnit);
        params.put(PARAMS_ORDER_PACKAGE_ID, orderPackageId);
        params.put(PARAMS_TRADENO, tradeNo);
        return params;
    }

    /**
     * 获取多天步数
     */
    public static RequestParams getDeviceStep(String trackerNo, String startDate, String endDate) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SET_DEVICESSTEP);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        params.put(PARAMS_STARTTIME, startDate);
        params.put(PARAMS_ENDTIME, endDate);
        return params;
    }

    /**
     * 获取当天的步数
     */
    public static RequestParams getDeviceLastStep(String trackerNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCITON_SET_GETDEVICELASTSTEP);
        params.put(PARAMS_TRACKER_NO, trackerNo);
        return params;
    }

    /**
     * facebook登录
     */
    public static RequestParams facebookLogin(String token, String openID) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNTCION_OPENUSERLOGIN);
        params.put(PARAMS_ACCESSTOKEN, token);
        params.put(PARAMS_OPENTYPE, 0);
        params.put(PARAMS_OPENID, openID);
        params.put(PARAMS_IS_CUSTOMIZED_APP, 4);
        params.put(PARAMS_USERAPPNUMBER, SystemUtil.getApkVersion());
        params.put(PARAMS_USERAPPVERSION, "2");
        params.put(PARAMS_PHONEVERSION, SystemUtil.getSystemModel());
        return params;
    }

    /**
     * facebook没有绑原账号，进行原账号绑定
     * isBind 0不绑定 1绑定
     */
    public static RequestParams facebookBindUser(String token, String userName, String password, int isBind, String openID, int timeZone, int serverNo) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_OPENUSERLOGINISBINDING);
        params.put(PARAMS_ACCESSTOKEN, token);
        params.put(PARAMS_OPENID, openID);
        params.put(PARAMS_IS_CUSTOMIZED_APP, 4);
        params.put(PARAMS_OPENTYPE, 0);
        params.put(PARAMS_USER_NAME, userName);
        params.put(PARAMS_PASSWORD, password);
        params.put(PARAMS_ISBINDING, isBind);
        params.put(PARAMS_TIMEZONE, timeZone);
        params.put(PARAMS_SERVERNO, serverNo);
        return params;
    }

    /**
     * 设置课程表名称和时间
     * isSchoolHours 0代表设置课程课程， 1代表上课时间
     */
    public static RequestParams setDeviceSchoolHours(int isSchoolHours, int dayOfWeek, int courseOfday, String courseName, String deviceSn, String schoolHour) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SETDEVICESCHOOLHOURS);
        params.put(PARAMS_isSchoolHours, isSchoolHours);
        if (isSchoolHours == 0) {
            params.put(PARAMS_TRACKER_NO, deviceSn);
            params.put(PARAMS_dayOfWeek, dayOfWeek);
            params.put(PARAMS_courseOfday, courseOfday + 1);
            params.put(PARAMS_courseName, courseName);
        } else {
            params.put(PARAMS_TRACKER_NO, deviceSn);
            params.put(PARAMS_schoolHour, schoolHour);
        }
        return params;
    }

    /**
     * 获取课程表名称和时间
     *
     * @return
     */
    public static RequestParams getDeviceSchoolHours(String deviceSn) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GETDEVICESCHOOLHOURS);
        params.put(PARAMS_TRACKER_NO, deviceSn);
        return params;
    }

    /**
     * 获取设备开关
     */
    public static RequestParams getDeviceEnableSwitch(String deviceSn) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_GETDEVICEENABLESWITCH);
        params.put(PARAMS_TRACKER_NO, deviceSn);
        return params;
    }

    /**
     * 设置设备开关
     * 0开启，1隐藏
     */
    public static RequestParams setDeviceEnableSwitch(String deviceSn, FunctionControlInfo reset, int position) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, FUNCTION_SETDEVICEENABLESWITCH);
        params.put(PARAMS_TRACKER_NO, deviceSn);
        if (position == 0)
            params.put(PARAMS_SHUTDOWN, reset.shutdown);
        else if (position == 1)
            params.put(PARAMS_RESTART, reset.restart);
        else if (position == 2)
            params.put(PARAMS_RESET, reset.reset);
        return params;
    }

    /**
     * 轮询litemall_IM
     *
     * @param liteMall
     * @return
     */
    public static RequestParams setLiteMallIm(LitemallInfo liteMall) {
        RequestParams params = new RequestParams();
        params.put("uid", liteMall.dscMallUserId);
        params.put("access_token", liteMall.dscMallToken);
        params.put("open_id", liteMall.dscMallOpenId);
        params.put("user_type", "customer");
        return params;
    }
}
