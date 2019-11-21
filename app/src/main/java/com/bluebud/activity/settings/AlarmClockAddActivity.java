package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.data.dao.AlarmClockDao;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.AlarmClockInfo;
import com.bluebud.info.ReBaseObj;
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
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class AlarmClockAddActivity extends BaseActivity implements
        OnClickListener, OnWheeClicked, OnProgressDialogClickListener {
    private EditText etTtile;
    private RadioButton rbAnniversaries;
    private RadioButton rbRepaymentDate;
    private RadioButton rbRepeatEveryWeek;
    private LinearLayout llWeek;
    private LinearLayout llTime;
    private LinearLayout llTimeAdd;
    private RelativeLayout rlDayAdd;
    private TextView tvDay;
    private TextView tvWeek1;
    private TextView tvWeek2;
    private TextView tvWeek3;
    private TextView tvWeek4;
    private TextView tvWeek5;
    private TextView tvWeek6;
    private TextView tvWeek7;
    private Button btnSubmit;
    private LinearLayout llMonthLast;
    private CheckBox cbMonthLast;

    private AlarmClockInfo alarmClockInfo;
    private int type;
    private String strTitle;
    private boolean bAddTime = true;
    private int iTimeNumRe = -1;
    private int iType = 0;
    private List<String> times = new ArrayList<String>();
    private String[] arrWeeks = {"0", "0", "0", "0", "0", "0", "0"};
    private int[] arrTvResId = {R.id.tv_week_1, R.id.tv_week_2,
            R.id.tv_week_3, R.id.tv_week_4, R.id.tv_week_5, R.id.tv_week_6,
            R.id.tv_week_7};
    private String sDay;

    private WheelViewUtil wheelViewUtil;

    private AlarmClockDao alarmClockDao;
    private String sUserName;

    private RequestHandle requestHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_alarm_clock_add);
        init();
        sUserName = UserSP.getInstance().getUserName(this);

        wheelViewUtil = new WheelViewUtil(this, this);
        alarmClockDao = new AlarmClockDao(this);

        alarmClockInfo = new AlarmClockInfo();

        Intent extraIntent = getIntent();
        type = extraIntent.getIntExtra("TYPE", 0);
        if (1 == type) {
            alarmClockInfo = (AlarmClockInfo) extraIntent
                    .getSerializableExtra("CLOCK_INFO");
            setData();
        }
    }

    private void init() {
        setBaseTitleText(R.string.alarm_clock_add);
        getBaseTitleLeftBack().setOnClickListener(this);

        etTtile = (EditText) findViewById(R.id.et_title);
        llTime = (LinearLayout) findViewById(R.id.ll_time);
        llTimeAdd = (LinearLayout) findViewById(R.id.ll_time_add);
        rlDayAdd = (RelativeLayout) findViewById(R.id.rl_day);
        tvDay = (TextView) findViewById(R.id.tv_day);
        rbAnniversaries = (RadioButton) findViewById(R.id.rb_anniversaries);
        rbRepaymentDate = (RadioButton) findViewById(R.id.rb_repayment_date);
        rbRepeatEveryWeek = (RadioButton) findViewById(R.id.rb_repeat_every_week);
        llWeek = (LinearLayout) findViewById(R.id.ll_week);
        tvWeek1 = (TextView) findViewById(R.id.tv_week_1);
        tvWeek2 = (TextView) findViewById(R.id.tv_week_2);
        tvWeek3 = (TextView) findViewById(R.id.tv_week_3);
        tvWeek4 = (TextView) findViewById(R.id.tv_week_4);
        tvWeek5 = (TextView) findViewById(R.id.tv_week_5);
        tvWeek6 = (TextView) findViewById(R.id.tv_week_6);
        tvWeek7 = (TextView) findViewById(R.id.tv_week_7);
        btnSubmit = (Button) findViewById(R.id.btn_commit);
        llMonthLast = (LinearLayout) findViewById(R.id.ll_month_last);
        cbMonthLast = (CheckBox) findViewById(R.id.cb_month_last);
        cbMonthLast.setChecked(true);

        rbAnniversaries.setOnClickListener(this);
        rbRepaymentDate.setOnClickListener(this);
        rbRepeatEveryWeek.setOnClickListener(this);

        llTimeAdd.setOnClickListener(this);
        rlDayAdd.setOnClickListener(this);
        tvWeek1.setOnClickListener(this);
        tvWeek2.setOnClickListener(this);
        tvWeek3.setOnClickListener(this);
        tvWeek4.setOnClickListener(this);
        tvWeek5.setOnClickListener(this);
        tvWeek6.setOnClickListener(this);
        tvWeek7.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        sDay = Utils.curDate2CharDays1(this);
        tvDay.setText(Utils.curDate2Week(this));
    }

    private void setData() {
        etTtile.setText(alarmClockInfo.title);
        btnSubmit.setVisibility(View.VISIBLE);

        times = alarmClockInfo.times;
        setTime();

        if (0 == alarmClockInfo.iType) {
            iType = 0;
            rbAnniversaries.setChecked(true);
            rbRepaymentDate.setChecked(false);
            rbRepeatEveryWeek.setChecked(false);
            tvDay.setText(Utils.dateString2Week(this, alarmClockInfo.sDay));
            sDay = alarmClockInfo.sDay;
        } else if (1 == alarmClockInfo.iType) {
            iType = 1;
            rbAnniversaries.setChecked(false);
            rbRepaymentDate.setChecked(true);
            rbRepeatEveryWeek.setChecked(false);
            tvDay.setText(Utils.dateString2Week(this, alarmClockInfo.sDay));
            sDay = alarmClockInfo.sDay;

            String day = Utils.dateString2Day(sDay);
            if (day.equals("29") || day.equals("30") || day.equals("31")) {
                llMonthLast.setVisibility(View.VISIBLE);
            }
            if (alarmClockInfo.isEnd) {
                cbMonthLast.setChecked(true);
            } else {
                cbMonthLast.setChecked(false);
            }
        } else if (2 == alarmClockInfo.iType) {
            iType = 2;
            rbAnniversaries.setChecked(false);
            rbRepaymentDate.setChecked(false);
            rbRepeatEveryWeek.setChecked(true);
            rlDayAdd.setVisibility(View.GONE);
            llWeek.setVisibility(View.VISIBLE);

            for (int i = 0; i < alarmClockInfo.arrWeeks.length; i++) {
                if ("1".equals(alarmClockInfo.arrWeeks[i])) {
                    arrWeeks[i] = "1";
                    findViewById(arrTvResId[i]).setBackgroundResource(
                            R.drawable.bg_week_pressed);
                    ((TextView) findViewById(arrTvResId[i]))
                            .setTextColor(getResources()
                                    .getColor(R.color.white));
                }
            }
        }
    }

    private void setTime() {
        llTime.removeAllViews();
        for (int i = 0; i < times.size(); i++) {
            addTime(times.get(i), i);
        }
    }

    private void addTime(final String sTime, final int position) {
        final View vTime = LayoutInflater.from(this).inflate(
                R.layout.layout_alarm_clock_time_item, null);
        final TextView tvNum = (TextView) vTime.findViewById(R.id.tv_num);
        final TextView tvTime = (TextView) vTime.findViewById(R.id.tv_time);
        ImageView ivDelete = (ImageView) vTime
                .findViewById(R.id.iv_time_delete);
        tvNum.setText("" + (position + 1));
        tvTime.setText("" + sTime);

        vTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                bAddTime = false;
                iTimeNumRe = Integer.parseInt(tvNum.getText().toString()) - 1;
                wheelViewUtil.showTime(tvTime.getText().toString());
            }
        });

        ivDelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                times.remove(position);
                setTime();
            }
        });

        llTime.addView(vTime);
    }

    private void setTextViewBackground(int position, TextView tv) {
        if ("0".equals(arrWeeks[position])) {
            arrWeeks[position] = "1";
            tv.setBackgroundResource(R.drawable.bg_week_pressed);
            tv.setTextColor(getResources().getColor(R.color.white));
        } else {
            arrWeeks[position] = "0";
            tv.setBackgroundResource(R.drawable.bg_week_nor);
            tv.setTextColor(getResources().getColor(R.color.black));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.ll_time_add:
                bAddTime = true;
                wheelViewUtil.showTime(Utils.curDate2Hour(this));
                break;
            case R.id.rl_day:
                wheelViewUtil.showDay(sDay);
                break;
            case R.id.tv_week_1:
                setTextViewBackground(0, tvWeek1);
                break;
            case R.id.tv_week_2:
                setTextViewBackground(1, tvWeek2);
                break;
            case R.id.tv_week_3:
                setTextViewBackground(2, tvWeek3);
                break;
            case R.id.tv_week_4:
                setTextViewBackground(3, tvWeek4);
                break;
            case R.id.tv_week_5:
                setTextViewBackground(4, tvWeek5);
                break;
            case R.id.tv_week_6:
                setTextViewBackground(5, tvWeek6);
                break;
            case R.id.tv_week_7:
                setTextViewBackground(6, tvWeek7);
                break;
            case R.id.btn_commit:
                if (UserUtil.isGuest(this)) {
                    ToastUtil.show(this, R.string.guest_no_set);
                    return;
                }

                addAlarmClock();
                break;
            case R.id.rb_anniversaries:
                iType = 0;
                if (View.VISIBLE == llWeek.getVisibility()) {
                    llWeek.setVisibility(View.GONE);
                }
                if (View.GONE == rlDayAdd.getVisibility()) {
                    rlDayAdd.setVisibility(View.VISIBLE);
                }
                rbRepaymentDate.setChecked(false);
                rbRepeatEveryWeek.setChecked(false);

                llMonthLast.setVisibility(View.GONE);
                break;
            case R.id.rb_repayment_date:
                iType = 1;
                if (View.VISIBLE == llWeek.getVisibility()) {
                    llWeek.setVisibility(View.GONE);
                }
                if (View.GONE == rlDayAdd.getVisibility()) {
                    rlDayAdd.setVisibility(View.VISIBLE);
                }
                rbAnniversaries.setChecked(false);
                rbRepeatEveryWeek.setChecked(false);

                String day = Utils.dateString2Day(sDay);
                if (day.equals("29") || day.equals("30") || day.equals("31")) {
                    llMonthLast.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rb_repeat_every_week:
                iType = 2;
                rlDayAdd.setVisibility(View.GONE);
                llWeek.setVisibility(View.VISIBLE);
                rbAnniversaries.setChecked(false);
                rbRepaymentDate.setChecked(false);

                llMonthLast.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    @Override
    public void getWheelDay(String sDayTime) {
        LogUtil.i(sDayTime);
        this.sDay = sDayTime;
        tvDay.setText(Utils.dateString2Week(this, sDayTime));

        if (rbRepaymentDate.isChecked()) {
            String day = Utils.dateString2Day(sDay);
            if (day.equals("29") || day.equals("30") || day.equals("31")) {
                llMonthLast.setVisibility(View.VISIBLE);
            } else {
                llMonthLast.setVisibility(View.GONE);
            }
        } else {
            llMonthLast.setVisibility(View.GONE);
        }
    }

    @Override
    public void getWheelTime(String sTime) {
        boolean isSame = false;
        if (bAddTime) {
            for (int i = 0; i < times.size(); i++) {
                if (times.get(i).equals(sTime)) {
                    isSame = true;
                    break;
                }
            }
            if (isSame) {
                return;
            }
            times.add(sTime);
            addTime(sTime, times.size() - 1);
        } else {
            for (int i = 0; i < times.size(); i++) {
                if (times.get(i).equals(sTime)) {
                    isSame = true;
                    break;
                }
            }
            if (isSame) {
                return;
            }
            times.set(iTimeNumRe, sTime);
            setTime();
        }

    }

    private void addAlarmClock() {
        strTitle = etTtile.getText().toString().trim();
        if (Utils.isEmpty(strTitle)) {
            ToastUtil.show(this, R.string.alarm_clock_title_input_empty);
            return;
        }
        if (times.size() <= 0) {
            ToastUtil.show(this, R.string.alarm_clock_title_input_time);
            return;
        }
        if (2 == iType) {
            boolean bWeek = false;
            for (int i = 0; i < arrWeeks.length; i++) {
                if ("1".equals(arrWeeks[i])) {
                    bWeek = true;
                    break;
                }
            }
            if (!bWeek) {
                ToastUtil.show(this, R.string.alarm_clock_title_input_week);
                return;
            }
        }

        alarmClockInfo.iType = iType;
        alarmClockInfo.title = strTitle;
        alarmClockInfo.times = times;
        alarmClockInfo.sDay = sDay;
        alarmClockInfo.arrWeeks = arrWeeks;
        alarmClockInfo.sUserName = sUserName;

        alarmClockInfo.isEnd = cbMonthLast.isChecked();

        saveClock();
    }

    private void saveClock() {
        String url = UserUtil.getServerUrl(this);

        alarmClockInfo.repeat_day = Utils.dateString2Day(sDay);
        alarmClockInfo.repeat_month = Utils.dateString2Month(sDay);
        alarmClockInfo.repeat_year = Utils.dateString2Year(sDay);

        int id = 0;
        if (1 == type) {
            id = alarmClockInfo.id;
        } else {
            id = -1;
        }

        RequestParams params = HttpParams.saveClock(id, alarmClockInfo);
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                AlarmClockAddActivity.this, null,
                                AlarmClockAddActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;
                        ToastUtil.show(AlarmClockAddActivity.this, obj.what);
                        if (0 == obj.code) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(AlarmClockAddActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void deleteAlarmClock() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.deleteClock(alarmClockInfo.id);
        requestHandle = HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                AlarmClockAddActivity.this, null,
                                AlarmClockAddActivity.this);
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
                            alarmClockDao.delete(alarmClockInfo.id);
                            setResult(RESULT_OK);
                            finish();
                        }
                        ToastUtil.show(AlarmClockAddActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(AlarmClockAddActivity.this,
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
