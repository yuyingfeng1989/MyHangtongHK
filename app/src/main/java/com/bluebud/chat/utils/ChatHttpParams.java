package com.bluebud.chat.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.bluebud.activity.LoginActivity;
import com.bluebud.activity.MainActivity;
import com.bluebud.app.App;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.Advertisement;
import com.bluebud.info.AroundStoreInfo;
import com.bluebud.info.HomePageInfo;
import com.bluebud.info.PhonebookInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.StepsInfo;
import com.bluebud.info.TakePhotoInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.service.EventsService;
import com.bluebud.utils.GlideCacheUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.UserUtil;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;

public class ChatHttpParams {
    private String HTTP_FUNCTION = "function";

    /**
     * 请求方法
     */
    private final String ADDGROUPUSER = "addGroupUser";// 添加聊天人员
    private final String GETGROUPUSER = "getGroupUser";// 获取群组成员
    private final String GETINVITEAUTHUSER = "getInviteAuthUser";// 查询设备可邀请授权用户
    private final String DELETEGROUPUSER = "deleteGroupUser";// 删除群组成员
    private final String SETUSERPORTRAIT = "setUserPortrait";// 设置用户头像
    private final String SENDMESSAGETXT = "sendMessageTxt";// 发送文字
    private final String SENDMESSAGEVOICE = "sendMessageVoice";// 发送语音
    private final String SETUSERINFO = "setUserInfo";// 设置用户信息
    private final String GETUSERINFO = "getUserInfo";// 获取用户信息
    private final String getGroupUserByName = "getGroupUserByName";//获取单个群成员
    private final String UPLOADLOCATION = "setLatlng";//上传定位信息

    private final String GETECONOMICALDRIVEDATA = "getEconomicalDriveData";//获取经济驾驶检测数据
    private final String GETSAFEDRIVEDATA = "getSafeDriveData";//获取安全驾驶检测数据
    //    private final String GETSAfEECOSTATISTICSDATA = "getSafeEcoStatisticsData";//安全和经济驾驶数据
    private final String GETOBDSTATISTICSDATA = "getObdStatisticsData";//安全和经济驾驶数据
    //    private final String GETSAFEANDECODRIVEDATA = "getSafeAndEcoDriveData";//安全和经济驾驶数据
    private final String GETDRIVEDATACOUNT = "getDriveDataCount";//获取轨迹数据
    private final String GETSCENERYMODE = "getDeviceProfile";//获取情景模式数据
    private final String SETSCENERYMODE = "setDeviceProfile";//设置情景模式
    private final String SETMEDICATIONREMIND = "setMedicationRemind";//设置吃药提醒
    private final String GETMEDICATIONREMIND = "getMedicationRemind";//获取吃药提醒信息
    private final String SAVECOURSEDISABLETIME = "savecoursedisabletime";//设置免打扰信息
    private final String GETCOURSEDISABLETIME = "getcoursedisabletime";//获取免打扰信息
    private final String GETAROUNDSTORE = "getAroundStore"; //获取周边商户信息
    private final String GETDEVICECUSTOMIZED = "getDeviceCustomized";//获取设备续费定制类型是否是德国用户
    private final String GETDEVICEATHOMEPAGE = "getDeviceAtHomePage";//综合首页获取设备列表位置信息
    private final String GETADVERTISING = "getAdvertising";//获取广告页
    private final String SETDEVICEPHOTO = "setDevicePhoto";//设备拍照
    private final String GETDEVICEPHOTO = "getDevicePhoto";//获取图片
    private final String DELETEDEVICEPHOTO = "deleteDevicePhoto";//删除设备拍照
    private final String GETCONTACT = "getContact";//获取电话本联系人
    private final String DELETECONTACT = "deleteContact";//删除电话本联系人
    private final String ADDORMODIFYCONTACT = "addOrModifyContact";//添加和修改联系人

