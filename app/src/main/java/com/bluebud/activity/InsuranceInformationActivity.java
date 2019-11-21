package com.bluebud.activity;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.PetInsur;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class InsuranceInformationActivity extends BaseActivity implements OnClickListener, OnProgressDialogClickListener {
    private RequestHandle requestHandle;
    private TextView tvUserName;
    private TextView tvPhone;
    private TextView tvPetNickname;
    private TextView tvPetType;
    private TextView tvColour;
    private TextView tvAge;
    private TextView tvOrderCode;
    private TextView tvTailShape;
    private TextView tvSex;
    private TextView tvlnsuranceStart;
    private TextView tvlnsuranceend;

    private Tracker mCurTracker;
    //    private Context mContext;
    private String trackerNo;
//    private int tail_shape = 1;
//    private int sex = 0;
//    private String real_name;
//    private String mobile;
//    private String petNickname;
//    private String type;
//    private String colour;
//    private String age;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_pet_insurance_information);
//        mContext = this;
        init();
        getPetInsurance();
    }

    private void init() {
        setBaseTitleText(R.string.insurance_information);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.submit1);
        setBaseTitleRightTextVisible(View.GONE);
        getBaseTitleRightText().setOnClickListener(this);
        getBaseTitleLeftBack().setOnClickListener(this);
        mCurTracker = UserUtil.getCurrentTracker(this);
        if (mCurTracker != null) {
            trackerNo = mCurTracker.device_sn;
        }
        tvOrderCode = (TextView) findViewById(R.id.tv_order_code);//订单号
        tvUserName = (TextView) findViewById(R.id.et_user_name);//姓名
        tvPhone = (TextView) findViewById(R.id.et_phone);//电话
        tvPetNickname = (TextView) findViewById(R.id.et_pet_nick_name);//昵称
        tvPetType = (TextView) findViewById(R.id.et_pet_type);//品种
        tvColour = (TextView) findViewById(R.id.et_colour);//颜色
        tvTailShape = (TextView) findViewById(R.id.tv_tail_shape);//尾巴形状
        tvAge = (TextView) findViewById(R.id.et_age);//年龄
        tvSex = (TextView) findViewById(R.id.tv_sex);//性别
        tvlnsuranceStart = (TextView) findViewById(R.id.tv_lnsurance_start);//开始时间
        tvlnsuranceend = (TextView) findViewById(R.id.tv_lnsurance_end);//结束时间
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:

                break;
            case R.id.rl_title_back:
                finish();
                break;
        }
    }


    private void getPetInsurance() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.getPetInsurance(trackerNo);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                InsuranceInformationActivity.this, null,
                                InsuranceInformationActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {//提交成功
                            PetInsur petInsurinfo = GsonParse.currentPetInsurParse(new String(response));
                            if (petInsurinfo != null) {
                                setData(petInsurinfo);

                            }
                        }
                        ToastUtil.show(InsuranceInformationActivity.this, obj.what);
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(InsuranceInformationActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });

    }

    private void setData(PetInsur petInsurinfo) {
        if (petInsurinfo == null) {
            return;
        }
        tvOrderCode.setText(petInsurinfo.no);//订单号
        tvUserName.setText(petInsurinfo.real_name);//姓名
        tvPhone.setText(petInsurinfo.mobile);//电话
        tvPetNickname.setText(petInsurinfo.dog_name);//昵称
        tvPetType.setText(petInsurinfo.type);//品种
        tvColour.setText(petInsurinfo.colour);//颜色
        if (petInsurinfo.tail_shape == 2) {//卷
            tvTailShape.setText(getString(R.string.volume));//尾巴形状
        } else if (petInsurinfo.tail_shape == 1) {//直
            tvTailShape.setText(getString(R.string.straight));//尾巴形状
        } else {//曲
            tvTailShape.setText(getString(R.string.song));//尾巴形状
        }
        tvAge.setText(petInsurinfo.age + getString(R.string.year_old));//年龄
        if (petInsurinfo.sex == 0) {//母
            tvSex.setText(getString(R.string.pet_female));//性别
        } else {//公
            tvSex.setText(getString(R.string.pet_male));//性别
        }
        String[] startsplit = petInsurinfo.start_time.split(" ");
        String[] endsplit = petInsurinfo.end_time.split(" ");
        if (startsplit.length > 0) {
            tvlnsuranceStart.setText(startsplit[0] + " " + getString(R.string.zero_hour));//开始时间
        }
        if (endsplit.length > 0) {
            tvlnsuranceend.setText(endsplit[0] + " " + getString(R.string.fill_time_end));//结束时间
        }
    }
}
