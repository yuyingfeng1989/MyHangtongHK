package com.bluebud.obd_optimize.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.obd_optimize.ObdDriverActivity;
import com.bluebud.obd_optimize.minterface.IDriverInterface;
import com.bluebud.utils.DateUtils;
import com.bluebud.utils.Utils;

/**
 * Created by Administrator on 2019/3/19.
 */

public class ObdWeeksFragment extends Fragment implements View.OnClickListener, IDriverInterface {
    private View contentView;
    private TextView obd_time;
    private int weekIndex;
    private ObdDriverActivity mContext;
    private ImageView obd_time_right;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (ObdDriverActivity) getActivity();
        mContext.setCallbackFragmentListener(null, this, null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == contentView) {
            contentView = inflater.inflate(R.layout.obd_fragment, container, false);
            initView();
        }
        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        contentView.findViewById(R.id.obd_time_left).setOnClickListener(this);
        obd_time_right = contentView.findViewById(R.id.obd_time_right);
        obd_time_right.setOnClickListener(this);
        obd_time = contentView.findViewById(R.id.obd_time);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.obd_time_left:
                weekIndex--;
                String lastWeek = DateUtils.getInsingle().getFetureDate(weekIndex * 7);
                Log.e("TAG", "lastWeek=" + lastWeek);
                showCalenderWeek(lastWeek);
                break;
            case R.id.obd_time_right:
                weekIndex++;
                String nextWeek = DateUtils.getInsingle().getFetureDate(weekIndex * 7);
                Log.e("TAG", "nextWeek=" + nextWeek);
                showCalenderWeek(nextWeek);
                break;
        }
    }


    /**
     * 请求驾驶数据
     */
    private void showCalenderWeek(String weekTime) {
        String[] split = weekTime.split("-");
        String startTime = split[0].replace("/", "-");
        String endTime = split[1].replace("/", "-");
        String textTime = split[0].substring(5) + "-" + split[1].substring(5);
        obd_time.setText(textTime);
        String currTime = Utils.getCurrentTime();
        int startState = DateUtils.getInsingle().compare_date(startTime,currTime);
        int endState = DateUtils.getInsingle().compare_date(endTime, currTime);
        Log.e("TAG","currTime="+currTime+" startState="+startState+" endState="+endState);
        if(startState<=0&&endState>=0){
            obd_time_right.setVisibility(View.GONE);
        }else{
            obd_time_right.setVisibility(View.VISIBLE);
        }
        mContext.obdRequestUtil.obdReuqestCallback(startTime, endTime);
    }

    @Override
    public void onclickPosition(int position) {
        String fetureDate = DateUtils.getInsingle().getFetureDate(weekIndex * 7);//当前周
        showCalenderWeek(fetureDate);
    }
}
