package com.bluebud.utils;

import android.content.Context;

import com.bluebud.app.App;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.info.Advertisement;
import com.bluebud.info.Tracker;
import com.bluebud.info.User;

import java.util.ArrayList;
import java.util.List;

public class UserUtil {

    public static User getUserInfo(Context context) {
        return UserSP.getInstance().getUserInfo(context);
    }

//    public static void savaUserInfo(Context context, String value) {
//        UserSP.getInstance().saveUserInfo(context, value);
//    }

    public static void savaUserInfo(Context context, User user) {
        UserSP.getInstance().saveUserInfo(context, GsonParse.object2Json(user));
    }

//    public static WisdomSeoretaryList getWisdomInfo(Context context) {
//        return UserSP.getInstance().getWisdomInfo(context);
//    }


//    public static void savaWisdomInfo(Context context, WisdomSeoretaryList wisdomSeoretaryList) {
//        UserSP.getInstance().saveWisdomInfo(context, GsonParse.object2Json(wisdomSeoretaryList));
//    }

//    public static void clearUserInfo(Context context) {
////        UserSP.getInstance().saveLogin(context, false);
//        UserUtil.saveServerUrl(context, "");
//    }

    public static void chooseCurrentTracker(Context context,
                                            List<Tracker> trackers) {
        Tracker mCurrentTracker = UserUtil.getCurrentTracker(context);
        boolean bDefault = false;
        if (null != mCurrentTracker
                && !Utils.isEmpty(mCurrentTracker.device_sn)) {
            for (int i = 0; i < trackers.size(); i++) {
                if (mCurrentTracker.device_sn.equals(trackers.get(i).device_sn)) {
                    bDefault = true;
                    mCurrentTracker = trackers.get(i);
                    break;
                }
            }
        }
        if (bDefault) {
            UserUtil.saveCurrentTracker(context, mCurrentTracker);
        } else {
            UserUtil.saveCurrentTracker(context, trackers.get(0));
        }
    }

    public static Tracker getCurrentTracker(Context context) {
        Tracker tracker = null;
        String value = UserSP.getInstance().getCurrentTracker(context);
        if (!Utils.isEmpty(value)) {
            tracker = GsonParse.json2object(value, Tracker.class);
        } else {
            List<Tracker> trackers = getUserInfo(context).device_list;
            if (null != trackers && trackers.size() > 0) {
                tracker = trackers.get(0);
            }
        }

        return tracker;
    }

    public static Tracker getCurrentTracker() {
        return getCurrentTracker(App.getContext());
    }

