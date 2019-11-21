package com.bluebud.utils;

import android.annotation.SuppressLint;

public class WifiSettingUtil {

    /**
     * ------------------校验数据协议格式数据 crc和校验
     */
    private final static String REGEX = "(.{2})";

    private final static int FCS_START = 0xffff;

    private final static int[] FCSTAB = {0x0000, 0x1189, 0x2312, 0x329b,
            0x4624, 0x57ad, 0x6536, 0x74bf, 0x8c48, 0x9dc1, 0xaf5a, 0xbed3,
            0xca6c, 0xdbe5, 0xe97e, 0xf8f7, 0x1081, 0x0108, 0x3393, 0x221a,
            0x56a5, 0x472c, 0x75b7, 0x643e, 0x9cc9, 0x8d40, 0xbfdb, 0xae52,
            0xdaed, 0xcb64, 0xf9ff, 0xe876, 0x2102, 0x308b, 0x0210, 0x1399,
            0x6726, 0x76af, 0x4434, 0x55bd, 0xad4a, 0xbcc3, 0x8e58, 0x9fd1,
            0xeb6e, 0xfae7, 0xc87c, 0xd9f5, 0x3183, 0x200a, 0x1291, 0x0318,
            0x77a7, 0x662e, 0x54b5, 0x453c, 0xbdcb, 0xac42, 0x9ed9, 0x8f50,
            0xfbef, 0xea66, 0xd8fd, 0xc974, 0x4204, 0x538d, 0x6116, 0x709f,
            0x0420, 0x15a9, 0x2732, 0x36bb, 0xce4c, 0xdfc5, 0xed5e, 0xfcd7,
            0x8868, 0x99e1, 0xab7a, 0xbaf3, 0x5285, 0x430c, 0x7197, 0x601e,
            0x14a1, 0x0528, 0x37b3, 0x263a, 0xdecd, 0xcf44, 0xfddf, 0xec56,
            0x98e9, 0x8960, 0xbbfb, 0xaa72, 0x6306, 0x728f, 0x4014, 0x519d,
            0x2522, 0x34ab, 0x0630, 0x17b9, 0xef4e, 0xfec7, 0xcc5c, 0xddd5,
            0xa96a, 0xb8e3, 0x8a78, 0x9bf1, 0x7387, 0x620e, 0x5095, 0x411c,
            0x35a3, 0x242a, 0x16b1, 0x0738, 0xffcf, 0xee46, 0xdcdd, 0xcd54,
            0xb9eb, 0xa862, 0x9af9, 0x8b70, 0x8408, 0x9581, 0xa71a, 0xb693,
            0xc22c, 0xd3a5, 0xe13e, 0xf0b7, 0x0840, 0x19c9, 0x2b52, 0x3adb,
            0x4e64, 0x5fed, 0x6d76, 0x7cff, 0x9489, 0x8500, 0xb79b, 0xa612,
            0xd2ad, 0xc324, 0xf1bf, 0xe036, 0x18c1, 0x0948, 0x3bd3, 0x2a5a,
            0x5ee5, 0x4f6c, 0x7df7, 0x6c7e, 0xa50a, 0xb483, 0x8618, 0x9791,
            0xe32e, 0xf2a7, 0xc03c, 0xd1b5, 0x2942, 0x38cb, 0x0a50, 0x1bd9,
            0x6f66, 0x7eef, 0x4c74, 0x5dfd, 0xb58b, 0xa402, 0x9699, 0x8710,
            0xf3af, 0xe226, 0xd0bd, 0xc134, 0x39c3, 0x284a, 0x1ad1, 0x0b58,
            0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c, 0xc60c, 0xd785, 0xe51e, 0xf497,
            0x8028, 0x91a1, 0xa33a, 0xb2b3, 0x4a44, 0x5bcd, 0x6956, 0x78df,
            0x0c60, 0x1de9, 0x2f72, 0x3efb, 0xd68d, 0xc704, 0xf59f, 0xe416,
            0x90a9, 0x8120, 0xb3bb, 0xa232, 0x5ac5, 0x4b4c, 0x79d7, 0x685e,
            0x1ce1, 0x0d68, 0x3ff3, 0x2e7a, 0xe70e, 0xf687, 0xc41c, 0xd595,
            0xa12a, 0xb0a3, 0x8238, 0x93b1, 0x6b46, 0x7acf, 0x4854, 0x59dd,
            0x2d62, 0x3ceb, 0x0e70, 0x1ff9, 0xf78f, 0xe606, 0xd49d, 0xc514,
            0xb1ab, 0xa022, 0x92b9, 0x8330, 0x7bc7, 0x6a4e, 0x58d5, 0x495c,
            0x3de3, 0x2c6a, 0x1ef1, 0x0f78};

