package com.bluebud.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

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

public class InformationCardEditorActivity extends BaseActivity implements OnClickListener,
        OnProgressDialogClickListener {

    //	private EditText etContent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_information_card_editor);
        tracker = UserUtil.getCurrentTracker(InformationCardEditorActivity.this);
        mTitleName = getIntent().getStringExtra("Titlename");
        mData = getIntent().getStringExtra("data");
        code = getIntent().getIntExtra("code", 0);
        trackerNo = getIntent().getStringExtra("trackerNo");
        type = getIntent().getIntExtra("type", 0);
        if (type == 2) {
            lostCard2Pet = (LostCard2Pet) getIntent().getSerializableExtra("lostCard");
        } else if (type == 5 || type == 1) {
            lostCard2People = (LostCard2People) getIntent().getSerializableExtra("lostCard");
        } else {
            lostCard2Car = (LostCard2Car) getIntent().getSerializableExtra("lostCard");
        }
        init();
    }

    private void init() {
        super.setBaseTitleText(mTitleName);
        super.setBaseTitleVisible(View.VISIBLE);
        super.getBaseTitleLeftBack().setOnClickListener(this);
        super.setBaseTitleRightBtnText(R.string.save);
        super.setBaseTitleRightBtnVisible(View.VISIBLE);
        super.getBaseTitleRightBtn().setOnClickListener(this);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mEtContent.setText(mData);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back://返回
                finish();
                break;
            case R.id.btn_title_right://保存
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
        switch (code) {
            case 3://昵称//昵称
                if (type == 5 || type == 1) {
                    lostCard2People.nickname = content;
                } else if (type == 2) {
                    lostCard2Pet.nickname = content;
                }
                break;
            case 4://身高
                lostCard2People.human_height = content;
                break;
            case 5://体重
                lostCard2People.human_weight = content;

                break;
            case 6://步长
                lostCard2People.human_step = content;

                break;
            case 7://宠物的品种
                lostCard2Pet.pet_breed = content;
                break;
            case 8://宠物的体重
                lostCard2Pet.pet_weight = content;
                break;
            case 9://外貌特证

                if (type == 2) {
                    lostCard2Pet.pet_feature = content;
                } else {
                    lostCard2People.human_feature = content;
                }
                break;
            case 10://紧急联络人
                if (type == 2) {
                    lostCard2Pet.mobile1 = content;
                } else {
                    lostCard2People.mobile1 = content;
                }
                break;
            case 11://车牌号
                if (type == 4) {
                    lostCard2Car.motor_no = content;
                } else {
                    lostCard2Car.car_no = content;
                }
                break;
            case 12://车的型号
                if (type == 4) {
                    lostCard2Car.moto_type = content;
                } else {
                    lostCard2Car.car_type = content;
                }
                break;
            case 13://车的紧急联络人
                lostCard2Car.mobile1 = content;
                break;
            default:
                break;
        }

        RequestParams params = new RequestParams();
        if (1 == type || 5 == type) {
            params = HttpParams.setLostCard2People(trackerNo,
                    lostCard2People);
        } else if (2 == type) {
            params = HttpParams.setLostCard2Pet(trackerNo, lostCard2Pet);
        } else {
            params = HttpParams.setLostCard2Car(trackerNo, lostCard2Car);
        }

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                InformationCardEditorActivity.this, null,
                                InformationCardEditorActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        // guoqz add 20160309.
                        if (0 == obj.code) {
                            saveTrackerInfoSuccess();
                            ToastUtil.show(InformationCardEditorActivity.this, obj.what);
                        } else {
                            ToastUtil.show(InformationCardEditorActivity.this, R.string.offline_info_card);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(InformationCardEditorActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                        finish();
                    }
                });
    }

//	private void setLostCard243() {
//		String url = UserUtil.getServerUrl(this);
//		RequestParams params = new RequestParams();
//		String content=mEtContent.getText().toString().trim();
//		params=HttpParams.setLostCard(trackerNo, code, type, content);
//
//		requestHandle = HttpClientUsage.getInstance().post(this, url, params,
//				new AsyncHttpResponseHandlerReset() {
//					@Override
//					public void onStart() {
//						super.onStart();
//						ProgressDialogUtil.showNoCanceled(
//								InformationCardEditorActivity.this, null,
//								InformationCardEditorActivity.this);
//					}
//
//					@Override
//					public void onSuccess(int statusCode, Header[] headers,
//							byte[] response) {
//						super.onSuccess(statusCode, headers, response);
//						ReBaseObj obj = GsonParse.reBaseObjParse(new String(
//								response));
//						if (obj == null)
//							return;
//
//						// guoqz add 20160309.
//						if(0 == obj.code){
//							//saveTrackerInfoSuccess();
//							ToastUtil.show(InformationCardEditorActivity.this, obj.what);
//						} else{
//							ToastUtil.show(InformationCardEditorActivity.this, R.string.offline_info_card);
//						}
//					}
//
//					@Override
//					public void onFailure(int statusCode, Header[] headers,
//							byte[] errorResponse, Throwable throwable) {
//						super.onFailure(statusCode, headers, errorResponse,
//								throwable);
//						ToastUtil.show(InformationCardEditorActivity.this,
//								R.string.net_exception);
//					}
//
//					@Override
//					public void onFinish() {
//						super.onFinish();
//						ProgressDialogUtil.dismiss();
//						finish();
//					}
//				});
//	}

    private void saveTrackerInfoSuccess() {

        if (type == 1 || type == 5) {

            tracker.nickname = lostCard2People.nickname;
        } else if (type == 2) {
            tracker.nickname = lostCard2Pet.nickname;
        } else if (type == 3) {
            tracker.nickname = lostCard2Car.car_no;
        } else if (type == 4) {
            tracker.nickname = lostCard2Car.motor_no;
        }


        UserUtil.saveTracker(InformationCardEditorActivity.this, tracker);

        sendBroadcast(new Intent(Constants.ACTION_TRACTER_NICKNAME_CHANGE));

        Intent intent = new Intent();
        intent.putExtra("data", mEtContent.getText().toString().trim());
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
