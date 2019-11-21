package com.bluebud.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.Tracker;
import com.bluebud.utils.LogUtil;
import com.bluebud.utils.Utils;
import com.bluebud.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class TrackerAdapter extends BaseAdapter {
    private Context context;
    private List<Tracker> lists;

    public TrackerAdapter(Context context, List<Tracker> lists) {
        this.context = context;
        this.lists = lists;
    }

    public void setList(List<Tracker> lists) {
        this.lists = lists;
    }

    @Override
    public int getCount() {
        if (null == lists) {
            return 0;
        } else {
            return lists.size();
        }

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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Tracker tracker = lists.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_tracker_item, null);
            holder = new ViewHolder();
            holder.ivPicture = (CircleImageView) convertView.findViewById(R.id.iv_picture);
            holder.tvTitleName = (TextView) convertView.findViewById(R.id.tv_title_name);
            holder.tvTitleNo = (TextView) convertView.findViewById(R.id.tv_title_no);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvTitleNo.setText(tracker.device_sn);
//        holder.tvTitleName.setText("");
        LogUtil.e("ranges:" + tracker.ranges);
        if (!Utils.isEmpty(tracker.nickname)) {
            holder.tvTitleName.setVisibility(View.VISIBLE);
            holder.tvTitleName.setText(tracker.nickname);
        } else {
            holder.tvTitleName.setVisibility(View.GONE);
        }

        // 使用范围 1.个人，2.宠物，3.汽车，4.摩托车,5.手表，6.OBD汽车

        LogUtil.e("tracler no:" + tracker.device_sn + ",,head_portrait:" + tracker.head_portrait);
//        holder.ivPicture.re
        if (TextUtils.isEmpty(tracker.head_portrait)) {
            holder.ivPicture.setImageResource(getTypeDefaultImage(tracker.ranges));
        } else {
            String pic_url = Utils.getImageUrl(tracker) + tracker.head_portrait;
            Glide.with(context).load(pic_url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.ivPicture);
        }
        return convertView;
    }

    /**
     * 获取默认本地头像
     *
     * @param rangs 范围
     * @return
     */
    private int getTypeDefaultImage(int rangs) {
        if (rangs == 1) {
            return R.drawable.image_preson_sos;
        } else if (rangs == 2) {
            return R.drawable.image_pet;
        } else if (rangs == 3 || rangs == 6) {
            return R.drawable.image_car;
        } else if (rangs == 4) {
            return R.drawable.image_motorcycle;
        } else {
            return R.drawable.image_watch;
        }
    }



    static class ViewHolder {
        CircleImageView ivPicture;
        TextView tvTitleName;
        TextView tvTitleNo;
    }

}