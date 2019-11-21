package com.bluebud.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebud.app.AppApplication;
import com.bluebud.listener.onGroupExpandedListener;
import com.bluebud.liteguardian_hk.R;


/**
 * @author Richie on 2017.07.31
 *         普通的 ExpandableListView 的适配器
 */
public class NormalExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String TAG = "NormalExpandableListAda";
    private int[] groupData;
    private int[] imageData;
    private String[][] childData;
    private onGroupExpandedListener mOnGroupExpandedListener;
    private int index = -1;
    private boolean isHaveFault = false;


    public NormalExpandableListAdapter(int[] groupData, int[] imageData, String[][] childData, int index) {
        this.groupData = groupData;
        this.childData = childData;
        this.imageData = imageData;
        this.index = index;
    }

    public void addChildData(String[][] childData, boolean isHaveFault, boolean isFromService) {
        this.childData = childData;
        this.isHaveFault = isHaveFault;
        if (!isFromService) {
            this.index = 5;
        }
        notifyDataSetChanged();
    }

    public void updateIndexIcon(int index) {
        this.index = index;
        notifyDataSetChanged();
    }

    public void setOnGroupExpandedListener(onGroupExpandedListener onGroupExpandedListener) {
        mOnGroupExpandedListener = onGroupExpandedListener;
    }

    @Override
    public int getGroupCount() {
        return groupData.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //return childData[groupPosition].length;
        if (groupPosition == groupData.length - 1) {
            return childData[groupPosition].length;
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupData[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childData[groupPosition][childPosition];


    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View
            convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_group_normal, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.mTvSpecies = (TextView) convertView.findViewById(R.id.tv_species);
            groupViewHolder.mIvSpecies = (ImageView) convertView.findViewById(R.id.iv_species);
            groupViewHolder.mIvStatus = (ImageView) convertView.findViewById(R.id.iv_status);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.mTvSpecies.setText(AppApplication.getInstance().getString(groupData[groupPosition]));
        groupViewHolder.mIvSpecies.setImageResource(imageData[groupPosition]);
        if (index >= groupPosition) {
            groupViewHolder.mIvStatus.setVisibility(View.VISIBLE);
        } else {
            groupViewHolder.mIvStatus.setVisibility(View.INVISIBLE);
        }
        if (groupPosition == groupData.length - 1) {
            if (isHaveFault) {
                groupViewHolder.mIvStatus.setImageResource(R.drawable.icon_error);
            } else {
                groupViewHolder.mIvStatus.setImageResource(R.drawable.icon_r);
            }
        } else {
            groupViewHolder.mIvStatus.setImageResource(R.drawable.icon_r);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_species);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tvContent.setText(childData[groupPosition][childPosition]);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        Log.d(TAG, "onGroupExpanded() called with: groupPosition = [" + groupPosition + "]");
        if (mOnGroupExpandedListener != null) {
            mOnGroupExpandedListener.onGroupExpanded(groupPosition);
        }
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        Log.d(TAG, "onGroupCollapsed() called with: groupPosition = [" + groupPosition + "]");
    }

    private static class GroupViewHolder {
        TextView mTvSpecies;//种类
        ImageView mIvSpecies;//种类图标
        ImageView mIvStatus;//类态
    }

    private static class ChildViewHolder {
        TextView tvContent;
    }
}
