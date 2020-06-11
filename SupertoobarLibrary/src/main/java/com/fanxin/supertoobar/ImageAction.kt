package com.fanxin.supertoobar

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable

/**
 * 作者: 范鑫
 * 时间:2020/6/9
 * 邮箱:itfanxin@163.com
 * 备注: 图片action
 */
class ImageAction(toolBar: SuperToolBar) :Action(toolBar){
    private lateinit var imageView:ImageView
    private lateinit var drawable: Drawable
    private var tintColor:Int = -1
    private var isSetTintColor = false

    constructor(toolBar: SuperToolBar,drawable: Drawable) : this(toolBar) {
        this.drawable = drawable
    }

    constructor(toolBar: SuperToolBar,@DrawableRes drawableId: Int) : this(toolBar) {
        this.drawable = getDrawable(context,drawableId)!!
    }


    fun setDrawable(drawable: Drawable):ImageAction{
        if (drawable != this.drawable){
            this.drawable = drawable
            updata()
        }
        return this
    }

    fun setDrawableId(drawableId: Int):ImageAction{
        val drawable = getDrawable(context,drawableId)
        if (drawable != this.drawable){
            if (drawable != null) {
                this.drawable = drawable
                updata()
            }
        }
        return this
    }


    /*
        设置渲染的颜色
     */
    fun setTintColor(tintColor:Int):ImageAction{
        if (!isSetTintColor || this.tintColor != tintColor){
            this.tintColor = tintColor
            isSetTintColor = true
            if (::imageView.isInitialized){
                imageView.setColorFilter(tintColor)
            }
        }
        return this
    }

    fun cleanTint():ImageAction{
        this.tintColor = -1
        if (::imageView.isInitialized){
            imageView.colorFilter = null
        }
        return this
    }

    /*
        获取ImageView
     */
    fun getImageView():ImageView{
        return imageView
    }




    /*
        创建一个ImageView
     */
    override fun createView(): View {
        val params = FrameLayout.LayoutParams(
            width,
            height
        )
        imageView = ImageView(context)
        updata()
        imageView.layoutParams = params
        return imageView
    }


    override fun updata() {
        if (::imageView.isInitialized) {
            imageView.id = IMAGE_ID
            imageView.setImageDrawable(drawable)
            imageView.setPadding(actionPadding, actionPadding, actionPadding, actionPadding)
            if (isSetTintColor || tintColor != -1) {
                imageView.setColorFilter(tintColor)
            }
            val params = LinearLayout.LayoutParams(
                width,
                height
            )
            imageView.layoutParams = params
        }
    }
}