    public static int GetFcs(int fcs, int s) {
        return (fcs >> 8) ^ FCSTAB[(fcs ^ s) & 0x00ff];
    }

    public static String getCRC(String str) {
        str = str.replaceAll(" ", "").replaceAll(REGEX, "$1,");
        int a = FCS_START;
        String[] split = str.split(",");
        for (String string : split) {
            int parseInt = Integer.parseInt(string, 16);
            a = GetFcs(a, parseInt);
        }
        a ^= FCS_START;
        String string = String.format("%04x", a);
        return string.substring(2, string.length()) + string.substring(0, 2);
    }

    // private final static int FCS_START = 0xffff;
    // private final static int[] FCSTAB = { 0x0000, 0x1189, 0x2312, 0x329b,
    // 0x4624, 0x57ad, 0x6536, 0x74bf, 0x8c48, 0x9dc1, 0xaf5a, 0xbed3,
    // 0xca6c, 0xdbe5, 0xe97e, 0xf8f7, 0x1081, 0x0108, 0x3393, 0x221a,
    // 0x56a5, 0x472c, 0x75b7, 0x643e, 0x9cc9, 0x8d40, 0xbfdb, 0xae52,
    // 0xdaed, 0xcb64, 0xf9ff, 0xe876, 0x2102, 0x308b, 0x0210, 0x1399,
    // 0x6726, 0x76af, 0x4434, 0x55bd, 0xad4a, 0xbcc3, 0x8e58, 0x9fd1,
    // 0xeb6e, 0xfae7, 0xc87c, 0xd9f5, 0x3183, 0x200a, 0x1291, 0x0318,
    // 0x77a7, 0x662e, 0x54b5, 0x453c, 0xbdcb, 0xac42, 0x9ed9, 0x8f50,
    // 0xfbef, 0xea66, 0xd8fd, 0xc974, 0x4204, 0x538d, 0x6116, 0x709f,
    // 0x0420, 0x15a9, 0x2732, 0x36bb, 0xce4c, 0xdfc5, 0xed5e, 0xfcd7,
    // 0x8868, 0x99e1, 0xab7a, 0xbaf3, 0x5285, 0x430c, 0x7197, 0x601e,
    // 0x14a1, 0x0528, 0x37b3, 0x263a, 0xdecd, 0xcf44, 0xfddf, 0xec56,
    // 0x98e9, 0x8960, 0xbbfb, 0xaa72, 0x6306, 0x728f, 0x4014, 0x519d,
    // 0x2522, 0x34ab, 0x0630, 0x17b9, 0xef4e, 0xfec7, 0xcc5c, 0xddd5,
    // 0xa96a, 0xb8e3, 0x8a78, 0x9bf1, 0x7387, 0x620e, 0x5095, 0x411c,
    // 0x35a3, 0x242a, 0x16b1, 0x0738, 0xffcf, 0xee46, 0xdcdd, 0xcd54,
    // 0xb9eb, 0xa862, 0x9af9, 0x8b70, 0x8408, 0x9581, 0xa71a, 0xb693,
    // 0xc22c, 0xd3a5, 0xe13e, 0xf0b7, 0x0840, 0x19c9, 0x2b52, 0x3adb,
    // 0x4e64, 0x5fed, 0x6d76, 0x7cff, 0x9489, 0x8500, 0xb79b, 0xa612,
    // 0xd2ad, 0xc324, 0xf1bf, 0xe036, 0x18c1, 0x0948, 0x3bd3, 0x2a5a,
    // 0x5ee5, 0x4f6c, 0x7df7, 0x6c7e, 0xa50a, 0xb483, 0x8618, 0x9791,
    // 0xe32e, 0xf2a7, 0xc03c, 0xd1b5, 0x2942, 0x38cb, 0x0a50, 0x1bd9,
    // 0x6f66, 0x7eef, 0x4c74, 0x5dfd, 0xb58b, 0xa402, 0x9699, 0x8710,
    // 0xf3af, 0xe226, 0xd0bd, 0xc134, 0x39c3, 0x284a, 0x1ad1, 0x0b58,
    // 0x7fe7, 0x6e6e, 0x5cf5, 0x4d7c, 0xc60c, 0xd785, 0xe51e, 0xf497,
    // 0x8028, 0x91a1, 0xa33a, 0xb2b3, 0x4a44, 0x5bcd, 0x6956, 0x78df,
    // 0x0c60, 0x1de9, 0x2f72, 0x3efb, 0xd68d, 0xc704, 0xf59f, 0xe416,
    // 0x90a9, 0x8120, 0xb3bb, 0xa232, 0x5ac5, 0x4b4c, 0x79d7, 0x685e,
    // 0x1ce1, 0x0d68, 0x3ff3, 0x2e7a, 0xe70e, 0xf687, 0xc41c, 0xd595,
    // 0xa12a, 0xb0a3, 0x8238, 0x93b1, 0x6b46, 0x7acf, 0x4854, 0x59dd,
    // 0x2d62, 0x3ceb, 0x0e70, 0x1ff9, 0xf78f, 0xe606, 0xd49d, 0xc514,
    // 0xb1ab, 0xa022, 0x92b9, 0x8330, 0x7bc7, 0x6a4e, 0x58d5, 0x495c,
    // 0x3de3, 0x2c6a, 0x1ef1, 0x0f78 };
    // public static String[] msg;
    //
    // public static int GetFcs(int fcs, int s) {
    // int xor = 0;
    // int iresult = 0;
    // xor = fcs;
    // xor ^= s;
    // int str = FCSTAB[xor & 0x00ff];
    // iresult = (((fcs) >> 8) ^ str);
    // return iresult;
    // }
    //
    // //
    // public static String getCRC(String str) {
    // String regex = "(.{2})";
    // str = str.replaceAll(regex, "$1,");
    // int a = 0;
    // a = FCS_START;
    // String[] split = str.split(",");
    // for (String string : split) {
    // int parseInt = Integer.parseInt(string, 16);
    // a = GetFcs(a, parseInt);
    // }
    // a ^= FCS_START;
    // String msg167 = Integer.toHexString(a);
    // String CRC = getDate(msg167);
    // return CRC;
    // }
    //
    // public static String getDate(String data) {
    // String datas = "";
    // String regex = "(.{2})";
    // data = data.replaceAll(regex, "$1 ");
    // String[] arr = data.split(" ");
    // for (int i = arr.length - 1; i >= 0; i--) {
    // datas += arr[i];
    // }
    // return datas;
    // }

