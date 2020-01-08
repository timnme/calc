package com.calc.calc.charts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class GlideModule : AppGlideModule()

class WolframChartActivity : AppCompatActivity() {
    companion object {
        private const val CHART_IMAGE_URL = "CHART_IMAGE_URL"

        fun start(from: Context?, chartImageUrl: String) {
            from?.startActivity(
                Intent(from, WolframChartActivity::class.java).apply {
                    putExtra(CHART_IMAGE_URL, chartImageUrl)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.extras?.getString(CHART_IMAGE_URL)?.let {
            setContentView(ImageView(this).apply {
                GlideApp
                    .with(this)
                    .load(it)
                    .into(this)
            })
        }
    }
}