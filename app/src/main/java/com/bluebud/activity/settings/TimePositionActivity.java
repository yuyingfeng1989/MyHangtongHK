package com.bluebud.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.bluebud.activity.BaseActivity;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.GPSInterval;
import com.bluebud.info.ReBaseObj;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.Constants;
import com.bluebud.utils.DeviceExpiredUtil;
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

import java.lang.ref.WeakReference;

//定位频率设置
public class TimePositionActivity extends BaseActivity implements
        OnClickListener, OnCheckedChangeListener, OnProgressDialogClickListener {
    //	private Button btnSubmit;
    private RadioGroup radioGroup;
    private int iCheckId = R.id.rbtn_close;
    private int iIndex = -1;
    private int iPosition;
    private int[] m_RbtnIds1 = {R.id.rbtn_one_min, R.id.rbtn_three_min,
            R.id.rbtn_five_min, R.id.rbtn_ten_min, R.id.rbtn_thirty_min,
            R.id.rbtn_sixty_min, R.id.rbtn_close};
    private int[] m_RbtnIds2 = {R.id.rbtn_forth_second, R.id.rbtn_one_min1,
            R.id.rbtn_three_min1, R.id.rbtn_five_min1, R.id.rbtn_ten_min1,
            R.id.rbtn_thirty_min1, R.id.rbtn_sixty_min1, R.id.rbtn_close1};
    private int[] m_RbtnIds3 = {R.id.rbtn_ten_second, R.id.rbtn_forth_second2,
            R.id.rbtn_one_min2, R.id.rbtn_three_min2, R.id.rbtn_five_min2,
            R.id.rbtn_ten_min2, R.id.rbtn_thirty_min2, R.id.rbtn_sixty_min2,
            R.id.rbtn_close2};
    private int[] m_RbtnIds4 = {R.id.rbtn_forth_second4, R.id.rbtn_one_min4,
            R.id.rbtn_two_min4, R.id.rbtn_five_min4, R.id.rbtn_ten_min4,
            R.id.rbtn_thirty_min4, R.id.rbtn_sixty_min4, R.id.rbtn_close4};
    private int[] m_RbtnIds5 = {R.id.rbtn_time1_group5, R.id.rbtn_time10_group5, R.id.rbtn_time30_group5,
            R.id.rbtn_time60_group5, R.id.rbtn_close5};
    private int[] m_Intervals1 = {60, 180, 300, 600, 1800, 3600, 0};
    private int[] m_Intervals2 = {30, 60, 180, 300, 600, 1800, 3600, 0};
    private int[] m_Intervals3 = {10, 30, 60, 180, 300, 600, 1800, 3600, 0};
    private int[] m_Intervals4 = {30, 60, 120, 300, 600, 1800, 3600, 0};
    private int[] m_Intervals5 = {60, 600, 1800, 3600, 0};
    //	private int[] m_Intervals5 = { 60,180, 360, 0 };
    private String strTrackerNo;
    private Tracker mCurTracker;
    private int[] iRbtnIds;
    private int[] iIntervals;
    private RequestHandle requestHandle;
    private TimePositionActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addContentView(R.layout.activity_time_position);
        WeakReference<TimePositionActivity> wf = new WeakReference(this);
        mContext = wf.get();
        init();
        getGPSInterval();
    }

    public void init() {
        setBaseTitleText(R.string.time_location);
        setBaseTitleVisible(View.VISIBLE);
        setBaseTitleRightText(R.string.submit1);
        setBaseTitleRightTextVisible(View.VISIBLE);
        getBaseTitleRightText().setOnClickListener(this);
        getBaseTitleLeftBack().setOnClickListener(this);

        mCurTracker = (Tracker) getIntent().getSerializableExtra(Constants.EXTRA_TRACKER);
        strTrackerNo = mCurTracker.device_sn;
//		btnSubmit = (Button) findViewById(R.id.btn_commit);
        if (1 == mCurTracker.ranges) {
            iRbtnIds = m_RbtnIds1;
            iIntervals = m_Intervals1;
            radioGroup = (RadioGroup) findViewById(R.id.group);
            findViewById(R.id.group4).setVisibility(View.GONE);
            findViewById(R.id.group1).setVisibility(View.GONE);
            findViewById(R.id.group2).setVisibility(View.GONE);
            findViewById(R.id.group5).setVisibility(View.GONE);
        } else if (2 == mCurTracker.ranges) {//[宠物]
            iRbtnIds = m_RbtnIds3;
            iIntervals = m_Intervals3;

            radioGroup = (RadioGroup) findViewById(R.id.group2);
            radioGroup.setVisibility(View.VISIBLE);
            /*//add by zengms
            radioGroup.check(iRbtnIds[4]);
			iCheckId=iRbtnIds[4];*/

            findViewById(R.id.group).setVisibility(View.GONE);
            findViewById(R.id.group1).setVisibility(View.GONE);
            findViewById(R.id.group4).setVisibility(View.GONE);
            findViewById(R.id.group5).setVisibility(View.GONE);
        } else if (3 == mCurTracker.ranges || 6 == mCurTracker.ranges) {
            iRbtnIds = m_RbtnIds2;
            iIntervals = m_Intervals2;

            radioGroup = (RadioGroup) findViewById(R.id.group1);
            radioGroup.setVisibility(View.VISIBLE);

            findViewById(R.id.group).setVisibility(View.GONE);
            findViewById(R.id.group2).setVisibility(View.GONE);
            findViewById(R.id.group4).setVisibility(View.GONE);
            findViewById(R.id.group5).setVisibility(View.GONE);
        } else if (5 == mCurTracker.ranges) {
            if ("24".equalsIgnoreCase(mCurTracker.product_type) || "30".equals(mCurTracker.product_type) || mCurTracker.product_type.equals("31")) {//790 4G手表
                iRbtnIds = m_RbtnIds5;
                iIntervals = m_Intervals5;
                radioGroup = (RadioGroup) findViewById(R.id.group5);
                radioGroup.setVisibility(View.VISIBLE);
                findViewById(R.id.group4).setVisibility(View.GONE);
                findViewById(R.id.group1).setVisibility(View.GONE);
                findViewById(R.id.group2).setVisibility(View.GONE);
                findViewById(R.id.group).setVisibility(View.GONE);
            } else {
                iRbtnIds = m_RbtnIds1;
                iIntervals = m_Intervals1;
                radioGroup = (RadioGroup) findViewById(R.id.group);
                radioGroup.setVisibility(View.VISIBLE);
                findViewById(R.id.group4).setVisibility(View.GONE);
                findViewById(R.id.group1).setVisibility(View.GONE);
                findViewById(R.id.group2).setVisibility(View.GONE);
                findViewById(R.id.group5).setVisibility(View.GONE);
            }
        } else {
            iRbtnIds = m_RbtnIds4;
            iIntervals = m_Intervals4;
            radioGroup = (RadioGroup) findViewById(R.id.group4);
            radioGroup.setVisibility(View.VISIBLE);
            findViewById(R.id.group).setVisibility(View.GONE);
            findViewById(R.id.group1).setVisibility(View.GONE);
            findViewById(R.id.group2).setVisibility(View.GONE);
            findViewById(R.id.group5).setVisibility(View.GONE);
        }

//		btnSubmit.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mCurTracker = UserUtil.getCurrentTracker(mContext);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_title_back:
                finish();

                break;
            case R.id.rl_title_right_text://提交
                if (Utils.isSuperUser(mCurTracker, mContext)) {
                    if (DeviceExpiredUtil.advancedFeatures(mContext, mCurTracker, true))
                        return;
                    setGPSInterval();
                }
                break;
        }
    }

    int i = 0;

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        i++;
        if (i < 3) {
            iCheckId = checkedId;
            LogUtil.i("进：" + i);
        } else {
            LogUtil.i("出：" + i);
            if (Utils.isSuperUser(mCurTracker, mContext)) {
                if (DeviceExpiredUtil.advancedFeatures(mContext, mCurTracker, true)) {
                    if (iIndex == -1)
                        return;
                    radioGroup.check(iRbtnIds[iIndex]);// 设置选中项
                }
                iCheckId = checkedId;
            } else {
                if (iIndex == -1)
                    return;
                radioGroup.check(iRbtnIds[iIndex]);// 设置选中项
            }
        }
        LogUtil.i("选择id icheckid:" + iCheckId);
    }

    @Override
    public void onProgressDialogBack() {
        LogUtil.i("onProgressDialogBack()");
        if (null != requestHandle && !requestHandle.isFinished()) {
            requestHandle.cancel(true);
        }
    }

    /**
     * 获取定位频率
     */
    private void getGPSInterval() {
        String url = UserUtil.getServerUrl(mContext);
        RequestParams params = HttpParams.getGPSInterval(strTrackerNo);
        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(mContext, null, mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null)
                            return;
                        if (obj.code == 0) {
                            GPSInterval gpsInterval = GsonParse.gpsIntervalParse(new String(response));
                            if (gpsInterval == null)
                                return;
                            iIndex = getIndex(gpsInterval.gps_interval);
                            if (iIndex == -1)
                                return;
                            radioGroup.check(iRbtnIds[iIndex]);// 设置选中项
                            iCheckId = iRbtnIds[iIndex];
                            LogUtil.i("index:=" + iIndex + ",id:" + iCheckId);

                            return;
                        }
                        ToastUtil.show(mContext, obj.what);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(mContext,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private void setGPSInterval() {
        int gpsInterval = 0;
        iPosition = getIndexFromId(iCheckId);
        LogUtil.i("iPosition:" + iPosition + ",icheckId:" + iCheckId);
        if (iPosition == -1)
            return;
        gpsInterval = iIntervals[iPosition];

        String url = UserUtil.getServerUrl(mContext);
        LogUtil.i("gpsInterval:" + gpsInterval);
        RequestParams params = HttpParams.setGPSInterval(strTrackerNo,
                gpsInterval);

        requestHandle = HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(
                                mContext, null,
                                mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(
                                response));
                        if (obj == null)
                            return;

                        /**
                         if (0 == obj.code) {
                         iIndex = iPosition;
                         // 实时定位自动刷新
                         mCurTracker.gps_interval = iIntervals[iIndex];

                         UserUtil.reviseFrequency(TimePositionActivity.this,
                         mCurTracker);

                         ToastUtil.show(TimePositionActivity.this, obj.what);
                         } else if(600 == obj.code){
                         ToastUtil.show(TimePositionActivity.this, R.string.offline_alert_switch);
                         }else{
                         ToastUtil.show(TimePositionActivity.this, obj.what);
                         } */

                        // guoqz add 20160309.
                        if (0 == obj.code) {
                            iIndex = iPosition;
                            // 实时定位自动刷新
                            mCurTracker.gps_interval = iIntervals[iIndex];

                            UserUtil.reviseFrequency(mContext,
                                    mCurTracker);

                            ToastUtil.show(mContext, obj.what);
                        } else {
                            ToastUtil.show(mContext, obj.what);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse,
                                throwable);
                        ToastUtil.show(mContext,
                                R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    private int getIndex(int interval) {
        int len = iIntervals.length;
        for (int i = 0; i < len; i++) {
            if (interval == iIntervals[i])
                return i;
        }
        return -1;
    }

    private int getIndexFromId(int checkedId) {
        int len = iIntervals.length;
        for (int i = 0; i < len; i++) {
            if (checkedId == iRbtnIds[i]) {
                return i;
            }
        }
        return -1;
    }

}
