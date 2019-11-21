package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.LoginActivity;
import com.bluebud.adapter.SettingAdapter;
import com.bluebud.app.AppManager;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.service.EventsService;
import com.bluebud.service.IMLiteGuardianService;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.FaceBookPlatform;
import com.bluebud.utils.GlideCacheUtil;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.request.DownloadApkUtil;
import com.facebook.AccessToken;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import io.rong.imkit.RongIM;

//设置页面
public class SettingActivity extends BaseActivity implements OnClickListener,
        OnItemClickListener, OnProgressDialogClickListener, FaceBookPlatform.FacebookListener {
    private ListView lvSetting;
    //    private RequestHandle requestHandle;
    private Tracker mCurTracker;
    private String strTrackerNo;
    //    private String strCurVer;
//    private VersionObj versionObj;
    private int[] generals;
    private int[] generals1 = new int[]{R.string.map_switch, R.string.change_passwd,
            R.string.help, R.string.feedback_suggestion,
            R.string.version_detection, R.string.about_mine, R.string.exit};
//    private int[] generals2 = new int[]{R.string.change_passwd, R.string.help, R.string.feedback_suggestion,
//            R.string.version_detection, R.string.about_mine, R.string.exit};

    private int ranges = 1;
    private boolean appHaveNewVer = false;
    //    private boolean firmwareHaveNewVer = false;
//    private boolean isZh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_setting);
        init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        super.setBaseTitleText(R.string.setting);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        mCurTracker = UserUtil.getCurrentTracker(this);
        if (null != mCurTracker) {
            strTrackerNo = mCurTracker.device_sn;
            ranges = mCurTracker.ranges;
            LogUtil.i("strtrackerNo:" + strTrackerNo + ",ranges:" + ranges);
        }
        lvSetting = (ListView) findViewById(R.id.lv_setting);
        boolean isNewVersion = appHaveNewVer;//|| firmwareHaveNewVer;
//        LogUtil.i("app是否有最新版本：" + appHaveNewVer + "，硬件是否有最新版本："
//                + firmwareHaveNewVer + "是否有最新版本：" + isNewVersion);
//        Locale locale = Locale.getDefault();
//        String language = locale.getLanguage();
        //if ("zh".equals(language)) {

        generals = generals1;
//        isZh = true;
//		} else {
//			generals=generals2;
//			isZh=false;
//		}
        SettingAdapter adapter = new SettingAdapter(this, generals, isNewVersion, ranges);
        lvSetting.setAdapter(adapter);
        lvSetting.setOnItemClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
        switch (position) {
            case 0:
//                if (isZh) {
                startActivity(new Intent(this, MapSwitchActivity.class));// 地图切换
//                } else {
//                    startActivity(new Intent(this, ChangePassWdActivity.class));//修改密码
//                }

                break;
            case 1:
//                if (isZh) {
                startActivity(new Intent(this, ChangePassWdActivity.class));//修改密码
//                } else {
//                    startActivity(new Intent(this, UseHelpActivity.class));// 使用帮助
//                }

                break;
            case 2:// 使用帮助
//                if (isZh) {
                startActivity(new Intent(this, UseHelpActivity.class));
//                } else {
//                    startActivity(new Intent(this, FeedbackActivity.class));// 意见反馈
//                }

                break;
            case 3:// 意见反馈

//                if (isZh) {
                startActivity(new Intent(this, FeedbackActivity.class));
//                } else {
//                    checkForUpdate();
//                }

                break;
            case 4:// 检测版本号// 版本检测
//                if (isZh) {
                checkForUpdate();
//                } else {
//                    startActivity(new Intent(this, AboutActivity.class));
//                }

                break;
            case 5:
                // 关于我们
//                if (isZh) {
                startActivity(new Intent(this, AboutActivity.class));
//                } else {
//                    exit();
//                }
                break;
            case 6:// 退出
                exit();
                break;

        }
    }
