package com.bluebud.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.data.sharedprefs.AppSP;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.utils.Constants;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.castel.obd.OBD;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;


public class ApnActivity extends BaseActivity implements OnClickListener {
    private TextView tvPrompt;
    private Button btnSubmit;
    private EditText etSIM;
    private EditText etAPN;
    private EditText etUserName;
    private EditText etPwd;

    private Tracker mTracker;
    private String sTrackerNo = "";
    private String sTrackerSim = "";
    private int iRanges = 1;
    private LinearLayout llSim;
    private LinearLayout llUserdata;
    private EditText etMcc;
    private EditText etMnc;
    private int protocol_type = 0;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_apn);
        mContext = this;
        init();
        //getDeviceAPN();
    }

    private void init() {
        setBaseTitleText(R.string.apn_setting);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.submit1);
        setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        getBaseTitleLeftBack().setOnClickListener(this);

        mTracker = (Tracker) getIntent().getSerializableExtra(Constants.EXTRA_TRACKER);

        if (null != mTracker) {
            sTrackerNo = mTracker.device_sn;
            sTrackerSim = mTracker.tracker_sim;
            iRanges = mTracker.ranges;
            protocol_type = mTracker.protocol_type;

        }

        tvPrompt = (TextView) findViewById(R.id.tv_prompt);
        btnSubmit = (Button) findViewById(R.id.btn_commit);
        btnSubmit.setVisibility(View.GONE);
        etSIM = (EditText) findViewById(R.id.et_sim);
        etAPN = (EditText) findViewById(R.id.et_apn);
        etUserName = (EditText) findViewById(R.id.et_username);
        etPwd = (EditText) findViewById(R.id.et_passwd);
        llSim = (LinearLayout) findViewById(R.id.ll_sim);
        llUserdata = (LinearLayout) findViewById(R.id.ll_mnc_mcc);
        etMcc = (EditText) findViewById(R.id.et_mcc);
        etMnc = (EditText) findViewById(R.id.et_mnc);
        limitInputAPN();//限制apn输入字符类型

        String strPrompt = getString(R.string.apn_hint);
        if (5 == iRanges) {
            if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
                strPrompt = getString(R.string.apn_watch770_hint);
            } else {
                strPrompt = getString(R.string.apn_watch_hint);
            }
        }
        if (5 == iRanges) {//只有770设备的时候才有用户数据
            if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
                llUserdata.setVisibility(View.VISIBLE);
            } else {
                llUserdata.setVisibility(View.GONE);
            }

        } else {
            llUserdata.setVisibility(View.GONE);
        }

        tvPrompt.setText(Html.fromHtml(strPrompt));
        btnSubmit.setOnClickListener(this);

        etSIM.setText(sTrackerSim);
    }

    /**
     * 设置apn输入限制
     */
    private void limitInputAPN() {
        etSIM.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//设备SIM卡
        etSIM.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        etAPN.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});//apn输入限制
        etMcc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});//MCC输入限制
        etMcc.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        etMnc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});//MNC输入限制
        etMnc.setInputType(EditorInfo.TYPE_CLASS_PHONE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //MobclickAgent.onPageEnd(Constants.UMENG_PAGE_APN);
        //MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //MobclickAgent.onPageStart(Constants.UMENG_PAGE_APN);
        //MobclickAgent.onResume(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                onBackPressed();
                break;
            case R.id.rl_title_right_text://提交
                if (Utils.isSuperUser(mTracker, ApnActivity.this)) {
                    setApn();

                }
                break;
        }
    }

    //设置770手表数据
    private void setWatchApn() {
        String apn = etAPN.getText().toString();
        if (apn == null || apn.equals("")) {
            ToastUtil.show(this, R.string.apn_null);
            return;
        }
        if (!Utils.isCorrectApn(apn)) {
            ToastUtil.show(this, R.string.apn_len_notice);
            return;
        }
        String userName = etUserName.getText().toString();
        String passwd = etPwd.getText().toString();
        if (userName == null) {
            userName = "";
        }
        if (passwd == null) {
            passwd = "";
        }
        if (!Utils.isCorrectApnUserName(userName)) {
            ToastUtil.show(this, R.string.apn_username_len_notice);
            return;
        }
        if (!Utils.isCorrectApnPasswd(passwd)) {
            ToastUtil.show(this, R.string.apn_passwd_len_notice);
            return;
        }


    }

    // 设置APN
    public void setDeviceAPN(String nameApn, String userName, String pwd, String userData) {

        String url = UserUtil.getServerUrl(mContext);

        RequestParams params = HttpParams.setDeviceAPN(sTrackerNo, nameApn, userName, pwd, userData);
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {

                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null) {

                            return;
                        }
                        if (obj.code == 0) {

                        } else {

                        }
                        ToastUtil.show(mContext, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void setApn() {
        String apn = etAPN.getText().toString();
        sTrackerSim = etSIM.getText().toString();
        if (!Utils.isCorrectMobile(sTrackerSim)) {
            ToastUtil.show(this, R.string.simno_error);
            return;
        }

        if (apn == null || apn.equals("")) {
            ToastUtil.show(this, R.string.apn_null);
            return;
        }
        if (!Utils.isCorrectApn(apn)) {
            ToastUtil.show(this, R.string.apn_len_notice);
            return;
        }
        String url = AppSP.getInstance().getRegisterAddressDNS(UserSP.getInstance().getUserName(this));
        String userName = etUserName.getText().toString();
        String passwd = etPwd.getText().toString();
        if (userName == null) {
            userName = "";
        }
        if (passwd == null) {
            passwd = "";
        }

        if (!(5 == iRanges && (5 == protocol_type || 6 == protocol_type || 7 == protocol_type))) {//不是770设备的时候要判断
            if (!Utils.isCorrectApnUserName(userName)) {
                ToastUtil.show(this, R.string.apn_username_len_notice);
                return;
            }
            if (!Utils.isCorrectApnPasswd(passwd)) {
                ToastUtil.show(this, R.string.apn_passwd_len_notice);
                return;
            }
        }
        String mcc = "";
        String mnc = "";
        if (5 == iRanges && (5 == protocol_type || 6 == protocol_type || 7 == protocol_type)) {//只有770设备的时候才有用户数据
            mcc = etMcc.getText().toString().trim();
            mnc = etMnc.getText().toString().trim();
            if (Utils.isEmpty(mcc)) {
                ToastUtil.show(this, R.string.empty_mcc);
                return;
            }
            if (Utils.isEmpty(mnc)) {
                ToastUtil.show(this, R.string.empty_mnc);
                return;
            }
        }


        String msg = "";
        if (5 == iRanges) {
            if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
                //pw,123456,apn,apn名字,username,password,mccmnc#
                //apn名字,username,password,mccmnc这4个都是apn的对应参数，其中apn名字，mccmnc是必须要有的，其他可以没有
                msg = "pw,123456,apn," + apn + "," + userName + "," + passwd + "," + mcc + mnc + "#";
            } else {
                msg = OBD.SMSAPNEncrypt(apn, userName, passwd);
            }
        } else {
            msg = "*" + sTrackerNo + ",set;gprs-para,1," + url + ",11888,"
                    + apn + "," + userName + "," + passwd + "#";
        }
        LogUtil.i("msg:" + msg + "protocal_type:" + protocol_type);
        Uri uri = Uri.parse("smsto:" + sTrackerSim);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", msg);
        startActivity(intent);

        // sendSMS(strTrackerSim, msg);
        //
        // DialogUtil.show(this, R.string.prompt, R.string.apn_send_notice,
        // R.string.confirm, new OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        // // TODO Auto-generated method stub
        //
        // DialogUtil.dismiss();
        //
        // if (iFromWhere == Constants.APN_LOGIN) {
        // toMainActivity();
        // }
        //
        // finish();
        // }
        // });
    }

    private void sendSMS(String phoneNum, String message) {
        // 初始化发短信SmsManager类
        SmsManager smsManager = SmsManager.getDefault();
        // 如果短信内容长度超过70则分为若干条发
        if (message.length() > 70) {
            ArrayList<String> msgs = smsManager.divideMessage(message);
            for (String msg : msgs) {
                smsManager.sendTextMessage(phoneNum, null, msg, null, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNum, null, message, null, null);
        }
    }

    private String getUrl(String url) {
        String[] urls1 = url.split("//");
        url = urls1[1];
        String[] urls2 = url.split(":");
        return urls2[0];
    }


    // 得到APN接口
    public void getDeviceAPN() {

        String url = UserUtil.getServerUrl(mContext);

        RequestParams params = HttpParams.getDeviceAPN(sTrackerNo);
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null) {
                            return;
                        }
                        if (obj.code == 0) {

                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });

    }

}