    /**
     * 上传参数
     */
    private final String USERNAME = "username";// : 帐号邮箱
    private final String NAME = "name";
    private final String DEVICESN = "deviceSn";// 群组设备号
    private final String FILESTREAM = "fileStream";// 上传头像
    private final String RECDEVICESN = "recDeviceSn";// 接受消息设备号
    private final String MSG = "msg";// 上传消息
    private final String TYPE = "type";// 0 APP发送给设备，1设备发送给APP
    private final String NICKNAME = "nickname";// 用户昵称
    private final String SEX = "sex";// 性别
    private final String AGE = "age";// 年龄
    private final String AREA = "area";// 地区
    private final String MARK = "mark";// 个人说明
    private final String UPLOADPARAM = "latlng";
    private final String STARTTIME = "startTime";//:开始时间
    private final String ENDTIME = "endTime";//结束时间
    private final String MODE = "mode";//情景模式
    private final String INDEX = "index";//吃药提醒第几个开关改变状态
    private final String TIME = "time";//吃药提醒时间
    private final String MESSAGE = "message";//吃药提醒语句

    private final String STORETYPE = "storeType";//获取周边信息类型字段
    private final String LATITUDE = "latitude";//纬度
    private final String LONGITUDE = "longitude"; //经度

    private final String REPEATDAY = "repeatday";
    private final String AMSTARTTIME = "amstarttime";
    private final String AMENDTIME = "amendtime";
    private final String TMSTARTTIME = "tmstarttime";
    private final String TMENDTIME = "tmendtime";
    private final String STARTTIME3 = "starttime3";//第三项开关
    private final String ENDTIME3 = "endtime3";
    private final String ENABLE = "enable";
    private final String PAGE_CODE = "page_code";//启动页广告
    private final String DEVICEPHOTOID = "devicePhotoID";//删除设备图片id

    //联系人
    private final String PHONENUM = "phoneNum";//电话号码
    private final String ISADMIN = "isAdmin";//管理员电话号码(0否，1是)
    private final String PHOTO = "photo";//头像编号(0-10)

    private static ChatHttpParams chatHttpParams;
    private Context mcontext;

    /**
     * 单例模式
     */
    public static ChatHttpParams getInstallSigle(Context context) {
        return getInstallSingle();
    }

    public static ChatHttpParams getInstallSingle() {
        if (chatHttpParams == null)
            chatHttpParams = new ChatHttpParams();
        return chatHttpParams;
    }

    private ChatHttpParams() {
        mcontext = App.getContext();
    }

