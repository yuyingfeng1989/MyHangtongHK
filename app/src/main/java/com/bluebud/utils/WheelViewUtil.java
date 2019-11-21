package com.bluebud.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.kankan.wheelview.ArrayWheelAdapter;
import com.kankan.wheelview.NumericWheelAdapter;
import com.kankan.wheelview.OnWheelChangedListener;
import com.kankan.wheelview.WheelView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class WheelViewUtil {
    private static int START_YEAR = 1900, END_YEAR = 2100;
    private Context mContext;
    private AlertDialog mDialog;
    private OnWheeClicked onWheeClicked;

    public WheelViewUtil(Context context, OnWheeClicked onWheeClicked) {
        mContext = context;
        this.onWheeClicked = onWheeClicked;
    }

    public interface OnWheeClicked {
        public void getWheelDay(String sDay);

        public void getFenceRange(String sRange);

        public void getWheelTime(String sTime);
    }

    public void showDay(String sDate) {
        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.show();

        int width = ((Activity) mContext).getWindowManager()
                .getDefaultDisplay().getWidth();
        int height = ((Activity) mContext).getWindowManager()
                .getDefaultDisplay().getHeight();
        if (width < height) {
            height = width * 6 / 10;
            width = width * 8 / 10;
        } else {
            width = height * 6 / 10;
            height = height * 8 / 10;
        }
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = width;
        params.height = LayoutParams.WRAP_CONTENT;
        mDialog.getWindow().setAttributes(params);

        Window window = mDialog.getWindow();
        window.setContentView(R.layout.layout_wheel_day);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Utils.dateString2Date(mContext, sDate));

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);

        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
        String[] months_little = {"4", "6", "9", "11"};

        final List<String> list_big = Arrays.asList(months_big);
        final List<String> list_little = Arrays.asList(months_little);

        // 年
        final WheelView wv_year = (WheelView) window.findViewById(R.id.year);
        wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
        wv_year.setCyclic(true);// 可循环滚动
        wv_year.setLabel(mContext.getResources().getString(R.string.year));// 添加文字
        wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

        // 月
        final WheelView wv_month = (WheelView) window.findViewById(R.id.month);
        wv_month.setAdapter(new NumericWheelAdapter(1, 12));
        wv_month.setCyclic(true);
        wv_month.setLabel(mContext.getResources().getString(R.string.month));
        wv_month.setCurrentItem(month);

        // 日
        final WheelView wv_day = (WheelView) window.findViewById(R.id.day);
        wv_day.setCyclic(true);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (list_big.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 31));
        } else if (list_little.contains(String.valueOf(month + 1))) {
            wv_day.setAdapter(new NumericWheelAdapter(1, 30));
        } else {
            // 闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                wv_day.setAdapter(new NumericWheelAdapter(1, 29));
            else
                wv_day.setAdapter(new NumericWheelAdapter(1, 28));
        }
        wv_day.setLabel(mContext.getResources().getString(R.string.day));
        wv_day.setCurrentItem(day - 1);

        // 添加"年"监听
        OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int year_num = newValue + START_YEAR;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big
                        .contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(wv_month
                        .getCurrentItem() + 1))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if ((year_num % 4 == 0 && year_num % 100 != 0)
                            || year_num % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                }
            }
        };
        // 添加"月"监听
        OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int month_num = newValue + 1;
                // 判断大小月及是否闰年,用来确定"日"的数据
                if (list_big.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 31));
                } else if (list_little.contains(String.valueOf(month_num))) {
                    wv_day.setAdapter(new NumericWheelAdapter(1, 30));
                } else {
                    if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
                            .getCurrentItem() + START_YEAR) % 100 != 0)
                            || (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
                        wv_day.setAdapter(new NumericWheelAdapter(1, 29));
                    else
                        wv_day.setAdapter(new NumericWheelAdapter(1, 28));
                }
            }
        };
        wv_year.addChangingListener(wheelListener_year);
        wv_month.addChangingListener(wheelListener_month);

        Button btn_sure = (Button) window.findViewById(R.id.btn_datetime_sure);
        Button btn_cancel = (Button) window
                .findViewById(R.id.btn_datetime_cancel);
        // 确定
        btn_sure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialog.dismiss();

                // 如果是个数,则显示为"02"的样式
                String parten = "00";
                DecimalFormat decimal = new DecimalFormat(parten);
                // 设置日期的显示
                String result = (wv_year.getCurrentItem() + START_YEAR) + "-"
                        + decimal.format((wv_month.getCurrentItem() + 1)) + "-"
                        + decimal.format((wv_day.getCurrentItem() + 1));

                onWheeClicked.getWheelDay(result);
            }
        });
        // 取消
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialog.dismiss();
            }
        });
    }

    public void showTime(String sDate) {
        mDialog = new AlertDialog.Builder(mContext).create();
        mDialog.show();

        int width = ((Activity) mContext).getWindowManager()
                .getDefaultDisplay().getWidth();
        int height = ((Activity) mContext).getWindowManager()
                .getDefaultDisplay().getHeight();
        if (width < height) {
            height = width * 6 / 10;
            width = width * 8 / 10;
        } else {
            width = height * 6 / 10;
            height = height * 8 / 10;
        }
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = width;
        params.height = LayoutParams.WRAP_CONTENT;
        mDialog.getWindow().setAttributes(params);

        Window window = mDialog.getWindow();
        window.setContentView(R.layout.layout_wheel_time);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Utils.hourString2Date(sDate));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 时
        final WheelView wv_hours = (WheelView) window.findViewById(R.id.hour);
        wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
        wv_hours.setCyclic(true);
        wv_hours.setLabel(mContext.getResources().getString(R.string.hour));// 添加文字
        wv_hours.setCurrentItem(hour);

        // 分
        final WheelView wv_mins = (WheelView) window.findViewById(R.id.mins);
        wv_mins.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wv_mins.setCyclic(true);
        wv_mins.setLabel(mContext.getResources().getString(R.string.minute));// 添加文字
        wv_mins.setCurrentItem(minute);

        Button btn_sure = (Button) window.findViewById(R.id.btn_datetime_sure);
        Button btn_cancel = (Button) window
                .findViewById(R.id.btn_datetime_cancel);
        // 确定
        btn_sure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialog.dismiss();

                // 如果是个数,则显示为"02"的样式
                String parten = "00";
                DecimalFormat decimal = new DecimalFormat(parten);
                // 设置日期的显示
                String result = (decimal.format(wv_hours.getCurrentItem())
                        + ":" + decimal.format(wv_mins.getCurrentItem()));
                onWheeClicked.getWheelTime(result);
            }
        });
        // 取消
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialog.dismiss();
            }
        });
    }

    /**
     * @param @param sRange
     * @return void
     * @throws
     * @Title: showFenceRange
     * @Description: 围栏设置半径时弹出半径显示界面
     */
    public void showFenceRange(String sRange, View parentview, Activity context) {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_wheel_radus, null);

        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()

        PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        window.setBackgroundDrawable(dw);
        final WheelView wv_hours = (WheelView) view
                .findViewById(R.id.radius);
        final String[] rangeArray = mContext.getResources().getStringArray(
                R.array.fence_range);
        int defaultIndex = 0;
        for (int i = 0; i < rangeArray.length; i++) {
            LogUtil.v("range is " + sRange + " array is " + rangeArray[i]);
            if (sRange.equals(rangeArray[i])) {
                defaultIndex = i;
                break;
            }
        }
        ArrayWheelAdapter<String> awRange = new ArrayWheelAdapter<String>(
                rangeArray);
        LogUtil.v("awrange leng " + awRange.getItemsCount());
        wv_hours.setAdapter(awRange);
        wv_hours.setCyclic(true);
        // wv_hours.setLabel(mContext.getResources().getString(R.string.meter));//
        // 添加文字
        wv_hours.setCurrentItem(defaultIndex);

        TextView btn_sure = (TextView) view.findViewById(R.id.btn_ok);
        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        // 确定
        btn_sure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialog.dismiss();

                String result = rangeArray[wv_hours.getCurrentItem()];

                onWheeClicked.getFenceRange(result);
            }
        });
        // 取消
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mDialog.dismiss();
            }
        });


    }

}