    public static void saveCurrentTracker(Context context, Tracker tracker) {
        String value = GsonParse.object2Json(tracker);
        UserSP.getInstance().saveCurrentTracker(context, value);

    }

//    //得到闹钟版本数据
//    public static int getAlarmClockVersion(Context context, String deviceNo, int id) {
//        int version = UserSP.getInstance().getAlarmClockVersion(context, deviceNo, id);
//        return version;
//    }

//    //保存闹钟版本数据
//    public static void saveAlarmClockVersion(Context context, String deviceNo, int id, int version) {
//        UserSP.getInstance().saveAlarmClockVersion(context, deviceNo, id, version);
//    }
//
//    //得到闹钟userid
//    public static userIDInfoList getCurrentuserIDData(Context context, String deviceNo) {
//        userIDInfoList list = null;
//        String value = UserSP.getInstance().getCurrentuserIDData(context, deviceNo);
//        if (!Utils.isEmpty(value)) {
//            list = GsonParse.json2object(value, userIDInfoList.class);
//        }
//        return list;
//    }
//
//    //保存闹钟userid
//    public static void saveCurrentuserIDData(Context context, String deviceNo, userIDInfoList info) {
//        String value = GsonParse.object2Json(info);
//        UserSP.getInstance().saveCurrentuserIDData(context, deviceNo, value);
//    }

//
//    //得到闹钟数据
//    public static WisdomSeoretaryList getCurrentWisdomSeoretyData(Context context, String device) {
//        WisdomSeoretaryList sportBluetoothInfo = null;
//        String value = UserSP.getInstance().getCurrentWisdomData(context, device);
//        if (!Utils.isEmpty(value)) {
//            sportBluetoothInfo = GsonParse.json2object(value, WisdomSeoretaryList.class);
//        }
//
//        return sportBluetoothInfo;
//    }
//
//    //保存闹钟数据
//    public static void saveCurrentWisdomSeoretyData(Context context, WisdomSeoretaryList info, String device) {
//        String value = GsonParse.object2Json(info);
//        UserSP.getInstance().saveCurrentWisdomData(context, value, device);
//    }

//    //得到运动数据
//    public static SportBluetoothList getCurrentSportData(Context context) {
//        SportBluetoothList sportBluetoothInfo = null;
//        String value = UserSP.getInstance().getCurrentSportData(context);
//        if (!Utils.isEmpty(value)) {
//            sportBluetoothInfo = GsonParse.json2object(value, SportBluetoothList.class);
//        }
//
//        return sportBluetoothInfo;
//    }

//    //保存运动数据
//    public static void saveCurrentSportData(Context context, SportBluetoothList info) {
//        String value = GsonParse.object2Json(info);
//        UserSP.getInstance().saveCurrentSportData(context, value);
//    }


//    //得到睡眠数据
//    public static SleepBluetoothList getCurrentSleepData(Context context) {
//        SleepBluetoothList sleepBluetoothInfo = null;
//        String value = UserSP.getInstance().getCurrentSleepData(context);
//        if (!Utils.isEmpty(value)) {
//            sleepBluetoothInfo = GsonParse.json2object(value, SleepBluetoothList.class);
//        }
//        return sleepBluetoothInfo;
//    }

//    //保存睡眠数据
//    public static void saveCurrentSleepData(Context context, SleepBluetoothList info) {
//        String value = GsonParse.object2Json(info);
//        UserSP.getInstance().saveCurrentSleepData(context, value);
//    }
//
//    //得到心率数据
//    public static HeadRateBluetoothList getCurrentHeadrateData(Context context) {
//        HeadRateBluetoothList headRateBluetoothInfo = null;
//        String value = UserSP.getInstance().getCurrentHeadrateData(context);
//        if (!Utils.isEmpty(value)) {
//            headRateBluetoothInfo = GsonParse.json2object(value, HeadRateBluetoothList.class);
//        }
//
//        return headRateBluetoothInfo;
//    }
//
//    //保存心率连续数据
//    public static void saveCurrentHeadratecontinuousData(Context context, HeadRateBluetoothList info) {
//        String value = GsonParse.object2Json(info);
//        UserSP.getInstance().saveCurrentHeadratecontinuousData(context, value);
//    }

//    //得到心率连续数据
//    public static HeadRateBluetoothList getCurrentHeadratecontinuousData(Context context) {
//        HeadRateBluetoothList headRateBluetoothInfo = null;
//        String value = UserSP.getInstance().getCurrentHeadratecontinuousData(context);
//        if (!Utils.isEmpty(value)) {
//            headRateBluetoothInfo = GsonParse.json2object(value, HeadRateBluetoothList.class);
//        }
//
//        return headRateBluetoothInfo;
//    }
//
//    //保存心率数据
//    public static void saveCurrentHeadrateData(Context context, HeadRateBluetoothList info) {
//        String value = GsonParse.object2Json(info);
//        UserSP.getInstance().saveCurrentHeadrateData(context, value);
//    }
//
//    public static FenceList getCurrentFence(Context context) {
//        FenceList fence = null;
//        String value = UserSP.getInstance().getCurrentFence(context);
//        fence = GsonParse.json2object(value, FenceList.class);
//        return fence;
//    }
//
//    public static void saveCurrentFence(Context context, FenceList fence) {
//        String value = GsonParse.object2Json(fence);
//        UserSP.getInstance().saveCurrentFence(context, value);
//    }

    /**
     * 注册服务器地址
     */
    public static String getServerUrl(Context context) {
        return UserSP.getInstance().getServerUrl(context);
    }

    public static String getServerIP(Context context){
        String url = UserSP.getInstance().getServerUrl(context);
        return  url.substring(0,url.indexOf("/We"));
    }

    public static void saveServerUrl(Context context, String serverUrl) {
        UserSP.getInstance().saveServerUrl(context, serverUrl);
    }

//    public static String getCurrentTrackerLatAndLng(Context context) {
//        return UserSP.getInstance().getCurrentTrackerGps(context);
//    }
//
//    public static void setCurrentTrackerLatAndLng(Context context, String latlng) {
//        UserSP.getInstance().saveCurrentTrackerGps(context, latlng);
//    }

