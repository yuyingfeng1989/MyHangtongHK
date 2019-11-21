package com.bluebud.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bluebud.activity.settings.TrackerEditActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.User;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.TextChangeUtils;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.ClearEditText;
import com.dtr.barcode.core.CaptureActivity1;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class BindActivity extends BaseActivity implements OnClickListener,
        OnProgressDialogClickListener {
    //    private Tracker mCurTracker;
    private String resutlHK = "http://app.livegpslocate.com/hk/d/?deviceid";
    private String resultGS = "http://app.livegpslocate.com/d/?deviceid";
    private String resultCN = "http://app.livegpslocate.com/cn/d/?deviceid";
    private Tracker mTracker;

    private String sSerialNo = "";
//    private String sSimNo = "";

    private ImageView ivEquipmentImage;
    private TextView tvEquipmentInfo;
    private ImageButton ibQrcode;
    private Button btnBind;
    private TextView tvBuyNow;
    private ClearEditText etSerialCode;
    private ClearEditText etSimCode;

    private String mEquipmentType;
    //    private int iEquipmentType = 1;
    private RequestHandle requestHandle;
    private TextView tvNoDevicetips;
    private Spinner spinnerRange;
    private ArrayAdapter<String> mRangeAdapter;
    private int iCheckedId = 1;
    private TextView tvNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_bind1);
        getEquipmentType();
        init();
    }

    /**
     * (non-Javadoc) 二维码扫描完毕后，返回二维码值
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    String backResult = data.getStringExtra("result");
                    if (backResult == null)
                        return;
                    if (!backResult.contains("http")) {
                        showDeviceId(backResult);
                        return;
                    }

                    Uri uri = Uri.parse(backResult);
                    String path = uri.getPath();
                    LogUtil.e("扫码返回地址==" + path);
                    if (!resutlHK.contains(path) && !resultCN.contains(path) && !resultGS.contains(path))
                        return;
                    String deviceid = uri.getQueryParameter("deviceid"); //解析参数
                    showDeviceId(deviceid);
                }
            }
        }
    }

    /**
     * 显示deviceId
     */
    private void showDeviceId(String deviceid) {
        if (TextUtils.isEmpty(deviceid))
            return;
        etSerialCode.setText(deviceid);
        sSerialNo = deviceid;
        int snType = Utils.serialNumberRange(deviceid);
        if (0 != snType) {
            iCheckedId = snType;
//            iEquipmentType = snType;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind:
                bindingRegister();
                break;

            case R.id.ib_get_qrcode:
                Intent intent = new Intent();
                intent.setClass(this, CaptureActivity1.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 1);
                break;

            case R.id.tv_buy_now:// 立即购买
                Intent intent2 = new Intent(BindActivity.this, ShoppingActivity1.class);
                if (mEquipmentType.equals(Constants.PET_EQUIPMENT)) {//宠物
                    intent2.putExtra("type", Constants.PET_EQUIPMENT);
                } else if (mEquipmentType.equals(Constants.WATCH_EQUIPMENT)) {//手表
                    intent2.putExtra("type", Constants.WATCH_EQUIPMENT);
                } else if (mEquipmentType.equals(Constants.CAR_EQUIPMENT)) {//汽车
                    intent2.putExtra("type", Constants.CAR_EQUIPMENT);
                } else if (mEquipmentType.equals(Constants.MOTO_EQUIPMENT)) {//摩托车
                    intent2.putExtra("type", Constants.MOTO_EQUIPMENT);
                } else if (mEquipmentType.equals(Constants.PERSON_EQUIPMENT)) {//个人
                    intent2.putExtra("type", Constants.PERSON_EQUIPMENT);
                }
                startActivity(intent2);
                break;

            case R.id.rl_title_back:
                finish();
                break;
            case R.id.tv_enter_main://随便逛逛
                startActivity(new Intent(BindActivity.this, MainActivity.class));
                sendBroadcast(new Intent(Constants.ACTION_TRACTER_ENTER_MAIN));
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

    @Override
    protected void onResume() {
        super.onResume();
        if (etSerialCode.getText().toString().length() > 0) {
            ((Button) findViewById(R.id.btn_bind)).setEnabled(true);
            ((Button) findViewById(R.id.btn_bind)).setTextColor(this
                    .getResources().getColor(R.color.white));

        } else {
            ((Button) findViewById(R.id.btn_bind)).setEnabled(false);
            ((Button) findViewById(R.id.btn_bind)).setTextColor(this
                    .getResources().getColor(R.color.text_theme3));

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
        setBaseTitleText(R.string.bind_equipment);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.submit1);
        setBaseTitleRightTextVisible(View.GONE);
        getBaseTitleRightText().setOnClickListener(this);
        tvNotify = (TextView) findViewById(R.id.tv_notify);

        ibQrcode = (ImageButton) findViewById(R.id.ib_get_qrcode);
        btnBind = (Button) findViewById(R.id.btn_bind);
        tvBuyNow = (TextView) findViewById(R.id.tv_buy_now);
        etSerialCode = (ClearEditText) findViewById(R.id.et_equip_id);
        etSimCode = (ClearEditText) findViewById(R.id.et_sim_code);
        ivEquipmentImage = (ImageView) findViewById(R.id.iv_equipment_image);
        tvEquipmentInfo = (TextView) findViewById(R.id.tv_equipment_info);
        tvNoDevicetips = (TextView) findViewById(R.id.tv_tips1);//无设备提示
        ((TextView) findViewById(R.id.tv_enter_main)).setOnClickListener(this);//随便逛逛

        spinnerRange = (Spinner) findViewById(R.id.spinner_range);

        String[] ranges = getResources().getStringArray(R.array.ranges);
        mRangeAdapter = new ArrayAdapter<String>(this, R.layout.layout_spinner,ranges);
        mRangeAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        spinnerRange.setAdapter(mRangeAdapter);
        spinnerRange.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                iCheckedId = position + 1;
                LogUtil.i("iCheckedId" + iCheckedId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        ibQrcode.setOnClickListener(this);
        btnBind.setOnClickListener(this);
        tvBuyNow.setOnClickListener(this);
        TextChangeUtils tc = new TextChangeUtils();  // 帐号和密码监听
        tc.addEditText(etSerialCode);
        tc.setButton(btnBind);
//        etSerialCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//限制输入类型及长度

        LogUtil.v("equipment type is " + mEquipmentType);
        // 根据设备类型设置说明以及头像
        if (mEquipmentType.equals(Constants.PET_EQUIPMENT)) {
            LogUtil.v("equipment type is1 " + mEquipmentType);
            ivEquipmentImage.setImageResource(R.drawable.img_chongwu);
            tvEquipmentInfo.setText(getResources().getString(R.string.pet_equipment_info1));
//            iEquipmentType = 2;
            tvNoDevicetips.setText(getString(R.string.no_equipment_tip, getString(R.string.pet_equipment_info1)));
        } else if (mEquipmentType.equals(Constants.WATCH_EQUIPMENT)) {
            int type = getIntent().getIntExtra("watcher", 1);
            if (type == 1) {
                ivEquipmentImage.setImageResource(R.drawable.icon_ht790);
                tvEquipmentInfo.setText(getResources().getString(R.string.pt790));
            } else if (type == 2) {
                ivEquipmentImage.setImageResource(R.drawable.icon_ht770);
                tvEquipmentInfo.setText(getResources().getString(R.string.ht770fo));
            } else {
                ivEquipmentImage.setImageResource(R.drawable.img_ertongshoubiao);
                tvEquipmentInfo.setText(getResources().getString(R.string.whatch_equipment_info1));
            }
//            iEquipmentType = 5;
            tvNotify.setText(getResources().getString(R.string.bind_watch_prompt));
            //tvNoDevicetips.setText(getString(R.string.no_equipment_tip, getString(R.string.pet_equipment_info1)));
        } else if (mEquipmentType.equals(Constants.PERSON_EQUIPMENT)) {
            ivEquipmentImage.setImageResource(R.drawable.img_gerenshibei);
            tvEquipmentInfo.setText(getResources().getString(
                    R.string.sos_equipment_info1));
//            iEquipmentType = 1;
            tvNoDevicetips.setText(getString(R.string.no_equipment_tip, getString(R.string.sos_equipment_info1)));
        } else if (mEquipmentType.equals(Constants.CAR_EQUIPMENT)) {
            ivEquipmentImage.setImageResource(R.drawable.img_car);
            tvEquipmentInfo.setText(getResources().getString(
                    R.string.car_equipment_info1));
//            iEquipmentType = 3;
            tvNoDevicetips.setText(getString(R.string.no_equipment_tip, getString(R.string.car_equipment_info1)));
        } else if (mEquipmentType.equals(Constants.MOTO_EQUIPMENT)) {
            ivEquipmentImage.setImageResource(R.drawable.img_moto);
            tvEquipmentInfo.setText(getResources().getString(
                    R.string.moto_equipment_info1));
//            iEquipmentType = 4;
            tvNoDevicetips.setText(getString(R.string.no_equipment_tip, getString(R.string.moto_equipment_info1)));

        } else if (mEquipmentType.equals(Constants.OLD_PEOPLE_EQUIPMENT)) {
            ivEquipmentImage.setImageResource(R.drawable.icon_old_people_watch);
            tvEquipmentInfo.setText(getResources().getString(R.string.old_people_watch));
//            iEquipmentType = 5;
            tvNotify.setText(getResources().getString(R.string.bind_watch_prompt));
//			tvNoDevicetips.setText(getString(R.string.no_equipment_tip, getString(R.string.old_people_watch)));
        }
//        else if (mEquipmentType.equals(Constants.Watcher_3G)) {//3G智能手表
//            ivEquipmentImage.setImageResource(R.drawable.icon_3gwatch);
//            tvEquipmentInfo.setText(getResources().getString(R.string.whatch_3G));
//            iEquipmentType = 5;
//            tvNotify.setText(getResources().getString(R.string.bind_3gwatch_prompt));
//            etSerialCode.setHint(R.string.hint_3gwatch_text);
//        }
    }


    /**
     * @param
     * @return void
     * @throws
     * @Title: getEquipmentType
     * @Description: 获取设备类型
     */
    private void getEquipmentType() {
        Intent intent = getIntent();
        mEquipmentType = intent.getStringExtra("equipment_type");
        LogUtil.v("equipment type is " + mEquipmentType);
    }

    /**
     * @param @param sResult
     * @return void
     * @throws
     * @Title: bindSuccess
     * @Description: 绑定成功
     */
    private void bindSuccess(String sResult) {
        User user = GsonParse.userParse(sResult);
        User user1 = UserUtil.getUserInfo(BindActivity.this);
        user1.device_list = user.device_list;
//		UserUtil.savaUserInfo(BindActivity.this, user1);

//		for (int i = 0; i < user1.device_list.size(); i++) {
//			LogUtil.v("serialno is  " + user1.device_list.get(i).device_sn
//					+ "  headurl " + user1.device_list.get(i).conn_name + ":"
//					+ user1.device_list.get(i).conn_port + "  "
//					+ user1.device_list.get(i).head_portrait);
//		}

        for (int i = 0; i < user1.device_list.size(); i++) {
            if (sSerialNo.equals(user1.device_list.get(i).device_sn)) {
                user1.device_list.get(i).isExistGroup = sSerialNo;
                mTracker = user1.device_list.get(i);
                break;
            }
        }
        UserUtil.savaUserInfo(BindActivity.this, user1);
        UserUtil.saveCurrentTracker(this, mTracker);

//		 if (null == mCurTracker) {
//		 UserUtil.saveCurrentTracker(this, user1.device_list.get(0));
//		 sendBroadcast(new Intent(Constants.ACTION_TRACTER_CHANGE));
//		 }
        if (mTracker.protocol_type == 8 && mTracker.product_type.equals("29")) {
            sendBroadcast(new Intent(Constants.ACTION_TRACTER_ENTER_MAIN));
            Intent intent1 = new Intent(BindActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
            sendBroadcast(new Intent(Constants.ACTION_TRACTER_CHANGE));
            return;
        }
        sendBroadcast(new Intent(Constants.ACTION_TRACTER_CHANGE));
        Intent intent1 = new Intent(BindActivity.this, TrackerEditActivity.class);
        intent1.putExtra(Constants.EXTRA_TRACKER, mTracker);
        intent1.putExtra("fromwhere", Constants.BINDACTIVITY);
        startActivity(intent1);
        finish();
        /*
         * if (0 == UserSP.getInstance().getServerAndMap(BindActivity1.this)) {
		 * DialogUtil.showNoCancel(BindActivity1.this,
		 * R.string.bind_success_title, R.string.bind_success1,
		 * R.string.fill_in_now, new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { DialogUtil.dismiss();
		 * 
		 * Intent intent1 = new Intent(BindActivity1.this,
		 * TrackerEditActivity.class); intent1.putExtra(Constants.EXTRA_TRACKER,
		 * mTracker); startActivity(intent1); finish();
		 * 
		 * } }, R.string.later, new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { DialogUtil.dismiss();
		 * 
		 * finish(); } }); } else { DialogUtil.showNoCancel(BindActivity1.this,
		 * R.string.bind_success_title, R.string.bind_success,
		 * R.string.fill_in_now, new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { DialogUtil.dismiss();
		 * 
		 * Intent apnIntent = new Intent(BindActivity1.this, ApnActivity.class);
		 * apnIntent.putExtra(Constants.EXTRA_TRACKER, mTracker);
		 * startActivity(apnIntent); finish(); } }, R.string.later, new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { DialogUtil.dismiss();
		 * 
		 * finish(); } }); }
		 */
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: bindingRegister
     * @Description: 绑定设备
     */
    private void bindingRegister() {
        sSerialNo = etSerialCode.getText().toString().trim();
//        sSimNo = etSimCode.getText().toString().trim();
        if (sSerialNo == null || sSerialNo.trim().equals("")) {
            ToastUtil.show(BindActivity.this, R.string.no_deviceno);
            return;
        }
//        if (sSerialNo.length() < 7 || sSerialNo.length() > 19) {
//            ToastUtil.show(BindActivity.this, R.string.deviceno_error);
//            return;
//        }
//		if (Utils.isEmpty(sSimNo) || !Utils.isCorrectMobile(sSimNo)) {
//			ToastUtil.show(BindActivity.this, R.string.simno_error);
//			return;
//		}

        // int snType = Utils.serialNumberRange(sSerialNo);
        // if (0 == snType) {
        // DialogUtil.show(this, R.string.prompt, R.string.tracker_type_error,
        // R.string.confirm, new OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        //
        // DialogUtil.dismiss();
        // }
        // });
        // return;
        // }
        //
        // if (iEquipmentType != snType) {
        // DialogUtil.show(this, R.string.prompt,
        // R.string.tracker_type_choose_error, R.string.confirm,
        // new OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        // DialogUtil.dismiss();
        //
        // trackerRegister();
        // }
        // }, R.string.cancel, new OnClickListener() {
        //
        // @Override
        // public void onClick(View arg0) {
        // DialogUtil.dismiss();
        // }
        // });
        // } else {
        // trackerRegister();
        // }

        trackerRegister();
    }

    /**
     * @Description: 绑定设备
     */
    private void trackerRegister() {
        String url = UserUtil.getServerUrl(this);
        int aroundRanges = iCheckedId;
        LogUtil.i("aroundRanges=" + aroundRanges);
        RequestParams params = HttpParams.bindingDevice(sSerialNo, "", 0);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(BindActivity.this, null, BindActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj reBaseObj = GsonParse.reBaseObjParse(new String(response));
                        if (reBaseObj == null) {
                            ToastUtil.show(BindActivity.this, R.string.net_exception);
                            return;
                        }
                        if (reBaseObj.code == 0) {
                            bindSuccess(new String(response));
                        } else {
                            if (2 == reBaseObj.code) {

                            } else {
                                ToastUtil.show(BindActivity.this, reBaseObj.what);
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(BindActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }
}
