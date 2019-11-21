package com.bluebud.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.CarTrackInfo;
import com.bluebud.info.HistoryGPSData;
import com.bluebud.info.ReBaseObj;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.view.CalendarView;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.List;

/**
 * Created by Administrator on 2019/3/28.
 */

public class ObdHistoryTrackUtil {
    private Activity mContext;
    private ObdCallbackTrackResult obdCallback;
    private RequestHandle requestHandle;

    public ObdHistoryTrackUtil(Activity context, ObdCallbackTrackResult obdCallback) {
        this.mContext = context;
        this.obdCallback = obdCallback;
    }

    public interface ObdCallbackTrackResult {
        void callbackOnDayTrack(CarTrackInfo info);

        void callbackGpsTrack(HistoryGPSData gpsData);
    }

    /**
     * @Title: addTrackListView
     * @Description: 创建汽车行驶记录界面
     */
    public View createTrackListView() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutInflater inflater3 = LayoutInflater.from(mContext);
        View view = inflater3.inflate(R.layout.layout_car_track_list, null);
        view.setLayoutParams(lp);
        return view;
    }

    /**
     * @Description: 创建日历选择控件
     */
    public View createDateSelectView() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutInflater inflater3 = LayoutInflater.from(mContext);
        View view = inflater3.inflate(R.layout.layout_calender, null);
        view.setLayoutParams(lp);
        return view;
    }


    /**
     * @Description: 上方中间显示日期
     */
    public void showCalenderDay(String setDay, TextView tvCurrentDay) {
        String curDay = Utils.curDate2Day();
        if (curDay.equals(setDay))
            tvCurrentDay.setText(mContext.getResources().getString(R.string.today));
        else
            tvCurrentDay.setText(setDay);
    }

    /**
     * @Title: getCalenderDay
     * @Description: 获取上方视图中间显示的日期
     */
    public String getCalenderDay(TextView tvCurrentDay) {
        String setDay = tvCurrentDay.getText().toString();
        if (Utils.getStrCount(setDay, "-") == 1) {
            setDay = setDay + "-01";
        }
        if (setDay.equals(mContext.getResources().getString(R.string.today))) {
            return Utils.curDate2Day();
        } else {
            return setDay;
        }
    }

    /**
     * @Description: 获取下一天的汽车轨迹数据，并显示在列表中，如果下一天无数据，则会继续获取后面第二天的数据，再不行就获取后面第三天的数据，
     * 仍然没数据则提示无轨迹数据
     */
    public void getNextDayCarTrack(TextView tvCurrentDay, String sTrackerNo) {
        if (getCalenderDay(tvCurrentDay).equals(Utils.curDate2Day())) {
            ToastUtil.show(mContext, mContext.getResources().getString(R.string.today_is_lastday));
            return;
        }
        showCalenderDay(Utils.getSpecifiedDayAfter(getCalenderDay(tvCurrentDay)), tvCurrentDay);
        if (getCalenderDay(tvCurrentDay).equals(Utils.curDate2Day()))
            getOnedayTrack(Utils.getCurTime(), sTrackerNo);
        else
            getOnedayTrack(getCalenderDay(tvCurrentDay) + " 23:59:59", sTrackerNo);
    }

    public void getLastDayCarTrack(TextView tvCurrentDay, String sTrackerNo) {
        showCalenderDay(Utils.getSpecifiedDayBefore(getCalenderDay(tvCurrentDay)), tvCurrentDay);
        if (getCalenderDay(tvCurrentDay).equals(Utils.curDate2Day()))
            getOnedayTrack(Utils.getCurTime(), sTrackerNo);
        else
            getOnedayTrack(getCalenderDay(tvCurrentDay) + " 23:59:59", sTrackerNo);
    }


    /**
     * @Description: 日历跳转到上一个月
     */
    public void calenderGoLastMouth(TextView tvCurrentDay, CalendarView cvCalendar) {
        // 点击上一月 同样返回年月
        String leftYearAndmonth = cvCalendar.clickLeftMonth();
        String[] ya = leftYearAndmonth.split("-");
        tvCurrentDay.setText(leftYearAndmonth);
    }

    /**
     * @Title: calenderGoNextMouth
     * @Description: 日历跳转到下一个月
     */
    public void calenderGoNextMouth(TextView tvCurrentDay, CalendarView cvCalendar) {
        // 点击上一月 同样返回年月
        String carlenderday = getCalenderDay(tvCurrentDay);
        String curday = Utils.curDate2Day();
        String[] carlenderdayArray = carlenderday.split("-");
        String[] curdayArray = curday.split("-");
        // 如果年和月都相同，那么说明当前日历是在当前月份，点击下一个月应该不能让它跳转
        if (carlenderdayArray[0].equals(curdayArray[0]) && carlenderdayArray[1].equals(curdayArray[1])) {
            return;
        }
        String rightYearAndmonth = cvCalendar.clickRightMonth();
        tvCurrentDay.setText(rightYearAndmonth);
    }


    /**
     * 获取当天的轨迹列表
     */
    public void getOnedayTrack(String datetime, String sTrackerNo) {
        String url = UserUtil.getServerUrl(mContext);
        RequestParams params = HttpParams.getDriveTrail(sTrackerNo, datetime);
        if (null != requestHandle && !requestHandle.isFinished())
            requestHandle.cancel(true);
        requestHandle = HttpClientUsage.getInstance().post(mContext, url,
                params, new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null) {
                            obdCallback.callbackOnDayTrack(null);
                            return;
                        }
                        if (obj.code == 0) {
                            CarTrackInfo carTrackInfo = GsonParse.carDateTrackParse(new String(response));
                            if (carTrackInfo == null) {
                                return;
                            }
                            obdCallback.callbackOnDayTrack(carTrackInfo);
                        } else {
                            obdCallback.callbackOnDayTrack(null);
                            ToastUtil.show(mContext, obj.what);
                        }
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

    /**
     * 从服务器获取一条轨迹记录
     */
    public void getOneTrackerFromServer(int arg, List<CarTrackInfo.DriveTrailData> mDrivetraildata, String sTrackerNo) {
        String starttime = mDrivetraildata.get(arg).start_time;
        String endtime = mDrivetraildata.get(arg).end_time;
        String url = UserUtil.getServerUrl(mContext);
        RequestParams params = HttpParams.getHistoricalGPSData(sTrackerNo, starttime, endtime);
        if (null != requestHandle && !requestHandle.isFinished())
            requestHandle.cancel(true);

        requestHandle = HttpClientUsage.getInstance().post(mContext, url,
                params, new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.show(mContext);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj == null) {
                            obdCallback.callbackGpsTrack(null);
                            return;
                        }
                        if (0 == obj.code) {
                            HistoryGPSData data = GsonParse.gpsDataParse(new String(response));
                            if (data == null)
                                return;
                            obdCallback.callbackGpsTrack(data);
                        } else {
                            obdCallback.callbackGpsTrack(null);
                            ToastUtil.show(mContext, R.string.trail_no);
                        }
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
}
