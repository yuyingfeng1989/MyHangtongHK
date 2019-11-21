package com.bluebud.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;

public class SettingAdapter extends BaseAdapter {
    //	private Context context;
    private LayoutInflater inflater;
    private int[] generals;
    private Boolean isNewVersion = true;
//    private int type;

    public SettingAdapter(Context context, int[] generals, Boolean isNewVersion, int type) {
        inflater = LayoutInflater.from(context);
//		this.context = context;
        this.generals = generals;
        this.isNewVersion = isNewVersion;
//        this.type = type;
    }

    @Override
    public int getCount() {
        if (null == generals) {
            return 0;
        }
        return generals.length;
    }

    @Override
    public Object getItem(int position) {

        return generals[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_setting_item, null);
            viewHolder = new ViewHolder();
            viewHolder.llTitle = (LinearLayout) convertView
                    .findViewById(R.id.ll_title);
            viewHolder.tvTitle = (TextView) convertView
                    .findViewById(R.id.tv_title);
            viewHolder.tvTitle1 = (TextView) convertView
                    .findViewById(R.id.tv_title1);
            viewHolder.ivPicture = (ImageView) convertView
                    .findViewById(R.id.iv_right);

            viewHolder.ivRedDot = (ImageView) convertView
                    .findViewById(R.id.iv_red_dot);
            viewHolder.llItem = (LinearLayout) convertView
                    .findViewById(R.id.ll_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(generals[position]);
        viewHolder.tvTitle.setVisibility(View.VISIBLE);
        viewHolder.tvTitle1.setVisibility(View.GONE);
        viewHolder.ivPicture.setVisibility(View.VISIBLE);

        if (3 == position && isNewVersion) {// 有新版本时显示红点
            viewHolder.ivRedDot.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivRedDot.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder {
        public LinearLayout llTitle;

        public TextView tvTitle;
        public TextView tvTitle1;
        public ImageView ivPicture;
        public ImageView ivRedDot;
        public LinearLayout llItem;


    }

}