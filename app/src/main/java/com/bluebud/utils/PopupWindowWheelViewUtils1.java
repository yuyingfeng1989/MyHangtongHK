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
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.liteguardian.wheelview.NumericWheelAdapter;
import com.liteguardian.wheelview.WheelView;

import java.text.DecimalFormat;
import java.util.Calendar;

public class PopupWindowWheelViewUtils1 {
    private Context context;
    private PopupWindow popupWindow;
    private OnWheeClicked onWheeClicked;

    public PopupWindowWheelViewUtils1(Context context, OnWheeClicked onWheeClicked) {
        this.context = context;
        this.onWheeClicked = onWheeClicked;
    }

    public interface OnWheeClicked {

        public void getWheelAmTime(String sOnTime, String sOffTime, int position);

        public void getWheelPmTime(String sOnTime, String sOffTime, int position);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    //选择时间PP
    public void ShowTime(String sStartDate, String sEndDate, String centerTitle, final Boolean isAm, final int position) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_bottom_popup_gettime1, null);


        Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
        Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
        final WheelView wvHoursOn = (WheelView) view.findViewById(R.id.hour_on);
        final WheelView wvMinsOn = (WheelView) view.findViewById(R.id.mins_on);
        final WheelView wvHoursOff = (WheelView) view.findViewById(R.id.hour_off);
        final WheelView wvMinsOff = (WheelView) view.findViewById(R.id.mins_off);
        TextView tvCenter = (TextView) view.findViewById(R.id.tv_center);
        tvCenter.setText(centerTitle);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Utils.hourString2Date(sEndDate));
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

        Calendar calendar1 = Calendar.getInstance();
        calendar.setTime(Utils.hourString2Date(sStartDate));
        int hour1 = calendar1.get(Calendar.HOUR_OF_DAY);
        int minute1 = calendar1.get(Calendar.MINUTE);
        //时
        wvHoursOn.setAdapter(new NumericWheelAdapter(0, 23));
        wvHoursOn.setCyclic(true);
        wvHoursOn.setVisibleItems(3);//设置显示行数
        wvHoursOn.setLabel("");// 添加文字
        wvHoursOn.setCurrentItem(hour1);

        //分
        wvMinsOn.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wvMinsOn.setCyclic(true);
        wvMinsOn.setLabel("");// 添加文字
        wvMinsOn.setCurrentItem(minute1);
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
                String resultOn = (decimal.format(wvHoursOn.getCurrentItem())
                        + ":" + decimal.format(wvMinsOn.getCurrentItem()));
                String resultOff = (decimal.format(wvHoursOff.getCurrentItem())
                        + ":" + decimal.format(wvMinsOff.getCurrentItem()));
                if (isAm) {
                    onWheeClicked.getWheelAmTime(resultOn, resultOff, position);
                } else {
                    onWheeClicked.getWheelPmTime(resultOn, resultOff, position);
                }

                popupWindow.dismiss();
            }
        });

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        // 设置popWindow的显示和消失动画
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
		
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


}
