package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bluebud.activity.BaseActivity;
import com.bluebud.adapter.memberManagementAdapter;
import com.bluebud.adapter.memberManagementAdapter.deleteAuthUserListener;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.OnlyUser;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.TrackerUser;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.List;


public class MemberManagementActivity extends BaseActivity implements OnClickListener, OnItemLongClickListener, OnProgressDialogClickListener, deleteAuthUserListener, OnItemClickListener {
    //    private RequestHandle requestHandle;
    private GridView gv;
    private TrackerUser trackerUser;
    private Tracker mCurTracker;
    private String trackerNo;
    private List<OnlyUser> users;
    private memberManagementAdapter adapter;
    private boolean isShowDelete = false;
    private int BackReuslt = RESULT_CANCELED;
    private boolean isChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_member_management);
        isChat = getIntent().getBooleanExtra("isChat", false);
        init();
        getTrackerUser();
    }

    private void init() {
        setBaseTitleText(R.string.member_manager);
        super.setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        super.setBaseTitleRightText(R.string.submit1);
        super.setBaseTitleRightTextVisible(View.GONE);
        getBaseTitleRightText().setOnClickListener(this);
        ImageView ivAdd = (ImageView) findViewById(R.id.iv_add);
        mCurTracker = UserUtil.getCurrentTracker(this);
        if (mCurTracker != null) {
            trackerNo = mCurTracker.device_sn;
            LogUtil.i("trackerNo:" + trackerNo);
        }
        ivAdd.setOnClickListener(this);
        gv = (GridView) findViewById(R.id.gv);

        RelativeLayout llAdd = (RelativeLayout) findViewById(R.id.ll_add);
        llAdd.setOnClickListener(this);
        adapter = new memberManagementAdapter(this, this, users);
        gv.setAdapter(adapter);
        gv.setOnItemLongClickListener(this);
        gv.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ll_add:
                LogUtil.i("124444");
                if (isShowDelete) {
                    isShowDelete = false;
                    adapter.setIsShowDelete(isShowDelete);
                }
                break;
            case R.id.rl_title_back:
                setResult(BackReuslt);
                finish();
                break;
            case R.id.iv_add://增加授权
                Intent intent = new Intent(this, AuthActivity.class);
                startActivityForResult(intent, 1);
                if (isShowDelete) {
                    isShowDelete = false;
                    adapter.setIsShowDelete(isShowDelete);
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data == null) {
                getTrackerUser();
            } else if (data != null) {
                OnlyUser onlyUser = (OnlyUser) data.getSerializableExtra("onlyUser");
                int position = data.getIntExtra("position", 0);
                users.set(position, onlyUser);
                adapter.notifyDataSetChanged();
            }
            BackReuslt = RESULT_OK;//添加新的授权用户，返回结果刷新微聊界面
        }
    }

    //取消授权
    @Override
    public void deleteAuthUser(int position) {
        cancelAuth(position);
    }

    //长按时退出删除图标
    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        if (isChat)
            return true;
        LogUtil.i("234");
        if (isShowDelete) {
            isShowDelete = false;
        } else {
            isShowDelete = true;
        }
        adapter.setIsShowDelete(isShowDelete);
        return true;
    }

    //点击时进去备注修改页面
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        Intent intent = new Intent(this, AuthActivity.class);
        OnlyUser onlyUser = trackerUser.users.get(position);
        intent.putExtra("onlyUser", onlyUser);
        intent.putExtra("position", position);
        startActivityForResult(intent, 1);
    }

    /**
     * 获取追踪器下的所有授权用户
     */
    private void getTrackerUser() {
        if (mCurTracker == null) {
            return;
        }
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.getTrackerUser(trackerNo);

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(MemberManagementActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null) {//暂无授权帐号
                            ToastUtil.show(MemberManagementActivity.this, getResources().getString(R.string.no_authed_account));
                            return;
                        }
                        if (obj.code == 0) {
                            trackerUser = GsonParse.usersParse(new String(response));
                            LogUtil.e("response==" + new String(response));
                            if (trackerUser != null) {
                                users = trackerUser.users;
                                adapter.setList(users);
//                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            ToastUtil.show(MemberManagementActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(MemberManagementActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }


    /**
     * 取消授权账号
     */
    private void cancelAuth(final int position) {
        if (mCurTracker == null) {
            return;
        }
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.cancelAuthorization(trackerNo,
                users.get(position).name);

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(MemberManagementActivity.this, null, MemberManagementActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        ToastUtil.show(MemberManagementActivity.this, obj.what);
                        if (obj.code == 0) {
                            LogUtil.i("position:" + position);
                            BackReuslt = RESULT_OK;//成功取消授权结果，刷新微聊界面
                            users.remove(position);
                            adapter.setList(users);
//                            adapter.notifyDataSetChanged();
                            trackerUser = new TrackerUser();
                            trackerUser.deviceSn = trackerNo;
                            trackerUser.users = users;
                            if (users.size() <= 0)
                                finish();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(MemberManagementActivity.this,
                                R.string.net_exception);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(BackReuslt);
    }
}
