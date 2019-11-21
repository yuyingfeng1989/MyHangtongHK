package com.bluebud.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.User;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.view.ClearEditText;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class ChangeDeviceManagementActivity extends BaseActivity implements OnClickListener,
        OnProgressDialogClickListener {
    private Tracker mCurTracker;
    private Tracker mTracker;

    private String sSerialNo = "";
    private String sSimNo = "";

    private ClearEditText etSerialCode;
    private ClearEditText etSimCode;


    private RequestHandle requestHandle;

    private Spinner spinnerRange;
    private ArrayAdapter<String> mRangeAdapter;
    private int iCheckedId = 1;
    private int aroundRanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_change_device_management);
        getData();
        init();
    }

    private void getData() {
        sSerialNo = getIntent().getStringExtra("device_sn");
        aroundRanges = getIntent().getIntExtra("around_ranges", 1);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_right_text://提交
                changearoundRanges();
                break;

            case R.id.rl_title_back:
                finish();
                break;
        }
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }


    /**
     * @param
     * @return void
     * @throws
     * @Title: init
     * @Description: 找到控件，并监听控件
     */
    private void init() {
        getBaseTitleLeftBack().setOnClickListener(this);
        setBaseTitleText(R.string.device_management);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.submit1);
        setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        mCurTracker = UserUtil.getCurrentTracker(this);

        etSerialCode = (ClearEditText) findViewById(R.id.et_equip_id);//设备号
        etSimCode = (ClearEditText) findViewById(R.id.et_sim_code);//sim卡号
        etSerialCode.setText(sSerialNo);
        etSerialCode.setEnabled(false);
        spinnerRange = (Spinner) findViewById(R.id.spinner_range);

        String[] ranges = getResources().getStringArray(R.array.ranges);
        mRangeAdapter = new ArrayAdapter<String>(this, R.layout.layout_spinner,
                ranges);
        mRangeAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        spinnerRange.setAdapter(mRangeAdapter);
        spinnerRange.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                iCheckedId = position + 1;
                LogUtil.i("iCheckedId" + iCheckedId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (1 == aroundRanges || 5 == aroundRanges || 7 == aroundRanges) {
            spinnerRange.setSelection(0);
        } else if (2 == aroundRanges) {
            spinnerRange.setSelection(1);
        } else if (3 == aroundRanges) {
            spinnerRange.setSelection(2);
        } else {
            spinnerRange.setSelection(3);
        }
    }

    /**
     * @param @param sResult
     * @return void
     * @throws
     * @Title: bindSuccess
     * @Description: 绑定成功
     */
    private void bindSuccess(String sResult) {

        User user1 = UserUtil.getUserInfo(ChangeDeviceManagementActivity.this);
        for (int i = 0; i < user1.device_list.size(); i++) {
            LogUtil.i("更新前****：" + user1.device_list.get(i).around_ranges);
        }

        for (int i = 0; i < user1.device_list.size(); i++) {
            if (sSerialNo.equals(user1.device_list.get(i).device_sn)) {
                mTracker = user1.device_list.get(i);
                mTracker.around_ranges = iCheckedId;
                break;
            }
        }
        UserUtil.savaUserInfo(ChangeDeviceManagementActivity.this, user1);

        User user2 = UserUtil.getUserInfo(ChangeDeviceManagementActivity.this);
        for (int i = 0; i < user2.device_list.size(); i++) {
            LogUtil.i("更新后****：" + user2.device_list.get(i).around_ranges);
        }
        if (mCurTracker != null) {
            if (mCurTracker.device_sn.equalsIgnoreCase(sSerialNo)) {
                mCurTracker.around_ranges = iCheckedId;
                UserUtil.saveCurrentTracker(this, mCurTracker);
            }
        }
        setResult(RESULT_OK);
        finish();
    }


    //更改设备的aroundRanges
    private void changearoundRanges() {
        String url = UserUtil.getServerUrl(this);
        int aroundRanges = iCheckedId;
        sSerialNo = etSerialCode.getText().toString().trim();
        sSimNo = etSimCode.getText().toString().trim();
        LogUtil.i("aroundRanges=" + aroundRanges);
        RequestParams params = HttpParams.updateDeviceAroundRanges(sSerialNo, sSimNo, aroundRanges);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(ChangeDeviceManagementActivity.this,
                                null, ChangeDeviceManagementActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj reBaseObj = GsonParse
                                .reBaseObjParse(new String(response));
                        if (reBaseObj == null) {
                            ToastUtil.show(ChangeDeviceManagementActivity.this,
                                    R.string.net_exception);
                            return;
                        }
                        if (reBaseObj.code == 0) {
                            bindSuccess(new String(response));
                            ToastUtil.show(ChangeDeviceManagementActivity.this, reBaseObj.what);
                        } else {
                            if (2 == reBaseObj.code) {

                            } else {
                                ToastUtil.show(ChangeDeviceManagementActivity.this, reBaseObj.what);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(ChangeDeviceManagementActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }
}