    /**
     * ---------------------------------------------------获取协议格式标准
     * 通过WIFI通道设置WIFI
     *
     * @param obdId
     * @param name
     * @param password
     * @return
     */

    public static byte[] setWifiNameAndPassword(String obdId, String name,
                                                String password) {

        // 1
        byte[] protocol_head = {0x40, 0x40};// 协议头2个字节
        // 3
        byte[] protocol_version = {0x03};// 协议版本 1个字节

        // 4
        byte[] obd_id = Utils.completionByte(20, obdId);// obd编号 20个字节

        // 5
        byte[] protocol_type = {0x20, 0x01};// 信息类型 2个字节,使用大端字节
        // 6
        byte[] cmd_seq = {0x00, 0x02};// 指令序号
        byte[] tlv_count = {0x02};// 数据包个数 , 0x00
        byte[] ssid = {0x02, 0x34};// wifi名tag小端数组
        byte[] nameByte = getCombinationByte(ssid, name);// 将WiFi名转为TLV格式数组
        byte[] wifipassword = {0x04, 0x34};// wifi密码tag小端数组
        byte[] passwordByte = getCombinationByte(wifipassword, password);// 将WiFi密码转为TLV格式数组
        byte[] tlv_array = Utils.byteMerger(nameByte, passwordByte);// 合并WiFi名和密码数组
        byte[] content = getTotalBytes(cmd_seq, tlv_count, tlv_array);// 得到整个content内容数组

        // 2
        int totalSize = content.length + 31;// 总协议长度
        byte[] protocol_length = Utils.short2byte(totalSize);// 将总协议长度转为2个字节数组

        // 7//校验码 2个字节 校验和, 计算校验和时包括(1),(2),(3),(4),(5),(6)
        byte[] crcByte = getTotalByte1(protocol_head, protocol_length,
                protocol_version, obd_id, protocol_type, content);
        String hexString = bytesToHexString(crcByte);// 字节数组转字符串
        String cc = getCRC(hexString);// 和校验获取
        byte[] crc = hexStringToBytes(cc);
        // 8
        byte[] protocol_tail = {0x0D, 0x0A};// 协议尾标志2个字节

        byte[] totalBytes = getTotalBytes(crcByte, crc, protocol_tail);
        String bytesToHexString = bytesToHexString(totalBytes);
        byte[] bytes = bytesToHexString.getBytes();
        byte[] byteMerger = Utils.byteMerger(bytes, protocol_tail);
        LogUtil.e("数据=" + bytesToHexString(byteMerger));

        return byteMerger;
    }

