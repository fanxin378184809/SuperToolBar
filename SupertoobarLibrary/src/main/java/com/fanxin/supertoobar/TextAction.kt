package com.fanxin.supertoobar

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.setPadding

/**
 * 作者: 范鑫
 * 时间:2020/6/10
 * 邮箱:itfanxin@163.com
 * 备注:文字action
 */
class TextAction(toolBar: SuperToolBar) : Action(toolBar) {


    companion object{
        const val DEFAULT_ACTION_TEXT_SIZE = 15
    }

    lateinit var text: CharSequence
    lateinit var textView:TextView

    constructor(toolBar: SuperToolBar,text: CharSequence) : this(toolBar) {
        this.text = text
    }

    override fun createView(): View {
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.MATCH_PARENT)
        textView = TextView(context)
        textView.maxLines = 1
        updata()
        textView.layoutParams = params
        return textView
    }


    override fun updata() {
        if (::textView.isInitialized){
            textView.id = TEXT_ID
            textView.gravity = Gravity.CENTER
            textView.text = text
            textView.textSize = DEFAULT_ACTION_TEXT_SIZE.toFloat()
            textView.setTextColor(actionTextColor)
            textView.setPadding(actionPadding,actionPadding,actionPadding,actionPadding)
        }

    }

}