package com.bluebud.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.view.CircleImageView;

public class PeripherAdapter extends BaseAdapter {
    //	private Context context;
    private LayoutInflater inflater;

    private int[] images;
    private int[] themes;


    public PeripherAdapter(Context context, int[] images, int[] themes) {
        inflater = LayoutInflater.from(context);
//		this.context = context;
        this.images = images;
        this.themes = themes;

    }

    public void setlist(int[] images, int[] themes) {
        this.images = images;
        this.themes = themes;
    }

    @Override
    public int getCount() {
        if (null == themes) {
            return 0;
        }
        return themes.length;
    }

    @Override
    public Object getItem(int position) {

        return themes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_peripher_item, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (CircleImageView) convertView
                    .findViewById(R.id.iv_image);
            viewHolder.themes = (TextView) convertView
                    .findViewById(R.id.tv_themes);
            viewHolder.tv_items = (TextView) convertView
                    .findViewById(R.id.tv_items);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.image.setImageResource(images[position]);
        viewHolder.themes.setText(themes[position]);


        return convertView;
    }

    static class ViewHolder {
        public CircleImageView image;
        public TextView themes;
        public TextView tv_items;

    }

}