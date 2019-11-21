package com.bluebud.activity.settings;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bluebud.activity.BaseActivity;
import com.bluebud.app.AppApplication;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.DeviceWifi;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.deviceWifiInfo;
import com.bluebud.listener.ReceListener;
import com.bluebud.listener.SendandRece;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.utils.WifiSettingUtil;
import com.bluebud.view.ClearEditText;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.lang.ref.SoftReference;
import java.util.List;

//WIFI设置
public class WifiSettingActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener, ReceListener {
    private ClearEditText mCetWifiName;
    private ClearEditText mCetWifiPassword;
    private ImageView mIv;
    private boolean isHide = false;
    private String device_sn;
    private String Mip = "192.168.225.1";
    private int Mport = 6666;
    private Handler mHandler;
    private String serverAddress;// ip地址
    private Button btn_wifi_commit;
    private WifiSettingActivity mContext;
    private String oldName;
    private String oldPassword;
    private String wifiName;
    private String wifiPassword;
    private String wifiStatu0 = "0";// 表示修改数据库WiFi名和密码
    private String wifiStatu1 = "1";// 表示通过WiFi接口
    private Tracker currentTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_wifi_setting);
        SoftReference<WifiSettingActivity> reference = new SoftReference<WifiSettingActivity>(
                this);
        mContext = reference.get();
        mHandler = new Handler();
        init();
        judgeRequestMode();
    }

    /**
     * 初始化控件
     */
    private void init() {
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleText(R.string.wifi_setting);
        getBaseTitleLeftBack().setOnClickListener(this);
        super.setBaseTitleRightText(R.string.submit1);
        super.setBaseTitleRightTextVisible(View.VISIBLE);
        super.setBaseTitleVisible(View.VISIBLE);
        getBaseTitleRightText().setVisibility(View.GONE);// 隐藏标题头提交按钮

        currentTracker = UserUtil.getCurrentTracker(this);
        if (currentTracker != null)
            device_sn = currentTracker.device_sn;

        mCetWifiName = (ClearEditText) findViewById(R.id.et_wifi_name);
        mCetWifiPassword = (ClearEditText) findViewById(R.id.et_wifi_password);
        mIv = (ImageView) findViewById(R.id.iv);
        ((RelativeLayout) findViewById(R.id.rl_hide_display_password))
                .setOnClickListener(this);
        btn_wifi_commit = (Button) findViewById(R.id.btn_wifi_commit);
        btn_wifi_commit.setOnClickListener(this);
        mCetWifiName.addTextChangedListener(mTextWatcher);
        mCetWifiPassword.addTextChangedListener(mTextWatcher);

        WifiManager wifiManage = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);// 查询ip地址
        DhcpInfo info = wifiManage.getDhcpInfo();
        serverAddress = intToIp(info.serverAddress);// 获取ip地址
    }

    /**
     * 判断采用哪种方式获取WiFi名和密码
     */
    private void judgeRequestMode() {
        if (!AppApplication.getInstance().isNetworkConnected(false)) {
            ToastUtil.show(WifiSettingActivity.this, R.string.network_error);
            return;
        }
        if (Mip.equals(serverAddress)) {// ip相等通过WiFi通道获取WiFi名和密码
            ChangeProgressBar(true);
            byte[] data1 = WifiSettingUtil.getWifiNameAndPassword(device_sn);
            new Thread(new SendandRece(mContext, data1, serverAddress, Mport, false)).start();
        } else
            mHandler.post(new Runnable() {// 通过接口查询WiFi信息
                @Override
                public void run() {
                    WifiNameAndPasswordApi(device_sn);
                }
            });
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    protected void onResume() {
        super.onResume();
        changeButtonStyle();
    }

    /**
     * 输入字符改变动态监听
     */
    private final TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String string = s.toString();
            if (Utils.isCorrectWifi(string) || string.length() > 20) {
                ((Editable) s).delete(start, start + count);
                ToastUtil.show(mContext, R.string.input_pwd);
            }
        }

        public void afterTextChanged(Editable s) {
            changeButtonStyle();// 改变保存按钮样式
        }
    };

    /**
     * 按钮样式设置
     */
    private void changeButtonStyle() {
        int nameLength = mCetWifiName.getText().toString().length();
        int passwordLenght = mCetWifiPassword.getText().toString().length();
        if (nameLength > 0 && (nameLength > 0 && passwordLenght > 7)) {
            btn_wifi_commit.setEnabled(true);
            btn_wifi_commit.setTextColor(getResources().getColor(R.color.white));
        } else {
            btn_wifi_commit.setEnabled(false);
            btn_wifi_commit.setTextColor(getResources().getColor(R.color.text_theme3));
        }
    }

    /**
     * 监听事件处理
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_title_back:// 返回
                SendandRece.isDestroy = true;
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
                finish();
                break;

            case R.id.rl_hide_display_password:// 隐藏显示密码
                setPasswordHideAndDisplay();
                break;

            case R.id.btn_wifi_commit:// 提交
                if(!Utils.isSuperUser(currentTracker, mContext))
                    return;
                if (!SendandRece.isDestroy)// 防止重复点击操作WiFi
                    return;
                wifiName = mCetWifiName.getText().toString().trim();
                wifiPassword = mCetWifiPassword.getText().toString().trim();

                if (wifiPassword.length() < 8) {
                    ToastUtil.show(mContext, R.string.input_pwd);
                    return;
                }

                // 弹出对话框提示是否更改WiFi
                DialogUtil.show(mContext, R.string.prompt, R.string.wifi_reminder,
                        R.string.confirm, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ChangeProgressBar(true);
                                setWifiMessge();
                                DialogUtil.dismiss();
                            }
                        }, R.string.cancel, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DialogUtil.dismiss();
                            }
                        });
                break;
        }
    }

    /**
     * 设置WiFi信息
     */
    private void setWifiMessge() {
        if (!Mip.equals(serverAddress)) {// 网络接口设置
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    UpdateWifiInfo(device_sn, wifiName, wifiPassword, wifiStatu1, true);// 网络接口设置
                }
            });
        } else
            UpdateWifiInfo(device_sn, wifiName, wifiPassword, wifiStatu0, true);// 先修改服务器wifi数据库，在进行WiFi通道设置
    }


    /**
     * wifi通道设置成功
     */
    @Override
    public void onSetSuccess(int setSuccess) {// 只吐司
        displayingResult(setSuccess, -1);
    }

    /**
     * wifi通道查询成功
     */
    @Override
    public void onReceiveData(final String wifiName, final String wifiPassword) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChangeProgressBar(false);
                mCetWifiName.setText(wifiName);
                mCetWifiPassword.setText(wifiPassword);
                oldName = wifiName;
                oldPassword = wifiPassword;
            }
        });
    }

    /**
     * WiFi通道设置或者查询失败
     */
    @Override
    public void onRequestFailure(int result, int statue) {
        SendandRece.isDestroy = true;
        displayingResult(result, statue);
    }

    /**
     * 显示结果
     */
    private void displayingResult(final int Result, final int statue) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChangeProgressBar(false);
                if (statue == 1) {
                    UpdateWifiInfo(device_sn, oldName, oldPassword, wifiStatu0, false);// 只修改服务器数据库
                    ToastUtil.show(mContext, Result);
                } else if (statue == 0)// socket查询失败走接口查询
                    WifiNameAndPasswordApi(device_sn);

                else if (statue == -1) {// WiFi设置成功
                    oldName = mCetWifiName.getText().toString().trim();
                    oldPassword = mCetWifiPassword.getText().toString().trim();
                    ToastUtil.show(mContext, Result);
                }
            }
        });
    }

    /**
     * 设置WiFi
     *
     * @param trackerNo 设备序号
     * @param name      WiFi名
     * @param password  WiFi密码
     * @param state     设置状态 0 、修改WiFi数据库信息 1、表示设置WiFi信息
     * @param isSocket  是否是失败修改数据库
     *                  true为设置WiFi和提前修改数据库WiFi信息,false为WiFit通道设置失败后修改数据库WiFi信息
     */
    private void UpdateWifiInfo(String trackerNo, final String name,String password, final String state, final boolean isSocket) {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.setDeviceWifiNameAndPassword(trackerNo, name, password, state);

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ChangeProgressBar(true);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        SendandRece.isDestroy = true;
                        String responseValue = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(responseValue);
                        if (obj == null)
                            return;

                        if (obj.code == 0) {
                            if (state.equals(wifiStatu1)) {//通过接口进行WiFi设置成功
                                ToastUtil.show(mContext, obj.what);
                                oldName = mCetWifiName.getText().toString().trim();
                                oldPassword = mCetWifiPassword.getText().toString().trim();
                            } else if (isSocket && state.equals(wifiStatu0)) {// 修改数据库成功后，进行WiFi通道设置wifi
                                byte[] data1 = WifiSettingUtil.setWifiNameAndPassword(device_sn, wifiName, wifiPassword);// ,wifiName,
                                new Thread(new SendandRece(mContext, data1, serverAddress, Mport, true)).start();
                            }
                        } else
                            ToastUtil.show(mContext, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        SendandRece.isDestroy = true;
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ChangeProgressBar(false);
                    }
                });
    }

    /**
     * 查询WiFi
     */
    private void WifiNameAndPasswordApi(String trackerNo) {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.getDeviceWifiNameAndPassword(trackerNo);// 获取

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ChangeProgressBar(true);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        SendandRece.isDestroy = true;
                        String responseValue = new String(response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(responseValue);
                        if (obj == null)
                            return;

                        if (obj.code == 0) {
                            deviceWifiInfo wifiParse = GsonParse.getWifiParse(responseValue);
                            if (wifiParse == null)
                                return;
                            List<DeviceWifi> list = wifiParse.deviceWifi;
                            if (list == null)
                                return;
                            DeviceWifi deviceWifi = list.get(0);
                            mCetWifiName.setText(deviceWifi.name);
                            mCetWifiPassword.setText(deviceWifi.password);
                            oldName = deviceWifi.name;
                            oldPassword = deviceWifi.password;
                        } else
                            ToastUtil.show(mContext, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        SendandRece.isDestroy = true;
                        ToastUtil.show(mContext, new String(errorResponse));
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ChangeProgressBar(false);
                    }
                });
    }

    /**
     * 进度条
     */
    private void ChangeProgressBar(boolean isShow) {
        if (isShow)
            ProgressDialogUtil.showNoCanceled(mContext, null, mContext);
        else
            ProgressDialogUtil.dismiss();
    }

    /**
     * 设置密码明文和密文显示
     */
    private void setPasswordHideAndDisplay() {
        if (isHide) {
            isHide = false;
            mIv.setBackgroundResource(R.drawable.btn_show_passwd);
            mCetWifiPassword
                    .setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance()); // 密码以 明文显示
        } else {
            isHide = true;
            mIv.setBackgroundResource(R.drawable.btn_hide_passwd);
            mCetWifiPassword
                    .setTransformationMethod(PasswordTransformationMethod
                            .getInstance()); // 以密文显示，以.代替
        }
    }

    // 将获取的int转为真正的ip地址,参考的网上的，修改了下
    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + ((i >> 24) & 0xFF);
    }

    @Override
    public void onProgressDialogBack() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SendandRece.isDestroy = true;
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

}
