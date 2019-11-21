package com.bluebud.info;

/**
 * Created by user on 2018/6/7.
 */

public class TakePhotoInfo {
    public String devicePhotoID;//照片id
    public String url;//原型图
    public String thumbnailUrl;//缩略图URL
    public boolean isShow;//是否显示圆圈
    public boolean isSelect;//是否显示已选择

    @Override
    public String toString() {
        return "TakePhotoInfo{" +
                "devicePhotoID='" + devicePhotoID + '\'' +
                ", url='" + url + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", isShow=" + isShow +
                ", isSelect=" + isSelect +
                '}';
    }
}
