package com.bluebud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;

import java.util.List;

public class ListPositionAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<String> lists;
    private int positions;
    private Context context;

    public ListPositionAdapter(Context context, List<String> lists, int position) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.lists = lists;
        this.positions = position;
    }

    @Override
    public int getCount() {
        if (null != lists) {
            return lists.size();
        } else {
            return 0;
        }
    }

    public void setpositon(int position) {
        this.positions = position;
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
                    .inflate(R.layout.layout_menu_list_position_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView
                    .findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(lists.get(position));
        if (positions == position) {
            viewHolder.title.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            viewHolder.title.setTextColor(context.getResources().getColor(R.color.text_9));
        }


        return convertView;
    }

    private class ViewHolder {
        public TextView title;
    }

}
