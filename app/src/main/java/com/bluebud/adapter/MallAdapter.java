package com.bluebud.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.GoodsInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class MallAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    List<GoodsInfo> goodsList;

    public MallAdapter(Context context, List<GoodsInfo> goodsList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.goodsList = goodsList;
    }

    public void setList(List<GoodsInfo> goodsList) {
        this.goodsList = goodsList;
    }

    @Override
    public int getCount() {
        if (null == goodsList) {
            return 0;
        } else {
            return goodsList.size();
        }

    }

    @Override
    public Object getItem(int position) {

        return goodsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_mall_item, null);
            viewHolder = new ViewHolder();
            viewHolder.ivGoodsImage = (ImageView) convertView
                    .findViewById(R.id.iv_goods_image);
            viewHolder.tvGoodsNameEn = (TextView) convertView
                    .findViewById(R.id.tv_goods_name_en);
            viewHolder.tvGoodsPrice = (TextView) convertView
                    .findViewById(R.id.tv_goods_price);
            viewHolder.tvGoodsNameCh = (TextView) convertView
                    .findViewById(R.id.tv_goods_name_ch);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (goodsList != null) {
            viewHolder.tvGoodsNameCh.setText(goodsList.get(position).name);
            String price = goodsList.get(position).price;
            if(!TextUtils.isEmpty(price)) {
                String unit = goodsList.get(position).currency_unit;
                if ("hkd".equalsIgnoreCase(unit)) {//港币
                    viewHolder.tvGoodsPrice.setText("HK $ " + price + ".00");
                    //"HK $";
                } else if ("usd".equalsIgnoreCase(unit)) {//美元
                    viewHolder.tvGoodsPrice.setText("$ " + price + ".00");
                } else if ("rmb".equalsIgnoreCase(unit)) {//人民币
                    viewHolder.tvGoodsPrice.setText(price + ".00");
                } else {
                    viewHolder.tvGoodsPrice.setText(price + ".00");
                }
            }else{
                viewHolder.tvGoodsPrice.setVisibility(View.GONE);
            }
            Glide.with(context).load(goodsList.get(position).pic_url).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.ivGoodsImage);
        }
        return convertView;
    }

    static class ViewHolder {
        public ImageView ivGoodsImage;
        public LinearLayout llTitle;

        public TextView tvGoodsNameEn;
        public TextView tvGoodsNameCh;
        public TextView tvGoodsPrice;


    }

}