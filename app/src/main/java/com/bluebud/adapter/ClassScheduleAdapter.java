package com.bluebud.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.SchoolTimetableInfo.SchoolTimetableBean;

import java.util.Random;

/**
 * Created by Administrator on 2018/10/10.
 */

public class ClassScheduleAdapter extends BaseAdapter {
    private Context mContext;
    private SchoolTimetableBean[] infos;
    private int[] colors;
    private Random random = new Random();

    public ClassScheduleAdapter(Context context, SchoolTimetableBean[] infos, int[] colors) {
        this.mContext = context;
        this.infos = infos;
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return infos.length;
    }

    @Override
    public Object getItem(int position) {
        return infos[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.class_shedule_item, null);
        TextView textview_item = (TextView) view.findViewById(R.id.schedule_name_item);
        SchoolTimetableBean info = infos[position];
        if (info != null && !TextUtils.isEmpty(info.courseName)) {
            textview_item.setText(infos[position].courseName);
            GradientDrawable myGrad = (GradientDrawable) textview_item.getBackground();
            int color = colors[random.nextInt(7)];
            myGrad.setColor(mContext.getResources().getColor(color));
        } else {
            textview_item.setBackground(mContext.getResources().getDrawable(R.drawable.ico_date6));
        }
        return view;
    }

    /**
     * 刷新数据
     */
    public void refreshValue(SchoolTimetableBean[] values) {
        if (values != null) {
            infos = values;
            notifyDataSetChanged();
        }
    }
}
