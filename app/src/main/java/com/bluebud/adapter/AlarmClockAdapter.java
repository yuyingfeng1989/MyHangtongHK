package com.bluebud.adapter;

import java.util.List;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.AlarmClockInfo;
import com.bluebud.utils.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlarmClockAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<AlarmClockInfo> alarmClockInfos;

	public AlarmClockAdapter(Context context,
			List<AlarmClockInfo> alarmClockInfos) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.alarmClockInfos = alarmClockInfos;
	}

	public void setData(List<AlarmClockInfo> alarmClockInfos) {
		this.alarmClockInfos = alarmClockInfos;
	}

	@Override
	public int getCount() {
		if (null != alarmClockInfos) {
			return alarmClockInfos.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return alarmClockInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_alarm_clock_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewHolder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.tvRepeat = (TextView) convertView
					.findViewById(R.id.tv_repeat);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		// int rePosition = alarmClockInfos.size() - (1 + position);
		AlarmClockInfo alarmClockInfo = alarmClockInfos.get(position);

		viewHolder.tvTitle.setText(alarmClockInfo.title);

		StringBuffer sbTime = new StringBuffer();
		for (int i = 0; i < alarmClockInfo.times.size(); i++) {
			sbTime.append(alarmClockInfo.times.get(i) + " ");
		}
		viewHolder.tvTime.setText(sbTime.toString());

		String strRepeat = "";
		if (0 == alarmClockInfo.iType) {
			strRepeat = Utils.dateString2Days(context, alarmClockInfo.sDay)
					+ " "
					+ context.getResources().getString(R.string.repeat_year);
		} else if (1 == alarmClockInfo.iType) {
			strRepeat = Utils.dateString2Days(context, alarmClockInfo.sDay)
					+ " "
					+ context.getResources().getString(R.string.repeat_month);
		} else if (2 == alarmClockInfo.iType) {
			strRepeat = Utils.getWeeks(context, alarmClockInfo.arrWeeks);
		}
		viewHolder.tvRepeat.setText(strRepeat);

		return convertView;
	}

	private class ViewHolder {
		public TextView tvTitle;
		public TextView tvTime;
		public TextView tvRepeat;
	}

}
