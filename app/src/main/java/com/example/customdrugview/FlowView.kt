package com.example.customdrugview

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.google.android.material.imageview.ShapeableImageView
import kotlin.math.abs

class FlowView : FrameLayout, View.OnTouchListener {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
        val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams = params

        val image = ShapeableImageView(context)
        image.setImageResource(R.mipmap.ic_my_1)
        setOnTouchListener(this)
        addView(image)
        post {
            mViewWide = width
            mViewHeight = height
        }
    }

    var mViewWide = 0
    var mViewHeight = 0
    var mDownX = 0f
    var mDownY = 0f
    var mRawDownX = 0f
    var mRawDownY = 0f

    private var mAdsorbType = ADSORB_TYPE_VERTICAL

    companion object {
        const val ADSORB_TYPE_VERTICAL = 1
        const val ADSORB_TYPE_HORIZONTAL = 2
    }

    fun setAdsorbType(type: Int) {
        mAdsorbType = type
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                //第一次按下的点
                mDownX = event.x
                mDownY = event.y
                //记录第一次按下相对于屏幕的坐标轴点
                mRawDownX = event.rawX
                mRawDownY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                //每次移动的坐标点
                val x = event.x
                val y = event.y
                //计算每次移动的坐标和第一次按下坐标的偏移量，进行对view的移动
                offsetLeftAndRight((x - mDownX).toInt())
                offsetTopAndBottom((y - mDownY).toInt())
            }
            MotionEvent.ACTION_UP -> {
                if (mAdsorbType == ADSORB_TYPE_VERTICAL) {
                    adsorbTopOrBottom(event)
                } else {
                    adsorbLeftOrRight(event)
                }
            }
        }
        return true
    }

    //判断原本是否在上半屏幕
    private fun isTopFromRaw(): Boolean {
        return mRawDownY < getScreenHeight() / 2
    }

    private fun adsorbTopOrBottom(event: MotionEvent) {
        if (isTopFromRaw()) {
            //view原位置在上半屏幕
            val offset = mViewHeight / 2 + abs(event.rawY - mRawDownY)
            if (offset < getScreenHeight() / 2) {
                //当位移加view半高（也就是中心点）不超过半屏，则回吸到顶部
                animate().setInterpolator(DecelerateInterpolator())
                    .setDuration(300)
                    .y(0f)
                    .start()
            } else {
                //吸底
                //这边如果用屏幕高度来算的话，就会多出导航栏（如果有）的尺寸
//                val bottomY = getScreenHeight() - mViewHeight - getNavBarHeight() - getStatusBarHeight()
                val bottomY = getContentHeight() - mViewHeight - getNavBarHeight()
                Log.d(javaClass.simpleName, "ScreenHeight: ${getScreenHeight()}")
                Log.d(javaClass.simpleName, "NavBarHeight: ${getNavBarHeight()}")
                Log.d(javaClass.simpleName, "bottomY: $bottomY")
                Log.d(javaClass.simpleName, "mViewHeight: $mViewHeight")
                animate().setInterpolator(DecelerateInterpolator())
                    .setDuration(300)
                    .y(bottomY.toFloat())
                    .start()
            }
        } else {
            //view原位置在下半屏幕
            val offset = mViewHeight / 2 + abs(event.rawY - mRawDownY)
            if (offset < getScreenHeight() / 2) {
                //当位移加view半高（也就是中心点）不超过半屏，则回吸到底部
//                val bottomY = getScreenHeight() - mViewHeight - getNavBarHeight() - getStatusBarHeight()
                val bottomY = getContentHeight() - mViewHeight - getNavBarHeight()
                Log.d(javaClass.simpleName, "ScreenHeight: ${getScreenHeight()}")
                Log.d(javaClass.simpleName, "NavBarHeight: ${getNavBarHeight()}")
                Log.d(javaClass.simpleName, "bottomY: $bottomY")
                Log.d(javaClass.simpleName, "mViewHeight: $mViewHeight")
                animate().setInterpolator(DecelerateInterpolator())
                    .setDuration(300)
                    .y(bottomY.toFloat())
                    .start()
            } else {
                //吸顶
                animate().setInterpolator(DecelerateInterpolator())
                    .setDuration(300)
                    .y(0f)
                    .start()
            }
        }
    }

    private fun adsorbLeftOrRight(event: MotionEvent) {
        if (mRawDownX < getScreenWidth() / 2) {
            //第一次点击时view在左半屏
            val offset = mViewWide / 2 + abs(event.rawX - mRawDownX)
            if (offset < getScreenWidth() / 2) {
                //没有位移过半屏，吸左
                animate().setInterpolator(DecelerateInterpolator())
                    .setDuration(300)
                    .x(0f)
                    .start()
            } else {
                //位移过半屏，吸右
                val endX = getScreenWidth() - mViewWide
                animate().setInterpolator(DecelerateInterpolator())
                    .setDuration(300)
                    .x(endX.toFloat())
                    .start()
            }
        } else {
            //第一次点击时view在右半屏
            val offset = mViewWide / 2 + abs(event.rawX - mRawDownX)
            if (offset < getScreenWidth() / 2) {
                //没有位移过半屏，吸右
                val endX = getScreenWidth() - mViewWide
                Log.d(javaClass.simpleName, "ScreenWidth: ${getScreenWidth()}")
                Log.d(javaClass.simpleName, "ContentWidth: ${getContentWidth()}")
                animate().setInterpolator(DecelerateInterpolator())
                    .setDuration(300)
                    .x(endX.toFloat())
                    .start()
            } else {
                //位移过半屏，吸左
                animate().setInterpolator(DecelerateInterpolator())
                    .setDuration(300)
                    .x(0f)
                    .start()
            }
        }
    }

    private fun getScreenHeight(): Int {
//        val displayMetrics = DisplayMetrics()
//        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return context.resources.displayMetrics.heightPixels
    }

    private fun getScreenWidth(): Int {
        return context.resources.displayMetrics.widthPixels
    }

    private fun getContentHeight(): Int {
        var height: Int
        val frameLayout =
            (context as Activity).window.decorView.findViewById<FrameLayout>(android.R.id.content)
        frameLayout.let {
            height = it.bottom
        }
        Log.d(javaClass.simpleName, "ContentHeight: $height")
        return height
    }

    private fun getContentWidth(): Int {
        var width: Int
        val frameLayout =
            (context as Activity).window.decorView.findViewById<FrameLayout>(android.R.id.content)
        frameLayout.let {
            width = it.right
        }
        Log.d(javaClass.simpleName, "ContentHeight: $width")
        return width
    }

    private fun getNavBarHeight(): Int {
        val identifier =
            context.resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return if (identifier != 0) {
            val resourceId =
                context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    private fun getStatusBarHeight(): Int {
        val localRect = Rect()
        (context as Activity).window.decorView.getWindowVisibleDisplayFrame(localRect)
        var mStatusBarHeight = localRect.top
        if (0 == mStatusBarHeight) {
            try {
                val localClass = Class.forName("com.android.internal.R\$dimen")
                val localObject = localClass.newInstance()
                val i5 =
                    localClass.getField("status_bar_height")[localObject].toString().toInt()
                mStatusBarHeight = context.resources.getDimensionPixelSize(i5)
            } catch (var6: ClassNotFoundException) {
                var6.printStackTrace()
            } catch (var7: IllegalAccessException) {
                var7.printStackTrace()
            } catch (var8: InstantiationException) {
                var8.printStackTrace()
            } catch (var9: NumberFormatException) {
                var9.printStackTrace()
            } catch (var10: IllegalArgumentException) {
                var10.printStackTrace()
            } catch (var11: SecurityException) {
                var11.printStackTrace()
            } catch (var12: NoSuchFieldException) {
                var12.printStackTrace()
            }
        }
        if (0 == mStatusBarHeight) {
            val resourceId: Int =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                mStatusBarHeight = context.resources.getDimensionPixelSize(resourceId)
            }
        }
        return mStatusBarHeight
    }

    private fun dp2px(dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

}