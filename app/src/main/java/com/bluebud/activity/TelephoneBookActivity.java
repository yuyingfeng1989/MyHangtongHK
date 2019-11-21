package com.bluebud.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bluebud.activity.settings.TelephoneBookEditActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.TelephoneInfo;
import com.bluebud.info.Tracker;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.CircleImageView;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class TelephoneBookActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener {
    private Spinner spinner;
    private LinearLayout llTelephone;

    private List<TextView> etTelephones = new ArrayList<TextView>();
    private List<TextView> etNickNames = new ArrayList<TextView>();
    private List<CircleImageView> imageViews = new ArrayList<CircleImageView>();

    private Tracker mTrakcer;
    private String sTrackerNo = "";

    private String sTelephones = "";
    private String[] arrName;
    //    private String[] arrNameEmergency;
    private int position = 0;


    private int[] image1 = new int[]{R.drawable.img_dad, R.drawable.img_mom,
            R.drawable.img_grandpa, R.drawable.img_grandma,
            R.drawable.img_grandfather, R.drawable.img_grandmother,
            R.drawable.elder_brother, R.drawable.elder_syster
            , R.drawable.younger_brother, R.drawable.younger_sister};

    private int[] image = new int[10];
    private int[] defaultImage = new int[]{R.drawable.img_defaulthead_628};
    private int[] defaultImage1 = new int[]{R.drawable.img_defaulthead_628, R.drawable.img_defaulthead_628,
            R.drawable.img_defaulthead_628,
            R.drawable.img_defaulthead_628,
            R.drawable.img_defaulthead_628,
            R.drawable.img_defaulthead_628,
            R.drawable.img_defaulthead_628,
            R.drawable.img_defaulthead_628,
            R.drawable.img_defaulthead_628,
            R.drawable.img_defaulthead_628};

    // guoqz add 20160301.
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapterTimeZone;

    private TelephoneInfo telephoneInfo;

    private RequestHandle requestHandle;

    // guoqz add 20160318.
    private boolean isNewDevice = false;
    private String photoData = ",,,,,,,,,";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_telephone_book);

        mTrakcer = UserUtil.getCurrentTracker(this);
        if (mTrakcer != null) {
            sTrackerNo = mTrakcer.device_sn;
            protocol_type = mTrakcer.protocol_type;
            product_type = mTrakcer.product_type;
        }
        telephoneInfo = new TelephoneInfo();
        init();
        getPhoneBook();
    }

    public void init() {
        setBaseTitleText(R.string.telephone_book);
        setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        setBaseTitleRightText(R.string.save);
        setBaseTitleRightTextVisible(View.GONE);
        getBaseTitleRightText().setOnClickListener(this);

        llTelephone = (LinearLayout) findViewById(R.id.ll_telephone);
        spinner = (Spinner) findViewById(R.id.spinner);
        if ((protocol_type == 5 && "15".equals(product_type)) || (protocol_type == 7 && "26".equals(product_type))) {//970和990老人手表
            arrName = getResources().getStringArray(R.array.contacts_name1);
        } else {
            arrName = getResources().getStringArray(R.array.contacts_name);
        }

        if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
            image = defaultImage1;
        } else {
            image = image1;
        }
