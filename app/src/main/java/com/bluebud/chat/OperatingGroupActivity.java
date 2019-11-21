package com.bluebud.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.settings.MemberManagementActivity;
import com.bluebud.app.AppManager;
import com.bluebud.chat.utils.ChatCallbackResult;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.chat.utils.OperatGroupAdapter;
import com.bluebud.chat.utils.UserInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;

public class OperatingGroupActivity extends BaseActivity implements OnClickListener, OnProgressDialogClickListener {
    private boolean isAdd;
    private ListView listview;
    private RelativeLayout chat_ll_null;
    private TextView chat_title_text;
    private Map<String, Boolean> map;
    private List<UserInfo> list;
    private String deviceSn;
    private OperatingGroupActivity mContext;
    private OperatGroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_operat_group);
        WeakReference<OperatingGroupActivity> wr = new WeakReference<OperatingGroupActivity>(this);
        mContext = wr.get();
        AppManager.getAppManager().addActivity(mContext);
        Intent intent = getIntent();
        list = new ArrayList<UserInfo>();
        isAdd = intent.getBooleanExtra("isAdd", false);
        List<UserInfo> deleteList = intent.getParcelableArrayListExtra("list");
        if (deleteList != null)
            list.addAll(deleteList);

        map = new HashMap<String, Boolean>();
        deviceSn = UserUtil.getCurrentTracker(mContext).device_sn;
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        findViewById(R.id.back).setOnClickListener(mContext);
        TextView text = (TextView) findViewById(R.id.txt1);
        findViewById(R.id.img3).setVisibility(View.GONE);
        chat_title_text = (TextView) findViewById(R.id.chat_title_text);
        listview = (ListView) findViewById(R.id.chat_group_list);
        chat_ll_null = (RelativeLayout) findViewById(R.id.chat_ll_null);
        findViewById(R.id.add_authorizat).setOnClickListener(mContext);
        chat_title_text.setOnClickListener(mContext);
        initValue(text);
    }

    /**
     * 初始化控件
     */
    private void initValue(TextView text) {
        if (isAdd) {
            text.setText(R.string.chat_add_member);
            requestUserInfo(null);
        } else {
            chat_title_text.setVisibility(View.VISIBLE);
            chat_title_text.setText(R.string.delete);
            text.setText(R.string.chat_conversation_member);
            setAdapter();
        }
    }

    /**
     * 邀请群成员
     */
    private void requestUserInfo(final String string) {
        int function;
        String userName = "";
        if(!TextUtils.isEmpty(string))
            userName = string.trim().toLowerCase();//转化为小写字母
        if (string == null) // 2查询设备可邀请授权用户
            function = 2;
        else if (isAdd) // 添加聊天成员
            function = 0;
        else // 删除群组成员
            function = 3;
        ChatHttpParams.getInstallSigle(mContext).chatHttpRequest(function,
                userName, deviceSn, null, null, null, null, null, null,
                new ChatCallbackResult() {
                    @Override
                    public void callBackStart() {
                        super.callBackStart();
                        ProgressDialogUtil.showNoCanceled(mContext, null, mContext);
                    }

                    @Override
                    public void callBackResult(String result) {
                        ProgressDialogUtil.dismiss();
                        if (TextUtils.isEmpty(result))
                            return;
                        resultCallBack(string, result);
                    }

                    @Override
                    public void callBackFailResult(String result) {
                        ProgressDialogUtil.dismiss();
                        ToastUtil.show(mContext, result);
                    }
                });
    }

    /**
     * 获取列表或删除成员
     */
    private void resultCallBack(String string, String result) {
        if (string == null) {// 获取授权用户列表
           if(listview==null)
               return;
            List<com.bluebud.chat.utils.UserInfo> userinfos = (List<com.bluebud.chat.utils.UserInfo>) ChatHttpParams.getParseResult(2, result);
            if (userinfos == null) {
                listview.setVisibility(View.GONE);
                chat_ll_null.setVisibility(View.VISIBLE);
                return;
            }
            chat_ll_null.setVisibility(View.GONE);
            chat_title_text.setVisibility(View.VISIBLE);
            listview.setVisibility(View.VISIBLE);
            list.clear();
            list.addAll(userinfos);
            setAdapter();
        } else {// 添加群成员 删除群成员
            refreshUserInfo(string);
            setResult(2);
            finish();
        }
    }

    /**
     * 刷新新增用户信息
     */
    private void refreshUserInfo(String string) {
        if (isAdd) {
            if (TextUtils.isEmpty(string))
                return;
            String chatName;
            Uri uri;
            for (int i = 0; i < list.size(); i++) {
                UserInfo info = list.get(i);
                String name = info.getName();
                String url = info.getPortrait();
                if (string.indexOf(name) != -1) {
                    if (info.getRemark() != null) chatName = info.getRemark();
                    else if (info.getNickname() != null) chatName = info.getNickname();
                    else chatName = name;
                    if (TextUtils.isEmpty(url))
                        uri = null;
                    else uri = Uri.parse(url);
                    RongIM.getInstance().refreshUserInfoCache(new io.rong.imlib.model.UserInfo(name, chatName, uri));
                }
            }
        }
    }


    /**
     * 初始化适配器
     */
    private void setAdapter() {
        for (int i = 0; i < list.size(); i++) {
            if (deviceSn.equals(list.get(i).getName())) {//隐藏设备
                list.remove(i);
                break;
            }
        }

        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getName(), false);
        }
        if (adapter == null) {
            adapter = new OperatGroupAdapter(mContext, list, map);
            listview.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            requestUserInfo(null);
        } else
            chat_ll_null.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.add_authorizat:
               Intent intent = new Intent(mContext, MemberManagementActivity.class);
                intent.putExtra("isChat",true);
                startActivityForResult(intent,1);
                listview.setVisibility(View.GONE);
                chat_ll_null.setVisibility(View.GONE);
                break;

            case R.id.chat_title_text:// 确定
                commitValue();
                break;

            default:
                break;
        }
    }

    /**
     * 提交数据
     */
    private void commitValue() {
        if (list == null || list.size() < 1)
            return;

        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            UserInfo userInfo = list.get(i);
            if (map.get(userInfo.getName()))
                buf.append(userInfo.getName()).append(",");
        }

        int length = buf.length();
        if (length < 1)
            return;
        String string = buf.deleteCharAt(buf.length() - 1).toString();
        if (!TextUtils.isEmpty(string))
            requestUserInfo(string);
        else
            ToastUtil.show(mContext, R.string.chat_select_user);
    }

    @Override
    public void onProgressDialogBack() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listview = null;
        chat_ll_null = null;
        chat_title_text = null;
        deviceSn = null;
        mContext = null;
        adapter = null;
        if (map != null)
            map.clear();
        if (list != null)
            list.clear();
        map = null;
        list = null;
    }
}
