package com.bluebud.chat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.app.AppManager;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.chat.utils.ChatInfo;
import com.bluebud.chat.utils.ChatUtil;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.PopupWindowUtils;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.SettingPhotoUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.view.CircleImageView;
import com.permission.RequestPermission;
import com.permission.RequestPermissionCallback;

import java.io.File;
import java.lang.ref.WeakReference;

import io.rong.eventbus.EventBus;

public class ChatInfoCardActivity extends BaseActivity implements OnClickListener,
        OnProgressDialogClickListener, RequestPermissionCallback {

    private CircleImageView chat_card_photo;
    private TextView chat_card_nickname;
    private TextView chat_card_sex;
    private TextView chat_card_age;
    private TextView chat_card_region;
    private TextView chat_card_instruction;
    private PopupWindowUtils popupWindowUtils;

    private final int PHOTO_GRAPH = 0;// 拍照
    private final int PICK = 111;// 相册
    private final int ZOOM = 121;// 图片处理
    private ChatInfo chatInfo;
    private ChatInfoCardActivity mContext;
    //    private String userName;
    private SettingPhotoUtil photoUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_info_card);
        WeakReference<ChatInfoCardActivity> wr = new WeakReference<ChatInfoCardActivity>(this);
        mContext = wr.get();
        AppManager.getAppManager().addActivity(mContext);
