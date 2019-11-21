package com.bluebud.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.bluebud.liteguardian_hk.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/8/2.
 * 登录facebook第三方sdk
 */

public class FaceBookPlatform {
    private final CallbackManager callbackManager;
    private final List<String> permissions;
    private LoginManager loginManager;
    private Activity context;
    private FacebookListener facebookListener;//回调接口
    public boolean isFaceBook = false;//点击了facebook第三方登录 true点击，false失败

    /**
     * 构造函数
     */
    public FaceBookPlatform(Activity context) {
        this.context = context;
        this.facebookListener = (FacebookListener) context;
        callbackManager = CallbackManager.Factory.create();
        getLoginManager().registerCallback(callbackManager, callback);//注册facebook登录回调
        permissions = Arrays.asList("email", "public_profile");
//        permissions = Arrays.asList("email", "user_likes","user_status", "user_photos", "user_birthday", "public_profile", "user_friends");
    }

    /**
     * 触发登录facebook
     */
    public void login(boolean isFacebookFail) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();/*获取缓存facebook登录对象*/
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        Log.e("TAG", "accessToken=" + accessToken + " 是否过期=" + isLoggedIn);
        if (isLoggedIn) {/*缓存中有token并且token未过期直接使用token*/
            facebookListener.facebookLoginSuccess(accessToken);
        } else {//已过期或者没有token
            if (isFacebookFail) {
                getLoginManager().logInWithReadPermissions(context, permissions);/*执行授权登录*/
                isFaceBook = true;
            } else {
                facebookListener.facebookLoginFail(context.getString(R.string.net_exception));
                isFaceBook = false;
            }
        }
    }

    /**
     * 注册登录回调
     */
    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            isFaceBook = false;
//            getLoginInfo(loginResult.getAccessToken()); //获取用户信息
            if (loginResult == null) {
                facebookListener.facebookLoginFail("登录失败");
                return;
            }
            AccessToken accessToken = loginResult.getAccessToken();
            String userId = accessToken.getUserId();
            String token = accessToken.getToken();
            Log.e("TAG", "------" + "userId=" + userId);
            Log.e("TAG", "------" + "token=" + token);
            facebookListener.facebookLoginSuccess(loginResult.getAccessToken());
        }

        @Override
        public void onCancel() {
            isFaceBook = false; //取消登录
            Toast.makeText(context, "login cancel", Toast.LENGTH_SHORT).show();
            facebookListener.facebookLoginFail("失败");
        }

        @Override
        public void onError(FacebookException error) {//登录出错
            isFaceBook = false;
            Toast.makeText(context, "something wrong", Toast.LENGTH_SHORT).show();
            Log.e("TAG", "------" + "error.toString()=" + error.toString());
            facebookListener.facebookLoginFail(error.getMessage());
        }
    };

    /**
     * 获取回调管理
     */
    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    /**
     * 判断登录facebook是否成功
     */
    public boolean getIssFaceBook() {
        return isFaceBook;
    }

    /**
     * 退出登录facebook
     */
    public void logout() {
        getLoginManager().logOut();
    }

    /**
     * 获取登录管理类
     */
    private LoginManager getLoginManager() {
        if (loginManager == null)
            loginManager = LoginManager.getInstance();
        return loginManager;
    }

    /**
     * 设置接口回调
     */
    public interface FacebookListener {
        void facebookLoginSuccess(AccessToken token);

        void facebookLoginFail(String message);
    }

    /**
     * 退出
     */
//    public void logout() {
//        String logout = context.getResources().getString(
//                com.facebook.R.string.com_facebook_loginview_log_out_action);
//        String cancel = context.getResources().getString(
//                com.facebook.R.string.com_facebook_loginview_cancel_action);
//        String message;
//        Profile profile = Profile.getCurrentProfile();
//        if (profile != null && profile.getName() != null) {
//            message = String.format(
//                    context.getResources().getString(
//                            com.facebook.R.string.com_facebook_loginview_logged_in_as),
//                    profile.getName());
//        } else {
//            message = context.getResources().getString(
//                    com.facebook.R.string.com_facebook_loginview_logged_in_using_facebook);
//        }
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage(message)
//                .setCancelable(true)
//                .setPositiveButton(logout, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        getLoginManager().logOut();
//                    }
//                })
//                .setNegativeButton(cancel, null);
//        builder.create().show();
//    }


    /**
     * 通过登录获取到token，同时到facebook信息库请求个人信息回调
     * @param accessToken
     */
//    public void getLoginInfo(AccessToken accessToken) {
//        Log.e("TAG","accessToken=="+accessToken.getToken());
//        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject object, GraphResponse response) {
//                if (object != null) {
//                    String id = object.optString("id");
//                    String name = object.optString("name");
//                    String gender = object.optString("gender");
//                    String emali = object.optString("email");
//                    JSONObject object_pic = object.optJSONObject("picture");
//                    JSONObject object_data = object_pic.optJSONObject("data");
//                    String photo = object_data.optString("url");
//                    //获取地域信息
//                    String locale = object.optString("locale");
//                    Log.e("TAG","getLoginInfo="+id);
//                    Log.e("TAG","name="+name);
//                    Log.e("TAG","gender="+gender);
//                    Log.e("TAG","emali="+emali);
//                    Log.e("TAG","object_pic="+object_pic);
//                    Log.e("TAG","photo="+photo);
//                    Log.e("TAG","photo="+locale);
//                    // Toast.makeText(context, "" + object.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name,link,gender,birthday,email,picture,locale," +
//                "updated_time,timezone,age_range,first_name,last_name");
//        request.setParameters(parameters);
//        request.executeAsync();
//    }
}
