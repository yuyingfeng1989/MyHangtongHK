package com.bluebud.activity.login.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.bluebud.activity.FacebookBindUserActivity;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.RegisterObj;
import com.bluebud.info.ServerConnInfo;
import com.bluebud.info.User;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2019/7/10.
 */

public class LoginModel implements ILoginMethod {
    private Context mContext;
    private String result;
    private RequestHandle requestHandle;

    public LoginModel(Context context) {
        this.mContext = context;
    }


    /**
     * 获取注册服务器ip及端口号
     *
     * @param params         请求参数
     * @param iLoginListener 回调接口
     */
    @Override
    public void getServiceIP(String url, RequestParams params, final IBackListener iLoginListener) {
        if (null != requestHandle && !requestHandle.isFinished())
            requestHandle.cancel(true);
        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        result = new String(response);
                        RegisterObj registerObj = GsonParse.registerObjParse(result);
                        if (registerObj == null) {
                            iLoginListener.failedBack(mContext.getApplicationContext().getString(R.string.net_exception));
                            return;
                        }

                        if (registerObj.code == 0) {
                            String stringResponse = new String(response);
                            if (Constants.ISSERVICEIP_USERNAME) {
                                ServerConnInfo serverConnInfo = GsonParse.serverConnInfoByUserParse(stringResponse);
                                iLoginListener.successBack(serverConnInfo);
                                return;
                            }
                            List<ServerConnInfo> serverConnInfos = GsonParse.serverConnInfoParse(stringResponse).ret;
                            if (serverConnInfos == null || serverConnInfos.size() < 1) {
                                iLoginListener.failedBack(mContext.getApplicationContext().getString(R.string.net_exception));
                                return;
                            }
                            iLoginListener.successBack(serverConnInfos.get(0));
                        } else {
                            iLoginListener.failedBack(registerObj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        iLoginListener.failedBack(mContext.getApplicationContext().getString(R.string.net_exception));
                    }
                });
    }

    /**
     * 登录接口
     *
     * @param url            请求接口
     * @param params         参数
     * @param iLoginListener 回调接口
     */
    @Override
    public void goLogin(String url, RequestParams params, final IBackListener iLoginListener) {
        if (null != requestHandle && !requestHandle.isFinished())
            requestHandle.cancel(true);
        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        result = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(result);
                        if (obj == null) {
                            iLoginListener.failedBack(mContext.getApplicationContext().getString(R.string.net_exception));
                            return;
                        }
                        if (obj.code == 0) {
                            DataParserTask parserTask = new DataParserTask(iLoginListener);
                            parserTask.executeOnExecutor(Executors.newCachedThreadPool());
                            AppSP.getInstance().saveLoginState(1);
                        } else {
                            iLoginListener.failedBack(obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        iLoginListener.failedBack(mContext.getApplicationContext().getString(R.string.net_exception));
                    }
                });

    }

    /**
     * facebook 登录
     *
     * @param url            请求接口
     * @param params         参数
     * @param iLoginListener 回调接口
     */
    @Override
    public void goFacebookLogin(String url, RequestParams params, final IBackListener iLoginListener) {
        if (null != requestHandle && !requestHandle.isFinished())
            requestHandle.cancel(true);
        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        result = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(result);
                        if (obj == null) {
                            iLoginListener.failedBack(mContext.getApplicationContext().getString(R.string.net_exception));
                            return;
                        }
                        if (obj.code == 0) {
                            DataParserTask parserTask = new DataParserTask(iLoginListener);
                            parserTask.executeOnExecutor(Executors.newCachedThreadPool());
//                            parserTask.execute();
                            AppSP.getInstance().saveLoginState(2);
                        } else if (obj.code == 1000) {//facebook绑定
                            mContext.startActivity(new Intent(mContext, FacebookBindUserActivity.class));
                            AppSP.getInstance().saveLoginState(2);
                            Activity activity = (Activity) mContext;
                            activity.finish();
                            ProgressDialogUtil.dismiss();
                        } else {
                            iLoginListener.failedBack(obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        iLoginListener.failedBack(mContext.getApplicationContext().getString(R.string.net_exception));
                    }
                });
    }

    /**
     * 注册
     *
     * @param url            注册接口
     * @param params         参数
     * @param iLoginListener 回调接口
     */
    @Override
    public void goregister(String url, final RequestParams params, final IBackListener iLoginListener) {
        if (null != requestHandle && !requestHandle.isFinished())
            requestHandle.cancel(true);
        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        RegisterObj registerObj = GsonParse.registerObjParse(new String(response));
                        if (registerObj == null) {
                            iLoginListener.failedBack(mContext.getString(R.string.net_exception));
                            return;
                        }
                        if (registerObj.code == 0) {
                            iLoginListener.successBack(null);//注册成功
                        } else {
                            iLoginListener.failedBack(registerObj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        iLoginListener.failedBack(mContext.getString(R.string.net_exception));
                    }
                });
    }

    /**
     * 解析保存数据
     */
    private class DataParserTask extends AsyncTask<String, Integer, Integer> {
        private IBackListener iLoginListener;

        private DataParserTask(IBackListener iLoginListener) {
            this.iLoginListener = iLoginListener;
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            String ret = "";
            JSONObject job;
            try {
                job = new JSONObject(result);
                ret = job.get("ret").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            User user = GsonParse.json2object(ret, User.class);
            LogUtil.e("登录ret=" + ret);
            UserSP.getInstance().saveUserInfo(mContext, ret);//保存所有数据
            Constants.MALL_SHOP = user.dscMallLogin;//商城赋值
            UserSP.getInstance().saveUserName(mContext, user.username);//保存登录名
            UserSP.getInstance().saveAutologin(mContext, true);//是否已经登录过
            UserSP.getInstance().saveLastManualLoginTime(mContext, Utils.getCurTime(mContext));//记录登录时间(30天免登录)
            UserSP.getInstance().saveChatValue(mContext, user.chat_token, user.fullUserPortrait, user.nickname, user.chatProductType);//保存微聊信息
            UserSP.getInstance().saveLiteMall(mContext,user.dscMallOpenId,user.dscMallToken,user.dscMallUserId,user.dscMallLogin);//保存litemall信息
            if (AppSP.getInstance().getFirstSaveMap(mContext)) {//是不是第一次保存地图 0百度地图，1 google地图
                if (Utils.isChineseMainland()) {
                    UserSP.getInstance().saveServerAndMap(mContext, 0);
                } else {
                    if (ConnectionResult.SUCCESS == GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext)) {
                        UserSP.getInstance().saveServerAndMap(mContext, 1);
                    } else {
                        UserSP.getInstance().saveServerAndMap(mContext, 0);
                    }
                }
                AppSP.getInstance().saveFirstSaveMap(mContext, false);
            }

            if (user.device_list == null || user.device_list.size() == 0) {
                UserUtil.saveCurrentTracker(mContext, null);
                return 0;
            } else if (user.device_list.size() > 1) {
                UserUtil.chooseCurrentTracker(mContext, user.device_list);
                return user.device_list.size();
            } else {
                UserUtil.chooseCurrentTracker(mContext, user.device_list);
                return 1;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            iLoginListener.successBack(result);
        }
    }

}
