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

public class MineAdapter extends BaseAdapter {
    //	private Context context;
    private LayoutInflater inflater;
    private int[] generals;
    private int[] images;
    //	private Boolean isNewVersion = true;
    private int type;
    private int protocol_type;

    public MineAdapter(Context context, int[] generals, int[] images, int type, int protocol_type) {
        inflater = LayoutInflater.from(context);
//		this.context = context;
        this.images = images;
        this.generals = generals;
//		this.isNewVersion = isNewVersion;
        this.type = type;
        this.protocol_type = protocol_type;
    }

    public void setList(int[] generals, int[] images,int type, int protocol_type) {
        this.images = images;
        this.generals = generals;
//		this.isNewVersion = isNewVersion;
        this.type = type;
        this.protocol_type = protocol_type;
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
            viewHolder.ivLeft = (ImageView) convertView
                    .findViewById(R.id.iv_left);

            viewHolder.ivRedDot = (ImageView) convertView
                    .findViewById(R.id.iv_red_dot);
            viewHolder.llItem = (LinearLayout) convertView
                    .findViewById(R.id.ll_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (0 == position)
            viewHolder.llTitle.setVisibility(View.VISIBLE);
        else if (protocol_type == 8 && 2 == position) {
            viewHolder.llTitle.setVisibility(View.VISIBLE);
        } else if (protocol_type != 8 && 3 == position) {
            viewHolder.llTitle.setVisibility(View.VISIBLE);
        } else {
            viewHolder.llTitle.setVisibility(View.GONE);
        }

        viewHolder.tvTitle.setText(generals[position]);
        viewHolder.tvTitle.setVisibility(View.VISIBLE);
        viewHolder.tvTitle1.setVisibility(View.GONE);
        viewHolder.ivPicture.setVisibility(View.VISIBLE);
        if (type == 5) {
            viewHolder.ivLeft.setVisibility(View.GONE);
        } else {
            viewHolder.ivLeft.setVisibility(View.VISIBLE);
            viewHolder.ivLeft.setImageResource(images[position]);
        }
        return convertView;
    }

    static class ViewHolder {
        public LinearLayout llTitle;

        public TextView tvTitle;
        public TextView tvTitle1;
        public ImageView ivPicture;
        public ImageView ivLeft;
        public ImageView ivRedDot;
        public LinearLayout llItem;

    }

}