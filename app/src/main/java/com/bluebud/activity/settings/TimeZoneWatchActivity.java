package com.bluebud.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.LanguageInfo;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.TimeZoneInfo;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ProgressDialogUtil.OnProgressDialogClickListener;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.bluebud.utils.WheelViewUtil.OnWheeClicked;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;


public class TimeZoneWatchActivity extends BaseActivity implements
        OnClickListener, OnWheeClicked, OnProgressDialogClickListener {
    private Spinner spAddress;
    private Spinner spLanguage;
    private TextView tvCurTime;
    private TextView tvNewTime;
    private Button btnSubmit;

    private String[] arrTimeZone;
    private String[] arrLanguage;
    private int iTimeZoneId = 31;
    private long iTimeZoneCheck = 0;
    private int iLanguage = 0;
    private String sCurTime;
    private String sNewTime;
    private String sNewTimeDay;
    //	private String sNewTimeHour;
    private String sNewTimeRe;
    private String sNewTimeHourRe;

    private boolean bTimeTe = false;

    private Tracker mTracker;

    //	private RequestHandle requestHandle;
    private LinearLayout llLanguage;
    private int protocol_type = 0;
    private String languageType;
    private List<LanguageInfo> languageList;
    private ArrayAdapter<String> languageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_time_zone_watch);

        mTracker = UserUtil.getCurrentTracker(this);
        if (mTracker != null) {
            protocol_type = mTracker.protocol_type;
        }
        init();
        getTimezone();
    }

    private void init() {
        if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
            setBaseTitleText(R.string.timezone_watch5);
        } else {
            setBaseTitleText(R.string.timezone_watch);
        }
        setBaseTitleVisible(View.VISIBLE);
        super.setBaseTitleRightText(R.string.submit1);
        super.setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        getBaseTitleLeftBack().setOnClickListener(this);

        spAddress = (Spinner) findViewById(R.id.spinner);
        tvCurTime = (TextView) findViewById(R.id.tv_cur_time);
        tvNewTime = (TextView) findViewById(R.id.tv_new_time);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        spLanguage = (Spinner) findViewById(R.id.spinner1);
        llLanguage = (LinearLayout) findViewById(R.id.ll_language);
        if (protocol_type == 5 || protocol_type == 6 || protocol_type == 7) {
            llLanguage.setVisibility(View.VISIBLE);
        } else {
            llLanguage.setVisibility(View.GONE);
        }
        btnSubmit.setOnClickListener(this);

        arrTimeZone = getResources().getStringArray(R.array.time_zone);
        //arrLanguage = getResources().getStringArray(R.array.language);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.layout_spinner, arrTimeZone);

        adapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        languageAdapter = new ArrayAdapter<String>(this, R.layout.layout_spinner);

        languageAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        spAddress.setAdapter(adapter);
        spLanguage.setAdapter(languageAdapter);
        spLanguage.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                //0表示英文，1简体，2繁体，3葡萄牙4.西班牙，5.德语

                //iLanguage=arg2;

                if (languageList != null && languageList.size() > 0) {
                    iLanguage = languageList.get(arg2).index;
                    LogUtil.i("index:" + languageList.get(arg2).index);
                } else {
                    LogUtil.i("languageList is null");
                }

                LogUtil.i("arg2=" + arg2 + ",iLanguage:" + iLanguage);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        spAddress.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (iTimeZoneId == arg2) {
                    LogUtil.i("select id :" + arg2);
                    return;
                }

                iTimeZoneId = arg2;
                if (0 == arg2) {
                    return;
                }

                sNewTime = Utils.getTimeForTimeZone(iTimeZoneId, arrTimeZone);
                sNewTimeDay = Utils.dateString2YearDay(sNewTime);