//        userName = UserSP.getInstance().getUserName(mContext);
        popupWindowUtils = new PopupWindowUtils(mContext);
        photoUtil = new SettingPhotoUtil(mContext, null, popupWindowUtils);
        initView();
        requestApi(null, 7);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        TextView chat_card_account = (TextView) findViewById(R.id.chat_card_account);
        chat_card_account.setText(UserSP.getInstance().getUserName(mContext));
        findViewById(R.id.chat_rl_photo).setOnClickListener(mContext);
        findViewById(R.id.chat_rl_account).setOnClickListener(mContext);
        findViewById(R.id.chat_rl_nickname).setOnClickListener(mContext);
        findViewById(R.id.chat_rl_sex).setOnClickListener(mContext);
        findViewById(R.id.chat_rl_age).setOnClickListener(mContext);
        findViewById(R.id.chat_rl_region).setOnClickListener(mContext);
        findViewById(R.id.chat_rl_instruction).setOnClickListener(mContext);
        findViewById(R.id.chat_card_back).setOnClickListener(mContext);

        chat_card_photo = (CircleImageView) findViewById(R.id.chat_card_photo);
        chat_card_nickname = (TextView) findViewById(R.id.chat_card_nickname);
        chat_card_sex = (TextView) findViewById(R.id.chat_card_sex);
        chat_card_age = (TextView) findViewById(R.id.chat_card_age);
        chat_card_region = (TextView) findViewById(R.id.chat_card_region);
        chat_card_instruction = (TextView) findViewById(R.id.chat_card_instruction);

        photoUtil.showPhoto(ChatUtil.userPhoto, chat_card_photo);
    }

    /**
     * 设置信息卡值
     */
    private void setIniteView() {
        if (chatInfo == null) {
            chatInfo = new ChatInfo();
            return;
        }
        chat_card_nickname.setText(chatInfo.getNickname());
        String sex = chatInfo.getSex();
        if (sex != null && sex.equals("1"))
            chat_card_sex.setText(R.string.woman);
        else if (sex != null)
            chat_card_sex.setText(R.string.man);
        chat_card_age.setText(chatInfo.getAge());
        chat_card_region.setText(chatInfo.getArea());
        chat_card_instruction.setText(chatInfo.getMark());
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, ChatInfoCardEditActivity.class);
        intent.putExtra("chatInfo", chatInfo);
        switch (v.getId()) {
            case R.id.chat_card_back:
                if (chatInfo != null)
                    EventBus.getDefault().post(chatInfo);
                finish();
                break;

            case R.id.chat_rl_photo:
                RequestPermission.create(mContext, mContext).checkSinglePermission(Manifest.permission.CAMERA, getString(R.string.permission_cameras));
//                photoUtil.requestPermission("chat.jpg");
                break;
            case R.id.chat_rl_nickname:
                intent.putExtra("code", 1);
                startActivityForResult(intent, 1);
                break;
            case R.id.chat_rl_sex:
                showSex();
                break;
            case R.id.chat_rl_age:
                intent.putExtra("code", 3);
                startActivityForResult(intent, 3);
                break;
            case R.id.chat_rl_region:
                intent.putExtra("code", 4);
                startActivityForResult(intent, 4);
                break;
            case R.id.chat_rl_instruction:
                intent.putExtra("code", 5);
                startActivityForResult(intent, 5);
                break;
        }
    }

    /**
     * 选择图片结果返回
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_GRAPH) {// 从手机拍照跳转过来
            File picture = new File(Environment.getExternalStorageDirectory() + "/chat.jpg");
            if (picture.exists()) {
                Uri uri;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    uri = Uri.fromFile(picture);
                else {
                    uri = FileProvider.getUriForFile(mContext, "com.bluebud.liteguardian_hk", picture);
                }
                photoUtil.startPhotoZOOM(uri, "chatPortrait.png");
            }
        }

        if (data == null)
            return;

        if (chatInfo == null)
            chatInfo = new ChatInfo();

        if (requestCode == 1) {
            String nickName = data.getStringExtra("card");
            if (TextUtils.isEmpty(nickName))
                return;
            chat_card_nickname.setText(nickName);
            ChatUtil.userNickname = nickName;
            chatInfo.setNickname(nickName);
            UserSP.getInstance().saveChatValue(mContext, null, null, nickName, null);
        } else if (requestCode == 3) {
            String age = data.getStringExtra("card");
            chat_card_age.setText(age);
            chatInfo.setAge(age);
        } else if (requestCode == 4) {
            String region = data.getStringExtra("card");
            chat_card_region.setText(region);
            chatInfo.setArea(region);
        } else if (requestCode == 5) {
            String instruction = data.getStringExtra("card");
            chat_card_instruction.setText(instruction);
            chatInfo.setMark(instruction);
        } else if (requestCode == PICK) {// 从相册跳转过来
            Uri uri = data.getData();

            photoUtil.startPhotoZOOM(uri, "chatPortrait.png");
        } else if (requestCode == ZOOM) {// 执行完startPhotoZOOM(uri)方法后跳转回来
            DialogUtil.show(mContext, R.string.prompt, R.string.head_portrait,
                    R.string.confirm, new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            DialogUtil.dismiss();
                            photoUtil.setUserHead(chat_card_photo, "chatPortrait.png");
                        }
                    }, R.string.cancel, new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            DialogUtil.dismiss();
                        }
                    });
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置显示性别
     */
    private void showSex() {
        if (chatInfo == null)
            chatInfo = new ChatInfo();
        popupWindowUtils.initPopupWindowSex(getString(R.string.man),
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {// 男
                        chatInfo.setSex("0");
                        requestApi(null, 6);
                        popupWindowUtils.dismiss();
                    }
                }, getString(R.string.woman), new OnClickListener() {
                    @Override
                    public void onClick(View v) {// 女
                        chatInfo.setSex("1");
                        requestApi(null, 6);
                        chat_card_sex.setText(R.string.woman);
                        popupWindowUtils.dismiss();
                    }
                }, getString(R.string.cancel), new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindowUtils.dismiss();
                    }
                }, true);
    }

    /**
     * 获取与设置信息
     */
    private void requestApi(final File file, final int function) {
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(function,
                null, null, file, null, null, null, null, chatInfo,
                new ChatCallbackResult() {
                    @Override
                    public void callBackStart() {
                        super.callBackStart();
                        ProgressDialogUtil.showNoCanceled(mContext, null, mContext);
                    }

                    @Override
                    public void callBackResult(String result) {
                        ProgressDialogUtil.dismiss();
                        if (function == 7) {// 获取用户信息
                            chatInfo = (ChatInfo) ChatHttpParams.getInstallSigle(mContext).getParseResult(7, result);
                            setIniteView();
                        } else if (function == 6) {// 上传性别
                            if (chatInfo.getSex().equals("0"))
                                chat_card_sex.setText(R.string.man);
                            else
                                chat_card_sex.setText(R.string.woman);
                        }
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        ProgressDialogUtil.dismiss();
                        ToastUtil.show(mContext, result);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (chatInfo != null)
            EventBus.getDefault().post(chatInfo);//返回到TabMineFragment界面更新头像和昵称
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chat_card_photo = null;
        chat_card_nickname = null;
        chat_card_sex = null;
        chat_card_age = null;
        chat_card_region = null;
        chat_card_instruction = null;
        popupWindowUtils = null;
        chatInfo = null;
        mContext = null;
    }

    @Override
    public void onProgressDialogBack() {
    }

    /**
     * 权限申请返回
     */
    @Override
    public void onPermissionSuccess() {
        photoUtil.requestPermission("chat.jpg");
    }
}
