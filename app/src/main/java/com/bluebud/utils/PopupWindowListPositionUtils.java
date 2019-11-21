package com.bluebud.utils;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bluebud.adapter.ListPositionAdapter;
import com.bluebud.liteguardian_hk.R;

import java.util.List;

public class PopupWindowListPositionUtils {
    private Context context;
    private PopupWindow popupWindow;

    private List<String> lists;
    private int value = 0;
    private ListPositionAdapter adapter;
    private ListPositon listPositon;


    public PopupWindowListPositionUtils(Context context, ListPositon listPositon) {
        this.context = context;
        this.listPositon = listPositon;
    }

    public interface ListPositon {

        public void getListPositon(int position);


    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    //选择时间PP

    public void ShowlistPosition(List<String> lists) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_list_position, null);

        Button btn_item1 = (Button) view.findViewById(R.id.btn_item1);
        Button btn_item2 = (Button) view.findViewById(R.id.btn_item2);
        TextView tvCenter = (TextView) view.findViewById(R.id.tv_center);
        ListView lvPositon = (ListView) view.findViewById(R.id.lv_postion);
        adapter = new ListPositionAdapter(context, lists, 0);
        lvPositon.setAdapter(adapter);
        if (null != lists && lists.size() > 0) {
            adapter.setpositon(lists.size() - 1);
            value = lists.size() - 1;
        }

        lvPositon.setOnItemClickListener(new OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position,
                                    long id) {
                value = position;
                adapter.setpositon(position);
                adapter.notifyDataSetChanged();

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
                listPositon.getListPositon(value);
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


        popupWindow.setAnimationStyle(R.style.AlphaAnimation);
        int xPos = windowManager.getDefaultDisplay().getWidth();
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }


}
