package com.fanxin.supertoobar

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

/**
 * 作者: 范鑫
 * 时间:2020/6/5
 * 邮箱:itfanxin@163.com
 * 备注:toolbar的action
 */
abstract class Action (toolBar: SuperToolBar){

    companion object{
        const val IMAGE_ID = 1125
        const val TEXT_ID = 1125
    }

    protected var context: Context = toolBar.context
    private var actionView:FrameLayout
    private var notificationTextColor:Int = 0
    protected var notificationBagColor:Int = 0
    protected var notificationStrokeColor:Int = 0
    protected var actionPadding = 0
    protected var actionTextColor = 0
    protected var notificationMax = 99
    protected var notificationNumber = -9999
    protected lateinit var mKeyedTags: SparseArray<Any>

    /*
        图标的宽高
     */
    protected var width = 0
    protected var height = 0
    private lateinit var notificationTextView: TextView
    private var gravity:Int = Gravity.CENTER



    init {
        notificationBagColor = toolBar.notificationBagColor
        notificationTextColor = toolBar.notificationTextColor //文字颜色
        notificationStrokeColor = toolBar.notificationStrokeColor
        actionTextColor = toolBar.actionTextColor
        actionPadding = toolBar.actionPadding
        width = toolBar.actionWidth
        height = toolBar.actionHeight

        actionView = FrameLayout(context)
        actionView.tag = this
        if (this is ImageAction){
            val params = LinearLayout.LayoutParams(width,height)
            params.gravity = Gravity.CENTER
            actionView.layoutParams = params
        }else{
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT)
            params.gravity = Gravity.CENTER
            actionView.layoutParams = params
        }


        BarHelp.setForeground(SuperToolBar.CLICK_COLOR.toInt(),actionView)
    }

    fun set():Action{
        val v = createView()
        if (v.layoutParams != null){
            (v.layoutParams as FrameLayout.LayoutParams).gravity = gravity
        }else{
            val layoutParams:FrameLayout.LayoutParams = FrameLayout.LayoutParams(width,height)
            layoutParams.gravity = gravity
            v.layoutParams = layoutParams
        }
        actionView.addView(v)
        convert(actionView)
        return this
    }

    fun setNotificationMax(notificationMax:Int):Action{
        this.notificationMax = notificationMax
        return this
    }

    protected fun addNotifcation(){
        if (!::notificationTextView.isInitialized){
            notificationTextView = createNotification()
            actionView.addView(notificationTextView,getRightNumberParams())
        }
    }

    /*
        右边的消息参数
     */
    protected fun getRightNumberParams():FrameLayout.LayoutParams{
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,dip2px(14F,context))
        params.setMargins(0,dip2px(4F,context),dip2px(4F,context),0)
        params.gravity = Gravity.RIGHT or Gravity.TOP
        return params
    }

    /*
        创建一个消息的文本，红点提示
     */
    protected fun createNotification(): TextView{
        notificationTextView = TextView(context)
        updataNotification()
        return notificationTextView
    }



    /** 可以调用修改的属性======================================================================*/

    /*
        获取整个布局
     */
    fun getActionView():FrameLayout{
        return actionView
    }


    fun setGravity(gravity: Int):Action{
        this.gravity = gravity
        return this
    }

    /**
     * 设置消息
     */
    fun setNotification(number:Int):Action{
        notificationNumber = number
        addNotifcation()
        updataNotification()
        return this
    }

    fun setActionPadding(actionPadding:Int):Action{
        this.actionPadding = actionPadding
        addNotifcation()
        updata()
        return this
    }

    fun setActionTextColor(actionTextColor:Int):Action{
        this.actionTextColor = actionTextColor
        updata()
        return this
    }

    fun setNotificationTextColor(notificationTextColor:Int):Action{
        this.notificationTextColor = notificationTextColor
        addNotifcation()
        updataNotification()
        return this
    }

    fun setNotificationBagColor(notificationBagColor:Int):Action{
        this.notificationBagColor = notificationBagColor
        addNotifcation()
        updataNotification()
        return this
    }

    fun setNotificationStrokeColor(notificationStrokColor:Int):Action{
        this.notificationStrokeColor = notificationStrokeColor
        addNotifcation()
        updataNotification()
        return this
    }

    fun setTag(key:Int,any:Any):Action{
        if (!this::mKeyedTags.isInitialized){
            mKeyedTags = SparseArray<Any>()
        }
        mKeyedTags.put(key,any)
        return this
    }

    fun getTag(key:Int):Any{
        if (!this::mKeyedTags.isInitialized){
            mKeyedTags = SparseArray<Any>()
        }
        return mKeyedTags.get(key)
    }





    /** 可以重写的属性========================================================================= */


    /*
        自己设置view的属性
     */
    protected fun convert(view:FrameLayout){

    }
    
    /*
        刷新通知
     */
    fun updataNotification(){
        if (::notificationTextView.isInitialized){
            notificationTextView.textSize = 8F
            notificationTextView.background = createNotificationBag()
            notificationTextView.gravity = Gravity.CENTER
            notificationTextView.minWidth = dip2px(14F,context)
            notificationTextView.setPadding(dip2px(2F,context),0,dip2px(2F,context),0)
            notificationTextView.setTextColor(notificationTextColor)
            BarHelp.setNotification(notificationTextView,notificationNumber,notificationMax)
        }
    }
    
    /*
        创建通知的背景
     */
    protected fun createNotificationBag():Drawable{
        var drawable = GradientDrawable()
        drawable.setColor(notificationBagColor)
        drawable.cornerRadius = dip2px(7f,context).toFloat()
        drawable.setStroke(dip2px(0.9f,context),notificationStrokeColor)
        return  drawable
    }


    /**
     * 更新这个Action
     */
    protected abstract fun updata()


    /**
     * 创建一个View
     */
    abstract fun createView(): View


    public interface OnActionClick{
        /*
            点击的时候
         */
        fun onActionClick(index:Int,action:Action)
    }

    public interface OnActionLeftClick{
        /*
            点击的时候
         */
        fun onActionClick(index:Int,action:Action)
    }

}