package com.calc.calc.charts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.calc.calc.displayHeight
import com.calc.calc.displayWidth

class ChartActivity : AppCompatActivity() {
    companion object {
        private const val POINTS = "POINTS"
        private const val LOWER_X = "LOWER_X"
        private const val UPPER_X = "UPPER_X"
        private const val LOWER_Y = "LOWER_Y"
        private const val UPPER_Y = "UPPER_Y"

        fun start(
            from: Context?, points: FloatArray,
            lowerX: Long, upperX: Long,
            lowerY: Float, upperY: Float
        ) {
            from?.startActivity(
                Intent(from, ChartActivity::class.java).apply {
                    putExtra(POINTS, points)
                    putExtra(LOWER_X, lowerX)
                    putExtra(UPPER_X, upperX)
                    putExtra(LOWER_Y, lowerY)
                    putExtra(UPPER_Y, upperY)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.extras?.let { extras ->
            extras.getFloatArray(POINTS)?.let { points ->
                drawChart(
                    points,
                    extras.getLong(LOWER_X), extras.getLong(UPPER_X),
                    extras.getFloat(LOWER_Y), extras.getFloat(UPPER_Y)
                )
            }
        }
    }

    private fun drawChart(
        points: FloatArray,
        lowerX: Long, upperX: Long,
        lowerY: Float, upperY: Float
    ) {
        val pointsToDraw = FloatArray(points.size)

        val displayWidthHalf = displayWidth() / 2
        val displayHeightHalf = displayHeight() / 2

        val xDensity = displayWidth().toFloat() / (upperX - lowerX)
        val yDensity = displayHeight().toFloat() / (upperY - lowerY)

        for (i in points.indices) {
            val point = points[i]
            pointsToDraw[i] =
                if (i % 2 == 0) displayWidthHalf + point * xDensity // x
                else displayHeightHalf - point * yDensity // y
        }

        setContentView(ChartView(this, pointsToDraw))
    }
}