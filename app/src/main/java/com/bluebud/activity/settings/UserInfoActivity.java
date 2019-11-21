
package com.bluebud.activity.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.activity.LoginActivity;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.info.User;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DialogUtil;
import com.bluebud.utils.DialogUtil.OnEditTextEditListener;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;


public class UserInfoActivity extends BaseActivity implements OnClickListener,
        OnEditTextEditListener, OnProgressDialogClickListener {
    private LinearLayout llDevice;
    private LinearLayout llDeviceAuth;
    private TextView tvDevice;
    private TextView tvDeviceAuth;
    private TextView tvDeviceNum;
    private TextView tvDeviceNumAuth;
    private View vDeviceLine;
    private View vDeviceLineAuth;
    private LinearLayout llSuperDevice;
    private LinearLayout llAuthDevice;
    private View superUserView;
    private View authUserView;

    private User user;
    private int iPosition = 0;

    private List<Tracker> superTrackers;

    private RequestHandle requestHandle;

    private View vAdvertisement;
    private ViewPager viewPager;
    private List<ImageView> imageViews;
    private List<View> dots;
    private int currentItem = 0;
    private ScheduledExecutorService scheduledExecutorService;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(currentItem);
        }
    };
    private Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_user_info);
        init();
    }

    @Override
    protected void onDestroy() {
        if (null != scheduledExecutorService) {
            scheduledExecutorService.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

//    private void initAdvertisement() {
//
//        final List<Advertisement> advertisements = UserUtil.getAdvertisement(
//                this, 1);
//
//        vAdvertisement = findViewById(R.id.view_advertisement);
//        vAdvertisement.setVisibility(View.VISIBLE);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LayoutParams.MATCH_PARENT, AppSP.getInstance()
//                .getAdHeight(this));
//        vAdvertisement.setLayoutParams(params);
//
//        LinearLayout llDot = (LinearLayout) vAdvertisement
//                .findViewById(R.id.ll_dot);
//        imageViews = new ArrayList<ImageView>();
//        dots = new ArrayList<View>();
//        for (int i = 0; i < advertisements.size(); i++) {
//            ImageView imageView = new ImageView(this);
//            // imageView.setBackgroundResource(Constants.imageIds[i]);
//            LogUtil.i(advertisements.get(i).image_url);
//            Glide.with(this).load(advertisements.get(i).image_url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
//            imageView.setScaleType(ScaleType.CENTER_CROP);
//            imageViews.add(imageView);
//
//            imageView.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View view) {
//                    LogUtil.i(advertisements.get(currentItem).ad_url);
//                    Intent it = new Intent(Intent.ACTION_VIEW, Uri
//                            .parse(advertisements.get(currentItem).ad_url));
//                    it.setClassName("com.android.browser",
//                            "com.android.browser.BrowserActivity");
//                    startActivity(it);
//                }
//            });
//
//            if (1 < advertisements.size()) {
//                View view = LayoutInflater.from(this).inflate(
//                        R.layout.layout_dot, null);
//                View vDot = view.findViewById(R.id.v_dot);
//                if (i == 0) {
//                    vDot.setBackgroundResource(R.drawable.dot_focused);
//                }
//                dots.add(vDot);
//                llDot.addView(view);
//            }
//        }
//
//        viewPager = (ViewPager) vAdvertisement.findViewById(R.id.vp);
//        viewPager.setAdapter(new ViewPagerAdapter(imageViews));
//
//        if (1 < advertisements.size()) {
//            viewPager.setOnPageChangeListener(new MyPageChangeListener());
//
//            scheduledExecutorService = Executors
//                    .newSingleThreadScheduledExecutor();
//            scheduledExecutorService.scheduleAtFixedRate(scrollRunnable, 1, 2,
//                    TimeUnit.SECONDS);
//        }
//    }
//
//    private class MyPageChangeListener implements OnPageChangeListener {
//        private int oldPosition = 0;
//
//        @Override
//        public void onPageSelected(int position) {
//            currentItem = position;
//            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
//            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
//            oldPosition = position;
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {
//
//        }
//
//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//
//        }
//    }

    private void init() {
        super.setBaseTitleText(R.string.myself);
        super.setBaseTitleRightSettingVisible(View.VISIBLE);
        setBaseTitleRightSettingBackground(R.drawable.title_pwd_selector);
        getBaseTitleLeftBack().setOnClickListener(this);
        getBaseTitleRightSetting().setOnClickListener(this);

        llDevice = (LinearLayout) findViewById(R.id.ll_device);
        llDeviceAuth = (LinearLayout) findViewById(R.id.ll_device_auth);
        tvDevice = (TextView) findViewById(R.id.tv_device);
        tvDeviceAuth = (TextView) findViewById(R.id.tv_device_auth);
        tvDeviceNum = (TextView) findViewById(R.id.tv_device_num);
        tvDeviceNumAuth = (TextView) findViewById(R.id.tv_device_num_auth);
        vDeviceLine = findViewById(R.id.v_device_line);
        vDeviceLineAuth = findViewById(R.id.v_device_line_auth);

        llSuperDevice = (LinearLayout) findViewById(R.id.ll_my_device);
        llAuthDevice = (LinearLayout) findViewById(R.id.ll_auth_device);

        llDevice.setOnClickListener(this);
        llDeviceAuth.setOnClickListener(this);

        tvDevice.setSelected(true);

        user = UserUtil.getUserInfo(this);
        setDeviceInfo();
    }

    private void setDeviceInfo() {
        user = UserUtil.getUserInfo(this);
        List<Tracker> trackers = user.device_list;
        superTrackers = new ArrayList<Tracker>();
        final List<Tracker> authTrackers = new ArrayList<Tracker>();
        for (int i = 0; i < trackers.size(); i++) {
            if (trackers.get(i).super_user.equals(UserSP.getInstance()
                    .getUserName(this))) {
                superTrackers.add(trackers.get(i));
            } else {
                authTrackers.add(trackers.get(i));
            }
        }

        llSuperDevice.removeAllViews();
        llAuthDevice.removeAllViews();

        for (int i = 0; i < superTrackers.size(); i++) {
            superUserView = LayoutInflater.from(this).inflate(
                    R.layout.layout_device_item, null);
            final EditText etTrackerNo = (EditText) superUserView
                    .findViewById(R.id.et_tracker_no);
            final EditText etSIMNo = (EditText) superUserView
                    .findViewById(R.id.et_sim_no);
            final ImageView ivCancel = (ImageView) superUserView
                    .findViewById(R.id.iv_cancel);
            final ImageView ivEdit = (ImageView) superUserView
                    .findViewById(R.id.iv_edit);
            final TextView tvRanges = (TextView) superUserView
                    .findViewById(R.id.tv_ranges);

            Spinner spinner = (Spinner) superUserView
                    .findViewById(R.id.spinner);

            ArrayAdapter<String> adapter;
            if (5 == superTrackers.get(i).ranges) {
                adapter = new ArrayAdapter<String>(this,
                        R.layout.layout_spinner2, getResources()
                        .getStringArray(R.array.ranges));
                adapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setClickable(false);
                spinner.setSelection(0);
            } else {
                adapter = new ArrayAdapter<String>(this,
                        R.layout.layout_spinner1, getResources()
                        .getStringArray(R.array.ranges));
                adapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(superTrackers.get(i).ranges - 1);
            }

            tvRanges.setTag(superTrackers.get(i).ranges);

            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    if (5 == Integer.parseInt(tvRanges.getTag().toString())) {
                        return;
                    }

                    int ranges = position + 1;

                    if (ranges != Integer
                            .parseInt(tvRanges.getTag().toString())) {
                        editTrackerRange(
                                superTrackers.get(Integer.parseInt(etTrackerNo
                                        .getTag().toString())).device_sn,
                                ranges);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            etTrackerNo.setText(superTrackers.get(i).device_sn);
            etTrackerNo.setTag(i);
            ivEdit.setTag(i);
            etSIMNo.setText(superTrackers.get(i).tracker_sim);
            ivCancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    DialogUtil.show(UserInfoActivity.this, R.string.unbind,
                            R.string.notice_unbind, R.string.confirm,
                            new OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    DialogUtil.dismiss();

                                    cancelAuthNetworkConnect(superTrackers
                                            .get(Integer.parseInt(etTrackerNo
                                                    .getTag().toString())).device_sn);
                                }
                            }, R.string.cancel, new OnClickListener() {

                                @Override
                                public void onClick(View arg0) {
                                    DialogUtil.dismiss();
                                }
                            });
                }
            });
            ivEdit.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    iPosition = Integer.parseInt(ivEdit.getTag().toString());

                    DialogUtil.showEditDialog(UserInfoActivity.this,
                            R.string.device_sim_no, R.string.confirm,
                            R.string.cancel, UserInfoActivity.this, etSIMNo
                                    .getText().toString(), "");
                }
            });

            llSuperDevice.addView(superUserView);
        }

        if (authTrackers.size() > 0) {
            for (int i = 0; i < authTrackers.size(); i++) {
                authUserView = LayoutInflater.from(this).inflate(
                        R.layout.layout_device_item1, null);
                final EditText etTrackerNo = (EditText) authUserView
                        .findViewById(R.id.et_tracker_no);
                final ImageView ivCancel = (ImageView) authUserView
                        .findViewById(R.id.iv_cancel);
                authUserView.findViewById(R.id.ll_auth)
                        .setVisibility(View.GONE);
                etTrackerNo.setText(authTrackers.get(i).device_sn);
                etTrackerNo.setTag(i);
                llAuthDevice.addView(authUserView);

                ivCancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.show(UserInfoActivity.this, R.string.unbind,
                                R.string.notice_unbind, R.string.confirm,
                                new OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        DialogUtil.dismiss();

                                        cancelAuthNetworkConnect(authTrackers
                                                .get(Integer
                                                        .parseInt(etTrackerNo
                                                                .getTag()
                                                                .toString())).device_sn);
                                    }
                                }, R.string.cancel, new OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        DialogUtil.dismiss();
                                    }
                                });
                    }
                });
            }
        }

        tvDeviceNum.setText(String.valueOf(superTrackers.size()));
        tvDeviceNumAuth.setText(String.valueOf(authTrackers.size()));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (0 == requestCode) {
            user = UserSP.getInstance().getUserInfo(this);
            setDeviceInfo();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.iv_title_right_setting:
                startActivity(new Intent(UserInfoActivity.this,
                        PasswdUpdateActivity.class));
                break;
            case R.id.ll_device:
                llSuperDevice.setVisibility(View.VISIBLE);
                llAuthDevice.setVisibility(View.GONE);

                tvDevice.setSelected(true);
                tvDeviceAuth.setSelected(false);
                tvDeviceNum.setBackgroundResource(R.drawable.bg_num_orange1);
                tvDeviceNumAuth.setBackgroundResource(R.drawable.bg_num_grey1);
                vDeviceLine.setBackgroundColor(getResources().getColor(
                        R.color.bg_theme));
                vDeviceLineAuth.setBackgroundColor(getResources().getColor(
                        R.color.grey_bg));
                break;
            case R.id.ll_device_auth:
                llSuperDevice.setVisibility(View.GONE);
                llAuthDevice.setVisibility(View.VISIBLE);

                tvDevice.setSelected(false);
                tvDeviceAuth.setSelected(true);
                tvDeviceNum.setBackgroundResource(R.drawable.bg_num_grey1);
                tvDeviceNumAuth.setBackgroundResource(R.drawable.bg_num_orange1);
                vDeviceLine.setBackgroundColor(getResources().getColor(
                        R.color.grey_bg));
                vDeviceLineAuth.setBackgroundColor(getResources().getColor(
                        R.color.bg_theme));
                break;
        }
    }

    private AlertDialog mDialog;

    @Override
    public void editTextEdit(String str, AlertDialog mDialog) {
        String sSimNo = str;
        if (!Utils.isCorrectMobile(sSimNo)) {
            ToastUtil.show(UserInfoActivity.this, R.string.input_tracker_sim);
            return;
        }
        this.mDialog = mDialog;
        String sTrackerNo = superTrackers.get(iPosition).device_sn;
        editSimNo(sTrackerNo, sSimNo);
    }

    private void editSimNo(final String trackerNo, final String simNo) {
        if (UserUtil.isGuest(this)) {
            ToastUtil.show(this, R.string.guest_no_set);
            return;
        }

        String url = UserUtil.getServerUrl(this);
        String userName = UserSP.getInstance().getUserName(this);

        RequestParams params = HttpParams.modifySIM(userName, trackerNo, simNo);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                UserInfoActivity.this, null,
                                UserInfoActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            // simNoPop.dismiss();

                            mDialog.dismiss();

                            UserUtil.reviseSimNo(UserInfoActivity.this,
                                    trackerNo, simNo);

                            setDeviceInfo();
                        }
                        ToastUtil.show(UserInfoActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(UserInfoActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });

    }

    private void editTrackerRange(final String trackerNo, final int range) {
        if (UserUtil.isGuest(this)) {
            ToastUtil.show(this, R.string.guest_no_set);
            return;
        }

        String url = UserUtil.getServerUrl(this);

        RequestParams params = HttpParams.changeDeviceRanges(trackerNo, range);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                UserInfoActivity.this, null,
                                UserInfoActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            Tracker mCurTracker = UserUtil
                                    .getCurrentTracker(UserInfoActivity.this);
                            if (mCurTracker.device_sn.equals(trackerNo)) {
                                mCurTracker.ranges = range;
                                UserUtil.saveCurrentTracker(
                                        UserInfoActivity.this, mCurTracker);
                                sendBroadcast(new Intent(
                                        Constants.ACTION_TRACTER_RANGES_CHANGE));
                            }
                            UserUtil.reviseRanges(UserInfoActivity.this,
                                    trackerNo, range);
                        }
                        ToastUtil.show(UserInfoActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(UserInfoActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });

    }

    /**
     * 解除绑定
     */
    private void cancelAuthNetworkConnect(final String trackerNo) {
        if (UserUtil.isGuest(this)) {
            ToastUtil.show(this, R.string.guest_no_set);
            return;
        }

        String url = UserUtil.getServerUrl(this);

        String userName = UserSP.getInstance().getUserName(this);

        RequestParams params = HttpParams.cancelAuthorization(trackerNo,
                userName);

        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                UserInfoActivity.this, null,
                                UserInfoActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        ToastUtil.show(UserInfoActivity.this, obj.what);
                        if (obj.code == 0) {
                            UserUtil.deleteTracker(UserInfoActivity.this,
                                    trackerNo);

                            List<Tracker> trackers = UserUtil
                                    .getUserInfo(UserInfoActivity.this).device_list;
                            if (trackers != null && trackers.size() > 0) {
                                if (trackerNo.equals(UserUtil
                                        .getCurrentTracker(UserInfoActivity.this).device_sn)) {
                                    UserUtil.saveCurrentTracker(
                                            UserInfoActivity.this,
                                            trackers.get(0));
                                    sendBroadcast(new Intent(
                                            Constants.ACTION_TRACTER_CHANGE));
                                }

                                setDeviceInfo();
                            } else {
                                UserUtil.saveCurrentTracker(
                                        UserInfoActivity.this, null);

                                DialogUtil.show(UserInfoActivity.this,
                                        R.string.prompt,
                                        R.string.notice_no_trackers,
                                        R.string.confirm,
                                        new OnClickListener() {

                                            @Override
                                            public void onClick(View arg0) {
                                                DialogUtil.dismiss();

                                                Intent intent = new Intent(
                                                        UserInfoActivity.this,
                                                        LoginActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(UserInfoActivity.this,
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
