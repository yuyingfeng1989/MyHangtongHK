package com.bluebud.info;

import java.io.Serializable;

/**
 * Created by user on 2018/6/11.
 */

public class PhonebookInfo implements Serializable{
    public String deviceSn;
    public String phoneNum;
    public String nickname;
    public int isAdmin;
    public String index;//电话号码编号(添加时不需要)
    public int photo =1;//头像编号(0-10),默认选中第二个头像

    @Override
    public String toString() {
        return "PhonebookInfo{" +
                "deviceSn='" + deviceSn + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", nickname='" + nickname + '\'' +
                ", isAdmin=" + isAdmin +
                ", index=" + index +
                ", photo=" + photo +
                '}';
    }
}
