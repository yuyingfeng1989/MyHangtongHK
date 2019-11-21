package com.bluebud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.Tracker;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.Utils;

public class AlarmAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int[] titles;
    private int[] switchs;
    private Context context;
    private SBOnCheckedChangeListener checkedChangeListener;
    private boolean bChecked = false;
    private Tracker tracker;
//    private boolean isFirst = false;
//    private int i = 0;

    public AlarmAdapter(Context context, int[] titles, int[] switchs,
                        SBOnCheckedChangeListener checkedChangeListener, Tracker tracker) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.titles = titles;
        this.switchs = switchs;
        this.checkedChangeListener = checkedChangeListener;
        this.tracker = tracker;
    }

    public void setList(int[] titles, int[] switchs) {//, boolean isFirst
        this.titles = titles;
        this.switchs = switchs;
//        this.isFirst = isFirst;
    }

    @Override
    public int getCount() {
        if (null != titles) {
            return titles.length;
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return titles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_alarm_switch_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.switchButton = (CheckBox) convertView.findViewById(R.id.switch_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(titles[position]);
        viewHolder.title.setTag(position);
        viewHolder.switchButton.setTag(position);

        if (switchs[position] == 1) {
            viewHolder.switchButton.setChecked(true);
        } else {
            viewHolder.switchButton.setChecked(false);
        }

        bChecked = false;


        //LogUtil.i("#################################:" + position + "=" + switchs[position]);
        viewHolder.switchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int a = (Integer) v.getTag();
                //viewHolder.switchButton.isChecked();
                LogUtil.i("POSITION:" + a + ",#####" + viewHolder.switchButton.isChecked() + ",,," + viewHolder.title.getTag().toString());
                checkedChangeListener.onCheckedChanged(Integer
                                .valueOf(viewHolder.title.getTag().toString()),
                        viewHolder.switchButton.isChecked());
            }
        });


        viewHolder.switchButton
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean arg1) {
                        //LogUtil.i("132eetet"+i);
                        if (!bChecked) {
                            return;
                        }
                        if (!Utils.isSuperUser(tracker, context)) {
                            //不是超级用户不让他点击
                            boolean checked = viewHolder.switchButton.isChecked();
                            if (checked) {
                                viewHolder.switchButton.setChecked(false);
                            } else {
                                viewHolder.switchButton.setChecked(true);
                            }
                            return;
                        }
                    }
                });

        if (position == (titles.length - 1)) {
            bChecked = true;
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView title;
        public CheckBox switchButton;
    }

    public interface SBOnCheckedChangeListener {
        public void onCheckedChanged(int position, boolean flag);
    }

}
