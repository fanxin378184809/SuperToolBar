package com.fanxin.supertoobar

import android.content.Context

/**
 * 作者: 范鑫
 * 时间:2020/6/8
 * 邮箱:itfanxin@163.com
 * 备注:
 */

fun dip2px(dipValue: Float,context:Context): Int {
    val scale = context.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}

fun dip2px(dipValue: Int,context:Context): Int {
    val scale = context.resources.displayMetrics.density
    return (dipValue * scale + 0.5f).toInt()
}
