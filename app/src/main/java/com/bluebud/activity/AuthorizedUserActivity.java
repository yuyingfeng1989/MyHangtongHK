package com.bluebud.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.OnlyUser;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.TrackerUser;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.DialogUtil;
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

import java.util.List;

//授 权用户 页面
public class AuthorizedUserActivity extends BaseActivity implements
        OnClickListener, OnProgressDialogClickListener {
    private TrackerUser trackerUser;
    private ListView mLiauthorization;
    private RequestHandle requestHandle;
    private String trackerNo;
    private List<OnlyUser> users;
    private AccountAuthorizationAdpter accountAuthorizationAdpter;
    private LinearLayout mLLAuthUser;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_authorized_user);
        initTitle();
        trackerUser = (TrackerUser) getIntent().getSerializableExtra("trackerUser");
        trackerNo = getIntent().getStringExtra("trackerNo");
        nickname = getIntent().getStringExtra("nickname");
        users = trackerUser.users;
        init();
    }

    private void initTitle() {
        getBaseTitleLeftBack().setOnClickListener(this);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleText(getString(R.string.authed_account1));

    }

    private void init() {
        mLLAuthUser = (LinearLayout) findViewById(R.id.ll_auth_user);
        TextView mTvNickName = (TextView) findViewById(R.id.tv_nickname);
        //mTvNickName.setText(nickname);
        mLiauthorization = (ListView) findViewById(R.id.lv_authorization);
        if (!TextUtils.isEmpty(nickname)) {
            mTvNickName.setText(nickname);
        } else {
            mTvNickName.setVisibility(View.GONE);
        }
        if (users.size() <= 0) {
            mLLAuthUser.setVisibility(View.GONE);
        } else {
            mLLAuthUser.setVisibility(View.VISIBLE);
        }


        accountAuthorizationAdpter = new AccountAuthorizationAdpter();
        mLiauthorization.setAdapter(accountAuthorizationAdpter);

    }


    // 已授权适配器
    private class AccountAuthorizationAdpter extends BaseAdapter {

        @Override
        public int getCount() {
            if (users.size() <= 0) {
                return 0;
            } else {
                return users.size();
            }
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(AuthorizedUserActivity.this,
                        R.layout.account_authorization_item, null);
                holder = new ViewHolder();
                holder.tvAuthorizationNumber = (TextView) convertView
                        .findViewById(R.id.tv_authorization_number);
                holder.btnCancelAuthorization = (Button) convertView
                        .findViewById(R.id.btn_cancel_authorization);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (!Utils.isEmpty(users.get(position).name)) {
                holder.tvAuthorizationNumber.setText(users.get(position).name);
            } else {
                holder.tvAuthorizationNumber.setText("");
            }


            holder.btnCancelAuthorization
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            DialogUtil.show(AuthorizedUserActivity.this, R.string.prompt, R.string.notice_auth,
                                    R.string.unauth, new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {


                                            if (UserUtil.isGuest(AuthorizedUserActivity.this)) {
                                                ToastUtil.show(AuthorizedUserActivity.this,
                                                        R.string.guest_no_set);
                                                return;
                                            }
                                            //取消授权
                                            cancelAuth(trackerNo, position);
                                            DialogUtil.dismiss();
                                        }
                                    }, R.string.not_unauth, new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            DialogUtil.dismiss();
                                        }
                                    });
                        }
                    });
            return convertView;
        }

    }

    private static class ViewHolder {
        private TextView tvAuthorizationNumber;
        private Button btnCancelAuthorization;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            default:
                break;
        }
    }


    /**
     * 取消授权账号
     */
    private void cancelAuth(String strTrackerNo, final int position) {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.cancelAuthorization(strTrackerNo,
                users.get(position).name);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(AuthorizedUserActivity.this,
                                null, AuthorizedUserActivity.this);

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        ToastUtil.show(AuthorizedUserActivity.this, obj.what);
                        if (obj.code == 0) {
                            trackerUser = GsonParse.usersParse(new String(
                                    response));
                            LogUtil.i("position:" + position);
                            users.remove(position);
                            //showAuthUsers();
                            accountAuthorizationAdpter.notifyDataSetChanged();
                            if (users.size() <= 0) {
                                finish();
                            }
                            ToastUtil.show(AuthorizedUserActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(AuthorizedUserActivity.this,
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
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }
}
