package com.bluebud.chat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Map;

public class OperatGroupAdapter extends BaseAdapter {

    private Context mcontext;
    private List<UserInfo> list;
    private Map<String, Boolean> map;

    public OperatGroupAdapter(Context context, List<UserInfo> userIdList, Map<String, Boolean> map) {
        this.mcontext = context;
        this.list = userIdList;
        this.map = map;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserInfo userInfo = list.get(position);
        View view = View
                .inflate(mcontext, R.layout.chat_item_operatgroup, null);
        CircleImageView imagephoto = (CircleImageView) view
                .findViewById(R.id.chat_group_image);
        TextView chat_text_name = (TextView) view
                .findViewById(R.id.chat_text_name);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        if (userInfo.getPortrait() != null)
            Glide.with(mcontext).load(userInfo.getPortrait()).dontAnimate().error(R.drawable.img_defaulthead_628).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imagephoto);
        else
            imagephoto.setImageResource(R.drawable.img_defaulthead_628);

        if (userInfo.getRemark() != null)//如果别名不为空显示，否则昵称不为空显示，否则显示用户名
            chat_text_name.setText(userInfo.getRemark());
        else if (userInfo.getNickname() != null)
            chat_text_name.setText(userInfo.getNickname());
        else
            chat_text_name.setText(userInfo.getName());

        checkBox.setChecked(map.get(userInfo.getName()));
        initeListner(view, checkBox, userInfo);
        return view;
    }

    private void initeListner(View view, final CheckBox checkBox, final UserInfo userInfo) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = userInfo.getName();
                if (map.get(name)) {
                    map.put(name, false);
                    checkBox.setChecked(false);
                } else {
                    map.put(name, true);
                    checkBox.setChecked(true);
                }
                notifyDataSetChanged();
            }
        });
    }
}
