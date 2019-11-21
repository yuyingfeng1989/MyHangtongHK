package com.bluebud.utils;

import com.bluebud.activity.settings.WifiSettingActivity;
import com.bluebud.liteguardian_hk.R;

/**
 * 解析WiFi通道返回结果
 *
 * @author Administrator
 */
public class ParseValueUtil {

    /**
     * wifi设置结果
     */
    public static void interceptParseSetting(WifiSettingActivity listener, String string) {
        int length = string.length();
        if (length <= 8) {
            listener.onRequestFailure(R.string.pay_network_error, 1);
            return;
        }

        String checkCode = string.substring(string.length() - 8, string.length() - 4);// 校验码

        String crc = string.substring(0, string.length() - 8);// 校验数据
        String backCrc = WifiSettingUtil.getCRC(crc);// 返回校验数据校验后的数据
        LogUtil.e("CRC校验数据==" + crc);
        LogUtil.e("backCrc验证码==" + backCrc);
        if (!backCrc.equalsIgnoreCase(checkCode)) {// 匹配校验位是否正确
            listener.onRequestFailure(R.string.pay_network_error, 1);
            return;
        }
        listener.onSetSuccess(R.string.set_success);
    }

    /**
     * 查询wifi结果
     */
    public static void interceptParseQuery(WifiSettingActivity listener, String string) {
        int length = string.length();
        if (length <= 8) {
            listener.onRequestFailure(R.string.pay_network_error, 1);
            return;
        }
        String checkCode = string.substring(string.length() - 8, string.length() - 4);// 校验码

        String crc = string.substring(0, string.length() - 8);// 校验数据
        String backCrc = WifiSettingUtil.getCRC(crc);// 返回校验数据校验后的数据
        LogUtil.e("CRC_parse==" + crc);
        LogUtil.e("验证码==" + backCrc);
        if (!backCrc.equalsIgnoreCase(checkCode)) {// 匹配校验位是否正确
            listener.onRequestFailure(R.string.pay_network_error, 0);
            return;
        }

        int startIndex1 = crc.indexOf("0234") + 4;// 截取wifi名开始位置
        int startIndex2 = crc.indexOf("0434") + 4;// 截取wifi密码开始位置

        String wifiNameSize = crc.substring(startIndex1, startIndex1 + 2);// 截取wifi名16进制字符串
        String wifiPasswordSize = crc.substring(startIndex2, startIndex2 + 2);// 截取wifi密码16进制字符串

        int nameSize = Integer.parseInt(wifiNameSize, 16);// wifi名长度
        int passwoidSize = Integer.parseInt(wifiPasswordSize, 16);// wifi密码长度
        String names = crc.substring(startIndex1 + 4, startIndex1 + 4 + nameSize * 2);// 截取制定长度的WiFi名
        String passwords = crc.substring(startIndex2 + 4, startIndex2 + 4 + passwoidSize * 2);// 截取制定长度的WiFi密码

        String ascllName = WifiSettingUtil.toAscllStringHex(names);// 将字符串转为ASCLL码
        String ascllPassword = WifiSettingUtil.toAscllStringHex(passwords);
        LogUtil.e("ascllName==" + ascllName + "ascllPassword==" + ascllPassword);
        listener.onReceiveData(ascllName, ascllPassword);// 接口回调参数
    }
}
