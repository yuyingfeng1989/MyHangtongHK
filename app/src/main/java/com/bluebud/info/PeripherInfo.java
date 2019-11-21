package com.bluebud.info;

import com.google.gson.JsonObject;

public class PeripherInfo {

    @Override
    public String toString() {
        return "PeripherInfo [name=" + name + ", vicinity=" + vicinity
                + ", geometry=" + geometry + "]";
    }

    public String name;
    public String vicinity;
    public JsonObject geometry;
//	public double lat;
//	public double lon;

}
