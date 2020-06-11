package com.fanxin.supertoobar

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * 作者: 范鑫
 * 时间:2020/6/5
 * 邮箱:itfanxin@163.com
 * 备注: 返回键
 */
class BackImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    /*
        返回键是否是正方形
     */
    var backImgSquare:Boolean = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (backImgSquare){
            var heightSize = MeasureSpec.getSize(heightMeasureSpec)
            super.onMeasure(MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY))
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}