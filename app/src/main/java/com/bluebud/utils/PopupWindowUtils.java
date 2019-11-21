package com.bluebud.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.view.CalendarView;
import com.bluebud.view.ZoomImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kankan.wheelview.ArrayWheelAdapter;
import com.liteguardian.wheelview.NumericWheelAdapter;
import com.liteguardian.wheelview.OnWheelChangedListener;
import com.liteguardian.wheelview.WheelView;

import java.util.Calendar;

public class PopupWindowUtils {
    private Context context;
    private PopupWindow popupWindow;
//    private Dialog openBigImageDialog;
//    private ZoomImageView iv_big_image;

    public PopupWindowUtils(Context context) {
        this.context = context;
    }

    public void initPopupWindow(String item1, OnClickListener item1Click,
                                String item2, OnClickListener item2Click, String cancel,
                                OnClickListener cancelOnclick) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_bottom_popup, null);

        Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
        Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_item1.setText(item1);
        btn_item2.setText(item2);
        btn_cancel.setText(cancel);

        btn_item1.setOnClickListener(item1Click);
        btn_item2.setOnClickListener(item2Click);
        btn_cancel.setOnClickListener(cancelOnclick);

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);


		/*view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popupWindow.isShowing())
					popupWindow.dismiss();
			}
		});*/

        // ColorDrawable dw = new ColorDrawable(0xb0000000);
        // // 设置SelectPicPopupWindow弹出窗体的背景
        // popupWindow.setBackgroundDrawable(dw);

        // 设置popWindow的显示和消失动画
        popupWindow.setAnimationStyle(R.style.AlphaAnimation);
        int xPos = windowManager.getDefaultDisplay().getWidth();
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    public void dismiss() {
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
    }

    /**
     * 找回密码
     *
     * @param item1
     * @param item1Click
     * @param item2
     * @param item2Click
     */
    public void initPopupWindowSex(String item1, OnClickListener item1Click,
                                   String item2, OnClickListener item2Click, String item3, OnClickListener item3Click, boolean isHideItem3) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_bottom_popup1, null);


        Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
        Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
        Button btn_item3 = (Button) view.findViewById(R.id.btn_item3);

        btn_item1.setText(item1);
        btn_item2.setText(item2);
        btn_item3.setText(item3);
        if (isHideItem3) {
            btn_item3.setVisibility(View.GONE);
        }
        btn_item1.setOnClickListener(item1Click);
        btn_item2.setOnClickListener(item2Click);
        btn_item3.setOnClickListener(item3Click);

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        //popupWindow.update();

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });

//		 ColorDrawable dw = new ColorDrawable(0xb0000000);
//		 // 设置SelectPicPopupWindow弹出窗体的背景
//		 popupWindow.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        //popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.setAnimationStyle(R.style.AlphaAnimation);
        int xPos = windowManager.getDefaultDisplay().getWidth();
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);


        //WindowManager.LayoutParams params=activity.getParent().getWindow().getAttributes();


        //  params.alpha=0.7f;

        //  activity.getParent().getWindow().setAttributes(params);


    }

    public interface RangeChoose {
        public void click(String range);
    }

    /**
     * 围栏范围选择
     */
    public void initPopupWindowRangeChoose(String sRange, final RangeChoose rangeChoose) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_wheel_radus, null);


        final com.kankan.wheelview.WheelView wv_hours = view.findViewById(R.id.radius);
        final String[] rangeArray = context.getResources()
                .getStringArray(R.array.fence_range);
        int defaultIndex = 0;
        for (int i = 0; i < rangeArray.length; i++) {
            LogUtil.v("range is " + sRange + " array is " + rangeArray[i]);
            if (TextUtils.equals(sRange + "m", rangeArray[i])) {
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
            public void onClick(View v) {

                String result = rangeArray[wv_hours.getCurrentItem()];
                String range = result.substring(0, result.indexOf("m"));
                LogUtil.v("get fence range is " + range + "leng "
                        + range.length());
                rangeChoose.click(range);

            }
        });

//			
        // 取消
        btn_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        //popupWindow.update();

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });

