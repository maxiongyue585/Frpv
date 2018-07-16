package com.d180523.frpv.utils;

import android.text.TextUtils;

/**
 * 字符串判断工具类
 *
 * @author mxy
 */
public class StringUtils {
    public static boolean isEmpty(String str) {
        if (TextUtils.isEmpty(str) || "null".equals(str) || (str != null && "".equals(str.trim()))) {
            return true;
        }
        return false;
    }
}
