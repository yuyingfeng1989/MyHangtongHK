package com.bluebud.activity.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.bluebud.activity.BaseActivity;
import com.bluebud.chat.utils.ChatHttpParams;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.StepsInfo;
import com.bluebud.info.Tracker;
import com.bluebud.utils.DateUtils;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.ProgressDialogUtil;
import com.bluebud.utils.ToastUtil;
import com.bluebud.utils.UserUtil;
import com.bluebud.view.RefreshLayout;
import com.bluebud.view.StepTrend;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2018/6/9 0009.
 */

public class SportStepActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private StepTrend stepTrend;
    private Context mContext;
    private Tracker mCurrTracker;
    private TextView text_countStep;
    private RefreshLayout swipeLayout;
    private StepsInfo[] steps = new StepsInfo[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sportstep_activity);
        WeakReference<SportStepActivity> weakReference = new WeakReference<>(this);
        mContext = weakReference.get();
        mCurrTracker = UserUtil.getCurrentTracker(mContext);
        initeView();
    }

    /**
     * 初始化控件
     */
    private void initeView() {
        findViewById(R.id.img3).setVisibility(View.GONE);
        findViewById(R.id.back).setOnClickListener(this);
        TextView textTitle = (TextView) findViewById(R.id.txt1);
        text_countStep = (TextView) findViewById(R.id.text_countStep);
        stepTrend = (StepTrend) findViewById(R.id.scoreTrend);
        textTitle.setText(getString(R.string.sports_count_step));
        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container); //刷新
        swipeLayout.setColorSchemeResources(R.color.material_blue_grey_900,
                R.color.material_blue_grey_900,
                R.color.material_blue_grey_900,
                R.color.material_blue_grey_900);
        swipeLayout.setOnRefreshListener(this);
        requestCurryDayStep();
        requestQueryStep();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    /**
     * 获取当天步数
     */
    private void requestCurryDayStep() {
        final String url = UserUtil.getServerUrl(mContext);
        RequestParams params = HttpParams.getDeviceLastStep(mCurrTracker.device_sn);//设置远程监听
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        if (response == null)
                            return;
                        String result = new String(response);
                        LogUtil.e("当天的步数resultc======" + result);
                        StepsInfo info = (StepsInfo) ChatHttpParams.getParseResult(-3, result);
                        if (info != null)
                            text_countStep.setText(info.step + "");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        swipeLayout.setRefreshing(false);
                    }
                });
    }

    /**
     * 查询多天步数
     */
    private void requestQueryStep() {
        final String startTime = DateUtils.getInsingle().getOldDate(-7);
        final String endTime = DateUtils.getInsingle().getOldDate(-1);
        final String url = UserUtil.getServerUrl(mContext);
        RequestParams params = HttpParams.getDeviceStep(mCurrTracker.device_sn, startTime, endTime);
        if (params == null || url == null)
            return;
        List<String> oldWeek7 = DateUtils.getInsingle().queryData(startTime, endTime, false);
        final List<String> days = DateUtils.getInsingle().queryData(startTime, endTime, true);
        stepTrend.setScore(null, oldWeek7, days, 0, 0, 7, -1);
        LogUtil.e("starttime==" + startTime + " endTime=" + endTime);
        HttpClientUsage.getInstance().post(mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        if (response == null)
                            return;
                        String result = new String(response);
                        List<StepsInfo> userinfos = (List<StepsInfo>) ChatHttpParams.getParseResult(-2, result);
                        if (userinfos == null)
                            return;
                        for (StepsInfo info : userinfos) {
                            String stepDay = info.createDate.substring(8, 10);
                            for (int i = 0; i < days.size(); i++) {
                                if (days.get(i).equals(stepDay)) {
                                    steps[i] = info;
                                    break;
                                }
                            }
                        }
                        DateComparator d = new DateComparator();//获取最大步数
                        int maxStep = Collections.max(userinfos, d).step + 1;
                        stepTrend.setRefreshValue(steps, maxStep);
                        userinfos.clear();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable throwable) {
                        super.onFailure(statusCode, headers, errorResponse, throwable);
                        ToastUtil.show(mContext, R.string.net_exception);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        ProgressDialogUtil.dismiss();
                    }
                });
    }

    @Override
    public void onRefresh() {
        requestCurryDayStep();
    }

    /**
     * 比较对象中的值大小
     */
    class DateComparator implements Comparator<StepsInfo> {
        @Override
        public int compare(StepsInfo lhs, StepsInfo rhs) {
            return (lhs.step < rhs.step ? -1 : (lhs.step == rhs.step ? 0 : 1));
        }
    }
}