//	private void mapSwatch(){
//		if (UserSP.getInstance().getServerAndMap(SettingActivity.this)==0) {
//
//			UserSP.getInstance().saveServerAndMap(SettingActivity.this, 1);
//			sendBroadcast(new Intent(Constants.ACTION_MAP_SWITCH));
//			LogUtil.i("切换的地图:"+UserSP.getInstance().getServerAndMap(SettingActivity.this));
//
//		}else {
//			UserSP.getInstance().saveServerAndMap(SettingActivity.this, 0);
//			sendBroadcast(new Intent(Constants.ACTION_MAP_SWITCH));
//			LogUtil.i("切换的地图:"+UserSP.getInstance().getServerAndMap(SettingActivity.this));
//		}
//	}

    @Override
    public void onProgressDialogBack() {
//        LogUtil.i("onProgressDialogBack()");
//        if (null != requestHandle && !requestHandle.isFinished()) {
//            requestHandle.cancel(true);
//        }
    }

    /**
     * 退出
     */
    private void exit() {
        DialogUtil.show(SettingActivity.this, R.string.exit,
                R.string.change_account_confirm, R.string.exit,
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        changeAccount();
                        DialogUtil.dismiss();
                    }
                }, R.string.cancel, new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                    }
                });
    }

    /**
     * 切换账号
     */
    private void changeAccount() {
        exitCurrentAccount();
        UserSP.getInstance().savePWD(SettingActivity.this, "");// 清空密码
//        UserUtil.clearUserInfo(this);
        UserSP.getInstance().saveAutologin(this, false);
        UserSP.getInstance().clearChatValue(this);//清除微聊保存信息
        GlideCacheUtil.getInstance().clearImageAllCache();//清除Glide所有缓存
        this.stopService(new Intent(this, EventsService.class));
        this.stopService(new Intent(this, IMLiteGuardianService.class));
        AppManager.getAppManager().finishAllActivity();
        UserSP.getInstance().saveUserName(this, null);//保存登录名
        RongIM.getInstance().logout();//退出微聊
        new FaceBookPlatform(SettingActivity.this).logout();//登出facebook
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    /**
     * 退出接口
     */
    private void exitCurrentAccount() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.exit();

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    /**
     * 版本检测更新
     */
    private void checkForUpdate() {
//        if (mCurTracker == null) {
//            return;
//        }
        String url = UserUtil.getServerUrl(this);
//        if (AppApplication.getInstance().isNetworkConnected(true))//wifi情况下检测版本更新
        new DownloadApkUtil(this).checkAPPUpdate(url, false);
//        String url = UserUtil.getServerUrl(this);
//        LogUtil.i("版本更新strTrackerNo:" + strTrackerNo);
//        RequestParams params = HttpParams.checkForUpdate(strTrackerNo, UserSP.getInstance().getUserName(this));
//
//        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
//                new AsyncHttpResponseHandlerReset() {
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                        /*
//                         * ProgressDialogUtil.showNoCanceled(
//						 * CheckUpdateActivity.this, null,
//						 * CheckUpdateActivity.this);
//						 */
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                        super.onSuccess(statusCode, headers, response);
//                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
//                        if (obj == null)
//                            return;
//                        if (obj.code == 0) {
//                            versionObj = GsonParse.versionObjParse(new String(response));
//                            if (versionObj == null)
//                                return;
//                            setVersionData(versionObj);
//                        } else {
//                            ToastUtil.show(SettingActivity.this, obj.what);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
//                        super.onFailure(statusCode, headers, errorResponse,
//                                throwable);
//                        ToastUtil.show(SettingActivity.this, R.string.net_exception);
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        super.onFinish();
//                        ProgressDialogUtil.dismiss();
//                    }
//                });
//    }

//    private void setVersionData(VersionObj obj) {
//        if (obj == null) {
//            return;
//        }
//
//        try {
//            strCurVer = Utils.getVersionName(this);// 得到当前版本号
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        int iCompare = Utils.verCompare(obj.appVersion, strCurVer);
//        if (0 < iCompare) {// APP有最新版本
//            appHaveNewVer = true;
//        } else {
//            appHaveNewVer = false;// APP无最新版本
//        }

//        LogUtil.i(obj.currentFirmwareVersion);
//        LogUtil.i(obj.lastFirmwareVersion);
//
//        if (!Utils.isEmpty(obj.currentFirmwareVersion)
//                && !Utils.isEmpty(obj.lastFirmwareVersion)) {
//            if (!obj.currentFirmwareVersion.equals(obj.lastFirmwareVersion)) {
//                firmwareHaveNewVer = true;// 硬件有最新
//            } else {
//                firmwareHaveNewVer = false;// 硬件无最新
//            }
//        }
//        LogUtil.i("appHaveNewVer:" + appHaveNewVer + ",firmwareHaveNewVer:" + firmwareHaveNewVer);
//        if (appHaveNewVer && firmwareHaveNewVer) {// app和硬件同时有最新版本更新
//            startActivity(new Intent(this, CheckUpdateActivity.class));
//        } else if (appHaveNewVer && !firmwareHaveNewVer) {// app有最新版本，硬件没有最新版本
//            if (versionObj != null) {
//                showAPPUpdate(versionObj);
//            }
//
//        } else if (!appHaveNewVer && firmwareHaveNewVer) {// app无最新版本，硬件有最新版本
//            if (!mCurTracker.super_user.equals(UserSP.getInstance()
//                    .getUserName(this))) {
//                ToastUtil.show(this, R.string.no_super_user);
//                return;
//            }
//            showDeviceSoftware(versionObj);
//        } else {// app和硬件同时无最新版本更新
//            ToastUtil.show(SettingActivity.this, R.string.is_new_ver);
//        }
    }

    @Override
    public void facebookLoginSuccess(AccessToken token) {
    }

    @Override
    public void facebookLoginFail(String message) {
    }

//    private void showAPPUpdate(VersionObj versionObj) {
//        String sCurVersion = Utils.getVersionName(this);
//        String sNewVersion = versionObj.appVersion;
//        final String sAPPUrl = versionObj.appUrl;
//        int iCompare = Utils.verCompare(sNewVersion, sCurVersion);
//        if (0 < iCompare) {
//            DialogUtil.show(SettingActivity.this, R.string.update_prompt,
//                    R.string.update_prompt_content, R.string.update,
//                    new OnClickListener() {
//
//                        @Override
//                        public void onClick(View arg0) {
//                            DialogUtil.dismiss();
//
//                            Uri uri = Uri.parse(sAPPUrl);
//                            Intent netIntent = new Intent(Intent.ACTION_VIEW,
//                                    uri);
//                            startActivity(netIntent);
//                        }
//                    }, R.string.cancel, new OnClickListener() {
//
//                        @Override
//                        public void onClick(View arg0) {
//                            DialogUtil.dismiss();
//                        }
//                    });
//        }
//    }

//    private void showDeviceSoftware(final VersionObj versionObj) {
//
//        DialogUtil.show(SettingActivity.this, R.string.update_prompt,
//                R.string.update_devicesoftware_content, R.string.update,
//                new OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        DialogUtil.dismiss();
//                        upgradDeviceSoftware(versionObj);
//                    }
//                }, R.string.cancel, new OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        DialogUtil.dismiss();
//                    }
//                });
//    }

    // 硬件更新
//    private void upgradDeviceSoftware(VersionObj versionObj) {
//        String url = UserUtil.getServerUrl(this);
//
//        RequestParams params = HttpParams.upgradDeviceSoftware(strTrackerNo,
//                versionObj.lastFirmwareVersion);
//
//        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
//                new AsyncHttpResponseHandlerReset() {
//                    @Override
//                    public void onStart() {
//                        super.onStart();
//                        ProgressDialogUtil.showNoCanceled(SettingActivity.this,
//                                null, SettingActivity.this);
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers,
//                                          byte[] response) {
//                        super.onSuccess(statusCode, headers, response);
//                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
//                                response));
//                        if (obj == null)
//                            return;
//                        if (obj.code == 0) {
//                            ToastUtil.show(SettingActivity.this, obj.what);
//                        } else {
//                            ToastUtil.show(SettingActivity.this, obj.what);
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers,
//                                          byte[] errorResponse, Throwable throwable) {
//                        super.onFailure(statusCode, headers, errorResponse,
//                                throwable);
//                        ToastUtil.show(SettingActivity.this,
//                                R.string.net_exception);
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        super.onFinish();
//                        ProgressDialogUtil.dismiss();
//                    }
//                });
//    }
}
