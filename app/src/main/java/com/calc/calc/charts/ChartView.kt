package com.calc.calc.charts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import com.calc.calc.displayHeight
import com.calc.calc.displayWidth

class ChartView(context: Context) : View(context) {
    private val paint = Paint()
    private lateinit var points: FloatArray

    constructor(context: Context, points: FloatArray) : this(context) {
        this.points = points
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val displayWidth = context.displayWidth().toFloat()
        val displayHeight = context.displayHeight().toFloat()
        // X axis
        drawAxis(
            canvas = canvas,
            xStart = 0f,
            yStart = displayHeight / 2,
            xEnd = displayWidth,
            yEnd = displayHeight / 2
        )
        // Y axis
        drawAxis(
            canvas = canvas,
            xStart = displayWidth / 2,
            yStart = 0f,
            xEnd = displayWidth / 2,
            yEnd = displayHeight
        )
        canvas.drawPoints(
            points, paint.apply {
                color = Color.RED
                strokeWidth = 8f
            }
        )
    }

    private fun drawAxis(
        canvas: Canvas, xStart: Float, yStart: Float, xEnd: Float, yEnd: Float
    ) {
        canvas.drawLine(
            xStart, yStart, xEnd, yEnd, paint.apply {
                color = Color.BLACK
                strokeWidth = 2f
            }
        )
    }
}