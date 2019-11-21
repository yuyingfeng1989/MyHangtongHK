package com.bluebud.chat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.bluebud.activity.BaseFragmentActivity;
import com.bluebud.app.AppManager;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.Tracker;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * 会话页面
 */
public class ConversationActivity extends BaseFragmentActivity implements OnClickListener {//RongIM.UserInfoProvider, RongIM.GroupInfoProvider,
    private String mTargetId;// 刚刚创建完讨论组后获得群组的id
    private List<com.bluebud.chat.utils.UserInfo> userIdList;
    private Conversation.ConversationType mConversationType;// 会话类型
    private final String defaultImage = "http://54.179.149.239:10000/image/HeadPortrait/watch.png";
    private Tracker mCurTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        AppManager.getAppManager().addActivity(this);
        mCurTracker = UserUtil.getCurrentTracker(ConversationActivity.this);
        userIdList = new ArrayList<>();
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        if (ChatUtil.token == null)
            ChatUtil.token = UserSP.getInstance().getToken(this);
        getIntentDate(intent);
        isReconnect(intent);
        if (mCurTracker.product_type.equals("30")) {//790和K1设备有表情
            RongExtension rc_extension = (RongExtension) findViewById(R.id.rc_extension);
            rc_extension.findViewById(R.id.rc_emoticon_toggle).setVisibility(View.VISIBLE);
        }
    }

    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {
        mTargetId = intent.getData().getQueryParameter("targetId");
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        getGroupUsers();//获取群组信息
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.img3).setOnClickListener(this);
    }

    /**
     * 加载会话页面 ConversationFragment
     */
    @SuppressLint("DefaultLocale")
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {
        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName)
                .buildUpon().appendPath("conversation")
                .appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();
        fragment.setUri(uri);
        EditText et = (EditText) findViewById(R.id.rc_edit_text);
        if (mCurTracker.product_type.equals("30")) {
            RongIM.getInstance().setMaxVoiceDurationg(30);
//            util.editInputLimit(this, et, 40);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
        } else if (mCurTracker.product_type.equals("31") || mCurTracker.product_type.equals("24")) {
            RongIM.getInstance().setMaxVoiceDurationg(30);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        } else {
            RongIM.getInstance().setMaxVoiceDurationg(15);
            et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
//            util.editInputLimit(this, et, 30);
        }
//        et.setHint("30个字符");
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.img3:
                Intent intent = new Intent(this, ChatMemberActivity.class);
                startActivityForResult(intent, 1);
                break;

            default:
                break;
        }
    }

    /**
     * 判断消息是否是 push 消息 通知栏点击进来，显示聊天界面信息
     */
    private void isReconnect(Intent intent) {// push或通知过来
        LogUtil.e("isReconnect_Token==" + ChatUtil.token);
        if (intent == null)
            return;
        Uri data = intent.getData();
        RongIMClient.ConnectionStatusListener.ConnectionStatus currState = RongIM.getInstance().getCurrentConnectionStatus();
        if (data != null && data.getScheme().equals("rong")) {// 通过intent.getData().getQueryParameter("push")
            if (RongIM.getInstance() != null && RongIM.getInstance().getRongIMClient() != null && currState.getValue() == 0)//已经连接
                enterFragment(mConversationType, mTargetId);
            else {
                reconnect(ChatUtil.token);//重新连接
            }
        }
    }

    /**
     * 重连
     */
    private void reconnect(String token) {
        if (getApplicationInfo().packageName.equals(ChatUtil
                .getCurProcessName(getApplicationContext()))) {
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {
                }

                @Override
                public void onSuccess(String s) {
                    enterFragment(mConversationType, mTargetId);// mConversationType, mTargetId
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                }
            });
        }
    }

    /**
     * 获取群组信息
     */
    private void getGroupUsers() {
        ChatHttpParams.getInstallSigle(this).chatHttpRequest(1, null,
                mTargetId, null, null, null, null, null, null,
                new ChatCallbackResult() {

                    @Override
                    public void callBackResult(String result) {
                        List<com.bluebud.chat.utils.UserInfo> userinfos = (List<com.bluebud.chat.utils.UserInfo>) ChatHttpParams.getParseResult(1, result);
                        if (userinfos != null && userIdList != null) {
                            LogUtil.e("群成员==" + userinfos.toString());
                            userIdList.clear();
                            userIdList.addAll(userinfos);
                            String chatName;
                            Uri uri;
                            for (int i = 0; i < userIdList.size(); i++) {
                                com.bluebud.chat.utils.UserInfo userInfo = userIdList.get(i);
                                String path = userInfo.getPortrait();
                                if (userInfo.getRemark() != null)
                                    chatName = userInfo.getRemark();
                                else if (userInfo.getNickname() != null)
                                    chatName = userInfo.getNickname();
                                else chatName = userInfo.getName();

                                if (TextUtils.isEmpty(path)) {
                                    if (!userInfo.getType().equals("1")) uri = null;
                                    else uri = Uri.parse(defaultImage);
                                } else uri = Uri.parse(path);
                                RongIM.getInstance().refreshUserInfoCache(new UserInfo(userInfo.getName(), chatName, uri));//刷新群组成员信息
                            }
                        }
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        ToastUtil.show(ConversationActivity.this, result);
                    }
                });
    }

    /**
     * 退出群组，关闭界面
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2)
            finish();
    }


    /**
     * 有人退出群聊,弹出对话框
     */
    public void onEventMainThread(Tracker currTracker) {
        if (TextUtils.isEmpty(currTracker.isExistGroup)) {//退出群聊，判断isExistGroup空位退出，存在为添加群员
            if (ChatUtil.isLoginOut)
                return;
            showCoversationDialog(R.string.chat_loginout, true);
        }
    }


    /**
     * dialog提示
     */
    private void showCoversationDialog(int msg, final boolean isStatue) {
        new ChatUtil().chatShowDialog(ConversationActivity.this, msg, false,new ChatCallbackResult() {
            @Override
            public void callBackResult(String result) {
            }

            @Override
            public void callBackFailResult(String result) {
            }

            @Override
            public void callOkDilaog(AlertDialog mDialog) {
                super.callOkDilaog(mDialog);
                mDialog.dismiss();
                if (isStatue) finish();
                else getGroupUsers();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mTargetId = null;// 刚刚创建完讨论组后获得群组的id
        mConversationType = null;// 会话类型
        if (userIdList != null)
            userIdList.clear();
        userIdList = null;
    }
}
