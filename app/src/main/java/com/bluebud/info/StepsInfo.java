package com.bluebud.info;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/6/9 0009.
 */

public class StepsInfo implements Serializable {
    public String createDate;
    public String deviceSn;
    public int step;

    @Override
    public String toString() {
        return "StepsInfo{" +
                "createDate='" + createDate + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", step=" + step +
                '}';
    }
}
