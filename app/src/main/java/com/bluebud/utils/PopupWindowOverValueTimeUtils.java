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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;

public class PopupWindowOverValueTimeUtils {
    private Context context;
    private PopupWindow popupWindow;
    private RadioGroup radiogroup;
    private RadiogroupValueTime radiogroupValueTime;
    private int value = 3;
    private int ranges = 1;

    public PopupWindowOverValueTimeUtils(Context context, RadiogroupValueTime radiogroupValueTime) {
        this.context = context;
        this.radiogroupValueTime = radiogroupValueTime;
    }

    public interface RadiogroupValueTime {

        public void getRadiogroupValueTime(int value);


    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    //选择时间PP

    public void ShowRadiogroupValue(int speedTime, final int ranges) {
        this.ranges = ranges;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_bottom_popup_over_value_time, null);

        Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
        Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
        TextView tvCenter = (TextView) view.findViewById(R.id.tv_center);
        //tvCenter.setText(centerTitle);
        radiogroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        RadioButton rb3 = (RadioButton) view.findViewById(R.id.rb_3);
        RadioButton rb5 = (RadioButton) view.findViewById(R.id.rb_5);
        RadioButton rb8 = (RadioButton) view.findViewById(R.id.rb_8);
        RadioButton rb10 = (RadioButton) view.findViewById(R.id.rb_10);
        RadioButton rb15 = (RadioButton) view.findViewById(R.id.rb_15);
        RadioButton rb30 = (RadioButton) view.findViewById(R.id.rb_30);
        if (speedTime > 0)
            value = speedTime;
        if (ranges == 4) {//是摩托车
            rb8.setText(context.getString(R.string.time_15s));
            rb10.setText(context.getString(R.string.time_30s));
            switch (speedTime) {
                case 5:
                    rb3.setChecked(true);
                    break;
                case 10:
                    rb5.setChecked(true);
                    break;
                case 15:
                    rb8.setChecked(true);
                    break;
                case 30:
                    rb10.setChecked(true);
                    break;
                default:
                    break;
            }
        } else {
            rb8.setText(context.getString(R.string.time_30s));
            rb10.setText(context.getString(R.string.time_10s));
            switch (speedTime) {
                case 5:
                    rb3.setChecked(true);
                    break;
                case 10:
                    rb5.setChecked(true);
                    break;
                case 30:
                    rb8.setChecked(true);
                    break;
                case 60:
                    rb10.setChecked(true);
                    break;

                default:
                    break;
            }
        }
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_3:
                        value = 5;
                        break;
                    case R.id.rb_5:
                        value = 10;
                        break;
                    case R.id.rb_8:
                        if (ranges == 4) {
                            value = 15;
                        } else {
                            value = 30;
                        }
                        break;
                    case R.id.rb_10:
                        if (ranges == 4) {
                            value = 30;
                        } else {
                            value = 60;
                        }
                        break;
                    default:
                        break;
                }

            }
        });
        btn_item1.setOnClickListener(new OnClickListener() {//取消

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        btn_item2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                radiogroupValueTime.getRadiogroupValueTime(value);
                popupWindow.dismiss();
            }
        });

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        /*view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
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
