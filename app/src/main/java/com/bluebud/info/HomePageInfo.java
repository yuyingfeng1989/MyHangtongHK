package com.bluebud.info;

/**
 * Created by user on 2018/3/27.
 */

public class HomePageInfo {
    /**
     * device_sn : 6109198179
     * ranges : 5
     * product_type : 26
     * lat : 22.5475303
     * lng : 113.9366836
     * online : 0
     * head_portrait : /image/HeadPortrait/201712060357326606.png
     */

    public String device_sn;
    public int ranges;
    public int product_type;
    public double lat;
    public double lng;
    public int online;
    public String head_portrait;

    @Override
    public String toString() {
        return "HomePageInfo{" +
                "device_sn='" + device_sn + '\'' +
                ", ranges=" + ranges +
                ", product_type=" + product_type +
                ", lat=" + lat +
                ", lng=" + lng +
                ", online=" + online +
                ", head_portrait='" + head_portrait + '\'' +
                '}';
    }
}
