package com.bluebud.utils;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.bluebud.liteguardian_hk.R;
import com.liteguardian.wheelview.NumericWheelAdapter;
import com.liteguardian.wheelview.WheelView;

import java.text.DecimalFormat;
import java.util.Calendar;

public class PopupWindowWheelViewUtils {
    private Context context;
    private PopupWindow popupWindow;
    private OnWheeClicked onWheeClicked;

    public PopupWindowWheelViewUtils(Context context, OnWheeClicked onWheeClicked) {
        this.context = context;
        this.onWheeClicked = onWheeClicked;
    }

    public interface OnWheeClicked {

        public void getWheelTime(String sTime, Boolean ison);

    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    //选择时间PP

    public void ShowTime(String sDate, final Boolean ison) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_bottom_popup_gettime, null);


        Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
        Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
        final WheelView wvHoursOff = (WheelView) view.findViewById(R.id.hour_off);
        final WheelView wvMinsOff = (WheelView) view.findViewById(R.id.mins_off);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Utils.hourString2Date(sDate));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        // 时
        wvHoursOff.setAdapter(new NumericWheelAdapter(0, 23));
        wvHoursOff.setCyclic(true);
        wvHoursOff.setVisibleItems(3);//设置显示行数
        wvHoursOff.setLabel("");// 添加文字
        wvHoursOff.setCurrentItem(hour);
        // 分
        wvMinsOff.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wvMinsOff.setCyclic(true);
        wvMinsOff.setLabel("");// 添加文字
        wvMinsOff.setCurrentItem(minute);
        btn_item1.setOnClickListener(new OnClickListener() {//取消

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        btn_item2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 如果是个数,则显示为"02"的样式
                String parten = "00";
                DecimalFormat decimal = new DecimalFormat(parten);
                // 设置日期的显示
                String result = (decimal.format(wvHoursOff.getCurrentItem()) + ":" + decimal.format(wvMinsOff.getCurrentItem()));
                onWheeClicked.getWheelTime(result, ison);
                popupWindow.dismiss();
            }
        });

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
		
		
	/*	view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(popupWindow.isShowing())
					popupWindow.dismiss();
			}
		});*/

//		ColorDrawable dw = new ColorDrawable(0xb0000000);
        // // 设置SelectPicPopupWindow弹出窗体的背景
//		popupWindow.setBackgroundDrawable(dw);

        popupWindow.setAnimationStyle(R.style.AlphaAnimation);
        int xPos = windowManager.getDefaultDisplay().getWidth();
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }


    //选择时间PP


    public void ShowClassTime(String sDateStart, String sDateEnd) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.class_popup_time, null);

        Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
        Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
        final WheelView hour_left_start = (WheelView) view.findViewById(R.id.hour_left_start);
        final WheelView mins_left_start = (WheelView) view.findViewById(R.id.mins_left_start);
        final WheelView hour_right_end = (WheelView) view.findViewById(R.id.hour_right_end);
        final WheelView mins_right_end = (WheelView) view.findViewById(R.id.mins_right_end);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Utils.hourString2Date(sDateStart));
        int hourStart = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteStart = calendar.get(Calendar.MINUTE);
        calendar.setTime(Utils.hourString2Date(sDateEnd));
        int hourEnd = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteEnd = calendar.get(Calendar.MINUTE);
        // 时
        hour_left_start.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        hour_left_start.setCyclic(true);
        hour_left_start.setVisibleItems(3);//设置显示行数
        hour_left_start.setLabel("");// 添加文字
        hour_left_start.setCurrentItem(hourStart);
        // 分
        mins_left_start.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        mins_left_start.setCyclic(true);
        mins_left_start.setLabel("");// 添加文字
        mins_left_start.setCurrentItem(minuteStart);

        hour_right_end.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
        hour_right_end.setCyclic(true);
        hour_right_end.setVisibleItems(3);//设置显示行数
        hour_right_end.setLabel("");// 添加文字
        hour_right_end.setCurrentItem(hourEnd);
        // 分
        mins_right_end.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        mins_right_end.setCyclic(true);
        mins_right_end.setLabel("");// 添加文字
        mins_right_end.setCurrentItem(minuteEnd);
        btn_item1.setOnClickListener(new OnClickListener() {//取消

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        btn_item2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String parten = "00";// 如果是个数,则显示为"02"的样式
                DecimalFormat decimal = new DecimalFormat(parten);
                // 设置日期的显示
                String resultStart = (decimal.format(hour_left_start.getCurrentItem()) + ":" + decimal.format(mins_left_start.getCurrentItem()));
                String resultEnd = (decimal.format(hour_right_end.getCurrentItem()) + ":" + decimal.format(mins_right_end.getCurrentItem()));
                if (!Utils.compareDateTo(resultStart, resultEnd)) {
                    ToastUtil.show(context, R.string.date_error);
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                onWheeClicked.getWheelTime(buffer.append(resultStart).append(",").append(resultEnd).toString(), null);
                popupWindow.dismiss();
            }
        });

//		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setAnimationStyle(R.style.AlphaAnimation);
//		windowManager.getDefaultDisplay().getWidth();
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }
}
