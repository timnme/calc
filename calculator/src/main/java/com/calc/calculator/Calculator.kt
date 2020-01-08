package com.calc.calculator

import kotlin.math.pow
import kotlin.math.sqrt

private const val EMPTY = ""
private const val OPENING_BRACKET = '('
private const val CLOSING_BRACKET = ')'
private const val POW = '^'
private const val SQRT = '√'
private const val TIMES = '*'
private const val DIV = '/'
private const val PLUS = '+'
private const val MINUS = '-'
private const val POINT = '.'
private const val E = 'E'

class InvalidBracketsException : Throwable()

fun String.solve(): Double = this
    .simplifyBrackets()
    .perform(mapOf(
        POW to { a, b -> a.pow(b) },
        SQRT to { a, b -> if (b >= 0) a * sqrt(b) else 0.0 }
    ))
    .perform(mapOf(
        TIMES to { a, b -> a * b },
        DIV to { a, b -> if (b != 0.0) a / b else 0.0 }
    ))
    .perform(mapOf(
        PLUS to { a, b -> a + b },
        MINUS to { a, b -> a - b }
    ))
    .toDouble()

internal fun String.simplifyBrackets(): String {
    var expression = this
    if (!expression.bracketsValid()) throw InvalidBracketsException()
    while (expression.any { it == OPENING_BRACKET || it == CLOSING_BRACKET }) {
        var openingBracketIndex = -1
        for ((index, char) in expression.withIndex()) {
            if (char == OPENING_BRACKET) openingBracketIndex = index
            else if (char == CLOSING_BRACKET) {
                expression = expression.replaceRange(
                    startIndex = openingBracketIndex,
                    endIndex = index + 1,
                    replacement = expression
                        .substring(openingBracketIndex + 1, index)
                        .solve()
                        .toString()
                )
                break
            }
        }
    }
    return expression
}

internal fun String.bracketsValid(): Boolean {
    var unclosed = 0
    forEach {
        if (it == OPENING_BRACKET) unclosed++
        if (it == CLOSING_BRACKET) {
            if (unclosed == 0) return false
            unclosed--
        }
    }
    return unclosed == 0
}

internal fun String.perform(operations: Map<Char, (Double, Double) -> Double>): String {
    val operators = operations.keys
    var expression = "0+$this"
    while (expression.any(operators::contains)) {
        for ((index, char) in expression.withIndex()) {
            if (operators.contains(char)) {
                if (index == 0) continue
                val (left, leftStart) = leftOperand(expression, index)
                val (right, rightEnd) = rightOperand(expression, index)
                val result: String = with(
                    operations.getValue(char).invoke(left, right)
                ) {
                    if (this >= 0) "+$this" else toString()
                }
                expression = expression.replaceRange(
                    startIndex = leftStart,
                    endIndex = rightEnd + 1,
                    replacement = result
                )
                break
            }
        }
        try {
            expression.toDouble()
            break
        } catch (e: NumberFormatException) {
        }
    }
    return expression
}

internal fun leftOperand(expression: String, operatorIndex: Int): Pair<Double, Int> {
    with(expression.substring(0, operatorIndex)) {
        var left = EMPTY
        var leftStart = -1
        for (index in this.length - 1 downTo 0) {
            val char = this[index]
            if (char.isNumPart()) {
                left += char
                leftStart = index
                if (char == MINUS || char == PLUS) break
            } else break
        }
        return if (left.isNotEmpty()) {
            when (left) {
                PLUS.toString() -> 1.0
                MINUS.toString() -> -1.0
                else -> left.reversed().toDouble()
            } to leftStart
        } else 1.0 to 0
    }
}

internal fun rightOperand(expression: String, operatorIndex: Int): Pair<Double, Int> {
    with(expression.substring(operatorIndex + 1, expression.length)) {
        var right = EMPTY
        var rightEnd = -1
        for (index in this.indices) {
            val char = this[index]
            if (char.isNumPart()) {
                if ((char == MINUS || char == PLUS) && index != 0) break
                right += char
                rightEnd = index
            } else break
        }
        return right.toDouble() to operatorIndex + 1 + rightEnd
    }
}

internal fun Char.isNumPart() = isDigit() ||
        this == PLUS || this == MINUS || this == POINT || this == E
