package com.bluebud.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.app.AppManager;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.chat.utils.ChatInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;

import java.lang.ref.WeakReference;

public class ChatInfoCardEditActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener {

    private EditText et_content;
    private ChatInfo chatInfo;
    private int code;
    private ChatInfoCardEditActivity mContext;
    private TextView m_name;
    private String hint;
    private boolean isChatInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_information_card_editor);
        WeakReference<ChatInfoCardEditActivity> wr = new WeakReference<ChatInfoCardEditActivity>(this);
        mContext = wr.get();
        AppManager.getAppManager().addActivity(mContext);
        Intent intent = getIntent();
        isChatInfo = intent.getBooleanExtra("isChatInfo", false);
        if (!isChatInfo) {
            chatInfo = intent.getParcelableExtra("chatInfo");
            code = intent.getIntExtra("code", -1);
            initeView();
        } else {//吃药提醒提示语
            initeView();
            hint = intent.getStringExtra("hint");
            m_name.setText(R.string.hint_text);
            et_content.setText(hint);
            m_name.setVisibility(View.VISIBLE);
            et_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        }

    }

    /**
     * 初始化控件
     */
    private void initeView() {
        et_content = (EditText) findViewById(R.id.et_content);
        m_name = (TextView) findViewById(R.id.m_name);
        super.getBaseTitleLeftBack().setOnClickListener(mContext);
        super.setBaseTitleRightText(R.string.save);
        super.setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(mContext);
        if (code == 1)//昵称
            et_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        else if (code == 3) { //年龄
            et_content.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            et_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        } else if (code == 4)//地区
            et_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        else if (code == 5)//个人说明
            et_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});

        if (isChatInfo)
            return;

        if (chatInfo == null)
            chatInfo = new ChatInfo();
        setCardInfo(null);
        et_content.setFocusable(false);
        et_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_content.setFocusable(true);
                et_content.setFocusableInTouchMode(true);
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:// 返回
                finish();
                break;
            case R.id.rl_title_right_text:// 保存
                if (!isChatInfo)
                    setLostCard();
                else {//吃药提醒设置
                    Intent intent = new Intent();
                    intent.putExtra("hint", et_content.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
        }
    }

    private void setCardInfo(String string) {
        m_name.setVisibility(View.VISIBLE);
        switch (code) {
            case 1:// 昵称
                if (string == null)
                    et_content.setText(chatInfo.getNickname());
                else
                    chatInfo.setNickname(string);
                m_name.setText(R.string.edit_name);
                break;
            case 3:// 年龄
                et_content.setInputType(InputType.TYPE_CLASS_NUMBER);
                if (string == null)
                    et_content.setText(chatInfo.getAge());
                else
                    chatInfo.setAge(string);
                m_name.setText(R.string.edit_age);
                break;
            case 4:// 地区
                if (string == null)
                    et_content.setText(chatInfo.getArea());
                else
                    chatInfo.setArea(string);
                m_name.setText(R.string.chat_card_region);
                break;
            case 5:// 个人说明
                if (string == null)
                    et_content.setText(chatInfo.getMark());
                else
                    chatInfo.setMark(string);
                m_name.setText(R.string.chat_card_pe);
                break;
        }
    }

    /**
     * 设置卡片信息
     */
    private void setLostCard() {
        final String content = et_content.getText().toString().trim();
        if (content == null || TextUtils.isEmpty(content)) {
            ToastUtil.show(mContext, R.string.content_is_null);
            return;
        }
        setCardInfo(content);

        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(6, null, null,
                null, null, null, null, null, chatInfo,
                new ChatCallbackResult() {

                    @Override
                    public void callBackStart() {
                        super.callBackStart();
                        ProgressDialogUtil.showNoCanceled(mContext, null, mContext);
                    }

                    @Override
                    public void callBackResult(String result) {
                        ProgressDialogUtil.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("card", content);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        ProgressDialogUtil.dismiss();
                        ToastUtil.show(mContext, result);
                    }
                });
    }

    @Override
    public void onProgressDialogBack() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        et_content = null;
        chatInfo = null;
        mContext = null;
        m_name = null;
    }
}
