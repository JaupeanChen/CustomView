package com.example.customdrugview.ext

import android.graphics.PointF
import androidx.core.graphics.minus

/**
 *
 * @ClassName: PointFExt
 * @Author: 史大拿
 * @CreateDate: 8/17/22$ 11:12 AM$
 * TODO
 */
/*
 * 作者:史大拿
 * 创建时间: 8/17/22 11:10 AM
 * TODO 判断当前PointF是否在 bPointF内
 *  bPadding: 外边距
 * return: 在内返回true
 */
fun PointF.contains(b: PointF, bPadding: Float = 0f): Boolean {
    val isX = this.x <= b.x + bPadding && this.x >= b.x - bPadding

    val isY = this.y <= b.y + bPadding && this.y >= b.y - bPadding
    return isX && isY
}
