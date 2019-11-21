package com.bluebud.utils;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;

public class PopupWindowCheckBoxUtils {
    private Context context;
    private PopupWindow popupWindow;
    private OnCheckBoxTime onCheckBoxTime;
    private String[] arrWeeks = {"0", "0", "0", "0", "0", "0", "0"};

    public PopupWindowCheckBoxUtils(Context context, OnCheckBoxTime onCheckBoxTime) {
        this.context = context;
        this.onCheckBoxTime = onCheckBoxTime;
    }

    public interface OnCheckBoxTime {

        public void getCheckBoxTime(String Time);


    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    //选择时间PP

    public void ShowCheckBox(String centerTitle, String repeatday) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_bottom_popup_checkbox, null);
//		view.findViewById(R.id.scroll_alarmclock).setVisibility(View.VISIBLE);
//		view.findViewById(R.id.ll_type_rong).setVisibility(View.GONE);

        Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
        Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
        TextView tvCenter = (TextView) view.findViewById(R.id.tv_center);
        tvCenter.setText(centerTitle);
        CheckBox cbWeek1 = (CheckBox) view.findViewById(R.id.cb_week1);
        CheckBox cbWeek2 = (CheckBox) view.findViewById(R.id.cb_week2);
        CheckBox cbWeek3 = (CheckBox) view.findViewById(R.id.cb_week3);
        CheckBox cbWeek4 = (CheckBox) view.findViewById(R.id.cb_week4);
        CheckBox cbWeek5 = (CheckBox) view.findViewById(R.id.cb_week5);
        CheckBox cbWeek6 = (CheckBox) view.findViewById(R.id.cb_week6);
        CheckBox cbWeek7 = (CheckBox) view.findViewById(R.id.cb_week7);

        String[] arrRepeat = repeatday.split(",");
        for (int i = 0; i < arrRepeat.length; i++) {
            switch (Integer.parseInt(arrRepeat[i])) {
                case 1:
                    setCheckBox(0, cbWeek1);
                    break;
                case 2:
                    setCheckBox(1, cbWeek2);
                    break;
                case 3:
                    setCheckBox(2, cbWeek3);
                    break;
                case 4:
                    setCheckBox(3, cbWeek4);
                    break;
                case 5:
                    setCheckBox(4, cbWeek5);
                    break;
                case 6:
                    setCheckBox(5, cbWeek6);
                    break;
                case 7:
                    setCheckBox(6, cbWeek7);
                    break;
            }
        }


        cbWeek1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    arrWeeks[0] = 1 + "";
                } else {
                    arrWeeks[0] = 0 + "";
                }
            }
        });
        cbWeek2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    arrWeeks[1] = 1 + "";
                } else {
                    arrWeeks[1] = 0 + "";
                }
            }
        });
        cbWeek3.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    arrWeeks[2] = 1 + "";
                } else {
                    arrWeeks[2] = 0 + "";
                }
            }
        });
        cbWeek4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    arrWeeks[3] = 1 + "";
                } else {
                    arrWeeks[3] = 0 + "";
                }
            }
        });
        cbWeek5.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    arrWeeks[4] = 1 + "";
                } else {
                    arrWeeks[4] = 0 + "";
                }
            }
        });

        cbWeek6.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    arrWeeks[5] = 1 + "";
                } else {
                    arrWeeks[5] = 0 + "";
                }
            }
        });

        cbWeek7.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    arrWeeks[6] = 1 + "";
                } else {
                    arrWeeks[6] = 0 + "";
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
                if (Utils.isEmpty(Utils.arrDayToString(arrWeeks))) {
                    ToastUtil.show(context, R.string.week_empty);
                    return;
                }
                onCheckBoxTime.getCheckBoxTime(Utils.arrDayToString(arrWeeks));
                popupWindow.dismiss();


            }
        });

//		WindowManager windowManager = (WindowManager) context
//				.getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        //动画
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.setAnimationStyle(R.style.AlphaAnimation);
//		int xPos = windowManager.getDefaultDisplay().getWidth();
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }


    private void setCheckBox(int position, CheckBox checkbox) {
        //if ("0".equals(arrWeeks[position])) {
        arrWeeks[position] = "1";
        checkbox.setChecked(true);
        LogUtil.i("Checkbox[" + position + 1 + "]" + checkbox.isChecked());

        //	}
    }

    /**
     * 设置提醒方式
     */
//	private int type;
//	public void showTypeCheckBox(String types) {
//		type = Integer.valueOf(types);
//		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View view = inflater.inflate(R.layout.layout_bottom_popup_checkbox, null);
//		view.findViewById(R.id.scroll_alarmclock).setVisibility(View.GONE);
//		view.findViewById(R.id.ll_type_rong).setVisibility(View.VISIBLE);
//		TextView tv_center = (TextView) view.findViewById(R.id.tv_center);
//		tv_center.setVisibility(View.VISIBLE);
//		tv_center.setText(context.getString(R.string.please_select));
//		Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
//		Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
//
//
//		final CheckBox cb_type_rong1 =(CheckBox) view.findViewById(R.id.cb_type_rong1);
//		final CheckBox cb_type_rong2 =(CheckBox) view.findViewById(R.id.cb_type_rong2);
//		final CheckBox cb_type_rong3 =(CheckBox) view.findViewById(R.id.cb_type_rong3);
//		switch (type){
//			case 2:
//				cb_type_rong1.setChecked(true);
//				break;
//			case 3:
//				cb_type_rong2.setChecked(true);
//				break;
//			case 1:
//				cb_type_rong3.setChecked(true);
//				break;
//		}
//
//		cb_type_rong1.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//					type=2;
//				cb_type_rong1.setChecked(true);
//				cb_type_rong2.setChecked(false);
//				cb_type_rong3.setChecked(false);
//			}
//		});
//
//		cb_type_rong2.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//					type=3;
//				cb_type_rong1.setChecked(false);
//				cb_type_rong2.setChecked(true);
//				cb_type_rong3.setChecked(false);
//			}
//		});
//		cb_type_rong3.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				type=1;
//				cb_type_rong1.setChecked(false);
//				cb_type_rong2.setChecked(false);
//				cb_type_rong3.setChecked(true);
//			}
//		});
//
//		btn_item1.setOnClickListener(new OnClickListener() {//取消
//
//			@Override
//			public void onClick(View v) {
//				popupWindow.dismiss();
//
//			}
//		});
//		btn_item2.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				onCheckBoxTime.getCheckBoxTime(String.valueOf(type));
//				popupWindow.dismiss();
//			}
//		});
//		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
//				LayoutParams.WRAP_CONTENT);
//		popupWindow.setFocusable(true);
//		popupWindow.setOutsideTouchable(false);
//		//动画
//		popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
//		popupWindow.setAnimationStyle(R.style.AlphaAnimation);
//		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//	}
}
