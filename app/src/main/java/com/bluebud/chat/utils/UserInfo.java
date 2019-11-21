package com.bluebud.chat.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/6/3.
 */

public class UserInfo implements Parcelable {
    private String id;
    private String name;
    private String type;
    private String device_sn;
    private String portrait;
    private String remark;
    private String nickname;
    

  
	@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.device_sn);
        dest.writeString(this.portrait);
        dest.writeString(this.remark);
        dest.writeString(this.nickname);
    }

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.device_sn = in.readString();
        this.portrait = in.readString();
        this.remark = in.readString();
        this.nickname = in.readString();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

	public UserInfo(String id, String name, String type, String device_sn,
			String portrait, String remark, String nickname) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.device_sn = device_sn;
		this.portrait = portrait;
		this.remark = remark;
		this.nickname = nickname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDevice_sn() {
		return device_sn;
	}

	public void setDevice_sn(String device_sn) {
		this.device_sn = device_sn;
	}

	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	  public String getPortrait() {
			return portrait;
		}

		public void setPortrait(String portrait) {
			this.portrait = portrait;
		}


	@Override
	public String toString() {
		return "UserInfo [id=" + id + ", name=" + name + ", type=" + type
				+ ", device_sn=" + device_sn + ", portrait="
				+ portrait + ", remark=" + remark + ", nickname="
				+ nickname + "]";
	}
    
    
    
}