//		 ColorDrawable dw = new ColorDrawable(0xb0000000);
//		 // 设置SelectPicPopupWindow弹出窗体的背景
//		 popupWindow.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        //popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.setAnimationStyle(R.style.AlphaAnimation);
        int xPos = windowManager.getDefaultDisplay().getWidth();
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);


        //WindowManager.LayoutParams params=activity.getParent().getWindow().getAttributes();


        //  params.alpha=0.7f;

        //  activity.getParent().getWindow().setAttributes(params);


    }

    // 选择时间PP

    public void initPopupWindowGetTime(Context context, String item1,
                                       OnClickListener item1Click, String item2,
                                       OnClickListener item2Click, String time, final Boolean ison) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater
                .inflate(R.layout.layout_bottom_popup_gettime, null);

        Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
        Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
        final WheelView wvHoursOff = (WheelView) view
                .findViewById(R.id.hour_off);
        final WheelView wvMinsOff = (WheelView) view
                .findViewById(R.id.mins_off);
        btn_item1.setText(item1);
        btn_item2.setText(item2);
        btn_item1.setOnClickListener(item1Click);
        btn_item2.setOnClickListener(item2Click);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Utils.hourString2Date(time));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        // 时
        wvHoursOff.setAdapter(new NumericWheelAdapter(0, 23));
        wvHoursOff.setCyclic(true);
        wvHoursOff.setVisibleItems(3);// 设置显示行数
        wvHoursOff.setLabel("");// 添加文字
        wvHoursOff.setCurrentItem(hour);
        // 分
        wvMinsOff.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
        wvMinsOff.setCyclic(true);
        wvMinsOff.setLabel("");// 添加文字
        wvMinsOff.setCurrentItem(minute);
        wvHoursOff.addChangingListener(new OnWheelChangedListener() {

            private String sHoursOn;
            private String sHoursOff;

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (wvHoursOff.equals(wheel)) {
                    if (ison) {// 是开机的小时
                        sHoursOn = String.format("%02d", newValue);
                    } else {
                        sHoursOff = String.format("%02d", newValue);
                    }
                }

            }
        });
        wvMinsOff.addChangingListener(new OnWheelChangedListener() {

            private String sMinsOn;
            private String sMinsOff;

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (wvMinsOff.equals(wheel)) {
                    if (ison) {// 是开机的分钟
                        sMinsOn = String.format("%02d", newValue);
                    } else {
                        sMinsOff = String.format("%02d", newValue);
                    }
                }

            }
        });

        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 设置popWindow的显示和消失动画
        //popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        //popupWindow.update();

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        });

        // ColorDrawable dw = new ColorDrawable(0xb0000000);
        // // 设置SelectPicPopupWindow弹出窗体的背景
        // popupWindow.setBackgroundDrawable(dw);

        // 设置popWindow的显示和消失动画
        popupWindow.setAnimationStyle(R.style.AlphaAnimation);
        //popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        int xPos = windowManager.getDefaultDisplay().getWidth();
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }


    /**
     * 选择头像
     */
    public void initPopupWindowPicture(CompoundButton.OnCheckedChangeListener item1Click, OnClickListener item2Click) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.show_takephoto, null);

        CheckBox btn_item1 = (CheckBox) view.findViewById(R.id.checkbox_all);
        ImageView btn_item2 = (ImageView) view.findViewById(R.id.photo_delect);
        btn_item1.setOnCheckedChangeListener(item1Click);
        btn_item2.setOnClickListener(item2Click);
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(false);
        // 设置popWindow的显示和消失动画
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }


    /**
     * 远程关机
     */
    public void popupWindRemoteShutdown(final MineHttpRequestUtl requestUtl) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.poppwind_shotdown, null);
        TextView remote_watch_restart = (TextView) view.findViewById(R.id.remote_watch_restart);//手表重启
        TextView remote_watch_off = (TextView) view.findViewById(R.id.remote_watch_off);//手表关机
        TextView remote_watch_cancel = (TextView) view.findViewById(R.id.remote_watch_cancel);//取消
        popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setAnimationStyle(R.style.AlphaAnimation);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAtLocation(remote_watch_cancel, Gravity.BOTTOM, 0, 0); // 设置popWindow弹出方向
        remote_watch_restart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUtl.remoteShutDowm(5);
                popupWindow.dismiss();
            }
        });
        remote_watch_off.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUtl.remoteShutDowm(3);
                popupWindow.dismiss();
            }
        });
        remote_watch_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    /**
     * 拍照放大图片
     */
    private Dialog openBigImageDialog;

    public void openBigImage(Context context, String url) {
        openBigImageDialog = new Dialog(context, R.style.AppTheme);
        View view = View.inflate(context, R.layout.showbig_image, null);
        ZoomImageView iv_big_image = (ZoomImageView) view.findViewById(R.id.image_big);
        openBigImageDialog.setContentView(view);
        Glide.with(context)
                .load(url)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(iv_big_image);
//        }
        iv_big_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openBigImageDialog.cancel();
            }
        });
        openBigImageDialog.show();
    }

    /**
     * obd日历表
     */
    View obdView;
    CalendarView cvCalendar;

    public CalendarView popupWindCalender(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        obdView = inflater.inflate(R.layout.obd_layout_calender, null);
        popupWindow = new PopupWindow(obdView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setAnimationStyle(R.style.AlphaAnimation);
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        popupWindow.setBackgroundDrawable(dw);
        cvCalendar = (CalendarView) obdView.findViewById(R.id.cv_calender);
        cvCalendar.setSelectMore(false); // 单选
        return cvCalendar;
    }

    public void showObdCalender() {
        popupWindow.showAtLocation(obdView, Gravity.TOP, 0, Utils.dipToPx(140)); // 设置popWindow弹出方向
    }
}
