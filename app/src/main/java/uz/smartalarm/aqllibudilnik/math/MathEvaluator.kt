package uz.smartalarm.aqllibudilnik.math

import java.util.Stack

/**
 * Standard arithmetic expression evaluator respecting precedence for +, -, ×, ÷
 */
object MathEvaluator {

    fun evaluate(expression: String): Int {
        // Standardize symbols
        val sanitized = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace(" ", "")

        val tokens = tokenize(sanitized)
        return evaluateTokens(tokens)
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() -> {
                    val sb = StringBuilder()
                    while (i < expr.length && expr[i].isDigit()) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                    continue
                }
                c == '+' || c == '-' || c == '*' || c == '/' -> {
                    // Check for unary minus at start or after another operator
                    if (c == '-' && (tokens.isEmpty() || tokens.last() in listOf("+", "-", "*", "/"))) {
                        val sb = StringBuilder("-")
                        i++
                        while (i < expr.length && expr[i].isDigit()) {
                            sb.append(expr[i])
                            i++
                        }
                        tokens.add(sb.toString())
                        continue
                    }
                    tokens.add(c.toString())
                }
            }
            i++
        }
        return tokens
    }

    private fun evaluateTokens(tokens: List<String>): Int {
        val numbers = Stack<Int>()
        val ops = Stack<String>()

        fun applyOp() {
            if (ops.isEmpty() || numbers.size < 2) return
            val op = ops.pop()
            val b = numbers.pop()
            val a = numbers.pop()
            val res = when (op) {
                "+" -> a + b
                "-" -> a - b
                "*" -> a * b
                "/" -> if (b != 0) a / b else 0
                else -> 0
            }
            numbers.push(res)
        }

        fun precedence(op: String): Int = when (op) {
            "+", "-" -> 1
            "*", "/" -> 2
            else -> 0
        }

        for (token in tokens) {
            val num = token.toIntOrNull()
            if (num != null) {
                numbers.push(num)
            } else {
                while (ops.isNotEmpty() && precedence(ops.peek()) >= precedence(token)) {
                    applyOp()
                }
                ops.push(token)
            }
        }

        while (ops.isNotEmpty()) {
            applyOp()
        }

        return if (numbers.isNotEmpty()) numbers.pop() else 0
    }
}
