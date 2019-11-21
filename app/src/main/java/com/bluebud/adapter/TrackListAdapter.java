package com.bluebud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebud.info.CarTrackListInfo;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

public class TrackListAdapter extends BaseAdapter {
    private Context context;
    //	private final DecimalFormat mFloatFormat = new DecimalFormat("0.00")
    private final DecimalFormat mFloatFormat = new DecimalFormat("0.0");
    private List<CarTrackListInfo> lists;

    public TrackListAdapter(Context context, List<CarTrackListInfo> lists) {
        this.context = context;
        this.lists = lists;
    }

    public void setList(List<CarTrackListInfo> lists) {
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {

        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        LogUtil.v("lists size is " + lists.size() + " position " + position);
        CarTrackListInfo trackerList = lists.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_car_track_list, null);
            holder = new ViewHolder();

            convertView.setTag(holder);
            holder.tvStartTime = (TextView) convertView
                    .findViewById(R.id.tv_start_time);
            holder.tvEndTime = (TextView) convertView
                    .findViewById(R.id.tv_end_time);
            holder.tvStartAddr = (TextView) convertView
                    .findViewById(R.id.tv_start_address);
            holder.tvEndAddr = (TextView) convertView
                    .findViewById(R.id.tv_end_address);
            holder.tvMileage = (TextView) convertView
                    .findViewById(R.id.tv_mileage);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvOil = (TextView) convertView.findViewById(R.id.tv_oil);
            //	holder.tvCarbonEmission = (TextView) convertView
            //			.findViewById(R.id.tv_ave_speed);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String starttime = "";
        String endtime = "";
        if (trackerList.sStartTime != null) {
            starttime = trackerList.sStartTime.split(" ")[1];
            endtime = trackerList.sEndTime.split(" ")[1];
        }

        holder.tvStartTime.setText(starttime);
        holder.tvEndTime.setText(endtime);
        holder.tvStartAddr.setText(trackerList.sStartAddr);
        holder.tvEndAddr.setText(trackerList.sEndAddr);
        holder.tvMileage.setText(Utils.format1(trackerList.sMileage) + "km");
        holder.tvTime.setText(trackerList.sTime);
        holder.tvOil.setText(Utils.format1(trackerList.sOil) + "L");
        //holder.tvCarbonEmission.setText(trackerList.sCarbonEmission + "Km/h");
        return convertView;
    }

    static class ViewHolder {
        TextView tvStartTime;
        TextView tvEndTime;
        TextView tvStartAddr;
        TextView tvEndAddr;
        TextView tvMileage;
        TextView tvTime;
        TextView tvOil;
        //TextView tvCarbonEmission;
    }

}