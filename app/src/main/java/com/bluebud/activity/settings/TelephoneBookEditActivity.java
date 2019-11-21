package com.bluebud.activity.settings;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bluebud.activity.BaseActivity;
import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.CircleImageView;
import com.bluebud.view.ClearEditText;
import com.bluebud.view.RecycleViewImage;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


public class TelephoneBookEditActivity extends BaseActivity implements OnClickListener, OnProgressDialogClickListener {

    //    private RequestHandle requestHandle;
    private CircleImageView ivImage;
    private ClearEditText etName;
    private ClearEditText etPhone;
    private String name;
    //    private int image;
    private String phone;
    private boolean isNewDevice;
    private int position;
    private Tracker mTrakcer;
    private String sTrackerNo;
    private String sTelephones;
    //    private ArrayList<String> etNickNames;
//    private ArrayList<String> etTelephones;
    private String sTelephones2;
    private int[] image1 = new int[]{R.drawable.img_dad, R.drawable.img_mom,
            R.drawable.img_grandpa, R.drawable.img_grandma,
            R.drawable.img_grandfather, R.drawable.img_grandmother,
            R.drawable.elder_brother, R.drawable.elder_syster
            , R.drawable.younger_brother, R.drawable.younger_sister};
    private int protocol_type = 0;
    //	private String product_type;
    private Context mContext;
    private int lastTag = 0;
    private int currentTag = 0;
    private String photoData = ",,,,,,,,,";
    private RelativeLayout mRl720;
    private HorizontalScrollView mHv770;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_telephone_book_edit);
        mContext = this;
        getData();
        init();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            phone = intent.getStringExtra("phone");
            isNewDevice = intent.getBooleanExtra("isNewDevice", false);
            position = intent.getIntExtra("position", 0);
            sTelephones2 = intent.getStringExtra("sTelephones");
            photoData = intent.getStringExtra("photoData");
            LogUtil.i("sTelephones2=" + sTelephones2 + ",postion=" + position + "photoData:" + photoData);
            currentTag = Utils.getPhotoSubscript(photoData, position);//获取当前选择的是哪个头像
            lastTag = currentTag;

        }
    }

    private void init() {
        setBaseTitleText(R.string.contact_add);
        super.setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        super.setBaseTitleRightText(R.string.submit1);
        super.setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        mTrakcer = UserUtil.getCurrentTracker(this);
        if (mTrakcer != null) {
            sTrackerNo = mTrakcer.device_sn;
            protocol_type = mTrakcer.protocol_type;
//			product_type = mTrakcer.product_type;
            //product_type = "15";
        }
        mRl720 = (RelativeLayout) findViewById(R.id.rl_720);
        mHv770 = (HorizontalScrollView) findViewById(R.id.hv_770);
        if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
            mRl720.setVisibility(View.GONE);
            mHv770.setVisibility(View.VISIBLE);
        } else {
            mRl720.setVisibility(View.VISIBLE);
            mHv770.setVisibility(View.GONE);
        }
        ivImage = (CircleImageView) findViewById(R.id.iv_tracker_image);//720手表默认头像view
        etName = (ClearEditText) findViewById(R.id.et_name);
        etPhone = (ClearEditText) findViewById(R.id.et_phone);
        new ChatUtil().editInputLimit(this, etName, 20);//昵称限制 只输入20个字符

        etPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//电话号限制
        etPhone.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        ivImage.setBackgroundResource(image1[position]);////720手表头像
        etName.setText(name);
        etPhone.setText(phone);
        if (isNewDevice) {
            etName.setEnabled(true);
        } else {
            etName.setEnabled(false);
        }
        if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {//770手表
            etName.setEnabled(true);

        } else {
            if (position < 2) {
                etName.setEnabled(false);
            }
        }

        if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
            init770View();//初始化770
        }
    }

    private void init770View() {
        final LinearLayout mLlAddView = (LinearLayout) findViewById(R.id.ll_add_view);
        for (int i = 0; i < image1.length; i++) {
            final RecycleViewImage recycleViewImage = new RecycleViewImage(mContext);
            recycleViewImage.setBackgroundResource(image1[i]);
            recycleViewImage.setTag(i);
            if (currentTag == i) {
                recycleViewImage.settBorderColor1(mContext.getResources().getColor(R.color.bg_theme));
            } else {
                recycleViewImage.settBorderColor1(mContext.getResources().getColor(
                        R.color.green_circle_solid));
            }
            recycleViewImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.i("你点了第几个：" + v.getTag());
                    currentTag = (Integer) v.getTag();
                    recycleViewImage.setSelected(true);
                    recycleViewImage.setSelected1();
                    if (lastTag != -1) {
                        if (currentTag != lastTag) {
                            mLlAddView.getChildAt(lastTag).setSelected(false);
                            ((RecycleViewImage) mLlAddView.getChildAt(lastTag)).setSelected1();
                        }
                    }
                    LogUtil.i("lastTag:" + lastTag);
                    lastTag = (Integer) v.getTag();
                }
            });
            mLlAddView.addView(recycleViewImage);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.btn_submit:
                if (UserUtil.isGuest(this)) {
                    ToastUtil.show(this, R.string.guest_no_set);
                    return;
                }
                break;
            case R.id.rl_title_right_text:
                confirm();
                break;
        }
    }


    private void confirm() {
        if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {//770手表选择图片
            photoData = Utils.getPhotoString(photoData, position, currentTag);
        }
        String msg = etPhone.getText().toString().trim();
        if (!Utils.isEmpty(msg) && !Utils.isCorrectPhone(msg)) {
            ToastUtil.show(this, R.string.input_tracker_contect);
            return;
        }
        String str = sTelephones2;
        if (!isNewDevice) {
            // 旧设备
            LogUtil.i("str旧设备：" + str);
            String[] strs = str.split(",", 10);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 10; i++) {
                int index = strs[i].indexOf(":");
                if (index != -1) {
                    String strtemp = strs[i].substring(0, index);
                    if (i == position) {
                        if (position == strs.length - 1) {
                            strs[i] = strtemp + ":" + etPhone.getText().toString().trim();
                        } else {
                            strs[i] = strtemp + ":" + etPhone.getText().toString().trim() + ",";
                        }

                        LogUtil.i("new strs[" + i + "]=" + strs[i].toString());
                    } else {
                        if (i == strs.length - 1) {
                            strs[i] = strs[i];
                        } else {
                            strs[i] = strs[i] + ",";
                        }
                    }

                } else {
                    //etTelephones.get(i).setText(strs[i]);
                    if (i == position) {
                        if (position == strs.length - 1) {
                            strs[i] = etPhone.getText().toString().trim();
                        } else {
                            strs[i] = etPhone.getText().toString().trim() + ",";
                        }

                        LogUtil.i("new strs[" + i + "]=" + strs[i].toString());
                    } else {
                        if (i == strs.length - 1) {
                            strs[i] = strs[i];
                        } else {
                            strs[i] = strs[i] + ",";
                        }
                    }

                }

                sb.append(strs[i]);
            }

            LogUtil.i("旧设备new end str sb=" + sb.toString());
            sTelephones = sb.toString();
        } else {
            // 新设备
//			str = "昵称1:,昵称2:电话号码2,:,:电话号码4";
            // guoqz add 20160302.
            // 解析通讯录昵称及电话号码
            // 数据格式举例: 昵称1:电话号码1,昵称2:电话号码2,昵称3:电话号码3,昵称4:电话号码4
            LogUtil.i("str新设备：" + str);
            String[] strs = str.split(",");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < strs.length; i++) {
                LogUtil.i("strs[" + i + "]=" + strs[i].toString());
                int index = strs[i].indexOf(":");
                if (-1 == index) {
                    // 针对接口之前的数据(无昵称)

                    if (i == position) {
                        if (position == strs.length - 1) {
                            strs[i] = etPhone.getText().toString().trim();
                        } else {
                            strs[i] = etPhone.getText().toString().trim() + ",";
                        }

                        LogUtil.i("new strs[" + i + "]=" + strs[i].toString());
                    } else {
                        if (i == strs.length - 1) {
                            strs[i] = strs[i];
                        } else {
                            strs[i] = strs[i] + ",";
                        }
                    }

                } else {
                    if (1 == strs[i].length()) {
                        // 第三种情况：昵称电话号码为空   ":"
                        //etNickNames.get(i).setText("");
                        //etTelephones.get(i).setText("");
                        String phone = etPhone.getText().toString().trim();
                        String nameold = etName.getText().toString().trim();
                        String name = nameold.replace(":", "");
                        if (i == position) {
                            if (position == strs.length - 1) {
                                strs[i] = name + ":" + phone;
                            } else {
                                strs[i] = name + ":" + phone + ",";
                            }
                        } else {
                            if (i == strs.length - 1) {
                                strs[i] = strs[i];
                            } else {
                                strs[i] = strs[i] + ",";
                            }
                        }

                    } else if (strs[i].length() > 1) {
                        if (1 == (index + 1)) {
                            // 第四种情况：昵称为空电话号码不为空   ":电话号码"
                            //etNickNames.get(i).setText("");
                            //etTelephones.get(i).setText(strs[i].substring(1, strs[i].length()));
                            String phone = etPhone.getText().toString().trim();
                            String nameold = etName.getText().toString().trim();
                            String name = nameold.replace(":", "");
                            if (i == position) {
                                if (position == strs.length - 1) {
                                    strs[i] = name + ":" + phone;
                                } else {
                                    strs[i] = name + ":" + phone + ",";
                                }
                            } else {
                                if (i == strs.length - 1) {
                                    strs[i] = strs[i];
                                } else {
                                    strs[i] = strs[i] + ",";
                                }
                            }

                        } else if (strs[i].length() == (index + 1)) {
                            // 第五种情况：昵称不为空电话号码为空   "昵称:"
                            //	etNickNames.get(i).setText(strs[i].substring(0, strs[i].length()-1));
                            //	etTelephones.get(i).setText("");
                            String phone = etPhone.getText().toString().trim();
                            String nameold = etName.getText().toString().trim();
                            String name = nameold.replace(":", "");
                            if (i == position) {
                                if (position == strs.length - 1) {
                                    strs[i] = name + ":" + phone;
                                } else {
                                    strs[i] = name + ":" + phone + ",";
                                }
                            } else {
                                if (i == strs.length - 1) {
                                    strs[i] = strs[i];
                                } else {
                                    strs[i] = strs[i] + ",";
                                }
                            }
                        } else {
                            // 第六种情况：昵称和电话号码都不为空   "昵称:电话号码"
                            //	etNickNames.get(i).setText(strs[i].substring(0, strs[i].indexOf(":")));
                            //	etTelephones.get(i).setText(strs[i].substring(strs[i].indexOf(":")+1, strs[i].length()));
                            String phone = etPhone.getText().toString().trim();
                            String nameold = etName.getText().toString().trim();
                            String name = nameold.replace(":", "");
                            if (i == position) {
                                if (position == strs.length - 1) {
                                    strs[i] = name + ":" + phone;
                                } else {
                                    strs[i] = name + ":" + phone + ",";
                                }
                            } else {
                                if (i == strs.length - 1) {
                                    strs[i] = strs[i];
                                } else {
                                    strs[i] = strs[i] + ",";
                                }
                            }
                        }
                    }
                    sb.append(strs[i]);
                }
                //LogUtil.i("new str sb"+sb.toString());
            }
            LogUtil.i("新设备new end str sb=" + sb.toString());
            sTelephones = sb.toString();
        }
        addPhoneBook();
    }


    private void addPhoneBook() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params;
        if (!isNewDevice) {  //旧设备
            params = HttpParams.addPhoneBook(sTrackerNo, sTelephones, photoData, 1);
        } else {  // 新设备
            params = HttpParams.addNamePhonebook(sTrackerNo, sTelephones, photoData, 1);
        }

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(TelephoneBookEditActivity.this, null, TelephoneBookEditActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {
                            Intent intent = new Intent();
                            LogUtil.i("回调前sTelephones=" + sTelephones);
                            intent.putExtra("sTelephones", sTelephones);
                            intent.putExtra("photoData", photoData);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        ToastUtil.show(TelephoneBookEditActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TelephoneBookEditActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    @Override
    public void onProgressDialogBack() {
    }
}
