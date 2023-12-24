package com.example.customdrugview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PointFEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.graphics.minus
import com.example.customdrugview.ext.contains
import com.example.customdrugview.ext.dp
import com.example.customdrugview.ext.getBitMap
import kotlin.math.*

class TempView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    companion object {
        private const val bigRadius = 75f

        private const val smallRadius = 75f

        private const val supportRadius = 500f
    }

    private val paint = Paint()
    private var bigMove = false

    //大圆初始位置
    //此时通过属性动画来改变bigPointF肯定是不可取的,因为他是懒加载
//    private val bigPointF by lazy {
//        PointF(width / 2f + 300, height / 2f)
//    }
    //则应改为：
    //注意：这里bigPointF不能再设置为私有，否则bigCircleAnimator()方法将访问不到
    //应该是通过反射去设置属性
    var bigPointF = PointF(0f, 0f)
        set(value) {
            field = value
            invalidate()
        }

    //小圆初始位置
    private val smallPointF by lazy {
        PointF(width / 2f, height / 2f)
    }

    //爆炸点坐标
    var explodeIndex = -1
        set(value) {
            field = value
            invalidate()
        }

    //通过属性动画修改爆炸下标，最后一帧的时候回到-1
    val explodeAnimator by lazy {
        ObjectAnimator.ofInt(this, "explodeIndex", 19, -1).apply {
            duration = 1000
        }
    }

    private val explodeImages by lazy {
        val list = arrayListOf<Bitmap>()
//        val width = (bigRadius * 2).toInt()
        val width = dragBitmapWith.toInt()
        list.add(getBitMap(R.mipmap.explode_0, width))
        list.add(getBitMap(R.mipmap.explode_1, width))
        list.add(getBitMap(R.mipmap.explode_2, width))
        list.add(getBitMap(R.mipmap.explode_3, width))
        list.add(getBitMap(R.mipmap.explode_4, width))
        list.add(getBitMap(R.mipmap.explode_5, width))
        list.add(getBitMap(R.mipmap.explode_6, width))
        list.add(getBitMap(R.mipmap.explode_7, width))
        list.add(getBitMap(R.mipmap.explode_8, width))
        list.add(getBitMap(R.mipmap.explode_9, width))
        list.add(getBitMap(R.mipmap.explode_10, width))
        list.add(getBitMap(R.mipmap.explode_11, width))
        list.add(getBitMap(R.mipmap.explode_12, width))
        list.add(getBitMap(R.mipmap.explode_13, width))
        list.add(getBitMap(R.mipmap.explode_14, width))
        list.add(getBitMap(R.mipmap.explode_15, width))
        list.add(getBitMap(R.mipmap.explode_16, width))
        list.add(getBitMap(R.mipmap.explode_17, width))
        list.add(getBitMap(R.mipmap.explode_18, width))
        list.add(getBitMap(R.mipmap.explode_19, width))
        list
    }

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        when (event?.action) {
//            MotionEvent.ACTION_DOWN -> {
//                //先判断点击点是否在大圆内，如果是再进行移动
//                val locX = event.x
//                val locY = event.y
//                val minX = bigPointF.x - bigRadius
//                val maxX = bigPointF.x + bigRadius
//                val minY = bigPointF.y - bigRadius
//                val maxY = bigPointF.y + bigRadius
//                val isContainsX = locX in minX..maxX
//                val isContainsY = locY in minY..maxY
//                bigMove = isContainsX && isContainsY
//
//                if (!bigPointF.contains(smallPointF, supportRadius)) {
//                    //当大圆消失时，重置
//                    bigPointF.x = width / 2f
//                    bigPointF.y = height / 2f
//                }
//            }
//            MotionEvent.ACTION_MOVE -> {
//                if (bigMove) {
//                    bigPointF.x = event.x
//                    bigPointF.y = event.y
//                }
//            }
//            MotionEvent.ACTION_UP -> {
//                if (bigPointF.contains(smallPointF, supportRadius)) {
//                    //回弹
//                    bigCircleAnimator().start()
//                } else {
//                    //绘制爆炸效果
//                    explodeAnimator.start()
//                    //爆炸结束，重置大圆坐标
//                    explodeAnimator.doOnEnd {
//                        bigPointF.x = width / 2f
//                        bigPointF.y = height / 2f
//                    }
//                }
//            }
//        }
//        invalidate()
//        //把事件消费掉
//        return true
//    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     * 也就是说在layout的时候，整个View大小改变会回调到这个方法，此时我们就可以把大圆的初始值
     * 赋给它。
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bigPointF.x = width / 2f
        bigPointF.y = height / 2f
    }

    fun initCircleLocation(smallX: Float, smallY: Float, bigX: Float, bigY: Float) {
        smallPointF.x = smallX
        smallPointF.y = smallY
        bigPointF.x = bigX
        bigPointF.y = bigY
    }

    fun updateBigCircle(x: Float, y: Float) {
        bigPointF.x = x
        bigPointF.y = y
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //重绘时重置颜色
        paint.color = Color.CYAN

        val d = circlesDistance()
        var ratio = d / supportRadius
        if (ratio > 0.6f) {
            ratio = 0.6f
        }
        //绘制小圆
        val updateSmallRadius = smallRadius - smallRadius * ratio
        canvas.drawCircle(smallPointF.x, smallPointF.y, updateSmallRadius, paint)

        //绘制大圆
        if (bigPointF.contains(smallPointF, supportRadius)) {
            //当大圆在辅助圆内再绘制
            canvas.drawCircle(bigPointF.x, bigPointF.y, bigRadius, paint)

            //绘制贝塞尔曲线
            drawBezier(canvas, updateSmallRadius, bigRadius)
        }

        dragBitmap?.let {
            canvas.drawBitmap(
                it,
                bigPointF.x - it.width / 2,
                bigPointF.y - it.height / 2,
                paint
            )
        }

        //绘制爆炸效果
        if (explodeIndex != -1) {
            //圆和bitmap的绘制坐标系不同，圆在中心，bitmap在左上角
            canvas.drawBitmap(
                explodeImages[explodeIndex],
                bigPointF.x - bigRadius,
                bigPointF.y - bigRadius,
                paint
            )
        }

        //绘制辅助圆
//        paint.color = Color.argb(20, 255, 0, 0)
//        canvas.drawCircle(smallPointF.x, smallPointF.y, supportRadius, paint)
    }

    private var dragBitmap: Bitmap? = null
    private var dragBitmapWith = 0f

    //抛出方法让外部传入控件对应的bitmap
    fun updateDragBitmap(dragBitmap: Bitmap?, with: Float) {
        this.dragBitmap = dragBitmap
        this.dragBitmapWith = with
        invalidate()
    }

    private fun circlesDistance(): Float {
        //利用ktx中自带的运算符重载函数
        val distance = bigPointF - smallPointF
        return sqrt(distance.x.toDouble().pow(2.0) + (distance.y.toDouble().pow(2.0)))
            .toFloat()
    }

