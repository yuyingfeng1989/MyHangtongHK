package com.bluebud.info;

import java.util.List;


/**
 * @author Andyzhao
 * @ClassName CarDetectInfo
 * @PackageName com.bluebud.info
 * @Description 从服务器返回的汽车检测数据bean
 * @Date 2016-6-13 上午9:47:35
 * @Version V1.0
 */
public class CarDetectInfo {
    public String deviceSn;
    public int historyScore;//历史分数
    public String historyTestTime;//历史时间
    public List<CarInspectionData> carInspectionData;
    public List<String> faultMsgList;
}
