package com.bluebud.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.PhonebookInfo;
import com.bluebud.view.CircleImageView;

import java.util.List;

/**
 * Created by user on 2018/6/11.
 */

public class PhoneBookAdapter extends BaseAdapter {
    private Context mContext;
    private List<PhonebookInfo> infos;
    private PhoneViewHolder holder;
    private int[] images;
    private String productType;

    public PhoneBookAdapter(Context mContext, List<PhonebookInfo> infos, int[] images, String productType) {
        this.mContext = mContext;
        this.infos = infos;
        this.images = images;
        this.productType = productType;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int i) {
        return infos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            holder = new PhoneViewHolder();
            view = View.inflate(mContext, R.layout.phonebook_item, null);
            holder.photoImage = (CircleImageView) view.findViewById(R.id.iv_phonebook_image);//头像
            holder.nickName = (TextView) view.findViewById(R.id.tv__phonebook_nickname);//昵称
            holder.phoneNumber = (TextView) view.findViewById(R.id.tv_phonebook_number);//号码
            view.setTag(holder);
        } else
            holder = (PhoneViewHolder) view.getTag();
        PhonebookInfo info = infos.get(i);
        holder.nickName.setText(info.nickname);
        holder.phoneNumber.setText(info.phoneNum);
        if (!productType.equals("24"))//|| !productType.equals("31")
            holder.photoImage.setImageResource(images[info.photo]);
        else holder.photoImage.setImageResource(R.drawable.ico_phone_default);
        return view;
    }

    /**
     * 刷新数据
     */
    public void refreshPhoneBookInfos(List<PhonebookInfo> infos) {
        this.infos = infos;
        notifyDataSetChanged();
    }

    /**
     * 重用布局控件
     */
    class PhoneViewHolder {
        CircleImageView photoImage;
        TextView nickName;
        TextView phoneNumber;
    }
}
