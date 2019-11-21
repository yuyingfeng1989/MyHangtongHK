package com.bluebud.adapter;

import java.util.List;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.AlarmClockHistoryInfo;
import com.bluebud.utils.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlarmClockHistoryAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<AlarmClockHistoryInfo> alarmClockHistoryInfos;

	public AlarmClockHistoryAdapter(Context context,
			List<AlarmClockHistoryInfo> alarmClockHistoryInfos) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.alarmClockHistoryInfos = alarmClockHistoryInfos;
	}

	public void setData(List<AlarmClockHistoryInfo> alarmClockHistoryInfos) {
		this.alarmClockHistoryInfos = alarmClockHistoryInfos;
	}

	@Override
	public int getCount() {
		if (null != alarmClockHistoryInfos) {
			return alarmClockHistoryInfos.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return alarmClockHistoryInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.layout_alarm_clock_history_item, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewHolder.tvDay = (TextView) convertView.findViewById(R.id.tv_day);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		AlarmClockHistoryInfo alarmClockHistoryInfo = alarmClockHistoryInfos
				.get(position);

		viewHolder.tvTitle.setText(alarmClockHistoryInfo.title);
		viewHolder.tvTime.setText(alarmClockHistoryInfo.time);

		if (2 != alarmClockHistoryInfo.type) {
			viewHolder.tvDay.setText(alarmClockHistoryInfo.day);
		} else {
			viewHolder.tvDay.setText(Utils.string2Week(context,
					alarmClockHistoryInfo.week));
		}

		return convertView;
	}

	private class ViewHolder {
		public TextView tvTitle;
		public TextView tvDay;
		public TextView tvTime;
	}

}
