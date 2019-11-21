package com.bluebud.utils;

import android.util.Log;

import com.bluebud.info.AlarmClockInfos;
import com.bluebud.info.AlarmClockList;
import com.bluebud.info.AlarmInfo;
import com.bluebud.info.AlarmSwitch;
import com.bluebud.info.BluetoothGPS;
import com.bluebud.info.CarDetectInfo;
import com.bluebud.info.CarInfo;
import com.bluebud.info.CarTrackInfo;
import com.bluebud.info.CurrentGPS;
import com.bluebud.info.DeviceStatusInfo;
import com.bluebud.info.DriverDate;
import com.bluebud.info.GPSInterval;
import com.bluebud.info.GPSMode;
import com.bluebud.info.Geofence;
import com.bluebud.info.GeofenceObj;
import com.bluebud.info.GoodsInfo;
import com.bluebud.info.HeadPortrait;
import com.bluebud.info.HistoryGPSData;
import com.bluebud.info.LatLng1;
import com.bluebud.info.LoginObj;
import com.bluebud.info.LostCard2Car;
import com.bluebud.info.LostCard2People;
import com.bluebud.info.LostCard2Pet;
import com.bluebud.info.OrderPackageInfo;
import com.bluebud.info.PaypalVerifyInfo;
import com.bluebud.info.PeripherInfo;
import com.bluebud.info.PetInsur;
import com.bluebud.info.PushAlarmInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.ReGPSObj;
import com.bluebud.info.RegisterObj;
import com.bluebud.info.SchoolTimetableInfo;
import com.bluebud.info.ServerConnInfo;
import com.bluebud.info.ServerConnInfos;
import com.bluebud.info.SpeedObj;
import com.bluebud.info.StepInfo;
import com.bluebud.info.SystemNoticeObj;
import com.bluebud.info.TelephoneInfo;
import com.bluebud.info.TimeSwitchCourseInfo;
import com.bluebud.info.TimeSwitchInfo;
import com.bluebud.info.TimeZoneInfo;
import com.bluebud.info.TrackerList;
import com.bluebud.info.TrackerUser;
import com.bluebud.info.UpgradProgressInfo;
import com.bluebud.info.User;
import com.bluebud.info.VersionObj;
import com.bluebud.info.deviceWifiInfo;
import com.bluebud.info.goodsMap;
import com.bluebud.info.PetWalkEndInfo;
import com.bluebud.info.petWalkInfo;
import com.bluebud.info.petWalkRecentGpsInfo;
import com.bluebud.info.petWalkRecordingDetailInfo;
import com.bluebud.info.petWalkRecordingInfo;
import com.bluebud.info.petWalkStatusInfo;
import com.bluebud.info.phoneNumberInfo;
import com.bluebud.info.remindListInfo;
import com.bluebud.info.unfinishWalkDogMapInfo;
import com.bluebud.info.userIDInfo;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class GsonParse {


    private static Gson gs = new Gson();

    /**
     * parse json to bean
     *
     * @param json
     * @param
     * @param type
     * @return
     */
    public static <T> T json2BeanObject(String json, Type type) {
        if (gs == null) {
            gs = new Gson();
        }
        T bean = null;
        try {
            // Logi("json2BeanObject -- : [" + json + "]");
            bean = gs.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            Log.i("gf", "json2BeanObject -- JsonSyntaxException : " + e.getMessage());

        } catch (Exception e) {
            Log.i("gf", "json2BeanObject -- Exception : " + e.getMessage());
        }

        return bean;
    }


    public static String object2Json(Object object) {
        return new Gson().toJson(object);
    }

    public static <T> T json2object(String json, Class<T> clz) {
        return new Gson().fromJson(json, clz);
    }

    public static LoginObj loginObjParse(String JsonData) {
        LoginObj loginObj = null;
        try {
            loginObj = new Gson().fromJson(JsonData, LoginObj.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return loginObj;
    }

    public static <T> T getResponseData(String json, Type type) {
        String data = "";
        JSONObject job = null;
        try {
            job = new JSONObject(json);
            data = job.get("ret").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json2BeanObject(data, type);
    }

    public static RegisterObj registerObjParse(String JsonData) {
        RegisterObj registerObj = null;
        try {
            registerObj = new Gson().fromJson(JsonData, RegisterObj.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return registerObj;
    }

    public static ReBaseObj reBaseObjParse(String JsonData) {
        ReBaseObj reBaseObj = null;
        try {
            reBaseObj = new Gson().fromJson(JsonData, ReBaseObj.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return reBaseObj;
    }

    public static ReGPSObj reGPSObjParse(String JsonData) {
        ReGPSObj reGPSObj = null;
        try {
            reGPSObj = new Gson().fromJson(JsonData, ReGPSObj.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return reGPSObj;
    }

    public static User userParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<User>() {
        }.getType();
        User dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;

    }

    public static StepInfo getStepPrase(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<StepInfo>() {
        }.getType();
        StepInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;

    }

    public static OrderPackageInfo getOrderPackageInfoPrase(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<OrderPackageInfo>() {
        }.getType();
        OrderPackageInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;

    }

    /**
     * 远程监护号码解析
     */
    public static phoneNumberInfo phoneNumberParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<phoneNumberInfo>() {
        }.getType();
        phoneNumberInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;

    }

    public static AlarmClockList alarmClockListParse(String JsonData) {
//        JSONObject job = null;
//        try {
//            job = new JSONObject(JsonData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        Type typeOfT = new TypeToken<AlarmClockList>() {}.getType();
//        AlarmClockList dataSet = null;
//        try {
        AlarmClockList dataSet = new Gson().fromJson(JsonData, typeOfT);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            return null;
//        }
        return dataSet;

    }


    public static CurrentGPS currentGPSParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<CurrentGPS>() {
        }.getType();
        CurrentGPS dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static PetInsur currentPetInsurParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<PetInsur>() {
        }.getType();
        PetInsur dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static userIDInfo userIDParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<userIDInfo>() {
        }.getType();
        userIDInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }


    public static BluetoothGPS BluetoothGPSParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<BluetoothGPS>() {
        }.getType();
        BluetoothGPS dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static remindListInfo remindListParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<remindListInfo>() {
        }.getType();
        remindListInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static petWalkRecordingDetailInfo getwalkDogTrailDetailParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<petWalkRecordingDetailInfo>() {
        }.getType();
        petWalkRecordingDetailInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static petWalkRecentGpsInfo recentGpsDataParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<petWalkRecentGpsInfo>() {
        }.getType();
        petWalkRecentGpsInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static unfinishWalkDogMapInfo petWalkUnfinishParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<unfinishWalkDogMapInfo>() {
        }.getType();
        unfinishWalkDogMapInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static PetWalkEndInfo petWalkEndDataParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<PetWalkEndInfo>() {
        }.getType();
        PetWalkEndInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static petWalkInfo startPetWalkParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<petWalkInfo>() {
        }.getType();
        petWalkInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static PaypalVerifyInfo PaypalVerifyParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<PaypalVerifyInfo>() {
        }.getType();
        PaypalVerifyInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("response").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;

    }

    public static petWalkStatusInfo PetWalkParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<petWalkStatusInfo>() {
        }.getType();
        petWalkStatusInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static List<GoodsInfo> GoodsInformationParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<goodsMap>() {
        }.getType();
        goodsMap dataSet = null;
        List<GoodsInfo> goodsMap = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
            if (dataSet != null) {
                goodsMap = dataSet.goodsMap;
            } else {
                goodsMap = null;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return goodsMap;
    }


    public static GeofenceObj geofenceObjParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<GeofenceObj>() {
        }.getType();
        GeofenceObj dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static petWalkRecordingInfo getPetWalkRecordParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<petWalkRecordingInfo>() {
        }.getType();
        petWalkRecordingInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static HistoryGPSData gpsDataParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<HistoryGPSData>() {
        }.getType();
        HistoryGPSData dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static TrackerUser usersParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<TrackerUser>() {
        }.getType();
        TrackerUser dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static GPSMode gpsModeParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<GPSMode>() {
        }.getType();
        GPSMode dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static GPSInterval gpsIntervalParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<GPSInterval>() {
        }.getType();
        GPSInterval dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static AlarmInfo alarmInfoParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<AlarmInfo>() {
        }.getType();
        AlarmInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static TrackerList listParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<TrackerList>() {
        }.getType();
        TrackerList dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static Geofence getGeofence(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<Geofence>() {
        }.getType();
        Geofence dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static TrackerUser usersParse2(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<TrackerUser>() {
        }.getType();
        TrackerUser dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static SpeedObj speedObjParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<SpeedObj>() {
        }.getType();
        SpeedObj dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static AlarmInfo alarmParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<AlarmInfo>() {
        }.getType();
        AlarmInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static VersionObj versionObjParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<VersionObj>() {
        }.getType();
        VersionObj dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static LostCard2People lostCard2PeopleParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<LostCard2People>() {
        }.getType();
        LostCard2People dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static LostCard2Pet lostCard2PetParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<LostCard2Pet>() {
        }.getType();
        LostCard2Pet dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static LostCard2Car lostCard2CarParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<LostCard2Car>() {
        }.getType();
        LostCard2Car dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static String getRet(String JsonData) {
        String result = "";
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
            result = job.get("ret").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static AlarmSwitch alarmSwitchParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<AlarmSwitch>() {
        }.getType();
        AlarmSwitch dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static AlarmClockInfos alarmClockParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<AlarmClockInfos>() {
        }.getType();
        AlarmClockInfos dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

//    public static WXOrderInfo wxOrderInfoParse(String JsonData) {
//        JSONObject job = null;
//        try {
//            job = new JSONObject(JsonData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Type typeOfT = new TypeToken<WXOrderInfo>() {
//        }.getType();
//        WXOrderInfo dataSet = null;
//        try {
//            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            return null;
//        }
//        return dataSet;
//    }

//    public static int getServerNo(String JsonData) {
//        JSONObject job = null;
//        try {
//            job = new JSONObject(JsonData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Type typeOfT = new TypeToken<ServerInfo>() {
//        }.getType();
//        ServerInfo dataSet = null;
//        try {
//            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            return 0;
//        }
//        return dataSet.serverNo;
//
//    }

    public static PushAlarmInfo pushAlarmInfoParse(String JsonData) {
        Type typeOfT = new TypeToken<PushAlarmInfo>() {
        }.getType();
        PushAlarmInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(JsonData, typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static HeadPortrait headPortraitParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<HeadPortrait>() {
        }.getType();
        HeadPortrait dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static ServerConnInfos serverConnInfoParse(String JsonData) {
        // JSONObject job = null;
        // try {
        // job = new JSONObject(JsonData);
        // } catch (JSONException e) {
        // e.printStackTrace();
        // }
        // Type typeOfT = new TypeToken<ServerConnInfos>() {
        // }.getType();
        // ServerConnInfos dataSet = null;
        // try {
        // dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        // } catch (JsonSyntaxException e) {
        // e.printStackTrace();
        // } catch (JSONException e) {
        // e.printStackTrace();
        // } catch (Exception e) {
        // return null;
        // }
        // return dataSet;

        Type typeOfT = new TypeToken<ServerConnInfos>() {
        }.getType();
        ServerConnInfos dataSet = null;
        try {
            dataSet = new Gson().fromJson(JsonData, typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static ServerConnInfo serverConnInfoByUserParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<ServerConnInfo>() {
        }.getType();
        ServerConnInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static PeripherInfo serverGooglePeripher(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<PeripherInfo>() {
        }.getType();
        PeripherInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static LatLng1 GooglePeripherLag(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<LatLng1>() {
        }.getType();
        LatLng1 dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("location").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

//    public static OrderPackageInfos getOrderPackage(String JsonData) {
//        Type typeOfT = new TypeToken<OrderPackageInfos>() {
//        }.getType();
//        OrderPackageInfos dataSet = null;
//        try {
//            dataSet = new Gson().fromJson(JsonData, typeOfT);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            return null;
//        }
//        return dataSet;
//    }

    public static UpgradProgressInfo getUpgradProgress(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<UpgradProgressInfo>() {
        }.getType();
        UpgradProgressInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static TimeSwitchInfo getTimeSwitch(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<TimeSwitchInfo>() {
        }.getType();
        TimeSwitchInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static TimeSwitchCourseInfo getTimeSwitchCourse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<TimeSwitchCourseInfo>() {
        }.getType();
        TimeSwitchCourseInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static TelephoneInfo getTelephone(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<TelephoneInfo>() {
        }.getType();
        TelephoneInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static TimeZoneInfo getTimeZone(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<TimeZoneInfo>() {
        }.getType();
        TimeZoneInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static DeviceStatusInfo deviceStatus(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<DeviceStatusInfo>() {
        }.getType();
        DeviceStatusInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    /**
     * @param @param  JsonData
     * @param @return
     * @return DeviceStatusInfo
     * @throws
     * @Title: carDetectInfoParse
     * @Description: 解析服务器传回来的汽车检测json数据
     */
    public static CarDetectInfo carDetectInfoParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<CarDetectInfo>() {
        }.getType();
        CarDetectInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    /**
     * @param @param  JsonData
     * @param @return
     * @return CarDetectInfo
     * @throws
     * @Title: carInfoParse
     * @Description: 汽车数据解析
     */
    public static CarInfo carInfoParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<CarInfo>() {
        }.getType();
        CarInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static DriverDate carDriveTestDataParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<DriverDate>() {
        }.getType();
        DriverDate dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }


//    /**
//     * obd安全与经济驾驶数据
//     *
//     * @param JsonData
//     * @return
//     */
//    public static StatisticsDataInfo carDriveDataParse(String JsonData) {
//        JSONObject job = null;
//        try {
//            job = new JSONObject(JsonData);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Type typeOfT = new TypeToken<StatisticsDataInfo>() {
//        }.getType();
//        StatisticsDataInfo dataSet = null;
//        try {
//            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            return null;
//        }
//        return dataSet;
//    }


    public static CarTrackInfo carDateTrackParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<CarTrackInfo>() {
        }.getType();
        CarTrackInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static GeofenceObj fenceSettingDataParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<GeofenceObj>() {
        }.getType();
        GeofenceObj dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    public static SystemNoticeObj SystemNoticeParse(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<SystemNoticeObj>() {
        }.getType();
        SystemNoticeObj dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    //获取WIFI信息解析
    public static deviceWifiInfo getWifiParse(String JsonData) {

        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<deviceWifiInfo>() {
        }.getType();
        deviceWifiInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
            if (dataSet == null || dataSet.deviceWifi == null || dataSet.deviceWifi.get(0) == null)
                return null;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return null;
        }
        return dataSet;
    }

    /**
     * 课程表解析
     */
    public static SchoolTimetableInfo getClassSchedule(String JsonData) {
        JSONObject job = null;
        try {
            job = new JSONObject(JsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Type typeOfT = new TypeToken<SchoolTimetableInfo>() {}.getType();
        SchoolTimetableInfo dataSet = null;
        try {
            dataSet = new Gson().fromJson(job.get("ret").toString(), typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSet;
    }
}
