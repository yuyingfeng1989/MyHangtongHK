package com.bluebud.obd_optimize.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluebud.liteguardian_hk.R;
import com.bluebud.obd_optimize.ObdDriverActivity;
import com.bluebud.obd_optimize.minterface.IDriverInterface;
import com.bluebud.utils.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2019/3/19.
 */

public class ObdMonthFragment extends Fragment implements View.OnClickListener, IDriverInterface {
    private View contentView;
    private TextView obd_time;
    private ObdDriverActivity mContext;
    private ImageView obd_time_right;
    private int monthIndex;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (ObdDriverActivity) getActivity();
        mContext.setCallbackFragmentListener(null, null, this);
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
            case R.id.obd_time_left://上一个月
                monthIndex++;
                String next12Months = DateUtils.getInsingle().getLast12Months(monthIndex);
                showCalenderMonth(next12Months);
                break;
            case R.id.obd_time_right://下一个月
                monthIndex--;
                String last12Months = DateUtils.getInsingle().getLast12Months(monthIndex);
                showCalenderMonth(last12Months);
                break;
        }
    }

    /**
     * 请求驾驶数据
     */
    private void showCalenderMonth(String monthTime) {
        String[] split = monthTime.split("-");
        String startTime = split[0].replace("/", "-");
        String endTime = split[1].replace("/", "-");
        String textTime = split[0].substring(5) + "-" + split[1].substring(5);
        obd_time.setText(textTime);
        String month = getCurrentMonth();
        if (month.equals(startTime)) {
            obd_time_right.setVisibility(View.GONE);
        } else {
            obd_time_right.setVisibility(View.VISIBLE);
        }
        mContext.obdRequestUtil.obdReuqestCallback(startTime, endTime);
    }

    @Override
    public void onclickPosition(int position) {
        String last12Months = DateUtils.getInsingle().getLast12Months(monthIndex);
        showCalenderMonth(last12Months);
    }

    /**
     * 获取当前月的第一天日期
     */
    SimpleDateFormat dateFormater;

    private String getCurrentMonth() {
        if (dateFormater == null)
            dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return dateFormater.format(cal.getTime());
    }
}
