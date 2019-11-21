package com.bluebud.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebud.info.DogTrailMap;
import com.bluebud.liteguardian_hk.R;

import java.util.List;

public class PetWalkRecordingAdapter extends BaseAdapter {
    //	private Context context;
    private LayoutInflater inflater;

    private List<DogTrailMap> dogTrailMap;

    public PetWalkRecordingAdapter(Context context, List<DogTrailMap> dogTrailMap) {
        inflater = LayoutInflater.from(context);
//		this.context = context;
        this.dogTrailMap = dogTrailMap;

    }

    public void setList(List<DogTrailMap> dogTrailMap) {
        this.dogTrailMap = dogTrailMap;
    }

    @Override
    public int getCount() {
        if (dogTrailMap == null) {
            return 0;
        } else {
            return dogTrailMap.size();
        }
    }

    @Override
    public Object getItem(int position) {

        return dogTrailMap.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_pet_walk_recording_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_day = (TextView) convertView.findViewById(R.id.tv_day);
            viewHolder.tv_time_start = (TextView) convertView.findViewById(R.id.tv_time_start);
            viewHolder.tv_time_all = (TextView) convertView.findViewById(R.id.tv_time_all);
            viewHolder.tv_mileage = (TextView) convertView.findViewById(R.id.tv_mileage);
            viewHolder.tv_calories = (TextView) convertView.findViewById(R.id.tv_calories);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        if (dogTrailMap.get(position).start_time != null) {
            viewHolder.tv_day.setText(dogTrailMap.get(position).start_time.substring(5, 10));
            viewHolder.tv_time_start.setText(dogTrailMap.get(position).start_time.substring(11, 16));
        } else {
            viewHolder.tv_day.setText("--");
            viewHolder.tv_time_start.setText("--");
        }
        if (dogTrailMap.get(position).spendtime != null) {
            viewHolder.tv_time_all.setText(dogTrailMap.get(position).spendtime);

        } else {
            viewHolder.tv_time_all.setText("--");
        }
        if (dogTrailMap.get(position).mileage != null) {
            viewHolder.tv_mileage.setText(dogTrailMap.get(position).mileage);
        } else {
            viewHolder.tv_mileage.setText("--");
        }
        if (dogTrailMap.get(position).calorie != null) {
            viewHolder.tv_calories.setText(dogTrailMap.get(position).calorie);
        } else {
            viewHolder.tv_calories.setText("--");
        }


        return convertView;
    }

    static class ViewHolder {
        public TextView tv_day;
        public TextView tv_time_start;
        public TextView tv_time_all;
        public TextView tv_mileage;
        public TextView tv_calories;

    }

}