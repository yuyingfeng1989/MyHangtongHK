package com.bluebud.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.TakePhotoInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;


/**
 * Created by owp on 2017/4/10.
 */

public class RemoteTakePhotoAdapter extends RecyclerView.Adapter<RemoteTakePhotoAdapter.ChildViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(View v, int position, TakePhotoInfo photo);
    }

    @Nullable
    public OnItemClickListener mItemClickListener;
    @NonNull
    private List<TakePhotoInfo> items;

    public RemoteTakePhotoAdapter(@Nullable Object listener, List<TakePhotoInfo> items) {
        this.items = items;
        addListener(listener);
    }

    public void addListener(@Nullable Object listener) {
        if (listener instanceof OnItemClickListener) {
            mItemClickListener = (OnItemClickListener) listener;
        }
    }

    public void setItems(@NonNull List<TakePhotoInfo> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    public List<TakePhotoInfo> getItems() {
        return items;
    }

    public TakePhotoInfo getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChildViewHolder(parent, R.layout.remote_takephoto_item);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ChildViewHolder holder, final int position) {
        Context context = holder.itemView.getContext();
        TakePhotoInfo photo = items.get(position);
        Glide.with(context)
                .load(photo.thumbnailUrl)
                .placeholder(R.drawable.ic_empty_photo)
                .error(R.drawable.ic_empty_photo)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.mIcon);

        if (photo.isShow)
            holder.checkBox.setVisibility(View.VISIBLE);
        else
            holder.checkBox.setVisibility(View.GONE);

        if (photo.isSelect)
            holder.checkBox.setChecked(true);
        else holder.checkBox.setChecked(false);
        initeListener(holder, photo, position);
    }

    /**
     * 监听
     */
    private void initeListener(final ChildViewHolder holder, final TakePhotoInfo photo, final int position) {
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChangeStateValue(holder,photo,!holder.checkBox.isChecked());
                mItemClickListener.onItemClick(view, position, photo);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    isChangeStateValue(holder,photo,holder.checkBox.isChecked());
                    mItemClickListener.onItemClick(v, position, photo);
                }
            }
        });
    }

    /**
     * 状态改变
     */
    private void isChangeStateValue(ChildViewHolder holder, TakePhotoInfo photo,boolean isCheck){
        if (photo.isShow) {
            if (holder.checkBox.isShown()&&isCheck) {
                holder.checkBox.setChecked(false);
                photo.isSelect = false;
            } else {
                holder.checkBox.setChecked(true);
                photo.isSelect = true;
            }
        } else {
            holder.checkBox.setChecked(false);
            photo.isSelect = false;
        }
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {

        ImageView mIcon;
        CheckBox checkBox;

        ChildViewHolder(@NonNull ViewGroup parent, @LayoutRes int layoutResId) {
            this(LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false));
        }

        ChildViewHolder(View itemView) {
            super(itemView);
            this.mIcon = (ImageView) itemView.findViewById(android.R.id.icon);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.checkboxs);
        }
    }
}
