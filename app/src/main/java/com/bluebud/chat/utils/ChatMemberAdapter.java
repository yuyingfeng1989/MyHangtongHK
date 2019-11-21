package com.bluebud.chat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ChatMemberAdapter extends BaseAdapter {
    private Context mcontext;
    private List<UserInfo> userlist;
    private boolean isSuper;

    public ChatMemberAdapter(Context context, List<UserInfo> userIdList, boolean isSuper) {
        this.mcontext = context;
        this.userlist = userIdList;
        this.isSuper = isSuper;
    }

    @Override
    public int getCount() {
        int size = userlist.size();
        if (isSuper) {
            if (size > 1 && size < 4)
                return size + 2;
            else
                return size + 1;
        } else
            return size;

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int size = userlist.size();
        View view = View.inflate(mcontext, R.layout.chat_item_member, null);
        CircleImageView imageview = (CircleImageView) view.findViewById(R.id.chat_member_image);
        TextView text_name = (TextView) view.findViewById(R.id.chat_text_name);
        if (size == 1) {//当只有自己和设备时
            if (position == 0) {//等于0显示设备
                UserInfo userInfo = userlist.get(position);
                onLoadingImage(userInfo, imageview);
                if (userInfo.getRemark() != null)
                    text_name.setText(userInfo.getRemark());
                else if (userInfo.getNickname() != null)
                    text_name.setText(userInfo.getNickname());
                else
                    text_name.setText(userInfo.getName());
            } else {//等于1时显示添加按钮
                imageview.setImageResource(R.drawable.chat_add_select);
                text_name.setVisibility(View.GONE);
            }
        } else if (position < size) {
            UserInfo userInfo = userlist.get(position);
            onLoadingImage(userInfo, imageview);
            if (userInfo.getRemark() != null)//如果别名不为空显示，否则昵称不为空显示，否则显示用户名
                text_name.setText(userInfo.getRemark());
            else if (userInfo.getNickname() != null)
                text_name.setText(userInfo.getNickname());
            else
                text_name.setText(userInfo.getName());
        } else if (position == size) {
            if (size < 4) {//小于四个用户时，等于最后一个用户时，显示添加
                imageview.setImageResource(R.drawable.chat_add_select);
                text_name.setVisibility(View.GONE);
            } else {//等于4时则只显示删除
                imageview.setImageResource(R.drawable.chat_delete_select);
                text_name.setVisibility(View.GONE);
            }
        } else if (position == size + 1) {//只显示删除
            imageview.setImageResource(R.drawable.chat_delete_select);
            text_name.setVisibility(View.GONE);
        }
        return view;
    }

    /**
     * 加载图片显示
     */
    private void onLoadingImage(UserInfo userInfo, CircleImageView imageview) {
        if (!TextUtils.isEmpty(userInfo.getPortrait()))
            Glide.with(mcontext).load(userInfo.getPortrait()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageview);
        else if ("1".equals(userInfo.getType()))
            imageview.setImageResource(R.drawable.image_watch);
        else imageview.setImageResource(R.drawable.img_defaulthead_628);
    }
}
