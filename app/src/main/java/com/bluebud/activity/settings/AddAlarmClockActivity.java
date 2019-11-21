package com.bluebud.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.utils.GsonParse;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.PopupWindowCheckBoxUtils;
import com.bluebud.utils.PopupWindowCheckBoxUtils.OnCheckBoxTime;
import com.bluebud.utils.PopupWindowWheelViewUtils;
import com.bluebud.utils.PopupWindowWheelViewUtils.OnWheeClicked;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.Calendar;

public class AddAlarmClockActivity extends BaseActivity implements OnClickListener, OnCheckBoxTime, OnWheeClicked {

    private TextView tvTime;
    private RelativeLayout rlWeek;
    private TextView tvWeek;
    private Tracker mTracker;

    private PopupWindowCheckBoxUtils checkBoxUtils;
    private String sTime;
    private PopupWindowWheelViewUtils wheelViewUtils;
    //    private Context context;
    private String deviceNo;


    private String days;
    private String time = "00:00";

    private RelativeLayout rlTime;
    private String week;
    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_add_alarm_clock);
        checkBoxUtils = new PopupWindowCheckBoxUtils(this, this);
        wheelViewUtils = new PopupWindowWheelViewUtils(this, this);
        getData();

        init();
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            time = intent.getStringExtra("time");
            week = intent.getStringExtra("week");
            index = intent.getIntExtra("index", 1);
            LogUtil.e("time:" + time + ",week:" + week + ",index:" + index);
        }
    }

    private void init() {
        setBaseTitleText(R.string.set_alarm_clock);
        super.setBaseTitleVisible(View.VISIBLE);
        getBaseTitleLeftBack().setOnClickListener(this);
        super.setBaseTitleRightText(R.string.submit1);
        super.setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        mTracker = UserUtil.getCurrentTracker(this);
        if (mTracker != null) {
            deviceNo = mTracker.device_sn;
        }
        tvTime = (TextView) findViewById(R.id.tv_time);//时间
        rlWeek = (RelativeLayout) findViewById(R.id.rl_week);//星期
        tvWeek = (TextView) findViewById(R.id.tv_week);//星期
        rlTime = (RelativeLayout) findViewById(R.id.rl_time);//时间
        rlTime.setOnClickListener(this);
        rlWeek.setOnClickListener(this);
        tvTime.setText(time);
        tvWeek.setText(Utils.strDaylongToWeek(AddAlarmClockActivity.this, Utils.strDayToWeek1(AddAlarmClockActivity.this, week)));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_title_back:
                finish();
                break;
            case R.id.rl_time:
                if (Utils.isEmpty(sTime)) {
                    Calendar calendar = Calendar.getInstance();
                    wheelViewUtils.ShowTime(Utils.getDate(calendar), true);
                } else {
                    wheelViewUtils.ShowTime(sTime, true);
                }
                break;

            case R.id.rl_week://选择周中的某一天
                if (Utils.isEmpty(week)) {
                    checkBoxUtils.ShowCheckBox(getString(R.string.repeat), "1,2,3,4,5");
                } else {
                    checkBoxUtils.ShowCheckBox(getString(R.string.repeat), week);
                }
                break;
            case R.id.rl_title_right_text:
                setDeviceRemind();
                break;
        }
    }

    //周时间
    @Override
    public void getCheckBoxTime(String Time) {
        week = Time;
        tvWeek.setText(Utils.strDaylongToWeek(AddAlarmClockActivity.this, Utils.strDayToWeek1(AddAlarmClockActivity.this, Time)));
    }

    //时间
    @Override
    public void getWheelTime(String sTime, Boolean ison) {
        this.sTime = sTime;
        time = sTime;
        tvTime.setText(sTime);
    }

    public void setDeviceRemind() {
        if (mTracker == null) {
            return;
        }
        String url = UserUtil.getServerUrl(this);
        //01:20-1-3-0110110;
        LogUtil.i("week666:" + week);
        days = Utils.strDayToWeek1(this, week);
        time = tvTime.getText().toString().trim();
        final String times = time + "-" + "1" + "-" + "3" + "-" + days;
        RequestParams params = HttpParams.setDeviceRemind(deviceNo, index, times);
        HttpClientUsage.getInstance().post(this, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(AddAlarmClockActivity.this);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null) {
                            return;
                        }
                        if (obj.code == 0) {
                            Intent intent = new Intent();
                            intent.putExtra("data", times);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            ToastUtil.show(AddAlarmClockActivity.this, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(AddAlarmClockActivity.this, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }
}
