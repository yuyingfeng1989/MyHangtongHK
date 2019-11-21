package com.bluebud.utils.resource;

import com.bluebud.liteguardian_hk.R;

/**
 * Created by Administrator on 2019/7/2.
 */

public class ResourceFactory {
    private static ResourceFactory factory;

    int[] generals1 = new int[]{R.string.my_device, R.string.device_management,
            R.string.member_manager, R.string.alarm_setting,
            R.string.time_zone_setting, R.string.advanced_settings};
    int[] generals2 = new int[]{R.string.my_pet, R.string.device_management,
            R.string.member_manager, R.string.alarm_setting,
            R.string.time_zone_setting, R.string.advanced_settings};
    int[] generals3 = new int[]{R.string.my_car, R.string.device_management,
            R.string.member_manager, R.string.alarm_setting,
            R.string.time_zone_setting, R.string.advanced_settings};
    int[] generals33 = new int[]{R.string.my_car, R.string.device_management,
            R.string.member_manager, R.string.wifi_setting, R.string.alarm_setting,
            R.string.time_zone_setting, R.string.advanced_settings};
    int[] generals4 = new int[]{R.string.my_motorcycle, R.string.device_management,
            R.string.member_manager, R.string.alarm_setting,
            R.string.time_zone_setting, R.string.advanced_settings};

    int[] generals5 = new int[]{R.string.my_watch, R.string.device_management,
            R.string.member_manager, R.string.alarm_setting,
            R.string.time_zone_setting, R.string.timezone_watch,
            R.string.advanced_settings};
    int[] generals55 = new int[]{R.string.my_watch, R.string.device_management,
            R.string.member_manager, R.string.alarm_setting,
            R.string.time_zone_setting, R.string.timezone_watch5,
            R.string.step_setting, R.string.advanced_settings};
    int[] generals_k1 = new int[]{R.string.my_watch, R.string.device_management,
            R.string.member_manager, R.string.alarm_setting,
            R.string.sports_pedometer,
            R.string.location_frequency};
    int[] generals_790 = new int[]{R.string.my_watch, R.string.device_management,
            R.string.member_manager, R.string.alarm_setting, R.string.time_zone_setting, R.string.timezone_watch5,
            R.string.sports_pedometer,
            R.string.location_frequency};

    int[] generals556 = new int[]{R.string.my_watch, R.string.device_management,
            R.string.member_manager, R.string.alarm_setting,
            R.string.time_zone_setting, R.string.timezone_watch5,
            R.string.advanced_settings};//772,771a不带计步设置
    int[] generals7 = new int[]{R.string.my_watch, R.string.device_management,
            R.string.member_manager, R.string.alarm_setting, R.string.time_zone_setting, R.string.notification_push,
            R.string.target_setting};

    int[] generalsfamily = new int[]{R.string.device_management, R.string.member_manager, R.string.alarm_setting};

    int[] image1 = new int[]{R.drawable.icon_pt718, R.drawable.icon_device_management,
            R.drawable.icon_member_manager, R.drawable.icon_alarm_setting,
            R.drawable.icon_time_zone_setting, R.drawable.icon_advanced_settings};
    int[] image2 = new int[]{R.drawable.icon_device_pet, R.drawable.icon_device_management,
            R.drawable.icon_member_manager, R.drawable.icon_alarm_setting, R.drawable.icon_time_zone_setting,
            R.drawable.icon_advanced_settings};
    int[] image3 = new int[]{R.drawable.icon_car, R.drawable.icon_device_management,
            R.drawable.icon_member_manager, R.drawable.icon_alarm_setting, R.drawable.icon_time_zone_setting,
            R.drawable.icon_advanced_settings};
    int[] image33 = new int[]{R.drawable.icon_car, R.drawable.icon_device_management,
            R.drawable.icon_member_manager, R.drawable.icon_wifiset, R.drawable.icon_alarm_setting, R.drawable.icon_time_zone_setting,
            R.drawable.icon_advanced_settings};
    int[] image4 = new int[]{R.drawable.icon_pt620, R.drawable.icon_device_management,
            R.drawable.icon_member_manager, R.drawable.icon_alarm_setting, R.drawable.icon_time_zone_setting,
            R.drawable.icon_advanced_settings};
    int[] image5 = new int[]{R.drawable.icon_my_watch, R.drawable.icon_device_management,
            R.drawable.icon_member_manager, R.drawable.icon_alarm_setting,
            R.drawable.icon_time_zone_setting, R.drawable.icon_time_zone_setting,
            R.drawable.icon_advanced_settings};
    int[] image55 = new int[]{R.drawable.icon_my_watch, R.drawable.icon_device_management,
            R.drawable.icon_member_manager, R.drawable.icon_alarm_setting,
            R.drawable.icon_time_zone_setting, R.drawable.icon_time_zone_setting,
            R.drawable.icon_advanced_settings, R.drawable.icon_advanced_settings};
    int[] image_k1 = new int[]{R.drawable.icon_my_watch, R.drawable.icon_device_management,
            R.drawable.icon_member_manager, R.drawable.icon_alarm_setting,
            R.drawable.icon_time_zone_setting, R.drawable.icon_time_zone_setting};

    int[] image7 = new int[]{R.drawable.icon_my_watch, R.drawable.icon_device_management,
            R.drawable.icon_member_manager, R.drawable.icon_alarm_setting, R.drawable.icon_time_zone_setting,
            R.drawable.icon_notation, R.drawable.icon_yundong};

    private ResourceFactory() {
    }

    public static ResourceFactory singleResource() {
        if (factory != null)
            return factory;
        factory = new ResourceFactory();
        return factory;
    }
}
