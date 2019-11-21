package com.bluebud.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.OrderPackageInfo;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.Utils;

import java.util.List;


public class PayWayAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private  List<OrderPackageInfo.PackageListBean> lists;
	private int positionTag=0;

	public PayWayAdapter(Context context, List<OrderPackageInfo.PackageListBean> lists) {
		inflater = LayoutInflater.from(context);
		this.lists = lists;
	}

	@Override
	public int getCount() {
		if (null != lists) {
			return lists.size();
		} else {
			return 0;
		}
	}

	public void setData(List<OrderPackageInfo.PackageListBean> list){
		if (lists!=null){
			lists.clear();
		}
		this.lists=list;
		//lists.addAll(list);
		notifyDataSetChanged();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.layout_pay_way_item, null);
			viewHolder = new ViewHolder();
		viewHolder.cb_is_selector= (CheckBox) convertView.findViewById(R.id.cb_is_selector);
			viewHolder.tv_package_name= (TextView) convertView.findViewById(R.id.tv_package_name);
			viewHolder.tv_money= (TextView) convertView.findViewById(R.id.tv_money);
			viewHolder.tv_time= (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.ll_paypal= (LinearLayout) convertView.findViewById(R.id.ll_paypal);
            viewHolder.ll_head=(LinearLayout) convertView.findViewById(R.id.ll_head);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

	 viewHolder.tv_package_name.setText(lists.get(position).getName());
		String mCurrecyUnit=lists.get(position).getCurrency_unit();
		String menoyUnit;
        if (Utils.isEmpty(mCurrecyUnit)){
            menoyUnit="  ";
        }else{
            menoyUnit=mCurrecyUnit.toUpperCase()+"  ";
		}
			viewHolder.tv_money.setText(menoyUnit+lists.get(position).getServe_fee()+"");
		viewHolder.tv_time.setText(Utils.utc2Local(lists.get(position).getExpired_time_de()));

		if (position==positionTag){
			viewHolder.cb_is_selector.setChecked(true);
		}else{
			viewHolder.cb_is_selector.setChecked(false);
		}
        if (lists!=null&&lists.size()>0){
            if (position!=lists.size()-1){
                viewHolder.ll_paypal.setVisibility(View.GONE);
            }else {
                viewHolder.ll_paypal.setVisibility(View.VISIBLE);
            }
            if (position==0){
                viewHolder.ll_head.setVisibility(View.VISIBLE);
            }else {
                viewHolder.ll_head.setVisibility(View.GONE);
            }
        }

		return convertView;
	}

	private class ViewHolder {
		public TextView tv_package_name;
		public TextView tv_money;
		public TextView tv_time;
		public CheckBox cb_is_selector;
        public LinearLayout ll_paypal;
        public LinearLayout ll_head;
	}
	public void updata(int postion){
		this.positionTag=postion;
		notifyDataSetChanged();
		LogUtil.i("updata: postion:"+postion);

	}

}