    /**
     * 得到WIFIp密码与账号
     *
     * @param obdId
     * @return
     */
    // 通过WIFI通道设置WIFI
    public static byte[] getWifiNameAndPassword(String obdId) {

        // 1
        byte[] protocol_head = {0x40, 0x40};// 协议头2个字节
        // 3
        byte[] protocol_version = {0x03};// 协议版本 1个字节
        // 4
        byte[] obd_id = Utils.completionByte(20, obdId);// obd编号 20个字节
        // 5
        byte[] protocol_type = {0x20, 0x02};// 信息类型 2个字节,使用大端字节

        // 6 协议内容
        byte[] cmd_seq = {0x02, 0x00};// 指令序号
        byte[] tlv_count = {0x02};// 数据包个数 , 0x00
        byte[] ssid = {0x02, 0x34};// wifi名tag小端数组
        byte[] wifipassword = {0x04, 0x34};// wifi密码tag小端数组
        byte[] tag_array = Utils.byteMerger(ssid, wifipassword);// 合并wifi名和密码
        byte[] content = getTotalBytes(cmd_seq, tlv_count, tag_array);// 内容

        // 2
        int totalSize = content.length + 31;// 协议字节总长度
        byte[] protocol_length = Utils.short2byte(totalSize);// 把长度转化为2位并且换位字节数组

        // 7 校验码 2个字节 校验和, 计算校验和时包括(1),(2),(3),(4),(5),(6)
        byte[] crcByte = getTotalByte1(protocol_head, protocol_length,
                protocol_version, obd_id, protocol_type, content);
        String hexString = bytesToHexString(crcByte);// 将字节转为字符串
        String crcString = getCRC(hexString);// crc检验返回字符串检验码
        byte[] hexStringToBytes = hexStringToBytes(crcString);// 字符串转为字节数组

        // 8
        byte[] protocol_tail = {0x0D, 0x0A};// 协议尾标志2个字节

        byte[] getcontentByte = getTotalBytes(crcByte, hexStringToBytes, protocol_tail);// 总字节数组
        String bytesToHexString = bytesToHexString(getcontentByte);
        LogUtil.e("CRC==" + bytesToHexString);
        byte[] bytes = bytesToHexString.getBytes();
        byte[] byteMerger = Utils.byteMerger(bytes, protocol_tail);

        return byteMerger;// 返回总协议字节数组
    }

