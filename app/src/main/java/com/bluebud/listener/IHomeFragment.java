package com.bluebud.listener;

import com.bluebud.info.CurrentGPS;

/**
 * Created by Administrator on 2019/4/24.
 */

public interface IHomeFragment {
    void callCurrentGPS(CurrentGPS currentGPS);//返回当前定位数据
    void callChatState();//是否是德国版
//    void callPushBluetoothWatchAlarmDataToService(int code);//蓝牙手表推送警情信息上传到服务器接口
//    void callCurrentBluetoothWatchGpsData(CurrentBluetoothGPS currentBluetoothGPS);//获取蓝牙手表最后定位位置
    void callLockVehicleState(boolean isLock);//锁定与解锁车辆
}
