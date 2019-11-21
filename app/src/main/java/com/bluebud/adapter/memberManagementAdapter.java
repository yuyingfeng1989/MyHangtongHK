package com.bluebud.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.OnlyUser;
import com.bluebud.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class memberManagementAdapter extends BaseAdapter {
    private Context context;
    private List<OnlyUser> users;
    private boolean isShowDelete = false;
    private deleteAuthUserListener listener;

    public memberManagementAdapter(Context context, deleteAuthUserListener listener, List<OnlyUser> users) {
        this.context = context;
        this.users = users;
        this.listener = listener;

    }

    public void setList(List<OnlyUser> userInfos) {
        this.users = userInfos;
        notifyDataSetChanged();
    }

    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (null == users) {
            return 0;
        } else {
            return users.size();
        }
        //return 10;

    }

    @Override
    public Object getItem(int position) {

        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        View view = View.inflate(context,R.layout.layout_member_management_item,null);
        CircleImageView iv_tracker_image = (CircleImageView) view.findViewById(R.id.iv_tracker_image);
        ImageView deleteView = (ImageView) view.findViewById(R.id.delete_markView);
        TextView tv_nick_name = (TextView) view.findViewById(R.id.tv_nick_name);
        OnlyUser onlyUser = users.get(position);
        if (onlyUser.nickname != null) {
            tv_nick_name.setText(onlyUser.nickname);
        }

        if (TextUtils.isEmpty(onlyUser.portrait)) {//图像
            iv_tracker_image.setImageResource(R.drawable.img_defaulthead_628);
        } else {
            Glide.with(context).load(onlyUser.portrait).error(R.drawable.img_defaulthead_628).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(iv_tracker_image);
        }
        deleteView.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);
        deleteView.setTag(position);
        deleteView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                listener.deleteAuthUser(position);
            }
        });
        return view;
    }

    public interface deleteAuthUserListener {
        public void deleteAuthUser(int position);
    }


}