    /**
     * 判断该用户是否当前追踪器的超级用户
     *
     * @return
     */
    public static boolean isSuperUser(Context context, String trackerNo) {
        String userName = UserSP.getInstance().getUserName(context);
        List<Tracker> trackerList = getUserInfo(context).device_list;
        int size = trackerList.size();
        for (int i = 0; i < size; i++) {
            Tracker tracker = trackerList.get(i);
            if (trackerNo.equals(tracker.device_sn)) {
                if (userName.equalsIgnoreCase(tracker.super_user))
                    return true;
            }
        }
        return false;
    }

//    /**
//     * 获取追踪器列表
//     *
//     * @param trackerList
//     * @param type        为0表示普通用户 为1表示超级用户
//     * @return
//     */
//    public static List<Tracker> getTrackers(String user,
//                                            List<Tracker> trackerList, int type) {
//        int size = trackerList.size();
//        List<Tracker> trackers = new ArrayList<Tracker>();
//        if (type == 1) {
//            for (int i = 0; i < size; i++) {
//                Tracker tracker = trackerList.get(i);
//                if (user.equals(tracker.super_user)) {
//                    trackers.add(tracker);
//                }
//            }
//            return trackers;
//        }
//        for (int i = 0; i < size; i++) {
//            Tracker tracker = trackerList.get(i);
//            if (!user.equals(tracker.super_user)) {
//                trackers.add(tracker);
//            }
//        }
//        return trackers;
//    }

    public static String getTitleTrackerName(Tracker tracker) {
        String sTrackerName = "";

        if (null != tracker) {
            // if (1 == tracker.ranges) {
            // if (!Utils.isEmpty(tracker.designation)) {
            // sTrackerName = tracker.designation;
            // }
            // } else if (2 == tracker.ranges) {
            // if (!Utils.isEmpty(tracker.petdesignation)) {
            // sTrackerName = tracker.petdesignation;
            // }
            // } else if (3 == tracker.ranges) {
            // if (!Utils.isEmpty(tracker.cardesignation)) {
            // sTrackerName = tracker.cardesignation;
            // }
            // } else if (4 == tracker.ranges) {
            // if (!Utils.isEmpty(tracker.motorbikedesignation)) {
            // sTrackerName = tracker.motorbikedesignation;
            // }
            // }

            if (!Utils.isEmpty(tracker.nickname)) {
                sTrackerName = tracker.nickname;
            }
        }
        return sTrackerName;
    }

    public static void reviseSimNo(Context context, String trackerNo,
                                   String simNo) {
        User user = UserUtil.getUserInfo(context);
        Tracker mCurTracker = UserUtil.getCurrentTracker(context);
        if (mCurTracker.device_sn.equals(trackerNo)) {
            mCurTracker.tracker_sim = simNo;
            UserUtil.saveCurrentTracker(context, mCurTracker);
        }
        for (int i = 0; i < user.device_list.size(); i++) {
            if (trackerNo.equals(user.device_list.get(i).device_sn)) {
                user.device_list.get(i).tracker_sim = simNo;
                break;
            }
        }
        UserUtil.savaUserInfo(context, user);
    }

    public static void reviseRanges(Context context, String trackerNo,
                                    int ranges) {
        User user = UserUtil.getUserInfo(context);
        for (int i = 0; i < user.device_list.size(); i++) {
            if (trackerNo.equals(user.device_list.get(i).device_sn)) {
                user.device_list.get(i).ranges = ranges;
                break;
            }
        }
        UserUtil.savaUserInfo(context, user);
    }

    public static void deleteTracker(Context context, String trackerNo) {
        User user = UserUtil.getUserInfo(context);
        for (int i = 0; i < user.device_list.size(); i++) {
            if (trackerNo.equals(user.device_list.get(i).device_sn)) {
                user.device_list.remove(i);
                break;
            }
        }
        UserUtil.savaUserInfo(context, user);
    }

    public static void saveTrackerportrait(Context context, Tracker mTracker) {
        UserUtil.saveCurrentTracker(context, mTracker);
        User user = UserUtil.getUserInfo(context.getApplicationContext());
        List<Tracker> trackers = user.device_list;
        for (int i = 0; i < trackers.size(); i++) {
            if (mTracker.device_sn.equals(trackers.get(i).device_sn)) {
                trackers.set(i, mTracker);
                break;
            }
        }
        user.device_list = trackers;
        UserUtil.savaUserInfo(context, user);
    }

    public static void saveTracker(Context context, Tracker tracker) {
        User user = UserUtil.getUserInfo(context);
        Tracker mCurTracker = UserUtil.getCurrentTracker(context);
        if (mCurTracker.device_sn.equals(tracker.device_sn)) {
            UserUtil.saveCurrentTracker(context, tracker);
        }
        for (int i = 0; i < user.device_list.size(); i++) {
            if (tracker.device_sn.equals(user.device_list.get(i).device_sn)) {
                user.device_list.remove(i);
                user.device_list.add(i, tracker);
                break;
            }
        }
        UserUtil.savaUserInfo(context, user);
    }

