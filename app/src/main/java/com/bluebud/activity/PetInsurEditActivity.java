package com.bluebud.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.bluebud.activity.settings.TrackerEditActivity;
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
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

public class PetInsurEditActivity extends BaseActivity implements OnClickListener, OnProgressDialogClickListener {
    private RequestHandle requestHandle;
    private EditText etUserName;
    private EditText etPhone;
    private EditText etPetNickname;
    private EditText etPetType;
    private EditText etColour;

    private EditText etAge;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    private Button btn_male;
    private Button btn_female;
    private Tracker mCurTracker;
    private Context mContext;
    private String trackerNo;
    private int tail_shape = 1;
    private int sex = 0;
    private String real_name;
    private String mobile;
    private String petNickname;
    private String type;
    private String colour;
    private String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_pet_insur_edit);
        mContext = this;
        init();

    }

    private void init() {
        setBaseTitleText(R.string.insurance_information);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.submit1);
        setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        getBaseTitleLeftBack().setOnClickListener(this);
        mCurTracker = UserUtil.getCurrentTracker(this);
        if (mCurTracker != null) {
            trackerNo = mCurTracker.device_sn;
        }

        etUserName = (EditText) findViewById(R.id.et_user_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etPetNickname = (EditText) findViewById(R.id.et_pet_nick_name);
        etPetType = (EditText) findViewById(R.id.et_pet_type);
        etColour = (EditText) findViewById(R.id.et_colour);
        etAge = (EditText) findViewById(R.id.et_age);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn_male = (Button) findViewById(R.id.btn_male);
        btn_female = (Button) findViewById(R.id.btn_female);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn_male.setOnClickListener(this);
        btn_female.setOnClickListener(this);

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
            case R.id.rl_title_right_text://提交
                commiPetInsurance();
                break;
            case R.id.btn1://直
                LogUtil.i("第1个按扭");
                btn1.setBackgroundResource(R.drawable.pet_shape_pressed);
                btn2.setBackgroundResource(R.drawable.pet_shape_nomal);
                btn3.setBackgroundResource(R.drawable.pet_shape_nomal);
                tail_shape = 1;
                break;
            case R.id.btn2://卷
                LogUtil.i("第2个按扭");
                btn2.setBackgroundResource(R.drawable.pet_shape_pressed);
                btn1.setBackgroundResource(R.drawable.pet_shape_nomal);
                btn3.setBackgroundResource(R.drawable.pet_shape_nomal);
                tail_shape = 2;
                break;
            case R.id.btn3://曲
                LogUtil.i("第3个按扭");
                btn3.setBackgroundResource(R.drawable.pet_shape_pressed);
                btn2.setBackgroundResource(R.drawable.pet_shape_nomal);
                btn1.setBackgroundResource(R.drawable.pet_shape_nomal);
                tail_shape = 3;
                break;
            case R.id.btn_male://公
                LogUtil.i("公按扭");
                btn_male.setBackgroundResource(R.drawable.pet_shape_pressed);
                btn_female.setBackgroundResource(R.drawable.pet_shape_nomal);
                sex = 1;
                break;
            case R.id.btn_female://母
                LogUtil.i("母按扭");
                btn_female.setBackgroundResource(R.drawable.pet_shape_pressed);
                btn_male.setBackgroundResource(R.drawable.pet_shape_nomal);
                sex = 0;
                break;

        }
    }

    private void commiPetInsurance() {
        real_name = etUserName.getText().toString().trim();
        mobile = etPhone.getText().toString().trim();
        if (!Utils.isEmpty(mobile) && !Utils.isCorrectPhone(mobile)) {
            ToastUtil.show(this, R.string.input_tracker_contect);
            return;
        }
        petNickname = etPetNickname.getText().toString().trim();
        type = etPetType.getText().toString().trim();
        colour = etColour.getText().toString().trim();
        age = etAge.getText().toString().trim();
        setPetInsurance();
    }

    private void setPetInsurance() {

        String url = UserUtil.getServerUrl(this);
        //验证宠物验证码
        RequestParams params = HttpParams.setPetInsurance(trackerNo, UserSP.getInstance().getUserName(mContext), real_name, mobile, petNickname, type, colour, tail_shape, age, sex);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                PetInsurEditActivity.this, null,
                                PetInsurEditActivity.this);
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
                            Intent trackerEditIntent = new Intent(mContext,
                                    TrackerEditActivity.class);
                            trackerEditIntent.putExtra(Constants.EXTRA_TRACKER, mCurTracker);
                            trackerEditIntent.putExtra("fromwhere", Constants.MINEACTIVITY);
                            startActivity(trackerEditIntent);
                            //finish();
                        }

                        ToastUtil.show(PetInsurEditActivity.this, obj.what);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(PetInsurEditActivity.this,
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
