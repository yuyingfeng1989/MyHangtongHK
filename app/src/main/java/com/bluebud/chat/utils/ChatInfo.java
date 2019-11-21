package com.bluebud.chat.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/6/3.
 */

public class ChatInfo implements Parcelable {
    private String name;
    private String nickname;
    private String sex;
    private String age;
    private String area;
    private String mark;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.nickname);
        dest.writeString(this.sex);
        dest.writeString(this.age);
        dest.writeString(this.area);
        dest.writeString(this.mark);
    }

    public ChatInfo() {
    }

    protected ChatInfo(Parcel in) {
        this.name = in.readString();
        this.nickname = in.readString();
        this.sex = in.readString();
        this.age = in.readString();
        this.area = in.readString();
        this.mark = in.readString();
    }

    public static final Creator<ChatInfo> CREATOR = new Creator<ChatInfo>() {
        @Override
        public ChatInfo createFromParcel(Parcel source) {
            return new ChatInfo(source);
        }

        @Override
        public ChatInfo[] newArray(int size) {
            return new ChatInfo[size];
        }
    };

    public ChatInfo(String name, String nickname, String sex, String age, String area, String mark) {
        this.name = name;
        this.nickname = nickname;
        this.sex = sex;
        this.age = age;
        this.area = area;
        this.mark = mark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "ChatInfo{" +
                "username='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex='" + sex + '\'' +
                ", age='" + age + '\'' +
                ", area='" + area + '\'' +
                ", mark='" + mark + '\'' +
                '}';
    }
}
