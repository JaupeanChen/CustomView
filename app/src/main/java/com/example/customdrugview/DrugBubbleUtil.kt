package com.example.customdrugview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.core.animation.doOnEnd
import com.example.customdrugview.ext.getBackGroundBitmap

class DrugBubbleUtil(private val view: View) {

    private val context by lazy {
        view.context
    }

    private val drugView by lazy {
        TempView(context)
    }

    private val windowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val layoutParams by lazy {
        WindowManager.LayoutParams().apply {
            format = PixelFormat.TRANSLUCENT
        }
    }

    private val statusBarHeight by lazy {
        context.statusBarHeight()
    }

    private val viewBitmap by lazy {
        view.getBackGroundBitmap()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun bind() {
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.visibility = View.INVISIBLE
                    windowManager.addView(drugView, layoutParams)
                    //当前View的中心点需要获取当前window的绝对坐标位置
                    val location = IntArray(2)
                    view.getLocationInWindow(location)
                    drugView.initCircleLocation(
                        location[0].toFloat() + view.width / 2,
                        location[1].toFloat() + view.height / 2 - statusBarHeight,
                        event.rawX,
                        event.rawY - statusBarHeight
                    )

                    drugView.updateDragBitmap(viewBitmap, viewBitmap.width.toFloat())

//                    Log.d("event.x", event.x.toString())
//                    Log.d("event.y", event.y.toString())
//                    Log.d("event.rawX", event.rawX.toString())
//                    Log.d("event.rawY", event.rawY.toString())
                }
                MotionEvent.ACTION_MOVE -> {
                    //这里如果传入event.x和event.y, 结果居然没有动画效果
                    drugView.updateBigCircle(event.rawX, event.rawY - statusBarHeight)
                }
                MotionEvent.ACTION_UP -> {
                    if (drugView.isContains()) {
                        //回弹
                        drugView.bigCircleAnimator().run {
                            start()
                            doOnEnd {
                                windowManager.removeView(drugView)
                                view.visibility = View.VISIBLE
                                drugView.updateDragBitmap(null, viewBitmap.width.toFloat())
                            }
                        }
                    } else {
                        //爆炸

                        //爆炸之前先清空拖拽控件(原先我居然要放到爆炸动画结束)
                        drugView.updateDragBitmap(null, viewBitmap.width.toFloat())

                        drugView.explodeAnimator.run {
                            start()
                            doOnEnd {
                                windowManager.removeView(drugView)
                                view.visibility = View.VISIBLE
                            }
                        }

                    }

                }
            }
            true
        }
    }

    fun Context.statusBarHeight() = let {
        var height = 0
        val resourceId: Int =
            resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId)
        }
        height
    }

}