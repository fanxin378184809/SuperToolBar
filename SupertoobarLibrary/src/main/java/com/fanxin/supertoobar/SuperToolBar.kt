package com.fanxin.supertoobar

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable


/**
 * 作者: 范鑫
 * 时间:2020/6/5
 * 邮箱:itfanxin@163.com
 * 备注:顶部的toolbar
 */
class SuperToolBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    companion object {
        const val ACTION_LAYOUT = "ACTION_LAYOUT"
        const val LEFT_ACTION_LAYOUT = "LEFT_ACTION_LAYOUT"
        const val CLICK_COLOR: Long = 0x88aaaaaa
        const val DEFAULT_HEIGHT = 45//默认高度 45dp
        val DEFAULT_BACKIMAGE: Int = R.drawable.material_back
    }


    /*
        三个主要的Layout
     */
    lateinit var leftLayout: FrameLayout
    lateinit var centerLayout: FrameLayout
    lateinit var rightLayout: FrameLayout
    var barHeight: Int = 0
    var statusHeight: Int = 0
    lateinit var titleView: TextView
    lateinit var backButton: BackImageView
    var titleColor: Int = -0xcccccd
    private var backImage = R.drawable.abc_vector_test
    var titleSize = 18F
    var title: CharSequence
    var linePaint: Paint

    /*
        底部的线
     */
    var bottonLineColor = -0x121213
    var notificationBagColor: Int = -0x10000
    var notificationTextColor = -0x1
    var notificationStrokeColor = -0x1
    var actionTextColor = -0x1

    /*
        透明状态栏，小于安卓M（6.0）的状态栏背景
     */
    private var setTranslucentStatusBarPaddingTop = false
    var androidMTranslucentStatusBar = 0x66aaaaaa
    private var backImgColor = 0

    /*
        返回键是否是正方形
     */
    var backImgSquare = false
    var isSetBackImgColor = false
    var bottonLineHeight = dip2px(0.5f, context)
    var actionPadding = dip2px(8f, context)

    /*
        返回键的内间距
     */
    var backPadding = dip2px(12f, context)

    /*
        action的宽高，一般用于图片
     */
    var actionWidth = ViewGroup.LayoutParams.WRAP_CONTENT
    var actionHeight = ViewGroup.LayoutParams.MATCH_PARENT


    private lateinit var onActionClickListener: Action.OnActionClick
    private lateinit var onActionLeftClickListener: Action.OnActionLeftClick


    init {
        barHeight = dip2px(45, context)
        if (background == null) {
            setBackgroundColor(resources.getColor(R.color.supertoolbar_backgroung_color))
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SuperToolBar)

        title = typedArray.getString(R.styleable.SuperToolBar_stb_title).toString()
        titleSize = typedArray.getDimension(
            R.styleable.SuperToolBar_stb_titleSize,
            dip2px(titleSize, context).toFloat()
        )
        titleColor = typedArray.getColor(R.styleable.SuperToolBar_stb_titleColor, titleColor)

        if (typedArray.hasValue(R.styleable.SuperToolBar_stb_backImgColor)) {
            isSetBackImgColor = true
        }

        backImgSquare =
            typedArray.getBoolean(R.styleable.SuperToolBar_stb_backImgSquare, backImgSquare)
        backPadding = typedArray.getDimension(
            R.styleable.SuperToolBar_stb_backImgPadding,
            backPadding.toFloat()
        ).toInt()
        backImgColor = typedArray.getColor(R.styleable.SuperToolBar_stb_backImgColor, backImgColor)
        backImage = typedArray.getResourceId(R.styleable.SuperToolBar_stb_backImg, backImage)

        bottonLineColor =
            typedArray.getColor(R.styleable.SuperToolBar_stb_bottonLineColor, bottonLineColor)
        bottonLineHeight = typedArray.getDimension(
            R.styleable.SuperToolBar_stb_bottonLineHeight,
            bottonLineHeight.toFloat()
        ).toInt()

        notificationBagColor = typedArray.getColor(
            R.styleable.SuperToolBar_stb_notificationBagColor,
            notificationBagColor
        )
        notificationTextColor = typedArray.getColor(
            R.styleable.SuperToolBar_stb_notificationTextColor,
            notificationTextColor
        )
        notificationStrokeColor = typedArray.getColor(
            R.styleable.SuperToolBar_stb_notificationStrokeColor,
            notificationStrokeColor
        )

        actionTextColor =
            typedArray.getColor(R.styleable.SuperToolBar_stb_actionTextColor, actionTextColor)
        actionWidth =
            typedArray.getDimension(R.styleable.SuperToolBar_stb_actionWidth, actionWidth.toFloat())
                .toInt()
        actionHeight = typedArray.getDimension(
            R.styleable.SuperToolBar_stb_actionHeight,
            actionHeight.toFloat()
        ).toInt()
        actionPadding = typedArray.getDimension(
            R.styleable.SuperToolBar_stb_actionPadding,
            actionPadding.toFloat()
        ).toInt()

        androidMTranslucentStatusBar = typedArray.getColor(
            R.styleable.SuperToolBar_stb_androidMTranslucentStatusBagColor,
            androidMTranslucentStatusBar
        )

        typedArray.recycle()

        linePaint = Paint()
        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.FILL
        setBottonLine(bottonLineHeight)
        //这个3个布局都是Fragment
        addMainLayout()
        initLeftLayout()
        initConterLayout()
        initRightLayout()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            val barHeightT = MeasureSpec.getSize(heightMeasureSpec)
            if (barHeightT > barHeight) {
                barHeight = barHeightT
            }
        }
        val height = barHeight + (if (setTranslucentStatusBarPaddingTop) getMStatusHeight() else 0)
        val mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, mHeightMeasureSpec)

        val leftLp = leftLayout.layoutParams as MarginLayoutParams
        val centerLp = centerLayout.layoutParams as MarginLayoutParams
        val rightLp = rightLayout.layoutParams as MarginLayoutParams

        val leftRightSize = leftLayout.measuredWidth + leftLp.leftMargin + leftLp.rightMargin +
                rightLayout.measuredWidth + rightLp.leftMargin + rightLp.rightMargin +
                paddingLeft + paddingRight +
                centerLp.leftMargin - centerLp.rightMargin

        //如果中间layout放不下，就强制设置成最大值
        if (centerLayout.measuredWidth >= measuredWidth - leftRightSize) {
            centerLayout.measure(
                MeasureSpec.makeMeasureSpec(
                    measuredWidth - leftRightSize,
                    MeasureSpec.EXACTLY
                ), MeasureSpec.makeMeasureSpec(centerLayout.measuredHeight, MeasureSpec.EXACTLY)
            )
        }
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val leftLp = leftLayout.layoutParams as MarginLayoutParams
        val rightLp = rightLayout.layoutParams as MarginLayoutParams
        val centerLp = centerLayout.layoutParams as MarginLayoutParams

        val leftSize = leftLayout.measuredWidth + leftLp.leftMargin + leftLp.rightMargin
        val rightSize = rightLayout.measuredWidth + rightLp.leftMargin + rightLp.rightMargin
        val leftRightSize =
            leftSize.coerceAtLeast(rightSize) * 2 + paddingLeft + paddingRight + centerLp.leftMargin - centerLp.rightMargin

        //如果中间layout放不下，就强制设置成最大值
        if (centerLayout.measuredWidth >= measuredWidth - leftRightSize) {
            var newLeft = 0
            var newRight = 0
            if (leftSize > rightSize) {
                //左边大
                newLeft = leftLayout.right + centerLp.leftMargin
                newRight = newLeft + centerLayout.measuredWidth
            } else {
                //右边大
                newRight = rightLayout.left - centerLp.rightMargin
                newLeft = newRight - centerLayout.measuredWidth
            }
            centerLayout.layout(newLeft, centerLayout.top, newRight, centerLayout.bottom)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制底部的线
        if (bottonLineHeight > 0) {
            linePaint.color = bottonLineColor
            canvas.drawRect(
                0F,
                height - bottonLineHeight.toFloat(),
                width.toFloat(),
                height.toFloat(),
                linePaint
            )
        }

        //绘制半透明状态栏
        if (setTranslucentStatusBarPaddingTop && Build.VERSION.SDK_INT < Build.VERSION_CODES.M && androidMTranslucentStatusBar != 0x00000000) {
            linePaint.color = androidMTranslucentStatusBar
            canvas.drawRect(0f, 0f, width.toFloat(), statusHeight.toFloat(), linePaint)
        }
    }


    /*
        初始化左边layout
        按需添加，默认添加返回键
     */
    private fun initLeftLayout() {
        leftLayout.removeAllViews()
    }


    /*
        初始化中间
        按需添加,默认标题
     */
    private fun initConterLayout() {
        centerLayout.removeAllViews()
        titleView = TextView(context)
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize.toFloat())
        titleView.setTextColor(titleColor)
        titleView.maxLines = 1
        titleView.ellipsize = TextUtils.TruncateAt.END
        titleView.gravity = Gravity.CENTER
        titleView.text = title
        val titleParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        centerLayout.addView(titleView, titleParams)
    }

    /*
        初始化右边layout
        按需添加
     */
    private fun initRightLayout() {
        rightLayout.removeAllViews()
    }

    /*
        3个主要的Layout
     */
    private fun addMainLayout() {
        leftLayout = FrameLayout(context)
        centerLayout = FrameLayout(context)
        rightLayout = FrameLayout(context)

        val leftParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        leftParams.gravity = Gravity.LEFT
        addView(leftLayout, leftParams)

        val centerParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        centerParams.gravity = Gravity.CENTER
        addView(centerLayout, centerParams)

        val rightParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        rightParams.gravity = Gravity.RIGHT
        addView(rightLayout, rightParams)

    }


    /*
        设置底部的线
     */
    fun setBottonLine(height: Int): SuperToolBar {
        //高度大于0就可以绘制
        bottonLineHeight = height
        if (bottonLineHeight > 0) {
            setWillNotDraw(false)
            setPadding(paddingLeft, paddingTop, paddingRight, bottonLineHeight)
        }
        invalidate()
        return this
    }

    /*
        设置底部线的颜色
     */
    fun setBottonLineColor(color: Int): SuperToolBar {
        bottonLineColor = color
        linePaint.color = bottonLineColor
        invalidate()
        return this
    }


    /**
     * 获取状态栏的高度
     */
    fun getMStatusHeight(): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }


    /*******************************************************************
     *                          设置左边的的控件
     *******************************************************************/

    /*
        添加左边多个action
     */
    fun addLeftAction(action: Action): Action {
        val index = getLeftActionLayout().childCount
        return addLeftAction(action, index)
    }


    /*
        添加一个左边的Action View
    */
    private fun addLeftAction(action: Action, index: Int): Action {
        action.getActionView().setOnClickListener {
            if (::onActionLeftClickListener.isInitialized) {
                onActionLeftClickListener.onActionClick(index, action)
            }
        }
        if (action.getActionView().layoutParams == null) {
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            action.getActionView().layoutParams = params
        }
        getLeftActionLayout().addView(action.getActionView(), index)
        return action
    }

    /*
        设置返回图片
     */
    fun setBackImg(@DrawableRes backImage: Int) {
        this.backImage = backImage
        //自动计算返回键颜色
        if (backImage == DEFAULT_BACKIMAGE && !isSetBackImgColor && background is ColorDrawable) {
            val color = (background as ColorDrawable).color
            backImgColor = (if (BarHelp.isColorDrak(color)) 0xffffffff else 0xff000000).toInt()
        }
        if (isSetBackImgColor) {
            backButton.setImageDrawable(
                BarHelp.getTintDrawable(
                    getDrawable(context, backImage)!!,
                    backImgColor
                )
            )
        } else {
            backButton.setImageResource(backImage)
        }
    }

    /*
        设置返回
     */
    fun setBack(activity: Activity) {
        if (!::backButton.isInitialized) {
            backButton = BackImageView(context)
            backButton.setPadding(backPadding, 0, backPadding, 0)
            backButton.backImgSquare = backImgSquare
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            params.gravity = Gravity.CENTER_VERTICAL or Gravity.LEFT
            leftLayout.addView(backButton, params)
        }
        setBackImg(backImage)
        backButton.setOnClickListener {
            activity.onBackPressed()
        }
    }

    /*
        设置返回键是否是正方形
     */
    fun setBackImageSquare(backImgSquare: Boolean) {
        backButton.backImgSquare = backImgSquare
    }

    /*
        设置返回键颜色
     */
    fun setBackImageColor(backImgColor: Int) {
        this.backImgColor = backImgColor
        setBackImg(backImgColor)
    }


    /*******************************************************************
     *                          设置中间的的控件
     *******************************************************************/

    fun setTitle(title: String): SuperToolBar {
        this.title = title
        titleView.text = title
        return this
    }

    fun setTitltColor(titleColor: Int) {
        this.titleColor = titleColor
        if (!::titleView.isInitialized) {
            titleView.setTextColor(titleColor)
        }
    }


    /*******************************************************************
     *                          设置右边的的控件
     *******************************************************************/

    /*
        移除全部action
     */
    fun removeAllActions() {
        getRightActionLayout().removeAllViews()
    }

    /*
        移除指定的action
     */
    fun removeActionAt(index: Int) {
        getRightActionLayout().removeViewAt(index)
    }

    /*
        移除指定的action
     */
    fun removeAction(action: Action) {
        val view = getViewByAction(action)
        if (view != null) {
            getRightActionLayout().removeView(view)
        }
    }

    /*
        获取action的个数
     */
    fun getActionCount(): Int {
        return getRightActionLayout().childCount
    }

    /*
        添加一个Action View
     */
    private fun addRightAction(action: Action, index: Int): Action {
        action.getActionView().setOnClickListener {
            if (::onActionClickListener.isInitialized) {
                onActionClickListener.onActionClick(index, action)
            }
        }
        if (action.getActionView().layoutParams == null) {
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            action.getActionView().layoutParams = params
        }
        getRightActionLayout().addView(action.getActionView(), index)
        return action
    }


    /*
        添加右边多个action View
     */
    fun addRightActions(actions: MutableList<Action>) {
        actions.forEachIndexed { index, item ->
            addRightAction(item, index)
        }
    }

    fun addRightAction(action: Action): Action {
        val index = getRightActionLayout().childCount
        return addRightAction(action, index)
    }

    private fun getViewByAction(action: Action): View {
        return getRightActionLayout().findViewWithTag(action)
    }


    /*
        获取右边的actionLayout
     */
    private fun getRightActionLayout(): LinearLayout {
        var actionLayout = rightLayout.findViewWithTag<LinearLayout>(ACTION_LAYOUT)
        if (actionLayout == null) {
            actionLayout = LinearLayout(context)
            actionLayout.orientation = LinearLayout.HORIZONTAL;
            actionLayout.tag = ACTION_LAYOUT
            rightLayout.setPadding(0, 0, dip2px(5, context), 0)
            rightLayout.addView(
                actionLayout,
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            )
        }
        return actionLayout
    }

    /*
        获取左边的actionLayout
     */

    private fun getLeftActionLayout(): LinearLayout {
        var actionLayout = leftLayout.findViewWithTag<LinearLayout>(LEFT_ACTION_LAYOUT)
        if (actionLayout == null) {
            actionLayout = LinearLayout(context)
            actionLayout.orientation = LinearLayout.HORIZONTAL;
            actionLayout.tag = LEFT_ACTION_LAYOUT
            leftLayout.setPadding(dip2px(5, context), 0, 0, 0)
            leftLayout.addView(
                actionLayout,
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
            )
        }
        return actionLayout
    }


    /*******************************************************************
     *                          其他方法
     *******************************************************************/

    /*
        设置透明状态栏时候，顶部的padding
     */
    fun setTranslucentStatusBarPaddingTop(): SuperToolBar {
        return setTranslucentStatusBarPaddingTop(false)
    }

    /**
     *  设置透明状态栏时候，顶部的padding
     *
     *  @param isNeedAndroidMHalf 6.0以下是否绘制半透明，因为不能设置状态栏字体颜色
     */
    fun setTranslucentStatusBarPaddingTop(isNeedAndroidMHalf: Boolean): SuperToolBar {
        setTranslucentStatusBarPaddingTop = true
        statusHeight = getMStatusHeight()
        setPadding(paddingLeft, statusHeight, paddingRight, paddingBottom)
        if (isNeedAndroidMHalf) {
            androidMTranslucentStatusBar = 0x00000000
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && isNeedAndroidMHalf) {
            //6.0以下绘制半透明,因为不能设置状态栏字体颜色
            setWillNotDraw(false)
        }
        requestLayout()
        return this
    }


    /*******************************************************************
     *                          事件
     *******************************************************************/

    fun setOnActionClickListener(onActionClick: Action.OnActionClick) {
        this.onActionClickListener = onActionClick
    }

    fun setOnActionClickListener(onActionClick: Action.OnActionLeftClick) {
        this.onActionLeftClickListener = onActionClick
    }

}