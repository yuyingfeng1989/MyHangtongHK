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

public class PopupWindowOverValueUtils {
    private Context context;
    private PopupWindow popupWindow;
    private RadioGroup radiogroup;
    private RadiogroupValue radiogroupValue;
    private int value = 100;

    public PopupWindowOverValueUtils(Context context, RadiogroupValue radiogroupValue) {
        this.context = context;
        this.radiogroupValue = radiogroupValue;
    }

    public interface RadiogroupValue {

        public void getRadiogroupValue(int value);


    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    //选择时间PP

    public void ShowRadiogroupValue(int spendValue) {
        LogUtil.i("传过来的超速值：" + spendValue);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_bottom_popup_over_value, null);

        Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
        Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
        TextView tvCenter = (TextView) view.findViewById(R.id.tv_center);
        //tvCenter.setText(centerTitle);
        radiogroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        RadioButton rb20 = (RadioButton) view.findViewById(R.id.rb_20);
        RadioButton rb40 = (RadioButton) view.findViewById(R.id.rb_40);
        RadioButton rb60 = (RadioButton) view.findViewById(R.id.rb_60);
        RadioButton rb80 = (RadioButton) view.findViewById(R.id.rb_80);
        RadioButton rb100 = (RadioButton) view.findViewById(R.id.rb_100);
        RadioButton rb120 = (RadioButton) view.findViewById(R.id.rb_120);
        RadioButton rb140 = (RadioButton) view.findViewById(R.id.rb_140);
        RadioButton rb160 = (RadioButton) view.findViewById(R.id.rb_160);
        RadioButton rb180 = (RadioButton) view.findViewById(R.id.rb_180);
        RadioButton rb200 = (RadioButton) view.findViewById(R.id.rb_200);
        if (spendValue > 0)
            value = spendValue;
        switch (spendValue) {
            case 20:
                rb20.setChecked(true);
                break;
            case 40:
                rb40.setChecked(true);
                break;
            case 60:
                rb60.setChecked(true);
                break;
            case 80:
                rb80.setChecked(true);
                break;
            case 100:
                rb100.setChecked(true);
                break;

            case 120:
                rb120.setChecked(true);
                break;
            case 160:
                rb160.setChecked(true);
                break;
            case 180:
                rb180.setChecked(true);
                break;
            case 200:
                rb200.setChecked(true);
                break;


            default:
                break;
        }

        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_20:
                        value = 20;
                        break;
                    case R.id.rb_40:
                        value = 40;
                        break;
                    case R.id.rb_60:
                        value = 60;
                        break;
                    case R.id.rb_80:
                        value = 80;
                        break;
                    case R.id.rb_100:
                        value = 100;
                        break;
                    case R.id.rb_120:
                        value = 120;
                        break;

                    case R.id.rb_140:
                        value = 140;
                        break;

                    case R.id.rb_160:
                        value = 160;
                        break;

                    case R.id.rb_180:
                        value = 180;
                        break;

                    case R.id.rb_200:
                        value = 200;
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
                radiogroupValue.getRadiogroupValue(value);
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
