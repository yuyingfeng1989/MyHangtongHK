package com.bluebud.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.bluebud.liteguardian_hk.R;

/**
 * Created by user on 2018/6/11.  电话本编辑头像
 */

public class RecycleViewImage extends RelativeLayout {
    private CircleImageView image;
    private Context context;

    public RecycleViewImage(Context context) {
        super(context);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.activity_recycleview, this);
        image = (CircleImageView) view.findViewById(R.id.iv_tracker_image);
    }

    public void setBackgroundResource(int resId) {
        if (image != null) {
            image.setImageResource(resId);
        }
    }

    public void settBorderColor1(int color) {
        if (image != null) {
            image.setBorderWidth(10);
            image.setBorderColor(color);
        }
    }

    public void setSelected1() {
        if (image != null) {
            image.setBorderWidth(10);
            if (isSelected()) {
                image.setBorderColor(context.getResources().getColor(R.color.bg_theme));
            } else {
                image.setBorderColor(context.getResources().getColor(R.color.green_circle_solid));
            }
        }
    }
}
