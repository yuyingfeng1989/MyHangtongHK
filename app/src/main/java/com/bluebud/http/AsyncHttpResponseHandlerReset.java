package com.bluebud.http;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.bluebud.activity.LoginActivity;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.info.ReBaseObj;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.service.EventsService;
import com.bluebud.service.IMLiteGuardianService;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GlideCacheUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import io.rong.imkit.RongIM;


public class AsyncHttpResponseHandlerReset extends AsyncHttpResponseHandler {
    private Context mContext;
    public AsyncHttpResponseHandlerReset() {
    }

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public void onStart() {
        LogUtil.e("onStart()");
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
        LogUtil.e("onSuccess()");
        if (mContext == null)
            return;
        String result = new String(response);
        if (null != result) {
            LogUtil.e("基类response:" + result);
        } else {
            LogUtil.e("response:null");
        }

        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
        final String sUserName = UserSP.getInstance().getUserName(mContext);
        if (null != obj && obj.code == 1) {//从新登录
            int state = AppSP.getInstance().getLoginState();
            if (state == 2)
                facebookLoginOrBind();
            else {
                toLogin(sUserName);
            }
        }
        if (null != obj && obj.code == 2 && mContext instanceof Activity) {//设备已在其他账号登录
            DialogUtil.showSystemAlert(mContext, R.string.prompt,
                    R.string.again_login1, R.string.again_confirm,
                    new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            DialogUtil.dismiss();
                            UserSP.getInstance().clearChatValue(mContext);//清空用户基本信息
//                            UserUtil.clearUserInfo(mContext);
//                            AppSP.getInstance().saveRegisterAddress(mContext, sUserName, null);
                            GlideCacheUtil.getInstance().clearImageAllCache();//清除Glide所有缓存
                            mContext.stopService(new Intent(mContext, EventsService.class));
                            LoginManager.getInstance().logOut();//退出facebook
                            UserSP.getInstance().saveUserName(mContext, null);//保存登录名
                            UserSP.getInstance().savePWD(mContext, "");//清空密码
                            RongIM.getInstance().logout();//退出融云
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            ((Activity) mContext).finish();
                        }
                    });
        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
        LogUtil.e(Utils.throwableToString(throwable));
    }

    @Override
    public void onFinish() {
        LogUtil.e("onFinish()");
    }


    /**
     * @Description: 登陆
     */
    private void toLogin(String sUserName) {
        String sPwd = UserSP.getInstance().getPWD(mContext);
        UserSP.getInstance().saveAutologin(mContext, false);
//        String sUrl = AppSP.getInstance().getRegisterAddress(mContext, sUserName);
        String sUrl = UserUtil.getServerUrl(mContext);
        if (TextUtils.isEmpty(sUrl) || TextUtils.isEmpty(sPwd) || TextUtils.isEmpty(sUserName)) {
            return;
        }
        RequestParams params = HttpParams.userLoginCN(sUserName, sPwd);
        HttpClientUsage.getInstance().post(mContext, sUrl, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                        super.onSuccess(statusCode, headers, response);
                        UserSP.getInstance().saveAutologin(mContext, true);
                        mContext.startService(new Intent(mContext, IMLiteGuardianService.class));
                    }
                });
    }


    /**
     * facebook登录
     */
    public void facebookLoginOrBind() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        if (token == null)
            return;
//        String sUserName = token.getUserId();
//        String sUrl = AppSP.getInstance().getRegisterAddress(mContext, sUserName);
        String sUrl = UserUtil.getServerUrl(mContext);
        if (Utils.isEmpty(sUrl)) //注册地址是否有缓存
            return;
        UserSP.getInstance().saveAutologin(mContext, false);
        RequestParams params = HttpParams.facebookLogin(token.getToken(), token.getUserId());
        HttpClientUsage.getInstance().post(mContext, sUrl, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                        super.onSuccess(statusCode, headers, response);
                        UserSP.getInstance().saveAutologin(mContext, true);
                        mContext.startService(new Intent(mContext, IMLiteGuardianService.class));
                    }
                });
    }
}