//    fun PointF.contains(dot: PointF, radius: Float): Boolean {
//        val minX = this.x - radius
//        val maxX = this.x + radius
//        val minY = this.y - radius
//        val maxY = this.y + radius
//        val isContainsX = dot.x in minX..maxX
//        val isContainsY = dot.y in minY..maxY
//        return isContainsX && isContainsY
//    }

    //判断当前拖拽控件的中心点是否在辅助圆内
    fun isContains(): Boolean {
        return bigPointF.contains(smallPointF, supportRadius)
    }

    //dp2px
    fun Number.dp(): Int {
        val f = toFloat()
        val scale: Float = Resources.getSystem().displayMetrics.density
        return (f * scale + 0.5f).toInt()
    }

    //大圆回弹动画
    //但是这个为什么用的是ObjectAnimator？
    fun bigCircleAnimator(): Animator {
        return ObjectAnimator.ofObject(
            this,
            "bigPointF",
            PointFEvaluator(),
            PointF(smallPointF.x, smallPointF.y)
        ).apply {
            duration = 400
            //设置回弹插值器
            interpolator = OvershootInterpolator(3f)
        }
    }

    private fun drawBezier(canvas: Canvas, smallRadius: Float, bigRadius: Float) {
//        val pointA = PointF()
//        var bc = 0f
//        var ac = 0f
        //也就是求出ac和bc即可推算出A点
//        pointA.x = smallPointF.x + bc
//        pointA.y = smallPointF.y - ac

        val current = bigPointF - smallPointF
        //FD,BF长度
        //这里为什么要转Double？
        val FD = current.x.toDouble()
        val BF = current.y.toDouble()
        //角BDF
        val BDF = atan(BF / FD)

        //计算出P1点坐标
//        bc = (smallRadius * sin(BDF)).toFloat()
//        ac = (smallRadius * cos(BDF)).toFloat()
//        pointA.x = smallPointF.x + bc
//        pointA.y = smallPointF.y - ac

        val p1X = smallPointF.x + smallRadius * sin(BDF)
        val p1Y = smallPointF.y - smallRadius * cos(BDF)

        //计算出P2点坐标
        val p2X = bigPointF.x + bigRadius * sin(BDF)
        val p2Y = bigPointF.y - bigRadius * cos(BDF)

        //计算出P3点坐标
        val p3X = smallPointF.x - smallRadius * sin(BDF)
        val p3Y = smallPointF.y + smallRadius * cos(BDF)

        //计算出P4点坐标
        val p4X = bigPointF.x - bigRadius * sin(BDF)
        val p4Y = bigPointF.y + bigRadius * cos(BDF)

        //控制点
        val controlPointX = current.x / 2 + smallPointF.x
        val controlPointY = current.y / 2 + smallPointF.y

        val path = Path()
        //移动到P1位置
        path.moveTo(p1X.toFloat(), p1Y.toFloat())
        //绘制贝塞尔
        path.quadTo(controlPointX, controlPointY, p2X.toFloat(), p2Y.toFloat())

        //连接到P4
        path.lineTo(p4X.toFloat(), p4Y.toFloat())
        //绘制贝塞尔
        //这里因为第二个参数写错成controlPointX，排查了一下午没排查出来！细心细心！！
        path.quadTo(controlPointX, controlPointY, p3X.toFloat(), p3Y.toFloat())
        //连接到P1
        path.close()

        //绘制
        canvas.drawPath(path, paint)
    }

}