//        arrNameEmergency = new String[]{arrName[0], arrName[1], arrName[2],
//                arrName[3], arrName[4], arrName[5]};

        // guoqz add 20160301.
        list.add(arrName[0]);
        list.add(arrName[1]);

        // guoqz add 20160301.
        adapterTimeZone = new ArrayAdapter<String>(this,
                R.layout.layout_spinner, list);

        adapterTimeZone
                .setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        spinner.setAdapter(adapterTimeZone);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                position = arg2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        // 添加通讯录联系人布局及点击事件
        for (int i = 0; i < arrName.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_telephone_book_item, null);
            CircleImageView ivTrackerImage = (CircleImageView) view.findViewById(R.id.iv_tracker_image);
            imageViews.add((CircleImageView) view.findViewById(R.id.iv_tracker_image));
            ivTrackerImage.setImageResource(image[i]);//image[position]
            llPrompt = (LinearLayout) view.findViewById(R.id.ll_prompt);// 提示
            TextView tvPrompt = (TextView) view.findViewById(R.id.tv_prompt);// 提示语
            if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
                tvPrompt.setText(getString(R.string.telephone_watch_book_prompt));
            } else if (protocol_type == 1) {//720提示
                tvPrompt.setText(getString(R.string.telephone_watch_book_prompt720));
            } else {
                tvPrompt.setText(getString(R.string.telephone_book_prompt));
            }

            if (i == arrName.length - 1) {// 显示提示用语
                llPrompt.setVisibility(View.VISIBLE);
                LogUtil.i("显示提示语" + i);
            } else {
                llPrompt.setVisibility(View.GONE);// 不显示提示用语
                LogUtil.i("不显示提示语" + i);
            }
            etNickNames.add((TextView) view.findViewById(R.id.tv_nickname));
            etTelephones.add((TextView) view.findViewById(R.id.tv_account));
            etNickNames.get(i).setText(arrName[i]);

            final RelativeLayout ivTelephone = (RelativeLayout) view
                    .findViewById(R.id.rl_account_information_card);
            ivTelephone.setTag(i);
            ivTelephone.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    int position = Integer.parseInt(ivTelephone.getTag()
                            .toString());
                    LogUtil.i("position:" + position);

                    if (!isNewDevice) {
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < etTelephones.size(); i++) {
                            String msg = etTelephones.get(i).getText().toString().trim();
                            if (i == etTelephones.size() - 1) {
                                sb.append(msg);
                            } else {
                                sb.append(msg + ",");
                            }
                        }

                        sTelephones = sb.toString();
                        LogUtil.i("旧设备ping" + sTelephones);

                    } else {


                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < etTelephones.size(); i++) {
                            String msg = etTelephones.get(i).getText().toString().trim();
                            // guoqz add 20160302.
                            String nick = etNickNames.get(i).getText().toString().trim();
                            LogUtil.i("msg=" + msg + ",nick=" + nick);
                            if (i == etTelephones.size() - 1) {
                                sb.append(nick + ":" + msg);
                            } else {
                                sb.append(nick + ":" + msg + ",");
                            }
                        }

                        sTelephones = sb.toString();
                        LogUtil.i("新设备ping:" + sTelephones);
                    }
                    Intent intent = new Intent(TelephoneBookActivity.this, TelephoneBookEditActivity.class);
                    intent.putExtra("image", image[position]);
                    intent.putExtra("sTelephones", sTelephones);
                    intent.putExtra("position", position);
                    intent.putExtra("photoData", photoData);

                    intent.putExtra("isNewDevice", isNewDevice);
                    intent.putExtra("name", etNickNames.get(position).getText().toString().trim());
                    intent.putExtra("phone", etTelephones.get(position).getText().toString().trim());

                    startActivityForResult(intent, 1);
                }
            });
            llTelephone.addView(view);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String sTelephones = data.getStringExtra("sTelephones");
            photoData = data.getStringExtra("photoData");
            LogUtil.i("回调sTelephones=" + sTelephones);
            if (sTelephones != null) {
                setTelephone(sTelephones);
            }

            if (!Utils.isEmpty(photoData)) {
                if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
                    setPhotoAll(photoData);
                }
            }
        }
    }


    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String str = etNickNames.get(2).getText().toString();
            adapterTimeZone.clear();
            adapterTimeZone.add(arrName[0]);
            adapterTimeZone.add(arrName[1]);
            if (str.length() > 0) {
                adapterTimeZone.add(str);
            }

        }

        @Override
        public void afterTextChanged(Editable arg0) {

        }
    };
    private LinearLayout llPrompt;
    private int protocol_type = 0;
    private String product_type;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:


                finish();
                break;
            case R.id.rl_title_right_text:
                if (Utils.isOperate(this, mTrakcer)) {
                    confirm();
                }
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

    private void callTelephone(String sim) {
        Intent callIntent = new Intent();
        callIntent.setAction(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + sim));
        startActivity(callIntent);
    }

    private void setTelephone(String str) {

        if (isNewDevice) {
            // 添加第三个联系人昵称编辑对话框监听
            //etNickNames.get(2).addTextChangedListener(textWatcher);
            for (int i = 0; i < etNickNames.size(); i++) {
                etNickNames.get(i).setText(arrName[i]);
                etNickNames.get(i).setEnabled(false);
                //adapterTimeZone.add(arrName[i]);
            }
        } else {
            adapterTimeZone.clear();
            for (int i = 0; i < etNickNames.size(); i++) {
                etNickNames.get(i).setText(arrName[i]);
                etNickNames.get(i).setEnabled(false);
                //adapterTimeZone.add(arrName[i]);
            }
            //zms add 20160321
            for (int i = 0; i < 6; i++) {
                adapterTimeZone.add(arrName[i]);
            }
        }

        if (0 < telephoneInfo.adminIndex) {
            spinner.setSelection(telephoneInfo.adminIndex - 1);
        } else {
            spinner.setSelection(0);
        }

        if (!isNewDevice) {
            // 旧设备
            LogUtil.i("str旧设备：" + str);
            String[] strs = str.split(",", 10);
            for (int i = 0; i < 10; i++) {
                if (etTelephones.size() < (i + 1))
                    return;
                int index = strs[i].indexOf(":");
                if (index != -1) {
                    String strtemp = strs[i].substring(index + 1, strs[i].length());
                    etTelephones.get(i).setText(strtemp);
                } else {
                    etTelephones.get(i).setText(strs[i]);
                }
            }
        } else {
            // 新设备
//			str = "昵称1:,昵称2:电话号码2,:,:电话号码4";
            // guoqz add 20160302.
            // 解析通讯录昵称及电话号码
            // 数据格式举例: 昵称1:电话号码1,昵称2:电话号码2,昵称3:电话号码3,昵称4:电话号码4
            LogUtil.i("str新设备：" + str);
            String[] strs = str.split(",");
            for (int i = 0; i < strs.length; i++) {
                if (etTelephones.size() < (i + 1))
                    return;
                int index = strs[i].indexOf(":");
                if (-1 == index) {
                    // 针对接口之前的数据(无昵称)
                    if (strs[i].length() > 0) {
                        // 第一种情况：电话号码不为空   "电话号码"
                        //etNickNames.get(i).setText("");
                        etTelephones.get(i).setText(strs[i]);
                    } else {
                        // 第二种情况：电话号码为空   ""
                        //etNickNames.get(i).setText("");
                        etTelephones.get(i).setText("");
                    }
                } else {
                    if (1 == strs[i].length()) {
                        // 第三种情况：昵称电话号码为空   ":"
                        //etNickNames.get(i).setText("");
                        etTelephones.get(i).setText("");
                    } else if (strs[i].length() > 1) {
                        if (1 == (index + 1)) {
                            // 第四种情况：昵称为空,电话号码不为空   ":电话号码"
                            //etNickNames.get(i).setText("");
                            etTelephones.get(i).setText(strs[i].substring(1, strs[i].length()));
                        } else if (strs[i].length() == (index + 1)) {
                            // 第五种情况：昵称不为空,电话号码为空   "昵称:"
                            //etNickNames.get(i).setText(strs[i].substring(0, strs[i].length()-1));
                            //etNickNames.get(i).setText(arrName[i]);
                            //etNickNames.get(i).setText(strs[i].substring(0, strs[i].indexOf(":")));
                            etTelephones.get(i).setText("");
                        } else {
                            // 第六种情况：昵称和电话号码都不为空   "昵称:电话号码"
                            etNickNames.get(i).setText(strs[i].substring(0, strs[i].indexOf(":")));
                            etTelephones.get(i).setText(strs[i].substring(strs[i].indexOf(":") + 1, strs[i].length()));
                        }
                    }
                }
            }

            if (!(protocol_type == 5 || protocol_type == 6 || protocol_type == 7)) {//不是老人手表
                etNickNames.get(0).setText(arrName[0]);
                etNickNames.get(1).setText(arrName[1]);
            }

        }

    }

    private void confirm() {
        int iEmpty = 0;
        for (int i = 0; i < etTelephones.size(); i++) {
            String msg = etTelephones.get(i).getText().toString().trim();
            if (Utils.isEmpty(msg)) {
                iEmpty = iEmpty + 1;
            }
        }

        if (etTelephones.size() == iEmpty) {
            ToastUtil.show(this, R.string.input_tracker_phone1_null);
            return;
        }

        for (int i = 0; i < etTelephones.size(); i++) {
            String msg = etTelephones.get(i).getText().toString().trim();
            if (!Utils.isEmpty(msg) && !Utils.isCorrectPhone(msg)) {
                ToastUtil.show(this, R.string.input_tracker_contect);
                return;
            }
        }

        if (Utils.isEmpty(etTelephones.get(position).getText().toString().trim())) {
            ToastUtil.show(this, R.string.emergency_contact_empty);
            return;
        }

        // guoqz add 20160318.
        if (!isNewDevice) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < etTelephones.size(); i++) {
                String msg = etTelephones.get(i).getText().toString().trim();
                if (i == etTelephones.size() - 1) {
                    sb.append(msg);
                } else {
                    sb.append(msg + ",");
                }
            }

            sTelephones = sb.toString();
            LogUtil.i("旧设备" + sTelephones);

        } else {
            // 紧急联系人电话不能为空
            if (etTelephones.get(position).getText().toString().trim().length() == 0) {
                ToastUtil.show(this, R.string.input_tracker_phone1_null);
                return;
            }

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < etTelephones.size(); i++) {
                String msg = etTelephones.get(i).getText().toString().trim();
                // guoqz add 20160302.
                String nickname = etNickNames.get(i).getText().toString().trim();
                String nick = nickname.replace(":", "");
                if (i == etTelephones.size() - 1) {
                    sb.append(nick + ":" + msg);
                } else {
                    sb.append(nick + ":" + msg + ",");
                }
            }

            sTelephones = sb.toString();
            LogUtil.i("新设备:" + sTelephones);
        }

        addPhoneBook();
    }

    private void getPhoneBook() {
        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.getPhoneBook(sTrackerNo);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TelephoneBookActivity.this, null,
                                TelephoneBookActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {
                            telephoneInfo = GsonParse.getTelephone(new String(response));

                            /**guoqz add 20160318.*/
//							isNewDevice = true;
                            if (telephoneInfo == null)
                                return;
                            if (telephoneInfo.hardware >= 15) {//720新版本可以编辑，720老版本不可编辑
                                isNewDevice = true;
                            } else {
                                isNewDevice = false;
                            }

                            if (setPhotoAll(telephoneInfo.photo)) {//设置完图片以后再设置电话号码
                                setTelephone(telephoneInfo.phone);
                            }
//							setTelephone("");

                        }
                        ToastUtil.show(TelephoneBookActivity.this, obj.what);
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TelephoneBookActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    private boolean setPhotoAll(String photo) {
        if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {//是770的设备
            image = setTelePhoto(photo);
        } else {
            image = image1;
        }
        for (int i = 0; i < imageViews.size(); i++) {
            imageViews.get(i).setImageResource(image[i]);
        }
        return true;
    }

    //解析返回来的头像数据
    private int[] setTelePhoto(String photo) {
        //String photo1="0,1,,3,4,5,,7,8,9";
        if (photo == null) {
            image = defaultImage1;
            photoData = ",,,,,,,,,";
        } else {
            photoData = photo;
            String[] split = photo.split(",", 10);
            for (int i = 0; i < split.length; i++) {
                LogUtil.i("return photo:[" + i + "]" + split[i]);

                if (!Utils.isEmpty(split[i])) {
                    switch (Integer.parseInt(split[i])) {
                        case 0:
                            image[i] = image1[0];
                            break;
                        case 1:
                            image[i] = image1[1];
                            break;
                        case 2:
                            image[i] = image1[2];
                            break;
                        case 3:
                            image[i] = image1[3];
                            break;
                        case 4:
                            image[i] = image1[4];
                            break;
                        case 5:
                            image[i] = image1[5];
                            break;
                        case 6:
                            image[i] = image1[6];
                            break;
                        case 7:
                            image[i] = image1[7];
                            break;
                        case 8:
                            image[i] = image1[8];
                            break;
                        case 9:
                            image[i] = image1[9];
                            break;
                        case 10:
                            image[i] = defaultImage[0];
                            break;

                        default:
                            break;
                    }
                } else {
                    image[i] = defaultImage[0];
                }
            }
        }
        return image;
    }


    private void addPhoneBook() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params;
        if (!isNewDevice) {  //旧设备
            params = HttpParams.addPhoneBook(sTrackerNo, sTelephones, ",,,,,,,,,", position + 1);
        } else {  // 新设备
            params = HttpParams.addNamePhonebook(sTrackerNo, sTelephones, ",,,,,,,,,", position + 1);
        }

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TelephoneBookActivity.this, null,
                                TelephoneBookActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (0 == obj.code) {

                        }
                        ToastUtil.show(TelephoneBookActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TelephoneBookActivity.this,
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
