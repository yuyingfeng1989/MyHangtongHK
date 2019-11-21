package com.bluebud.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.User;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.utils.WheelViewUtil;
import com.bluebud.utils.WheelViewUtil.OnWheeClicked;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;


public class TimeZoneActivity extends BaseActivity implements OnClickListener,
        OnWheeClicked, OnProgressDialogClickListener {
    private Spinner spAddress;
    private TextView tvCurTime;
    private TextView tvNewTime;
    private LinearLayout llNewTime;
    private ImageView ivNext;
    private Button btnSubmit;

    private WheelViewUtil wheelViewUtil;

    private String[] arrTimeZone;
    private int iTimeZoneId = 0;
    private long iTimeZoneCheck = 0;

    private String sCurTime;
    private String sNewTime;
    private String sNewTimeDay;
    private String sNewTimeHour;
    private String sNewTimeRe;
    private String sNewTimeHourRe;
    private boolean bTimeTe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_time_zone);

        iTimeZoneId = UserUtil.getUserInfo(this).timezone_id;
        iTimeZoneCheck = UserUtil.getUserInfo(this).timezone_check;
        LogUtil.i(iTimeZoneId + " " + iTimeZoneCheck);
        init();
    }

    private void init() {
        setBaseTitleText(R.string.timezone_app);

        super.setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        super.setBaseTitleRightText(R.string.submit1);
        super.setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);

        spAddress = (Spinner) findViewById(R.id.spinner);
        tvCurTime = (TextView) findViewById(R.id.tv_cur_time);
        tvNewTime = (TextView) findViewById(R.id.tv_new_time);
        llNewTime = (LinearLayout) findViewById(R.id.ll_new_time);
        ivNext = (ImageView) findViewById(R.id.iv_next);
        btnSubmit = (Button) findViewById(R.id.btn_submit);

        llNewTime.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        arrTimeZone = getResources().getStringArray(R.array.time_zone);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.layout_spinner, arrTimeZone);
        adapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        spAddress.setAdapter(adapter);
        spAddress.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (iTimeZoneId == arg2) {
                    return;
                }

                iTimeZoneId = arg2;
                if (0 == arg2) {
                    return;
                }

                sNewTime = Utils.getTimeForTimeZone(iTimeZoneId, arrTimeZone);
                sNewTimeDay = Utils.dateString2YearDay(sNewTime);
                sNewTimeHour = Utils.dateString2Hour(sNewTime);
                LogUtil.i(sNewTime);

                tvNewTime.setText(getString(R.string.new_time, sNewTime));
                ivNext.setVisibility(View.VISIBLE);
                llNewTime.setClickable(true);

                bTimeTe = false;
                iTimeZoneCheck = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        if (0 < iTimeZoneId && iTimeZoneId <= 88) {
            spAddress.setSelection(iTimeZoneId);

            sCurTime = Utils.getTimeForTimeZone(iTimeZoneId, arrTimeZone);
            LogUtil.i(sCurTime);

            tvCurTime.setText(getString(R.string.current_time, sCurTime));

            if (iTimeZoneCheck > 0) {
                sNewTime = Utils.getTimeForTimeZone(iTimeZoneId,
                        arrTimeZone, iTimeZoneCheck * 1000);
            } else {
                sNewTime = sCurTime;
            }
            LogUtil.i("sNewTime:" + sNewTime);
            tvNewTime.setText(getString(R.string.new_time, sNewTime));
            sNewTimeDay = Utils.dateString2YearDay(sNewTime);
            sNewTimeHour = Utils.dateString2Hour(sNewTime);
        } else {
            tvCurTime.setText(getString(R.string.current_time, ""));
            tvNewTime.setText(getString(R.string.new_time, ""));
            ivNext.setVisibility(View.GONE);
            llNewTime.setClickable(false);
        }

        wheelViewUtil = new WheelViewUtil(this, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.ll_new_time:
                wheelViewUtil.showTime(sNewTimeHour);
                break;
            case R.id.btn_submit:
                if (UserUtil.isGuest(this)) {
                    ToastUtil.show(this, R.string.guest_no_set);
                    return;
                }

                submit();
                break;
            case R.id.rl_title_right_text:
                if (UserUtil.isGuest(this)) {
                    ToastUtil.show(this, R.string.guest_no_set);
                    return;
                }
                submit();
                break;
        }
    }

    @Override
    public void getWheelDay(String sDay) {
    }

    @Override
    public void getWheelTime(String sTime) {
        sNewTimeHourRe = sTime;
        sNewTimeRe = sNewTimeDay + " " + sNewTimeHourRe;
        tvNewTime.setText(getString(R.string.new_time, sNewTimeRe));

        bTimeTe = true;
    }

    @Override
    public void onProgressDialogBack() {
    }

    private void submit() {
        if (0 == iTimeZoneId) {
            ToastUtil.show(this, R.string.time_zone_select_pormpt);
            return;
        }

        if (bTimeTe) {
            long lNewTime = Utils.getLongTime(sNewTime);
            long lNewTimeRe = Utils.getLongTime(sNewTimeRe);
            iTimeZoneCheck = (lNewTimeRe - lNewTime) / 1000;
            LogUtil.i("time1:" + lNewTimeRe);
            LogUtil.i("time2:" + lNewTime);
            LogUtil.i("time:" + iTimeZoneCheck);
        }

        long totalTime = Utils.timezone(arrTimeZone[iTimeZoneId]) + iTimeZoneCheck;
        LogUtil.i("totalTime:" + totalTime);

        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.setTimeZone(totalTime, iTimeZoneId,
                iTimeZoneCheck);

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(TimeZoneActivity.this, null, TimeZoneActivity.this);
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
                            User user = UserUtil.getUserInfo(TimeZoneActivity.this);
                            user.timezone_id = iTimeZoneId;
                            user.timezone_check = iTimeZoneCheck;
                            UserUtil.savaUserInfo(TimeZoneActivity.this, user);
                        }
                        ToastUtil.show(TimeZoneActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeZoneActivity.this,
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
    public void getFenceRange(String sRange) {

    }

}
