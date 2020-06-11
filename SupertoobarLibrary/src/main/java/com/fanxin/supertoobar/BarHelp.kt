package com.fanxin.supertoobar

import android.R
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorSpace
import android.graphics.drawable.*
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat

/**
 * 作者: 范鑫
 * 时间:2020/6/8
 * 邮箱:itfanxin@163.com
 * 备注:设置前景色效果的兼容
 */
class BarHelp {

    companion object {
        /*
            设置view的背景点击效果
         */
        fun setForeground(color: Int, view: View) {
            var color = color
            val drawable: Drawable
            val background = view.background
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                color = color and -0x11000001
                val colorList =
                    ColorStateList(arrayOf(intArrayOf()), intArrayOf(color))
                drawable = RippleDrawable(
                    colorList,
                    if (!BarHelp.isCanForeground(view)) background else null,
                    background ?: ColorDrawable(Color.WHITE)
                )
            } else {
                if (BarHelp.isCanForeground(view)) {
                    color = color and 0x33FFFFFF
                }
                val bg =
                    StateListDrawable()
                val colorDrawable: Drawable
                if (background is GradientDrawable) {
                    background.mutate()
                    colorDrawable = background.getConstantState()!!.newDrawable()
                    (colorDrawable as GradientDrawable).setColor(color)
                } else {
                    colorDrawable = ColorDrawable(color)
                }
                bg.addState(intArrayOf(R.attr.state_pressed), colorDrawable)
                // View.EMPTY_STATE_SET
                bg.addState(
                    intArrayOf(),
                    if (!BarHelp.isCanForeground(view)) background else null
                )
                drawable = bg
            }
            BarHelp.setForeground(view, drawable)
        }

        private fun setForeground(view: View, drawable: Drawable?) {
            if (BarHelp.isCanForeground(view)) {
                if (view is FrameLayout) {
                    view.foreground = drawable
                } else {
                    view.foreground = drawable
                }
            } else {
                //不可设置前景色，就只能设置背景色
                ViewCompat.setBackground(view, drawable)
            }
        }


        /*
            是否可以设置前景色
         */
        private fun isCanForeground(view: View): Boolean {
            return Build.VERSION.PREVIEW_SDK_INT >= Build.VERSION_CODES.M || view is FrameLayout
        }


        /*
            设置图片渲染色
         */
        fun getTintDrawable(drawable: Drawable, color: Int): Drawable {
            val wapDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wapDrawable, color)
            return wapDrawable
        }


        fun setNotification(v: TextView, num: Int, max: Int) {
            if (num > 0) {
                v.visibility = View.VISIBLE
                if (num > max){
                    v.text = String.format("%d+",max)
                }else{
                    v.text = num.toString()
                }
            }else{
                v.visibility = View.GONE
                v.text = ""
            }

        }

        /*
            这个颜色是不是深色的
         */
        fun isColorDrak(color:Int):Boolean{
            //int t = (color >> 24) & 0xFF;
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            return r * 0.299 + g * 0.578 + b * 0.114 <= 192
        }

    }


}