    public static void reviseFrequency(Context context, Tracker tracker) {
        UserUtil.saveCurrentTracker(context, tracker);

        User user = UserUtil.getUserInfo(context);
        List<Tracker> trackers = user.device_list;
        for (int i = 0; i < trackers.size(); i++) {
            if (tracker.device_sn.equals(trackers.get(i).device_sn)) {
                trackers.get(i).gps_interval = tracker.gps_interval;
            }
        }
        user.device_list = trackers;
        UserUtil.savaUserInfo(context, user);
    }

//    public static void reviseTimeZone(Context context, String trackerNo,
//                                      int iTimeZoneId) {
//        User user = UserUtil.getUserInfo(context);
//        Tracker mCurTracker = UserUtil.getCurrentTracker(context);
//        if (mCurTracker.device_sn.equals(trackerNo)) {
//            mCurTracker.timezone = iTimeZoneId;
//            UserUtil.saveCurrentTracker(context, mCurTracker);
//        }
//        for (int i = 0; i < user.device_list.size(); i++) {
//            if (trackerNo.equals(user.device_list.get(i).device_sn)) {
//                user.device_list.get(i).timezone = iTimeZoneId;
//                break;
//            }
//        }
//        UserUtil.savaUserInfo(context, user);
//    }

    public static void changeTrackerList(Context context, Tracker mTracker) {
        User user = UserUtil.getUserInfo(context);

        List<Tracker> trackerLists = user.device_list;
        for (int i = 0; i < trackerLists.size(); i++) {
            Tracker tracker = trackerLists.get(i);
            if (tracker.device_sn.equals(mTracker.device_sn)) {
                trackerLists.set(i, mTracker);
                UserUtil.saveCurrentTracker(context, mTracker);
                break;
            }
        }
        user.device_list = trackerLists;

        UserUtil.savaUserInfo(context, user);
    }

    public static List<Tracker> searchTrackers(Context context, String msg,
                                               List<Tracker> trackerLists) {
        List<Tracker> trackers = new ArrayList<Tracker>();
        for (int i = 0; i < trackerLists.size(); i++) {
            String sTrackerName = getTitleTrackerName(trackerLists.get(i));

            if (sTrackerName.toLowerCase().contains(msg.toLowerCase())
                    || trackerLists.get(i).device_sn.toLowerCase().contains(
                    msg.toLowerCase())) {
                trackers.add(trackerLists.get(i));
            }
        }

        return trackers;
    }

    /**
     * @param context
     * @param type    1.我的帐号，2。提醒，3.信息卡，4，警情记录，5.帐号授权，6.缴费充值 7,周边，8商城
     * @return
     */
    public static List<Advertisement> getAdvertisement(Context context, int type) {
        List<Advertisement> advertising = getUserInfo(context).advertising;
        List<Advertisement> advertising1 = new ArrayList<Advertisement>();
        for (Advertisement advertisement : advertising) {
            if (advertisement.page_code == type) {
                advertising1.add(advertisement);
            }
        }

        return advertising1;
    }

    public static boolean isGuest(Context context) {
        return UserSP.getInstance().getGuest(context);
    }

    public static void setTrackerDeviceInfo(Context context, Tracker tracker) {
        Tracker mCurTracker = UserUtil.getCurrentTracker(context);
        if (mCurTracker.device_sn.equals(tracker.device_sn)) {
            mCurTracker.mDeviceInfo = tracker.mDeviceInfo;
            UserUtil.saveCurrentTracker(context, mCurTracker);
        }

        User user = UserUtil.getUserInfo(context);
        for (int i = 0; i < user.device_list.size(); i++) {
            if (tracker.device_sn.equals(user.device_list.get(i).device_sn)) {
                user.device_list.get(i).mDeviceInfo = tracker.mDeviceInfo;
                break;
            }
        }
        UserUtil.savaUserInfo(context, user);
    }

    public static void saveCurTrackerChange(Context context, Tracker mCurTracker) {
        UserUtil.saveCurrentTracker(context, mCurTracker);

        User user = UserUtil.getUserInfo(context);
        List<Tracker> trackers = user.device_list;
        for (int i = 0; i < trackers.size(); i++) {
            if (mCurTracker.device_sn.equals(trackers.get(i).device_sn)) {
                trackers.set(i, mCurTracker);
                break;
            }
        }
        user.device_list = trackers;
        UserUtil.savaUserInfo(context, user);
    }

    public static void setLocationLatLng(Context context, double lat, double lng) {
        User user = UserUtil.getUserInfo(context);
        user.lat = lat;
        user.lng = lng;
        UserUtil.savaUserInfo(context, user);
    }

//    public static Tracker getTrackerInfo(Context context, String sSN) {
//        User user = UserUtil.getUserInfo(context);
//        List<Tracker> trackers = user.device_list;
//        for (int i = 0; i < trackers.size(); i++) {
//            if (sSN.equals(trackers.get(i).device_sn)) {
//                return trackers.get(i);
//            }
//        }
//
//        return null;
//    }


}
