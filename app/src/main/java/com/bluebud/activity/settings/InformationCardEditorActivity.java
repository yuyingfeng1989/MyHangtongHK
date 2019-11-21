package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.LostCard2Car;
import com.bluebud.info.LostCard2People;
import com.bluebud.info.LostCard2Pet;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
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

//信息卡编辑页面

public class InformationCardEditorActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener {

    //    private EditText etContent;
    private RequestHandle requestHandle;
    private String mTitleName;
    private String mData;
    private EditText mEtContent;
    private int code;
    private String trackerNo;
    private int type;
    private LostCard2People lostCard2People;
    private LostCard2Pet lostCard2Pet;
    private LostCard2Car lostCard2Car;
    private Tracker tracker;
    private TextView tvUnit;
    private String simInfo;//sim卡号
    private int typeInfo = 0;//判断是哪个类型信息卡1、宠物 2、个人 3、车辆

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_information_card_editor);
        Intent intent = getIntent();
        tracker = (Tracker) intent.getSerializableExtra(Constants.EXTRA_TRACKER);
        mTitleName = intent.getStringExtra("Titlename");
        mData = intent.getStringExtra("data");
        code = intent.getIntExtra("code", 0);
        trackerNo = intent.getStringExtra("trackerNo");
        type = intent.getIntExtra("type", 0);
