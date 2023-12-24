package com.example.customdrugview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import kotlin.math.log

/**
 * 实现经典的流式布局
 * 因为是ViewGroup，所以我们不需要去重写onDraw方法
 */
class TagFlowLayout(context: Context, attributeSet: AttributeSet) :
    ViewGroup(context, attributeSet) {

    /**
     * 重写onMeasure方法，通过得到子view的测量尺寸，来测量出自己的尺寸
     * widthMeasureSpec、heightMeasureSpec: 父类传递给我们的推荐值
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //先通过MeasureSpec来获取父类传给我们的宽推荐值（mode和size）
        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSuggestSize = MeasureSpec.getSize(widthMeasureSpec)
        //先通过MeasureSpec来获取父类传给我们的高推荐值（mode和size）
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSuggestSize = MeasureSpec.getSize(heightMeasureSpec)

        //设置保存当前行宽高值变量
        var lineWith = 0
        var lineHeight = 0
        //总宽和总高
        var width = 0
        var height = 0

        //遍历得到子类的测量值
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            //一样，我们把推荐值传递给子view，让子view测量自己
            //TODO 这里误调了child.measure()导致了问题
//            child.measure(widthMeasureSpec, heightMeasureSpec)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)

            //获取子view的margin参数
            val layoutParams = child.layoutParams as MarginLayoutParams

            //注意：这里只有子view先测量完了，我们才能拿到它的measuredWidth和measuredHeight
//            val childWidth = child.measuredWidth
//            val childHeight = child.measuredHeight

            //把margin加入计算
            val childWidth =
                child.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            val childHeight =
                child.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin

            //如果当前行的宽度加上当前子view的宽度超过父类给我们的推荐宽度widthSize，那么就需要换行了
            if (lineWith + childWidth < widthSuggestSize) {
                //不换行
                lineWith += childWidth
                lineHeight = Math.max(lineHeight, childHeight) //取最大行高
            } else {
                //换行
                //记录上一行的数据
                height += lineHeight
                width = Math.max(width, lineWith)
                //重置行高行宽，开启新一行
                lineHeight = childHeight
                lineWith = childWidth
            }
            //因为我们前面是在换行时去累加上一行的数据，所以要在最后一个子view时累加当前行的数据
            if (i == childCount - 1) {
                height += lineHeight
                width = Math.max(width, lineWith)
            }
        }

        //通过子view测量完自己的尺寸之后，设置给系统
        val backWidth = if (widthMeasureMode == MeasureSpec.EXACTLY) widthSuggestSize else width
        val backHeight = if (heightMeasureMode == MeasureSpec.EXACTLY) heightSuggestSize else height
        setMeasuredDimension(backWidth, backHeight)
    }

    /**
     * 一个一个来对子view定位布局，我们只布局子view，也就是说我们自己也是父类来布局的
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        println("changed: $changed, l: $l, t: $t, r:$r, b: $b")

        //当前行行高
        var lineHeight = 0
        //当前left值
        var curLeft = 0
        //当前top值
        var curTop = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            //一样，先拿到子view的margin参数，然后加入计算
            val layoutParams = child.layoutParams as MarginLayoutParams

            //val childWidth = child.measuredWidth
            //val childHeight = child.measuredHeight
            val childWidth =
                child.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            val childHeight =
                child.measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin

            //当子view的测量宽度加上去超过我们本身的测量宽度的话，就需要换行
            if (curLeft + childWidth < measuredWidth) {
                //不换行
                lineHeight = Math.max(lineHeight, childHeight)
            } else {
                //换行
                curLeft = 0 //重置curLeft
                curTop += lineHeight //更新curTop
            }

            //val curL = curLeft
            //val curT = curTop
            val curL = curLeft + layoutParams.leftMargin
            val curT = curTop + layoutParams.topMargin
            val curR = curL + child.measuredWidth
            val curB = curT + child.measuredHeight

            child.layout(curL, curT, curR, curB)
            curLeft += childWidth
        }
    }

    /**
     * 如果要支持子view的margin参数，就需要重写一下三个方法
     */
    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }
}