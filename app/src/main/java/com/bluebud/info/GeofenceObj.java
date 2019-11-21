package com.bluebud.info;

import java.io.Serializable;
import java.util.List;

public class GeofenceObj {
    public List<DefenceList> defenceList;

    public class DefenceList implements Serializable {
        public double lat;//经度
        public double lng;//为度
        public int radius;//半径
        public int areaid;//id
        public String defencename="";//围栏昵称
        public int defencestatus;//状态
        public int isOut;//围栏范围

        @Override
        public String toString() {
            return "DefenceList{" +
                    "lat=" + lat +
                    ", lng=" + lng +
                    ", radius=" + radius +
                    ", areaid=" + areaid +
                    ", defencename='" + defencename + '\'' +
                    ", defencestatus=" + defencestatus +
                    ", isOut=" + isOut +
                    '}';
        }
    }
}