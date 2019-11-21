package com.bluebud.info;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/10/15.
 */

public class FunctionControlInfo implements Serializable{
    public String deviceSn;
    public int reset;//恢复出厂设置
    public int shutdown;//关机
    public int restart;//复位

}
