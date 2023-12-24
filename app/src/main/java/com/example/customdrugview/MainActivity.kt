package com.example.customdrugview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val frameLayout = window.decorView.findViewById<FrameLayout>(android.R.id.content)
//        val flowView = FlowView(this)
////        flowView.setAdsorbType(FlowView.ADSORB_TYPE_HORIZONTAL)
//        frameLayout.addView(flowView)

//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        recyclerView.addItemDecoration()
    }

    private fun doAnimate() {
        val imageView = findViewById<ImageView>(R.id.iv)
        val localUrl = "file:///android_asset/jinitaimei.gif"
        Glide.with(this)
            .asGif()
            .load(localUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(imageView)
        DrugBubbleUtil(imageView).bind()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val decorView = window.decorView as ViewGroup
            val childCount = decorView.childCount
            Log.d("DecorView.childCount", childCount.toString())
            val children = decorView.children
            val iterator = children.iterator()
            iterator.forEach {
                //DecorView一般情况下包含一个竖直方向的LinearLayout
                //(和状态栏及导航栏)
                Log.d("DecorView.children", it.toString())
            }
            val linearLayout = decorView.getChildAt(0) as ViewGroup
            Log.d("DecorView.childAt_0", linearLayout.toString())

            Log.d("childAt0.childCount", linearLayout.childCount.toString())
            val children1 = linearLayout.children
            val iterator1 = children1.iterator()
            iterator1.forEach {
                //LinearLayout则一般包含标题栏及内容栏(R.id.content)
                //内容栏也是一个FrameLayout
                Log.d("childAt0.children", it.toString())
            }

            val contentView = linearLayout.getChildAt(1) as ViewGroup
            Log.d("childAt0.childAt_1", contentView.toString())
            val id = contentView.id
            Log.d("childAt0.childAt_1_id", id.toString())

            Log.d("childAt0.childCount", contentView.childCount.toString())
            val children2 = contentView.children
            val iterator2 = children2.iterator()
            iterator2.forEach {
                Log.d("childAt1.children", it.toString())
            }

            val content = findViewById<ViewGroup>(android.R.id.content)
            Log.d("Content", content.toString())
            val contentChildren = content.children
            val iterator3 = contentChildren.iterator()
            iterator3.forEach {
                Log.d("content.children", it.toString())
            }

        }
    }
}