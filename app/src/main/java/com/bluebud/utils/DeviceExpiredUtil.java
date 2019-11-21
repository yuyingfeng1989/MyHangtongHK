package com.bluebud.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.bluebud.activity.settings.PayWayActivity;
import com.bluebud.data.sharedprefs.UserSP;
import com.bluebud.liteguardian_hk.R;
import com.bluebud.info.Tracker;

/**
 * Created by user on 2018/1/19.
 */

public class DeviceExpiredUtil {


    /**
     * 判断设备过期公共方法
     */
    @SuppressLint("StringFormatInvalid")
    public static boolean advancedFeatures(final Context mContext, final Tracker mCurTracker, boolean isAdvancedFeatures) {
        if (null == mCurTracker) {//没有设备
            return false;
        } else if (mCurTracker.expiredTime) {//企业到期mCurTracker.expiredTime
            DialogUtil.show(mContext, R.string.prompt, mContext.getString(R.string.business_expires), R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtil.dismiss();
                }
            });
            return true;
        } else if (mCurTracker.disable == 1) {//已停用mCurTracker.disable
            DialogUtil.show(mContext, R.string.prompt, mContext.getString(R.string.device_disabled), R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtil.dismiss();
                }
            });
            return true;
        } else if (mCurTracker.expiredTimeDe && isAdvancedFeatures) {// 设备到期mCurTracker.expiredTimeDe
            showDialog(mContext, mCurTracker, mContext.getString(R.string.pay_showdialog_content));//弹出框 device_expires
            return true;
        } else if (isAdvancedFeatures) {//设备到期, 一个月内到期,t每天只弹一次
            String sCurDay = Utils.curDate2CharDay(mContext);//当前天数时间
            String sDay = UserSP.getInstance().getTrackerExpire(mContext, mCurTracker.device_sn);
            if (!sDay.equals(sCurDay) && mCurTracker.one_month_expired) {// 一个月内到期,t每天只弹一次
                UserSP.getInstance().saveTrackerExpire(mContext, mCurTracker.device_sn, sCurDay);
                String sPrompt = mContext.getString(R.string.expire_time, Utils.utc2Local(mCurTracker.expired_time_de));
                showDialog(mContext, mCurTracker, sPrompt);//弹出框
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 弹出框
     */
    private static void showDialog(final Context mContext, final Tracker mCurTracker, String sPrompt) {
        DialogUtil.show(mContext, R.string.prompt, sPrompt,
                R.string.immediate_renewal, new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (Utils.isSuperUser(mCurTracker, mContext)) {
                            DialogUtil.dismiss();
                            mContext.startActivity(new Intent(mContext,
                                    PayWayActivity.class));
                        }
                    }
                }, R.string.cancel, new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        DialogUtil.dismiss();
                    }
                });
    }
}
