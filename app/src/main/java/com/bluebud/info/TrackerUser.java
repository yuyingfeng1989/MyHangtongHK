package com.bluebud.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TrackerUser implements Serializable {
	public String deviceSn;
	public List<OnlyUser> users = new ArrayList<OnlyUser>();
}