    /**
     * 添加聊天人员
     */
    private RequestParams addGroupUser(String usernamne, String deviceSn) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, ADDGROUPUSER);
        params.put(NAME, usernamne);
        params.put(DEVICESN, deviceSn);
        return params;
    }

    /**
     * 获取群组成员
     */
    private RequestParams getGroupUser(String deviceSn) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETGROUPUSER);
        params.put(DEVICESN, deviceSn);
        return params;
    }

    /**
     * 获取单个群成员
     *
     * @return
     */
    private RequestParams getGroupUserByName(String deviceSn, String name) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, getGroupUserByName);
        params.put(DEVICESN, deviceSn);
        params.put(NAME, name);
        return params;
    }

    /**
     * 查询设备可邀请授权用户
     */
    private RequestParams getInviteAuthUser(String deviceSn) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETINVITEAUTHUSER);
        params.put(DEVICESN, deviceSn);
        return params;
    }

    /**
     * 删除群组人员
     */
    private RequestParams deleteGroupUser(String userName, String deviceSn) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, DELETEGROUPUSER);
        params.put(NAME, userName);
        params.put(DEVICESN, deviceSn);
        return params;
    }

    /**
     * 设置用户头像
     */
    private RequestParams setUserPortrait(String userName, File fileStream) {
        RequestParams params = new RequestParams();
        try {
            params.put(HTTP_FUNCTION, SETUSERPORTRAIT);
            params.put(USERNAME, userName);
            params.put(FILESTREAM, fileStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return params;
    }

    /**
     * 发送消息
     */
    private RequestParams sendMessage(String recDeviceSn, String msg,
                                      String type, String msgType) {
        RequestParams params = new RequestParams();
        if (msgType.equals("1")) {
            params.put(HTTP_FUNCTION, SENDMESSAGETXT);
            params.put(RECDEVICESN, recDeviceSn);
            params.put(TYPE, type);
            params.put(MSG, msg);
        } else {
            params.put(HTTP_FUNCTION, SENDMESSAGEVOICE);
            params.put(RECDEVICESN, recDeviceSn);
            params.put(TYPE, type);
            File file = new File(msg);
            if (!file.exists())
                return null;
            try {
                params.put(MSG, file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return params;
    }

    /**
     * 设置用户信息
     */
    private RequestParams setUserInfo(Object object) {
        if (object == null)
            return null;
        ChatInfo info = (ChatInfo) object;
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, SETUSERINFO);
        params.put(USERNAME, info.getName());
        params.put(NICKNAME, info.getNickname());
        params.put(SEX, info.getSex());
        params.put(AGE, info.getAge());
        params.put(AREA, info.getArea());
        params.put(MARK, info.getMark());
        return params;
    }

    /**
     * 获取用户信息
     */
    private RequestParams getUserInfo() {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETUSERINFO);
        return params;
    }

    /**
     * 上传定位信息
     */
    private RequestParams getUploadInfo(String json) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, UPLOADLOCATION);
        params.put(UPLOADPARAM, json);
        return params;
    }


    /**
     * 获取经济和安全驾驶数据
     *
     * @return
     */
//    private RequestParams getSafeEcoStatisticsData(String deviceSn, String startTime, String endTime, String type) {
//        RequestParams params = new RequestParams();
//        params.put(HTTP_FUNCTION, GETSAfEECOSTATISTICSDATA);
//        params.put(DEVICESN, deviceSn);
////        String string = endTime.replace(".", "-");
//        params.put(STARTTIME, startTime + " 00:00:00");
//        params.put(ENDTIME, endTime + " 23:59:59");
//        params.put("scoreType", type);
//        return params;
//    }
    private RequestParams getObdStatisticsData(String deviceSn, String startTime, String endTime, String type) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETOBDSTATISTICSDATA);
        params.put(DEVICESN, deviceSn);
