package com.bluebud.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.PeripherDetail;

import java.util.List;

public class PeripherDetailAdapter extends BaseAdapter {
    //    private Context context;
    private LayoutInflater inflater;
    List<PeripherDetail> peripherDetailList;


    public PeripherDetailAdapter(Context context, List<PeripherDetail> peripherDetailList) {
        inflater = LayoutInflater.from(context);
//        this.context = context;
        this.peripherDetailList = peripherDetailList;

    }

    public void setList(List<PeripherDetail> peripherDetailList) {
        this.peripherDetailList = peripherDetailList;
    }

    @Override
    public int getCount() {
        if (null == peripherDetailList) {
            return 0;
        } else {
            return peripherDetailList.size();
        }

    }

    @Override
    public Object getItem(int position) {

        return peripherDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_peripher_detail_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_address = (TextView) convertView
                    .findViewById(R.id.tv_address);
            viewHolder.tv_name = (TextView) convertView
                    .findViewById(R.id.tv_name);
            viewHolder.tv_distance = (TextView) convertView
                    .findViewById(R.id.tv_distance);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PeripherDetail detail = peripherDetailList.get(position);
        if (detail == null)
            return convertView;
        viewHolder.tv_address.setText(detail.address.toString());
        viewHolder.tv_name.setText(detail.name.toString());
        int distance = detail.distance;
        if (distance < 100)
            viewHolder.tv_distance.setText(distance + "m");
        else viewHolder.tv_distance.setText(Math.round((distance / 10f)) / 100f + "km");
        return convertView;
    }

    static class ViewHolder {
        public TextView tv_address;
        public TextView tv_name;
        public TextView tv_distance;

    }

}