    /**
     * 获取协议长度的字节数组
     *
     * @param byte_1
     * @param data
     * @return
     */
    public static byte[] getCombinationByte(byte[] byte_1, String data) {
        try {
            byte[] dataByte = data.getBytes();
            LogUtil.e(data + "==" + dataByte.length);
            byte[] dataLengthByte = Utils.short2byte(dataByte.length);
            byte[] byteMerger = Utils.byteMerger(byte_1, dataLengthByte);
            return Utils.byteMerger(byteMerger, dataByte);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;

    }

    /**
     * ----------------------------------------------------------------协议字节数组合并
     * 返回6 content内容拼接数组
     *
     * @param byte_1
     *            指令数组
     * @param byte_2
     *            数据包个数数组
     * @param byte_3
     *            WiFi名和密码数组
     * @return
     */
    // public static byte[] getcontentByte(byte[] byte_1, byte[] byte_2,
    // byte[] byte_3) {
    // byte[] byteMerger = Utils.byteMerger(byte_1, byte_2);
    // return Utils.byteMerger(byteMerger, byte_3);
    //
    // }

    /**
     * 合并总的字节
     *
     * @param crcByte       和检验字节数组
     * @param crc           检验字节返回检验码数组
     * @param protocol_tail 固定拼接数组
     * @return 返回总数组
     */
    public static byte[] getTotalBytes(byte[] crcByte, byte[] crc,
                                       byte[] protocol_tail) {
        byte[] data1 = Utils.byteMerger(crcByte, crc);
        return Utils.byteMerger(data1, protocol_tail);
    }

    /**
     * crc和校验后返回校验码数组
     *
     * @param protocol_head    协议头2个字节
     * @param protocol_length  协议字节总长度
     * @param protocol_version 协议版本 1个字节
     * @param obd_id           obd编号 20个字节
     * @param protocol_type    信息类型 2个字节,使用大端字节
     * @param content          协议内容
     * @return
     */
    public static byte[] getTotalByte1(byte[] protocol_head,
                                       byte[] protocol_length, byte[] protocol_version, byte[] obd_id,
                                       byte[] protocol_type, byte[] content) {
        byte[] data1 = Utils.byteMerger(protocol_head, protocol_length);
        byte[] data2 = Utils.byteMerger(protocol_version, obd_id);

        byte[] data3 = Utils.byteMerger(protocol_type, content);
        byte[] data5 = Utils.byteMerger(data1, data2);
        return Utils.byteMerger(data5, data3);
    }

    /**
     * ------------------------------------------------------------------------
     * 数组转化为16进制字符串 Convert byte[] to hex
     * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 得到16进制字节数组
     *
     * @param hexString the hex string
     * @return byte[]
     */
    @SuppressLint("DefaultLocale")
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();// 字母转为小写
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 十六进制转换字符串
     *
     * @return String 对应的字符串
     * @paramStrings tr Byte字符串(Byte之间无分隔符 如:[616C6B])
     */
    public static String hexStr16Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * 转为ASCLL码后字符后返回ASCLL码字符串
     *
     * @param s
     * @return
     */
    public static String toAscllStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        int j = 0;

        for (int i = 0; i < baKeyword.length; i++) {
            try {
                String string = s.substring(i * 2, i * 2 + 2);
                if ("00".equals(string)) {
                    j++;
                    continue;
                }

                baKeyword[i] = (byte) (0xff & Integer.parseInt(string, 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        byte[] backword = new byte[s.length() / 2 - j];
        for (int i = 0; i < backword.length; i++) {
            backword[i] = baKeyword[i];
        }
        try {
            s = new String(backword, "ASCII");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * Convert char to byte
     *
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
