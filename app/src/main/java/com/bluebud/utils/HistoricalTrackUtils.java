package com.bluebud.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.http.AsyncHttpResponseHandlerReset;
import com.bluebud.http.HttpClientUsage;
import com.bluebud.http.HttpParams;
import com.bluebud.info.HistoryGPSData;
import com.bluebud.info.ReBaseObj;
import com.bluebud.listener.IMapCallback;
import com.liteguardian.wheelview.NumericWheelAdapter;
import com.liteguardian.wheelview.OnWheelChangedListener;
import com.liteguardian.wheelview.WheelView;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/12/12.
 */

public class HistoricalTrackUtils {
    private Context mContext;
    private String sTrackerNo;
    private static final int START_YEAR = 1990, END_YEAR = 2100;


    public HistoricalTrackUtils(Context context, String sTrackerNo) {
        this.mContext = context;
        this.sTrackerNo = sTrackerNo;
    }

    /**
     * 历史轨迹接口
     */
    public void getHistoricalGPSData(final String mDate, String mTimeStart, String mTimeEnd, final IMapCallback callback, final ProgressDialogUtil.OnProgressDialogClickListener progress) {
        int result = Utils.compareTime(mTimeStart, mTimeEnd);
        if (result > 0) {
            ToastUtil.show(mContext, R.string.time_error);
            return;
        }
        String url = UserUtil.getServerUrl(mContext);
        String sDateStart = mDate + " " + mTimeStart;
        String sDateEnd = mDate + " " + mTimeEnd;
        RequestParams params = HttpParams.getHistoricalGPSData(sTrackerNo, sDateStart, sDateEnd);
        HttpClientUsage.getInstance().post(
                mContext, url, params,
                new AsyncHttpResponseHandlerReset() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        ProgressDialogUtil.showNoCanceled(mContext, null, progress);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                        super.onSuccess(statusCode, headers, response);
                        ReBaseObj obj = GsonParse.reBaseObjParse(new String(response));
                        if (obj != null && 0 == obj.code) {
                            HistoryGPSData data = GsonParse.gpsDataParse(new String(response));
                            if (data == null)
                                return;
                            callback.mapCallBack(data);
                        } else {
                            callback.mapCallBack(null);
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
     * 结束时间
     */
    @SuppressLint("SimpleDateFormat")
    public void getEndTimePick(final WheelView wvHourEnd, final WheelView wvMinEnd, final TextView tvEndTime) {
        int mMinute_e;
        int mHour_e;
        wvHourEnd.setAdapter(new NumericWheelAdapter(0, 23));
        wvHourEnd.setLabel("");
        wvHourEnd.setCyclic(true);
        wvMinEnd.setAdapter(new NumericWheelAdapter(0, 59));
        wvMinEnd.setLabel("");
        wvMinEnd.setCyclic(true);
        SimpleDateFormat minute = new SimpleDateFormat("mm");
        SimpleDateFormat hour = new SimpleDateFormat("HH");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        mMinute_e = Integer.parseInt(minute.format(curDate));
        mHour_e = Integer.parseInt(hour.format(curDate));
        wvHourEnd.setCurrentItem(mHour_e);
        wvMinEnd.setCurrentItem(mMinute_e);
        wvHourEnd.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String time = getHourAndMintueDate(wvHourEnd, wvMinEnd);
                tvEndTime.setText(time);

            }
        });
        wvMinEnd.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String time = getHourAndMintueDate(wvHourEnd, wvMinEnd);
                tvEndTime.setText(time);

            }
        });

    }

    /**
     * 获取开始时间
     */
    public void getStartTimePick(final WheelView wvHourStart, final WheelView wvMinStart, final TextView tvBeginTime) {
        int mMinute;
        int mHour;
        wvHourStart.setAdapter(new NumericWheelAdapter(0, 23));
        wvHourStart.setLabel("");
        wvHourStart.setCyclic(true);
        wvMinStart.setAdapter(new NumericWheelAdapter(0, 59));
        wvMinStart.setLabel("");
        wvMinStart.setCyclic(true);
        mMinute = 0;
        mHour = 0;
        wvHourStart.setCurrentItem(mHour);
        wvMinStart.setCurrentItem(mMinute);
        wvHourStart.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String time = getHourAndMintueDate(wvHourStart, wvMinStart);
                tvBeginTime.setText(time);

            }
        });
        wvMinStart.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String time = getHourAndMintueDate(wvHourStart, wvMinStart);
                tvBeginTime.setText(time);
            }
        });
    }

    /**
     * 获取小时，分钟
     */
    private String getHourAndMintueDate(WheelView hour, WheelView mintue) {
        // 如果是个数,则显示为"02"的样式
        String parten = "00";
        DecimalFormat decimal = new DecimalFormat(parten);
        // 设置日期的显示
        String result = decimal.format(hour.getCurrentItem()) + ":"
                + decimal.format(mintue.getCurrentItem());
        return result;
    }


    /**
     * 获取日期
     */
    public void getDataPick(final WheelView wvYear, final WheelView wvMouth, final WheelView wvDay, final TextView tvDay) {
        Calendar calendar = Calendar.getInstance();
        // calendar.setTime(DateUtils.dateString2Date(mContext, sDate));

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);
        // 年
        wvYear.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
        wvYear.setCyclic(true);// 可循环滚动
        wvYear.setLabel("");// 添加文字
        wvYear.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
        // 月
        wvMouth.setAdapter(new NumericWheelAdapter(1, 12));
        wvMouth.setCyclic(true);
        wvMouth.setLabel("");
        wvMouth.setCurrentItem(month);

        wvDay.setCyclic(true);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month + 1))) {
            wvDay.setAdapter(new NumericWheelAdapter(1, 31));
        } else if (list_little.contains(String.valueOf(month + 1))) {
            wvDay.setAdapter(new NumericWheelAdapter(1, 30));
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wvDay.setAdapter(new NumericWheelAdapter(1, 29));
            else
                wvDay.setAdapter(new NumericWheelAdapter(1, 28));
        }
        wvDay.setLabel("");
        wvDay.setCurrentItem(day - 1);

        // 添加"年"监听
        OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int year_num = newValue + START_YEAR;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big
                        .contains(String.valueOf(wvMouth.getCurrentItem() + 1))) {
                    wvDay.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(wvMouth
                        .getCurrentItem() + 1))) {
                    wvDay.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0)
                            || year_num % 400 == 0)
                        wvDay.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wvDay.setAdapter(new NumericWheelAdapter(1, 28));
                }
                String mDate = getDate(wvYear, wvMouth, wvDay);
                tvDay.setText(mDate);

            }
        };
        // 添加"月"监听
        OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int month_num = newValue + 1;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    wvDay.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(month_num))) {
                    wvDay.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if (((wvYear.getCurrentItem() + START_YEAR) % 4 == 0 && (wvYear
                            .getCurrentItem() + START_YEAR) % 100 != 0)
                            || (wvYear.getCurrentItem() + START_YEAR) % 400 == 0)
                        wvDay.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wvDay.setAdapter(new NumericWheelAdapter(1, 28));
                }
                String mDate = getDate(wvYear, wvMouth, wvDay);
                tvDay.setText(mDate);
            }
        };

        // 添加"日"监听
        OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String mDate = getDate(wvYear, wvMouth, wvDay);
                tvDay.setText(mDate);
            }
        };

        wvYear.addChangingListener(wheelListener_year);
        wvMouth.addChangingListener(wheelListener_month);
        wvDay.addChangingListener(wheelListener_day);
    }

    /**
     * 获取日期格式
     */
    public String getDate(WheelView year, WheelView month, WheelView day) {
        // 如果是个数,则显示为"02"的样式
        String parten = "00";
        DecimalFormat decimal = new DecimalFormat(parten);
        // 设置日期的显示
        String result = (year.getCurrentItem() + START_YEAR) + "-"
                + decimal.format((month.getCurrentItem() + 1)) + "-"
                + decimal.format((day.getCurrentItem() + 1));
        return result;
    }
}
