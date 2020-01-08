package com.calc.calc

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.TableRow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.calc.calc.charts.ChartActivity
import com.calc.calc.charts.WolframChartActivity
import com.calc.calc.wolframapi.ServiceProvider
import com.calc.calc.wolframapi.ApiResponse
import com.calc.calculator.InvalidBracketsException
import com.calc.calculator.solve
import kotlinx.android.synthetic.main.activity_calc.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalcActivity : AppCompatActivity() {
    companion object {
        private const val OPENING_BRACKET = 0
        private const val CLOSING_BRACKET = 1
        private const val EXPONENTIATION = 2
        private const val SQUARE_ROOT = 3
        private const val SEVEN = 4
        private const val EIGHT = 5
        private const val NINE = 6
        private const val DIVISION = 7
        private const val FOUR = 8
        private const val FIVE = 9
        private const val SIX = 10
        private const val MULTIPLICATION = 11
        private const val ONE = 12
        private const val TWO = 13
        private const val THREE = 14
        private const val SUBTRACTION = 15
        private const val ZERO = 16
        private const val DECIMAL_POINT = 17
        private const val VARIABLE = 18
        private const val ADDITION = 19
        private const val CLEAR = 20
        private const val DELETE = 21

        private val symbols = mapOf(
            EXPONENTIATION to "^",
            OPENING_BRACKET to "(",
            CLOSING_BRACKET to ")",
            SQUARE_ROOT to "√",
            SEVEN to "7",
            EIGHT to "8",
            NINE to "9",
            DIVISION to "/",
            FOUR to "4",
            FIVE to "5",
            SIX to "6",
            MULTIPLICATION to "*",
            ONE to "1",
            TWO to "2",
            THREE to "3",
            SUBTRACTION to "-",
            ZERO to "0",
            DECIMAL_POINT to ".",
            VARIABLE to "x",
            ADDITION to "+",
            CLEAR to "CLEAR",
            DELETE to "DELETE"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_calc)

        setupInputFields()
        setupKeypads()

        drawChartButton.setOnClickListener { drawChart() }
        stub.setOnClickListener {}
    }

    private fun setupInputFields() {
        listOf(expressionInput, lowerBound, upperBound).forEach {
            it.setRawInputType(InputType.TYPE_CLASS_TEXT)
            it.setTextIsSelectable(true)

        }
    }

    private fun setupKeypads() {
        for (row in 0 until 6) {
            val keyRow = TableRow(this)
            val keypadColumns = if (row == 5) 2 else 4
            for (column in 0 until keypadColumns) {
                val position = if (row == 5) 20 + column else keypadColumns * row + column
                val key = Button(this).apply {
                    text = symbols[position]
                    textSize = 16f
                    setOnClickListener { handleKeyClick(position) }
                }
                keyRow.addView(key, displayWidth() / keypadColumns, WRAP_CONTENT)
            }
            if (row == 5) editKeypad.addView(keyRow)
            else keypad.addView(keyRow)
        }
    }

    private fun handleKeyClick(keyPosition: Int) {
        val inputField = when {
            expressionInput.hasFocus() -> expressionInput
            lowerBound.hasFocus() -> lowerBound
            upperBound.hasFocus() -> upperBound
            else -> return
        }
        val cursorPos = inputField.selectionStart
        val symbol = symbols[keyPosition]
        val symbolBefore =
            if (cursorPos == 0) null
            else inputField.text[cursorPos - 1].toString()

        fun insertSymbol() = inputField.text.insert(cursorPos, symbol)

        when (keyPosition) {
            CLEAR -> inputField.text.clear()
            DELETE -> {
                if (cursorPos != 0) inputField.text.delete(cursorPos - 1, cursorPos)
            }
            ADDITION, SUBTRACTION -> {
                if (cursorPos != 0) {
                    if (symbolBefore != symbols[ADDITION] && symbolBefore != symbols[SUBTRACTION])
                        insertSymbol()
                } else insertSymbol()
            }
            EXPONENTIATION, MULTIPLICATION, DIVISION, DECIMAL_POINT -> {
                if (cursorPos != 0 &&
                    (symbolBefore!!.isDigitsOnly() || symbolBefore == symbols[VARIABLE])
                ) insertSymbol()
            }
            VARIABLE -> {
                if (cursorPos != 0) {
                    if (symbolBefore != symbols[VARIABLE]) insertSymbol()
                } else insertSymbol()
            }
            else -> insertSymbol()
        }
    }

    private fun drawChart() {
        val expression = expressionInput.text.toString()
        if (expression.isEmpty()) toast("Введите выражение")
        else {
            val lowerBoundText = lowerBound.text.toString()
            val upperBoundText = upperBound.text.toString()
            if (lowerBoundText.isNotEmpty() && upperBoundText.isNotEmpty()) {
                val lowerX = lowerBoundText.toLong()
                val upperX = upperBoundText.toLong()
                if (lowerX >= upperX) {
                    toast("Нижняя граница должна быть меньше верхней")
                } else {
                    showProgress(true)
                    if (calcModule.selectedItemId == 0L) { // local module selected
                        local(expression, lowerX, upperX)
                    } else { // wolfram module selected
                        wolfram(expression, lowerX, upperX)
                    }
                }
            } else {
                toast("Введите диапазон Х")
            }
        }
    }

    private fun local(expression: String, lowerX: Long, upperX: Long) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val points = ArrayList<Float>()
                var lowerY = 0f
                var upperY = 0f
                withContext(Dispatchers.IO) {
                    for (x in lowerX..upperX) {
                        val exprToSolve = expression.replace(
                            symbols.getValue(VARIABLE),
                            x.toString()
                        )
                        points.add(x.toFloat()) // x
                        points.add( // y
                            exprToSolve.solve().toFloat().also {
                                if (it < lowerY) lowerY = it
                                if (it > upperY) upperY = it
                            }
                        )
                    }
                }
                ChartActivity.start(
                    from = this@CalcActivity,
                    points = points.toFloatArray(),
                    lowerX = lowerX, upperX = upperX,
                    lowerY = lowerY, upperY = upperY
                )
            } catch (e: InvalidBracketsException) {
                toast("Проверьте скобки!")
            } catch (e: Exception) {
                toast("Ошибка!")
            } finally {
                showProgress(false)
            }
        }
    }

    private fun wolfram(expression: String, lowerX: Long, upperX: Long) {
        ServiceProvider.service.getChart(
            input = "plot+$expression+from+$lowerX+to+$upperX"
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                showProgress(false)
                if (response.isSuccessful) {
                    val chartImageUrl = response.body()
                        ?.queryresult
                        ?.pods?.get(1)
                        ?.subpods?.get(0)
                        ?.img
                        ?.src
                    if (chartImageUrl != null) {
                        WolframChartActivity.start(this@CalcActivity, chartImageUrl)
                    } else {
                        toast("Ошибка")
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                showProgress(false)
                toast(t.message)
            }
        })
    }

    private fun showProgress(show: Boolean) {
        stub.visibility = if (show) View.VISIBLE else View.GONE
        progress.visibility = if (show) View.VISIBLE else View.GONE
        drawChartButton.visibility = if (show) View.INVISIBLE else View.VISIBLE
    }
}