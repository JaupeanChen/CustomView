package com.example.customdrugview.ext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.annotation.DrawableRes

fun View.getBitMap(@DrawableRes res: Int, width: Int = 640): Bitmap = let {
    val option = BitmapFactory.Options()
    //inJustDecodeBounds为true时，允许调用者查询位图而无需为其像素分配内存。
    //当我们需要获取图片的宽高等属性时且不对数据进行操作，那么我们不应该把图片的数据加载到内存中，
    // 这时我们可以设置inJustDecodeBounds属性为true.
    option.inJustDecodeBounds = true
    BitmapFactory.decodeResource(resources, res)

    option.inJustDecodeBounds = false
    option.inDensity = option.outWidth
    option.inTargetDensity = width
    BitmapFactory.decodeResource(resources, res, option)
}

fun View.getBackGroundBitmap(): Bitmap = let {
    it.buildDrawingCache()
    it.drawingCache
}