//        String string = endTime.replace(".", "-");
        params.put(STARTTIME, startTime + " 00:00:00");
        params.put(ENDTIME, endTime + " 23:59:59");
        params.put("scoreType", type);
        return params;
    }

    /**
     * 获取经济驾驶检测数据
     *
     * @return
     */
    private RequestParams getEconomicalDriveData(String deviceSn, String endTime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETECONOMICALDRIVEDATA);
        params.put(DEVICESN, deviceSn);
        params.put(STARTTIME, "");
        params.put(ENDTIME, endTime);
        return params;
    }

    /**
     * 获取安全驾驶检测数据
     *
     * @return
     */
    private RequestParams getSafeDriveData(String deviceSn, String endTime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETSAFEDRIVEDATA);
        params.put(DEVICESN, deviceSn);
        params.put(STARTTIME, "");
        params.put(ENDTIME, endTime);
        return params;
    }

    /**
     * 获取轨迹信息
     */
    private RequestParams getTrackData(String deviceSn, String startTime) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETDRIVEDATACOUNT);
        params.put(DEVICESN, deviceSn);
        params.put(STARTTIME, startTime);
        return params;
    }

    /**
     * 获取与设置情景模式信息
     */
    private RequestParams deviceProfile(String deviceSn, String username, String mode) {
        RequestParams params = new RequestParams();
        if (mode.equals("0")) {//获取情景模式信息
            params.put(HTTP_FUNCTION, GETSCENERYMODE);
        } else {
            params.put(HTTP_FUNCTION, SETSCENERYMODE);
            params.put(MODE, mode);
        }
        params.put(DEVICESN, deviceSn);
        params.put(USERNAME, username);
        return params;
    }

    /**
     * 吃药提醒
     */
    private RequestParams medicationRemind(String deviceSn, String index, String time, String message, File fileStream) {
        RequestParams params = new RequestParams();
        if (index.equals("0")) {//获取吃药提醒
            params.put(HTTP_FUNCTION, GETMEDICATIONREMIND);
        } else {//设置吃药提醒
            params.put(HTTP_FUNCTION, SETMEDICATIONREMIND);
            params.put(INDEX, index);
            params.put(TIME, time);
            params.put(MESSAGE, message);
            try {
                params.put(FILESTREAM, fileStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        params.put(DEVICESN, deviceSn);
        return params;
    }

    /**
     * 免打扰
     * 第一项控制amstarttime  开关
     * 第二项控制  开关
     * 第三项控制    开关
     * 第一项控制  开关
     * 第二项控制  开关
     * 第三项控制    开关
     */
//    private RequestParams courseDisabletime(String deviceSn,String enable,String amstarttime,
//                                            String amendtime,String tmstarttime,String tmendtime,
//                                            String starttime3,String endtime3,String repeatday,String userName){
    private RequestParams courseDisabletime(String deviceSn, String enable, String times, String repeatday, String userName) {
        RequestParams params = new RequestParams();
        if (enable.equals("-1")) {//获取免打扰信息
            params.put(HTTP_FUNCTION, GETCOURSEDISABLETIME);
        } else {//设置免打扰信息
            String[] split = times.split(",");
            params.put(HTTP_FUNCTION, SAVECOURSEDISABLETIME);
            params.put(ENABLE, enable);
            params.put(AMSTARTTIME, split[0]);
            params.put(AMENDTIME, split[1]);
            params.put(TMSTARTTIME, split[2]);
            params.put(TMENDTIME, split[3]);
            params.put(STARTTIME3, split[4]);
            params.put(ENDTIME3, split[5]);
//            params.put(AMSTARTTIME,amstarttime);
//            params.put(AMENDTIME,amendtime);
//            params.put(TMSTARTTIME,tmstarttime);
//            params.put(TMENDTIME,tmendtime);
//            params.put(STARTTIME3,starttime3);
//            params.put(ENDTIME3,endtime3);
            params.put(REPEATDAY, repeatday);
        }
        params.put(USERNAME, userName);
        params.put(DEVICESN, deviceSn);
        return params;
    }

    /**
     * 获取周边信息
     */
    private RequestParams getAroundStoreParams(String deviceSn, String storeType, String longitude, String latitude) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETAROUNDSTORE);
        params.put(DEVICESN, deviceSn);
        params.put(STORETYPE, storeType);
        params.put(LONGITUDE, longitude);
        params.put(LATITUDE, latitude);
        return params;
    }

    /**
     * 获取当前设备是否德国用户
     */
    private RequestParams getGermanyUser(String deviceSn) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETDEVICECUSTOMIZED);
        params.put(DEVICESN, deviceSn);
        return params;
    }

    /**
     * 综合首页
     */
    private RequestParams getDeviceAtHomePage(String userName) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETDEVICEATHOMEPAGE);
        params.put(USERNAME, userName);
        return params;
    }

    /**
     * 获取广告信息
     */
    private RequestParams getAdvertising(String pagecode) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETADVERTISING);
        params.put(PAGE_CODE, pagecode);
        return params;
    }

    /**
     * 设备拍照
     */
    private RequestParams takingPictures(String deviceSn, String mode) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, SETDEVICEPHOTO);
        params.put(DEVICESN, deviceSn);
        params.put(MODE, mode);
        return params;
    }

    /**
     * 获取设备照片
     */
    private RequestParams getTakingPictures(String deviceSn) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETDEVICEPHOTO);
        params.put(DEVICESN, deviceSn);
        return params;
    }

    /**
     * 删除设备照片
     */
    private RequestParams delectTakingPictures(String deviceSn, String id) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, DELETEDEVICEPHOTO);
        params.put(DEVICESN, deviceSn);
        params.put(DEVICEPHOTOID, id);
        return params;
    }

    /**
     * 获取电话本号码
     */
    private RequestParams getContact(String deviceSn) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, GETCONTACT);
        params.put(DEVICESN, deviceSn);
        return params;
    }

    /**
     * 添加和修改联系人
     */
    private RequestParams addOrModifyContact(Object object) {
        PhonebookInfo info = (PhonebookInfo) object;
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, ADDORMODIFYCONTACT);
        params.put(DEVICESN, info.deviceSn);
        params.put(PHONENUM, info.phoneNum);
        params.put(NICKNAME, info.nickname);
        if (!TextUtils.isEmpty(info.index))
            params.put(INDEX, info.index);
        params.put(ISADMIN, info.isAdmin);
        params.put(PHOTO, info.photo);
        return params;
    }

    /**
     * 刪除联系人
     */
    private RequestParams deleteContact(String deviceSn, String index) {
        RequestParams params = new RequestParams();
        params.put(HTTP_FUNCTION, DELETECONTACT);
        params.put(DEVICESN, deviceSn);
        params.put(INDEX, index);
        return params;
    }

    /**
     * 通过接口操作wifi
     * function
     * 0添加聊天成员,1获取群成员,2查询设备可邀请授权用户,3删除群组成员,4设置用户头像,5发送消息,6设置用户信息
     * ,7获取用户信息
     */
    public void chatHttpRequest(
            int function,// 判断借口功能
            String object,// 帐号邮箱,或者为结束时间
            String deviceSn,// 群组设备号
            File fileStream,// 上传头像
            String object1,// 接受消息设备号
            String object2,// 上传消息
            String type,// 类型
            String msgType,// 0 语音，1 文字
            Object info,
            final ChatCallbackResult callback) {// 用户信息

        String url = UserUtil.getServerUrl(mcontext);
        RequestParams params = null;
        if (function == 0)
            params = addGroupUser(object, deviceSn);// 添加聊天成员
        else if (function == 1) {
            params = getGroupUser(deviceSn);// 获取群成员
        } else if (function == 2)
            params = getInviteAuthUser(deviceSn);// 查询设备可邀请授权用户
        else if (function == 3)
            params = deleteGroupUser(object, deviceSn);// 删除群组成员
        else if (function == 4)
            params = setUserPortrait(object, fileStream);// 设置用户头像
        else if (function == 5)
            params = sendMessage(object1, object2, type, msgType);// 发送消息
        else if (function == 6)
            params = setUserInfo(info);// 设置用户信息
        else if (function == 7)
            params = getUserInfo();// 获取用户信息
        else if (function == 8)
            params = getGroupUserByName(object1, object);//获取当个群成员信息
        else if (function == 9)//获取驾驶经济数据
            params = getObdStatisticsData(deviceSn, object1, object2, type);
//        else if (function == 10) //获取驾驶安全数据
//            params = getSafeEcoStatisticsData(deviceSn, object, 0);

        else if (function == 11)//最近检测时间段安全驾驶数据
            params = getSafeDriveData(deviceSn, ChatUtil.getCurrDate());
        else if (function == 12)//最近检测时间段经济驾驶数据
            params = getEconomicalDriveData(deviceSn, ChatUtil.getCurrDate());
        else if (function == 13)//获取指定12个月轨迹油耗，里程数据
            params = getTrackData(deviceSn, object);//username为开始时间
        else if (function == 14)//获取和设置情景模式信息
            params = deviceProfile(deviceSn, object, msgType);
        else if (function == 15)//吃药提醒
            params = medicationRemind(deviceSn, type, object, object2, null);
        else if (function == 16)//免打扰
            params = courseDisabletime(deviceSn, type, object2, msgType, object);
        else if (function == 17)//获取周边信息
            params = getAroundStoreParams(deviceSn, type, object, object2);
        else if (function == 18)//获取是否是哪个版本用户
            params = getGermanyUser(deviceSn);
        else if (function == 19)
            params = getDeviceAtHomePage(object);//综合首页传入用户名
        else if (function == 20)
            params = getAdvertising(type);
        else if (function == 21)//设备拍照
            params = takingPictures(deviceSn, type);
        else if (function == 22)//获取设备照片
            params = getTakingPictures(deviceSn);
        else if (function == 23)//删除图片
            params = delectTakingPictures(deviceSn, object);
        else if (function == 24)//获取电话号码
            params = getContact(deviceSn);
        else if (function == 25)//添加联系人
            params = addOrModifyContact(info);
        else if (function == 26)//删除联系人
            params = deleteContact(deviceSn, object);
        if (params == null)
            return;
        HttpClientUsage.getInstance().post(mcontext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        callback.callBackStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        if (response == null) {
                            callback.callBackFailResult(String.valueOf(statusCode));
                            return;
                        }
                        String responseValue = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(responseValue);
                        if (obj == null) {
                            callback.callBackFailResult(String.valueOf(statusCode));
                            return;
                        }
                        if (obj.code == 0)//正确值返回
                            callback.callBackResult(responseValue);

                        else if (obj.code == 1) {//登录超时需重新登录
                            int state = AppSP.getInstance().getLoginState();
                            if (state == 2)
                                facebookLoginOrBind();
                            else
                                toLogin();

                        } else if (obj.code == 2) { //弹框是否显示被迫下线
                            callback.callBackFailResult(obj.what);
                            showDialogUtil();
                        } else
                            callback.callBackFailResult(obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        if (errorResponse == null) {
                            callback.callBackFailResult(String.valueOf(statusCode));
                            return;
                        }
                        LogUtil.e("结果==" + new String(errorResponse));
                        callback.callBackFailResult(new String(errorResponse));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        callback.callBackFinish();
                    }
                });
    }

    /**
     * 上传文件
     */
    public void chatHttpRequestFile(int function,// 判断借口功能
                                    String username,// 帐号邮箱
                                    File fileStream,// 上传头像
                                    String recDeviceSn,// 接受消息设备号
                                    String msg,// 上传消息
                                    String type,// 0 APP发送给设备，1设备发送给APP
                                    String msgType,// 0 语音，1 文字
                                    final ChatCallbackResult callback) {// 用户信息

        String url = UserUtil.getServerUrl(mcontext);
        RequestParams params = null;
        if (function == 4)
            params = setUserPortrait(username, fileStream);// 设置用户头像
        else if (function == 5)
            params = sendMessage(recDeviceSn, msg, type, msgType);// 发送消息

        if (params == null)
            return;

        HttpClientUsage.getInstance().postFile(mcontext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        callback.callBackStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        if (response == null) {
                            callback.callBackFailResult(String.valueOf(statusCode));
                            return;
                        }
                        String responseValue = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(responseValue);
                        if (obj == null) {
                            callback.callBackFailResult(String.valueOf(statusCode));
                            return;
                        }
                        if (obj.code == 0)
                            callback.callBackResult(responseValue);

                        else if (obj.code == 1) {//过长时间未操作，重新登录
                            int state = AppSP.getInstance().getLoginState();
                            if (state == 2)
                                facebookLoginOrBind();
                            else
                                toLogin();
                        } else if (obj.code == 2) {
                            callback.callBackFailResult(obj.what);
                            showDialogUtil();
                        } else if (obj.what != null)
                            callback.callBackFailResult(obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        if (errorResponse == null) {
                            callback.callBackFailResult(String.valueOf(statusCode));
                            return;
                        }
                        callback.callBackFailResult(new String(errorResponse));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        callback.callBackFinish();
                    }
                });
    }


    /**
     * 解析据结果BaseObj
     */
    public static ReBaseObj reBaseObjParse(String JsonData) {
        ReBaseObj reBaseObj;
        try {
            reBaseObj = new Gson().fromJson(JsonData, ReBaseObj.class);
            return reBaseObj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更具不同function,解析不同信息解析数据
     */
    public static Object getParseResult(int function, String JsonData) {
        if (JsonData == null || TextUtils.isEmpty(JsonData))
            return null;

        Object object = null;
        try {
            JSONObject job = new JSONObject(JsonData);
            String ret = job.get("ret").toString();

            if (function == 1 || function == 2) { // 获取群成员 查询设备可邀请授权用户
                String obj = new JSONObject(ret).get("obj").toString();
                if (obj != null || TextUtils.isEmpty(obj))
                    object = new Gson().fromJson(obj, new TypeToken<List<UserInfo>>() {
                    }.getType());
            } else if (function == 4) {// 设置用户头像
                JSONObject photoJson = new JSONObject(ret);
                object = photoJson.get("headPortrait");
            } else if (function == 7) {// 获取用户信息
                String obj = new JSONObject(ret).get("userInfo").toString();
                object = new Gson().fromJson(obj, new TypeToken<ChatInfo>() {
                }.getType());
            } else if (function == 13) {
                object = new Gson().fromJson(ret, new TypeToken<List<TrackDriverBean>>() {
                }.getType());
            } else if (function == 14) {//情景模式设置
                job = new JSONObject(ret);
                object = job.get("mode").toString();
            } else if (function == 17) {
                String obj = new JSONObject(ret).get("storeMap").toString();
                object = new Gson().fromJson(obj, new TypeToken<List<AroundStoreInfo>>() {
                }.getType());
            } else if (function == 18) {
                String obj = new JSONObject(ret).get("map").toString();
                Map map = new Gson().fromJson(obj, new TypeToken<HashMap<String, String>>() {
                }.getType());
                object = map.get("is_customized");
            } else if (function == 19) {//综合首页数据解析
                String obj = new JSONObject(ret).get("map").toString();
                object = new Gson().fromJson(obj, new TypeToken<List<HomePageInfo>>() {
                }.getType());
            } else if (function == 20) {//广告页信息
                String obj = new JSONObject(ret).get("map").toString();
                object = new Gson().fromJson(obj, new TypeToken<List<Advertisement>>() {
                }.getType());
            } else if (function == 22) {
                String obj = new JSONObject(ret).get("imgMap").toString();
                object = new Gson().fromJson(obj, new TypeToken<List<TakePhotoInfo>>() {
                }.getType());
            } else if (function == 24) {
                String obj = new JSONObject(ret).get("contactMap").toString();
                object = new Gson().fromJson(obj, new TypeToken<List<PhonebookInfo>>() {}.getType());
            } else if (function == 25) {
                String obj = new JSONObject(ret).get("contactMap").toString();
                object = new Gson().fromJson(obj, new TypeToken<PhonebookInfo>() {}.getType());
            } else if (function == -2) {
                String obj = new JSONObject(ret).get("stepMap").toString();
                object = new Gson().fromJson(obj, new TypeToken<List<StepsInfo>>() {
                }.getType());
            } else if (function == -3) {
                String obj = new JSONObject(ret).get("stepMap").toString();
                object = new Gson().fromJson(obj, new TypeToken<StepsInfo>() {
                }.getType());
            }
            return object;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 强迫下线弹出框
     */
    private void showDialogUtil() {
        final Context ct = MainActivity.mainActivity;
        new ChatUtil().chatShowDialog(mcontext, R.string.again_login1, false, new ChatCallbackResult() {
            @Override
            public void callBackResult(String result) {
            }

            @Override
            public void callBackFailResult(String result) {
            }

            @Override
            public void callOkDilaog(AlertDialog mDialog) {
                super.callOkDilaog(mDialog);
                mDialog.dismiss();
//                UserUtil.clearUserInfo(ct);
                ct.stopService(new Intent(ct, EventsService.class));
                UserSP.getInstance().savePWD(ct, "");// 清空密码
                UserSP.getInstance().clearChatValue(ct);//清空融云信息
                GlideCacheUtil.getInstance().clearImageAllCache();//清除Glide所有缓存
                UserSP.getInstance().saveUserName(ct, null);//保存登录名
                LoginManager.getInstance().logOut();//登出facebook
                RongIM.getInstance().logout();//退出融云
                Intent intent = new Intent(ct, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                ct.startActivity(intent);
            }
        });
    }

    /**
     * @Description: 登录时间过长重新登录
     */
    public void toLogin() {
        String sUserName = UserSP.getInstance().getUserName(mcontext);
        String sPwd = UserSP.getInstance().getPWD(mcontext);
        String sUrl = UserUtil.getServerUrl(mcontext);
        if (TextUtils.isEmpty(sUrl) || TextUtils.isEmpty(sPwd) || TextUtils.isEmpty(sUserName)) {
            return;
        }
        RequestParams params = HttpParams.userLoginCN(sUserName, sPwd);
        HttpClientUsage.getInstance().post(mcontext, sUrl, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                    }
                });
    }
}
