package com.bluebud.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluebud.info.Alarm;
import com.bluebud.info.Tracker;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.utils.UserUtil;
import com.bluebud.utils.Utils;

import java.util.HashMap;
import java.util.List;

public class AlarmNoteAdapter1 extends BaseAdapter {
    private Context context;
    private List<Alarm> alarms;
    private int flag = 0;
    public static HashMap<Integer, Boolean> isSelected; // 用来控制CheckBox的选中状况
    private String nickname;

    @SuppressLint("UseSparseArrays")
    public AlarmNoteAdapter1(Context context, List<Alarm> alarms,
                             int flag) {
        this.context = context;
        this.alarms = alarms;
        this.flag = flag;
        isSelected = new HashMap<Integer, Boolean>();
        // 初始化 设置所有checkbox都为未选择
        for (int i = 0; i < 100000; i++) {
            isSelected.put(i, false);
        }

    }


    public void setLists(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    public void setflag(int flag) {
        this.flag = flag;
    }


    @Override
    public int getCount() {
        if (alarms == null || alarms.size() == 0) {
            return 0;
        }
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_alarm_item, null);

            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_alarm);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_dtime);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
            viewHolder.tvSN = (TextView) convertView.findViewById(R.id.tv_sn);
            viewHolder.ibSelect = (CheckBox) convertView.findViewById(R.id.iv_select);
            viewHolder.ibSelect.setTag(position);
            viewHolder.ivDot = (ImageView) convertView.findViewById(R.id.iv_dot);
            viewHolder.llSelectAlarm = (LinearLayout) convertView.findViewById(R.id.ll_select_alarm);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvContent.setText(Utils.getAlarmType(context, alarms.get(position).type));
//		viewHolder.tvTime.setText(Utils.dateString2DaysSS(context,
//				alarms.get(position).dtime));
        viewHolder.tvTime.setText(alarms.get(position).dtime);
        Tracker mCurTracker = UserUtil.getCurrentTracker(context);

        if (!Utils.isEmpty(alarms.get(position).address)) {//地址为空不显示
            //得到昵称
            if (alarms.get(position).id <= 0) {//推送过来的信息得到昵称
                nickname = Utils.getNickName(context, alarms.get(position).serialNumber);
            } else {
                if (mCurTracker != null) {//获取设备的昵称
                    nickname = mCurTracker.nickname;
                    if (Utils.isEmpty(nickname)) {
                        nickname = "";
                    }
                }
            }
            viewHolder.tvAddress.setVisibility(View.VISIBLE);
            // viewHolder.tvAddress.setText(alarms.get(position).address);
            String alarmType = Utils.getAlarmType(context, alarms.get(position).type);//得到报警类型
            String finallyAlarmType = null;
            if (!Utils.isEmpty(alarmType)) {//报警类型不为空时
//				if (alarmType.indexOf(context.getString(R.string.alarm_note)) >0) {//判断是否含有“报警”二字，有去掉
//					String alarmTypeNew = alarmType.replace(context.getString(R.string.alarm_note), "");
//					if (alarmTypeNew.indexOf(context.getString(R.string.warning)) >0) {//判断是否含有“告警”二字，有去掉
//						String alarmTypeNew2 = alarmType.replace(context.getString(R.string.warning), "");
//						finallyAlarmType=alarmTypeNew2;
//					} else {
//						finallyAlarmType=alarmTypeNew;
//					}
//				} else {
//					finallyAlarmType=alarmType;
//				}
                finallyAlarmType = alarmType;
            } else {
                finallyAlarmType = "";
            }
            viewHolder.tvAddress.setText(context.getString(R.string.alarm_event, nickname, alarms.get(position).dtime, alarms.get(position).address, finallyAlarmType));
        } else {
            viewHolder.tvAddress.setVisibility(View.GONE);
        }
        if (alarms.get(position).readstatus == 0) {//表示未读
            viewHolder.ivDot.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivDot.setVisibility(View.GONE);//表示已读
        }


        if (flag == 0) {//不在编辑状态
            viewHolder.ibSelect.setVisibility(View.GONE);
            viewHolder.llSelectAlarm.setBackgroundResource(R.drawable.list_item_selector);
            if (alarms.get(position).readstatus == 0) {//表示未读
                viewHolder.tvContent.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.tvSN.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.tvAddress.setTextColor(context.getResources().getColor(R.color.black));
            } else {//表示已读
                viewHolder.tvContent.setTextColor(context.getResources().getColor(R.color.text_9));
                viewHolder.tvSN.setTextColor(context.getResources().getColor(R.color.text_9));
                viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.text_9));
                viewHolder.tvAddress.setTextColor(context.getResources().getColor(R.color.text_9));
            }
        } else {//在编辑状态holder
            viewHolder.ibSelect.setVisibility(View.VISIBLE);
            viewHolder.ibSelect.setChecked(getIsSelected().get(position));
            //viewHolder.ibSelect.setChecked(alarms.get(position).getFlag());
            if (getIsSelected().get(position)) {//选中状态
                viewHolder.llSelectAlarm.setBackgroundResource(R.color.opaque);
                viewHolder.tvContent.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.tvSN.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.tvAddress.setTextColor(context.getResources().getColor(R.color.black));

            } else {//未选中状态
                viewHolder.llSelectAlarm.setBackgroundResource(R.color.bg_white_bg_nor);
                viewHolder.tvContent.setTextColor(context.getResources().getColor(R.color.text_9));
                viewHolder.tvSN.setTextColor(context.getResources().getColor(R.color.text_9));
                viewHolder.tvTime.setTextColor(context.getResources().getColor(R.color.text_9));
                viewHolder.tvAddress.setTextColor(context.getResources().getColor(R.color.text_9));
            }


        }
        return convertView;
    }

    public class ViewHolder {
        public TextView tvContent;//警情类型
        public TextView tvSN;
        public TextView tvTime;//时间
        public TextView tvAddress;//地址
        public CheckBox ibSelect;//选中开关
        public ImageView ivDot;//红点
        public LinearLayout llSelectAlarm;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        AlarmNoteAdapter1.isSelected = isSelected;
    }

}