//        int typeInfo = intent.getIntExtra("typeInfo",0);
        simInfo = intent.getStringExtra("sim");//手表sim卡号

        if (type == 2) {
            lostCard2Pet = (LostCard2Pet) getIntent().getSerializableExtra("lostCard");
            typeInfo = 1;
        } else if (type == 7 || type == 5 || type == 1) {
            lostCard2People = (LostCard2People) getIntent().getSerializableExtra("lostCard");
            typeInfo = 2;
        } else {
            lostCard2Car = (LostCard2Car) getIntent().getSerializableExtra("lostCard");
            typeInfo = 3;
        }
        init();
    }

    private void init() {

        super.setBaseTitleText(mTitleName);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);


        super.setBaseTitleRightText(R.string.save);
        super.setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        mEtContent = (EditText) findViewById(R.id.et_content);
        tvUnit = (TextView) findViewById(R.id.tv_unit);
        setLimitInput();//各种输入限制
        //身高体重时显示单位
        if (4 == code) {
            tvUnit.setText("cm");
            tvUnit.setVisibility(View.VISIBLE);
            if (!Utils.isEmpty(mData)) {
                mEtContent.setText(mData.replaceAll("cm", ""));
            }
        } else if (5 == code || 8 == code) {
            tvUnit.setText("kg");
            tvUnit.setVisibility(View.VISIBLE);
            if (!Utils.isEmpty(mData)) {
                mEtContent.setText(mData.replaceAll("kg", ""));
            }
        } else if (code == 15) {
            mEtContent.setText(simInfo);
            tvUnit.setVisibility(View.GONE);
        } else {
            tvUnit.setVisibility(View.GONE);
            mEtContent.setText(mData);
        }
    }

    /**
     * 设置限制输入长度和类型
     */
    private void setLimitInput() {
        if (code == 3) //昵称
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        else if (code == 4) {//身高
            mEtContent.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        } else if (code == 5) {//体重
            mEtContent.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        } else if (code == 7)//品种
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        else if (code == 8)//宠物体重
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        else if (code == 9)//外貌特征
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        else if (code == 11)//车牌号
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        else if (code == 12)//车型
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
        else if (code == 14)//车架号
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)});
        else if (code == 15) {//设备sim卡
            mEtContent.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:// 返回
                finish();
                break;
            case R.id.rl_title_right_text:// 保存
                LogUtil.i("信息卡保存了");
                setLostCard();
                break;
        }
    }

    private void setLostCard() {
        String url = UserUtil.getServerUrl(this);
        String content = mEtContent.getText().toString().trim();
        if (Utils.isEmpty(content)) {
            content = "";
        }
        if (14 == code && content.length() != 17) {
            ToastUtil.show(this, R.string.car_vin_reminder);
            return;
        }

        switch (code) {
            case 3:// 昵称//昵称
                if (type == 7 || type == 5 || type == 1) {
                    lostCard2People.nickname = content;
                } else if (type == 2) {
                    lostCard2Pet.nickname = content;
                }
                break;
            case 4:// 身高

                lostCard2People.human_height = content;

                break;
            case 5:// 体重

                lostCard2People.human_weight = content;

                break;
            case 6:// 步长
                lostCard2People.human_step = content;

                break;
            case 7:// 宠物的品种
                lostCard2Pet.pet_breed = content;
                break;
            case 8:// 宠物的体重
                lostCard2Pet.pet_weight = content;
                break;
            case 9:// 外貌特证

                if (type == 2) {
                    lostCard2Pet.pet_feature = content;
                } else {
                    lostCard2People.human_feature = content;
                }
                break;
            case 10:// 紧急联络人
                if (type == 2) {
                    lostCard2Pet.mobile1 = content;
                } else {
                    lostCard2People.mobile1 = content;
                }
                break;
            case 11:// 车牌号
                if (type == 4) {
                    lostCard2Car.nickname = content;

                } else if (type == 3) {

                    lostCard2Car.nickname = content;
                } else {

                    lostCard2Car.nickname = content;
                }
                break;
            case 12:// 车的型号
                if (type == 4) {
                    lostCard2Car.moto_type = content;
                } else if (type == 3) {
                    lostCard2Car.car_type = content;
                } else {
                    lostCard2Car.obd_type = content;
                }
                break;
            case 13:// 车的紧急联络人
                lostCard2Car.mobile1 = content;
                break;
            case 14://车架号vin
                if (type == 6)
                    lostCard2Car.car_vin = content;
                break;

            default:
                break;
        }
        // 昵称不能为空
        boolean isNoNickName = true;
        if (type == 7 || 1 == type || 5 == type) {
            if (lostCard2People != null && Utils.isEmpty(lostCard2People.nickname)) {// || lostCard2People.nickname == "")
                isNoNickName = true;
            } else {
                isNoNickName = false;
            }
        } else if (2 == type) {
            if (lostCard2People != null && Utils.isEmpty(lostCard2Pet.nickname)) {//|| lostCard2Pet.nickname == ""
                isNoNickName = true;
            } else {
                isNoNickName = false;
            }
        } else if (3 == type || 4 == type || 6 == type) {
            if (lostCard2People != null && Utils.isEmpty(lostCard2Car.nickname)) {//|| lostCard2Car.nickname == ""
                isNoNickName = true;
            } else {
                isNoNickName = false;
            }
        }
        if (isNoNickName) {
            ToastUtil.show(InformationCardEditorActivity.this, getString(R.string.no_nickname));
            return;
        }

        if (10 == code) {// 不是正确的手机号码
            if (Utils.serialNumberRange7181(type, tracker.product_type)) {
                if (!Utils.isCorrectMobile(lostCard2People.mobile1)) {
                    ToastUtil.show(InformationCardEditorActivity.this,
                            getString(R.string.phone_error));
                    return;
                }
            }
        }

        LogUtil.i("设备号：" + trackerNo);

        RequestParams params = new RequestParams();

        if (code == 15) {//手表sim卡信息
//            if (ChatUtil.isPhone(content)) {
            params = HttpParams.setSimCard(trackerNo, content);
            LogUtil.e("content==" + content + "trackerNo==" + trackerNo + "simInfo==" + simInfo);
//            } else {
//                ToastUtil.show(InformationCardEditorActivity.this, getString(R.string.phone_error));
//                return;
//            }
        } else if (type == 7 || 1 == type || 5 == type) {
            params = HttpParams.setLostCard2People(trackerNo, lostCard2People);
            LogUtil.i("上传信息卡" + lostCard2People.toString());
        } else if (2 == type) {
            params = HttpParams.setLostCard2Pet(trackerNo, lostCard2Pet);
            LogUtil.i("上传信息卡" + lostCard2Pet.toString());
        } else if (3 == type) {
            params = HttpParams.setLostCard2Car(trackerNo, lostCard2Car);
            LogUtil.i("上传信息卡" + lostCard2Car.toString());
        } else if (4 == type) {
            params = HttpParams.setLostCard2Motor(trackerNo, lostCard2Car);
            LogUtil.i("上传信息卡" + lostCard2Car.toString());

        } else if (6 == type) {
            params = HttpParams.setLostCard2Obd(trackerNo, lostCard2Car);
            LogUtil.i("上传信息卡" + lostCard2Car.toString());
        }

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        // TODO Auto-generated method stub
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                InformationCardEditorActivity.this, null,
                                InformationCardEditorActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        // TODO Auto-generated method stub
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        // guoqz add 20160309.
                        if (0 == obj.code) {
                            saveTrackerInfoSuccess();
                            ToastUtil.show(InformationCardEditorActivity.this, obj.what);
                        } else {
                            ToastUtil.show(InformationCardEditorActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        // TODO Auto-generated method stub
                        super.onFailure(statusCode, headers, errorResponse,throwable);
                        ToastUtil.show(InformationCardEditorActivity.this,R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        // TODO Auto-generated method stub
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                        finish();
                    }
                });
    }

    /**
     * 信息设置成功
     */
    private void saveTrackerInfoSuccess() {

        if (code != 15) {
            if (type == 7 || type == 1 || type == 5) {
                tracker.nickname = lostCard2People.nickname;
            } else if (type == 2) {
                tracker.nickname = lostCard2Pet.nickname;
            } else if (type == 3) {
                tracker.nickname = lostCard2Car.nickname;
            } else if (type == 4) {
                tracker.nickname = lostCard2Car.nickname;
            } else {
                tracker.nickname = lostCard2Car.nickname;
            }
        }

        LogUtil.v("save nickname is " + tracker.nickname);
        UserUtil.saveTracker(InformationCardEditorActivity.this, tracker);
        if (code == 3) {
            sendBroadcast(new Intent(Constants.ACTION_TRACTER_NICKNAME_CHANGE));
        }
        Intent intent = new Intent();
        intent.putExtra("data", mEtContent.getText().toString().trim());
        if (code == 15)
            intent.putExtra("typeInfo", typeInfo);
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public void onProgressDialogBack() {

        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

}
