package com.bluebud.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import com.bluebud.activity.BaseActivity;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.PhonebookInfo;
import com.bluebud.info.Tracker;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.view.ClearEditText;
import com.bluebud.view.RecycleViewImage;

/**
 * Created by user on 2018/6/11.
 */

public class PhoneBookEditActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private PhonebookInfo info;
    private int currentIndex = 1;//当前选择的位置
    private int oldIndex = 1;//上一次选择的位置

    private int[] image1 = new int[]{R.drawable.img_defaulthead_628, R.drawable.img_dad, R.drawable.img_mom,
            R.drawable.img_grandpa, R.drawable.img_grandma,
            R.drawable.img_grandfather, R.drawable.img_grandmother,
            R.drawable.elder_brother, R.drawable.elder_syster
            , R.drawable.younger_brother, R.drawable.younger_sister};
    private Tracker mTrakcer;
    private ClearEditText etName;
    private ClearEditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_telephone_book_edit);
        mContext = this;
        getData();
        init();
    }

    /**
     * 获取当前选择电话号码信息
     */
    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            info = (PhonebookInfo) intent.getSerializableExtra("phonebookinfo");
            currentIndex = info.photo;
            oldIndex = currentIndex;
        }
    }

    /**
     * 初始化布局
     */
    private void init() {
        setBaseTitleText(R.string.contact_add);
        super.setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        super.setBaseTitleRightText(R.string.submit1);
        super.setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);

        mTrakcer = UserUtil.getCurrentTracker(this);
        findViewById(R.id.rl_720).setVisibility(View.GONE);
        etName = (ClearEditText) findViewById(R.id.et_name);//昵称
        etPhone = (ClearEditText) findViewById(R.id.et_phone);//号码
        new ChatUtil().editInputLimit(this, etName, 20);//昵称限制 只输入20个字符
        etPhone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});//电话号限制
        etPhone.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        etName.setText(info.nickname);
        etPhone.setText(info.phoneNum);
        etName.setEnabled(true);
        init770View();//初始化770
    }

    /**
     * 动态添加头像
     */
    private void init770View() {

        final LinearLayout mLlAddView = (LinearLayout) findViewById(R.id.ll_add_view);
        if(mTrakcer!=null&&(mTrakcer.product_type.equals("24"))){//790没有头像选择，只有默认头像，||mTrakcer.product_type.equals("31")
            findViewById(R.id.hv_770).setVisibility(View.GONE);
            return;
        }
        for (int i = 0; i < image1.length; i++) {
            final RecycleViewImage recycleViewImage = new RecycleViewImage(mContext);
            recycleViewImage.setBackgroundResource(image1[i]);
            recycleViewImage.setTag(i);
            if (i == 0)//隐藏默认头像
                recycleViewImage.setVisibility(View.GONE);

            if (currentIndex == i) {
                recycleViewImage.settBorderColor1(mContext.getResources().getColor(R.color.bg_theme));
            } else {
                recycleViewImage.settBorderColor1(mContext.getResources().getColor(R.color.green_circle_solid));
            }
            recycleViewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentIndex = (Integer) v.getTag();//当前点击的位置
                    recycleViewImage.setSelected(true);
                    recycleViewImage.setSelected1();
                    if (currentIndex != oldIndex) {
                        mLlAddView.getChildAt(oldIndex).setSelected(false);
                        ((RecycleViewImage) mLlAddView.getChildAt(oldIndex)).setSelected1();
                        oldIndex = currentIndex;//当前点击的位置;
                    }
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
            case R.id.rl_title_right_text:
                addOrModifyContact();
                break;
        }
    }

    /**
     * 添加修改联系人
     */
    private void addOrModifyContact() {
        String nameold = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(nameold)) {
            ToastUtil.show(mContext, getString(R.string.no_nickname));
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(mContext, getString(R.string.input_user_login2));
            return;
        }
        String name = nameold.replace(":","");
        info.photo = currentIndex;
        info.phoneNum = phone;
        info.nickname = name;
        info.deviceSn = mTrakcer.device_sn;
        info.isAdmin = 0;
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(25, null, null, null, null, null, null, null, info, new ChatCallbackResult() {
            @Override
            public void callBackStart() {
                super.callBackStart();
                ProgressDialogUtil.show(mContext);
            }

            @Override
            public void callBackResult(String result) {
                ProgressDialogUtil.dismiss();
                PhonebookInfo bookInfo = (PhonebookInfo) ChatHttpParams.getParseResult(25, result);
                if (bookInfo == null) {
                    ToastUtil.show(mContext, getString(R.string.net_exception));
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("phonebookinfo", bookInfo);
                setResult(1, intent);
                finish();
            }

            @Override
            public void callBackFailResult(String result) {
                ProgressDialogUtil.dismiss();
                ToastUtil.show(mContext, result);
            }
        });
    }
}
