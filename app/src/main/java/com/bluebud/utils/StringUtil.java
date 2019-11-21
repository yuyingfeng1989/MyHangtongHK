package com.bluebud.utils;

import android.text.TextUtils;

public class StringUtil {

    /**
     * 取字符串，如果为空就使用默认的
     * @param str1
     * @param defaultStr
     * @return
     */
    public static String optString(String str1, String defaultStr) {
        if (TextUtils.isEmpty(str1)) {
            return defaultStr;
        }
        return str1;
    }

}
