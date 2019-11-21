package com.bluebud.chat.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/8/24 0024.
 */

public class TrackDriverBean implements Parcelable {
    /**
     * date : 2017.08
     * fuel : 57.4
     * km : 1203.03
     * mpg : 0.21
     * time : 1358
     */

    private String date;
    private float fuel;
    private float km;
    private String mpg;
    private int time;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getFuel() {
        return fuel;
    }

    public void setFuel(float fuel) {
        this.fuel = fuel;
    }

    public float getKm() {
        return km;
    }

    public void setKm(float km) {
        this.km = km;
    }

    public String getMpg() {
        return mpg;
    }

    public void setMpg(String mpg) {
        this.mpg = mpg;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "TrackDriverBean{" +
                "date='" + date + '\'' +
                ", fuel=" + fuel +
                ", km='" + km + '\'' +
                ", mpg='" + mpg + '\'' +
                ", time=" + time +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeDouble(this.fuel);
        dest.writeFloat(this.km);
        dest.writeString(this.mpg);
        dest.writeInt(this.time);
    }

    public TrackDriverBean() {
    }

    protected TrackDriverBean(Parcel in) {
        this.date = in.readString();
        this.fuel = in.readFloat();
        this.km = in.readFloat();
        this.mpg = in.readString();
        this.time = in.readInt();
    }

    public static final Parcelable.Creator<TrackDriverBean> CREATOR = new Parcelable.Creator<TrackDriverBean>() {
        @Override
        public TrackDriverBean createFromParcel(Parcel source) {
            return new TrackDriverBean(source);
        }

        @Override
        public TrackDriverBean[] newArray(int size) {
            return new TrackDriverBean[size];
        }
    };
}
