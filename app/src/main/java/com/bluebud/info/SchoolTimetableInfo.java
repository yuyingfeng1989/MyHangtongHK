package com.bluebud.info;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/10/9.
 */

public class SchoolTimetableInfo implements Serializable{

    /**
     * schoolTimetable : [{"courseName":"语文","courseOfDay":1,"dayOfWeek":1,"deviceSn":null}]
     * schoolHourMap : {"class1End":"00:00:00","class1Start":"00:00:00","class2End":"00:00:00","class2Start":"00:00:00","class3End":"00:00:00","class3Start":"00:00:00","class4End":"00:00:00","class4Start":"00:00:00","class5End":"00:00:00","class5Start":"00:00:00","class6End":"00:00:00","class6Start":"00:00:00","class7End":"00:00:00","class7Start":"00:00:00","class8End":"00:00:00","class8Start":"00:00:00","deviceSn":null}
     */

    public SchoolHourMapBean schoolHourMap;
    public List<SchoolTimetableBean> schoolTimetable;


    public static class SchoolHourMapBean implements Serializable{
        public String class1End;
        public String class1Start;
        public String class2End;
        public String class2Start;
        public String class3End;
        public String class3Start;
        public String class4End;
        public String class4Start;
        public String class5End;
        public String class5Start;
        public String class6End;
        public String class6Start;
        public String class7End;
        public String class7Start;
        public String class8End;
        public String class8Start;

        @Override
        public String toString() {
            return "SchoolHourMapBean{" +
                    "class1End='" + class1End + '\'' +
                    ", class1Start='" + class1Start + '\'' +
                    ", class2End='" + class2End + '\'' +
                    ", class2Start='" + class2Start + '\'' +
                    ", class3End='" + class3End + '\'' +
                    ", class3Start='" + class3Start + '\'' +
                    ", class4End='" + class4End + '\'' +
                    ", class4Start='" + class4Start + '\'' +
                    ", class5End='" + class5End + '\'' +
                    ", class5Start='" + class5Start + '\'' +
                    ", class6End='" + class6End + '\'' +
                    ", class6Start='" + class6Start + '\'' +
                    ", class7End='" + class7End + '\'' +
                    ", class7Start='" + class7Start + '\'' +
                    ", class8End='" + class8End + '\'' +
                    ", class8Start='" + class8Start + '\'' +
                    '}';
        }
    }

    public static class SchoolTimetableBean implements Serializable{
        public String courseName;
        public int courseOfDay;
        public int dayOfWeek;

        @Override
        public String toString() {
            return "SchoolTimetableBean{" +
                    "courseName='" + courseName + '\'' +
                    ", courseOfDay=" + courseOfDay +
                    ", dayOfWeek=" + dayOfWeek +
                    '}';
        }
    }
}
