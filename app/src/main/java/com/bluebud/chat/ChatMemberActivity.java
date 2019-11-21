package com.bluebud.chat;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.app.AppManager;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.chat.utils.ChatMemberAdapter;
import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.chat.utils.HorizontalGridView;
import com.bluebud.chat.utils.UserInfo;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.rong.eventbus.EventBus;
import io.rong.message.InformationNotificationMessage;

@SuppressWarnings("ResourceAsColor")
public class ChatMemberActivity extends BaseActivity {
    private HorizontalGridView gridview;
    private Button exit_chat;
    private ImageView back;
    private List<UserInfo> list;
    private int intentCode = 0;
    private ChatMemberAdapter adapter;
    private boolean isSuper;
    private String userName;
    private ChatMemberActivity mContext;
    private Tracker currentTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_member_activity);
        WeakReference<ChatMemberActivity> wr = new WeakReference<ChatMemberActivity>(this);
        mContext = wr.get();
        AppManager.getAppManager().addActivity(mContext);
        EventBus.getDefault().register(mContext);// 注册eventBus

        list = new ArrayList<UserInfo>();
        userName = UserSP.getInstance().getUserName(mContext);
        currentTracker = UserUtil.getCurrentTracker(mContext);
        initeValue();
        initListener();
        getGroupUsers();
    }

    /**
     * 初始化控件
     */
    private void initeValue() {
        back = (ImageView) findViewById(R.id.back);
        TextView text = (TextView) findViewById(R.id.txt1);// 设置
        findViewById(R.id.img3).setVisibility(View.GONE);
        gridview = (HorizontalGridView) findViewById(R.id.gridview);
        exit_chat = (Button) findViewById(R.id.exit_chat);
        isSuper = Utils.isSuperUserNotprompt(currentTracker, getApplicationContext());// 判断是否是超级用户
        if (isSuper)
            exit_chat.setVisibility(View.GONE);// 是超级用户，隐藏退出群按鈕
        text.setText(R.string.chat_conversation_member);
        setAdapter();
    }

    /**
     * 适配器初始化
     */
    private void setAdapter() {
        adapter = new ChatMemberAdapter(mContext, list, isSuper);
        gridview.setAdapter(adapter);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (list == null || list.size() < 1) {
                    ToastUtil.show(mContext, R.string.chat_toast_data_erroy);
                    return;
                }
                int size = list.size();
                Intent intent = new Intent(mContext, OperatingGroupActivity.class);
                intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) list);
                gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));

                if (size < 4) {
                    if (position == size) {// 只有添加
                        gridview.setSelector(new ColorDrawable(R.drawable.chat_add_select));
                        intent.putExtra("isAdd", true);
                        startActivityForResult(intent, intentCode);
                    } else if (position == size + 1) {// 删除
                        gridview.setSelector(new ColorDrawable(R.drawable.chat_delete_select));
                        intent.putExtra("isAdd", false);
                        startActivityForResult(intent, intentCode);
                    }
                } else if (size == 4) {// 最多显示四个，只能删除，不能添加
                    if (position == size) {
                        intent.putExtra("isAdd", false);
                        startActivityForResult(intent, intentCode);
                    }
                }
            }
        });

        exit_chat.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChatUtil().chatShowDialog(mContext, R.string.chat_exit_dialog, true, new ChatCallbackResult() {
                    @Override
                    public void callBackResult(String result) {
                    }

                    @Override
                    public void callBackFailResult(String result) {
                    }

                    @Override
                    public void callOkDilaog(AlertDialog mDialog) {
                        super.callOkDilaog(mDialog);
                        ChatUtil.isLoginOut = true;
                        mDialog.dismiss();
                        deleteUser();
                    }

                    @Override
                    public void callCanceDilaog(AlertDialog mDialog) {
                        super.callCanceDilaog(mDialog);
                        mDialog.dismiss();
                        ChatUtil.isLoginOut = false;
                    }
                });
            }
        });
    }


    /**
     * 删除用户，移除群
     */
    private void deleteUser() {
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(3, userName,
                currentTracker.device_sn, null, null, null, null, null, null,
                new ChatCallbackResult() {
                    @Override
                    public void callBackStart() {
                        ProgressDialogUtil.show(mContext);
                        super.callBackStart();
                    }

                    @Override
                    public void callBackResult(String result) {
                        ProgressDialogUtil.dismiss();
                        setResult(2);
                        finish();
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        ProgressDialogUtil.dismiss();
                        ChatUtil.isLoginOut = false;
                        if (!TextUtils.isEmpty(result))
                            ToastUtil.show(mContext, result);
                    }

                    @Override
                    public void callBackFinish() {
                        ProgressDialogUtil.dismiss();
                        super.callBackFinish();
                    }
                });
    }

    /**
     * 动态改变群成员
     */
    public void onEventMainThread(InformationNotificationMessage information) {
        LogUtil.e("获取到有动态变化");
        getGroupUsers();
    }

    /**
     * 有人退出群聊
     */
    public void onEventMainThread(Tracker currTracker) {
        if (TextUtils.isEmpty(currTracker.isExistGroup)) {//自己退出，则不需要显示被踢出群对话框
            if (ChatUtil.isLoginOut) {
                ChatUtil.isLoginOut = false;
                return;
            }
            showCoversationDialog(R.string.chat_loginout, true);
        }
    }

    /**
     * 获取群成员列表
     */
    private void getGroupUsers() {
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(1, null, currentTracker.device_sn, null, null, null, null, null, null,
                new ChatCallbackResult() {
                    @Override
                    public void callBackResult(String result) {
                        LogUtil.e("member==" + result);
                        List<com.bluebud.chat.utils.UserInfo> userinfos = (List<com.bluebud.chat.utils.UserInfo>) ChatHttpParams.getParseResult(1, result);
                        removeMyself(userinfos);
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        ToastUtil.show(mContext, result);
                    }
                });
    }

    /**
     * 移除自己，刷新微聊信息列表
     */
    private void removeMyself(List<UserInfo> userinfos) {
        if (userinfos != null && list != null) {
            list.clear();
            list.addAll(userinfos);

            for (int i = 0; i < userinfos.size(); i++) {
                if (userinfos.get(i).getName().toLowerCase().equals(userName.toLowerCase())) {// 移除自己的信息
                    list.remove(i);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * dialog提示
     */
    private void showCoversationDialog(int msg, final boolean isStatue) {
        new ChatUtil().chatShowDialog(mContext, msg, false, new ChatCallbackResult() {
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
                if (isStatue) {
                    if (ChatUtil.isLoginOut) {
                        deleteUser();
                        return;
                    }
                    setResult(2);
                    finish();
                } else getGroupUsers();
            }
        });
    }


    @Override
    protected void onDestroy() {
        ChatUtil.isLoginOut = false;
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
        gridview = null;
        exit_chat = null;
        back = null;
        adapter = null;
        userName = null;
        mContext = null;
        currentTracker = null;
        if (list != null)
            list.clear();
        list = null;
    }
}
