package com.huizetime.basketball.time.utils

/**
 * Created by Administrator on 2017/11/7.
 */
object TextUtil {

    /**
     * Returns true if the string is null or 0-length.
     * @param str the string to be examined
     * *
     * @return true if str is null or zero length
     */
    fun isEmpty( str: CharSequence?): Boolean {
        if (str == null || str.length == 0)
            return true
        else
            return false
    }
}