package com.calc.calc

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast

fun Context.toast(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.displayWidth(): Int = displayMetrics().widthPixels
fun Context.displayHeight(): Int = displayMetrics().heightPixels

private fun Context.displayMetrics(): DisplayMetrics = DisplayMetrics().apply {
    (getSystemService(Context.WINDOW_SERVICE) as WindowManager)
        .defaultDisplay
        .getMetrics(this)
}