package com.bluebud.info;

import java.io.Serializable;

public class remindInfo implements Serializable {

	public int id;
	//public String user_id;
	public String deviceSn;
	public String week;
	public String time;
	public int ring;
	public int alert_type;
	public int flag;
	public int type;
	public int title_len;
	public String title;
	public int image_len;
	public String image_name;
	public int version;
	@Override
	public String toString() {
		return "remindInfo [id=" + id + ", deviceSn=" + deviceSn + ", week="
				+ week + ", time=" + time + ", ring=" + ring + ", alert_type="
				+ alert_type + ", flag=" + flag + ", type=" + type
				+ ", title_len=" + title_len + ", title=" + title
				+ ", image_len=" + image_len + ", image_name=" + image_name
				+ ", version=" + version + "]";
	}



	
}
