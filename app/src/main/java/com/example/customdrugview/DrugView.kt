package com.example.customdrugview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class DrugView(context: Context) : View(context) {

    constructor(context: Context, attributeSet: AttributeSet) : this(context) {
    }

    constructor(
        context: Context,
        attributeSet: AttributeSet,
        defStyleAttr: Int
    ) : this(context, attributeSet) {
    }

    private val pointF = PointF(400f, 600f)
    private val supportPaint = Paint()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        supportPaint.color = Color.CYAN
        canvas?.drawCircle(pointF.x, pointF.y, 200f, supportPaint)
    }
}