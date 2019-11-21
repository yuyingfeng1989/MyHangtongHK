//package com.bluebud.adapter;
//
//import java.util.List;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.bluebud.info.PoiReInfo;
//import com.bluebud.liteguardian_hk.R;
//
//public class SugestionAdapter extends BaseAdapter {
//	private Context context;
//	private List<PoiReInfo> lists;
//
//	public SugestionAdapter(Context context, List<PoiReInfo> lists) {
//		this.context = context;
//		this.lists = lists;
//	}
//
//	public void setList(List<PoiReInfo> lists) {
//		this.lists = lists;
//	}
//
//	@Override
//	public int getCount() {
//		return lists.size();
//	}
//
//	@Override
//	public Object getItem(int position) {
//
//		return lists.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup viewGroup) {
//		PoiReInfo tracker = lists.get(position);
//		ViewHolder holder;
//		if (convertView == null) {
//			convertView = LayoutInflater.from(context).inflate(
//					R.layout.item_sugestion, null);
//			holder = new ViewHolder();
//
//			convertView.setTag(holder);
//
//			holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
//			holder.tvAdrress = (TextView) convertView
//					.findViewById(R.id.tv_name1);
//
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
//
//		//holder.tvName.setText(tracker.name);
//		holder.tvAdrress.setText(tracker.address);
//
//		return convertView;
//	}
//
//	static class ViewHolder {
//		TextView tvName;
//		TextView tvAdrress;
//	}
//
//}