//				sNewTimeHour = Utils.dateString2Hour(sNewTime);
                LogUtil.i(sNewTime);
                tvNewTime.setText(getString(R.string.new_time, sNewTime));

                bTimeTe = false;
                iTimeZoneCheck = 0;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        LogUtil.i("select iTimeZoneId :" + iTimeZoneId);
        setTimeZone();
    }

    private void setTimeZone() {
        LogUtil.i("select iTimeZoneId1 :" + iTimeZoneId);
        if (0 < iTimeZoneId && iTimeZoneId <= 88) {
            spAddress.setSelection(iTimeZoneId);

            sCurTime = Utils.getTimeForTimeZone(iTimeZoneId, arrTimeZone);
            LogUtil.i(sCurTime);

            tvCurTime.setText(getString(R.string.current_time, sCurTime));

            if (iTimeZoneCheck > 0) {
                sNewTime = Utils.getTimeForTimeZone(iTimeZoneId, arrTimeZone,
                        iTimeZoneCheck * 1000);
            } else {
                sNewTime = sCurTime;
            }
            LogUtil.i("sNewTime:" + sNewTime);
            tvNewTime.setText(getString(R.string.new_time, sNewTime));
            sNewTimeDay = Utils.dateString2YearDay(sNewTime);
//			sNewTimeHour = Utils.dateString2Hour(sNewTime);
        } else {
            tvCurTime.setText(getString(R.string.current_time, ""));
            tvNewTime.setText(getString(R.string.new_time, ""));
        }


        if (!Utils.isEmpty(languageType)) {
            languageList = new ArrayList<LanguageInfo>();
            String[] split = languageType.split(",");
            arrLanguage = new String[split.length];
            for (int i = 0; i < split.length; i++) {
                LogUtil.i("split[" + i + "]:" + split[i]);
                String[] split2 = split[i].split(":");
                LanguageInfo languageInfo = new LanguageInfo();
                languageInfo.index = Integer.parseInt(split2[1]);
                languageInfo.language = split2[0];
                languageList.add(languageInfo);
                arrLanguage[i] = split2[0];
                if (iLanguage == Integer.parseInt(split2[1])) {
                    spLanguage.setSelection(i);//设置语言选择上的
                }
            }
            languageAdapter.addAll(arrLanguage);
            languageAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.rl_title_right_text:
                if (Utils.isSuperUser(mTracker, TimeZoneWatchActivity.this)) {
                    submit();
                }
                break;


//		case R.id.btn_submit:
//			if (UserUtil.isGuest(this)) {
//				ToastUtil.show(this, R.string.guest_no_set);
//				return;
//			}
//
//			submit();
            //	break;
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
//		LogUtil.i("onProgressDialogBack()");
//		if (null != requestHandle && !requestHandle.isFinished()) {
//			requestHandle.cancel(true);
//		}
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

        long totalTime = Utils.timezone(arrTimeZone[iTimeZoneId])
                + iTimeZoneCheck;
        LogUtil.i("totalTime:" + totalTime);

        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.setTimeZoneWatch(mTracker.device_sn,
                totalTime, iTimeZoneId, iLanguage);

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TimeZoneWatchActivity.this, null,
                                TimeZoneWatchActivity.this);
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
                        }
                        ToastUtil.show(TimeZoneWatchActivity.this, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeZoneWatchActivity.this,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void getTimezone() {
        String url = UserUtil.getServerUrl(this);
        RequestParams params = HttpParams.getTimeZoneWatch(mTracker.device_sn);

        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {


                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                TimeZoneWatchActivity.this, null,
                                TimeZoneWatchActivity.this);
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
                            TimeZoneInfo timeZoneInfo = GsonParse
                                    .getTimeZone(new String(response));
                            iTimeZoneId = timeZoneInfo.timezoneid;
                            LogUtil.i(iTimeZoneId + " " + iTimeZoneId);
                            iLanguage = timeZoneInfo.language;
                            languageType = timeZoneInfo.languageType;
                            setTimeZone();
                        } else {
                            ToastUtil.show(TimeZoneWatchActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(TimeZoneWatchActivity.this,
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
