package uz.smartalarm.aqllibudilnik.math

import uz.smartalarm.aqllibudilnik.data.model.Difficulty
import kotlin.random.Random

object MathQuestionGenerator {

    fun generate(difficulty: Difficulty): MathQuestion {
        return when (difficulty) {
            Difficulty.EASY -> generateEasy()
            Difficulty.MEDIUM -> generateMedium()
            Difficulty.HARD -> generateHard()
        }
    }

    fun generateQuestions(difficulty: Difficulty, count: Int = 3): List<MathQuestion> {
        val list = mutableListOf<MathQuestion>()
        repeat(count) {
            list.add(generate(difficulty))
        }
        return list
    }

    private fun generateEasy(): MathQuestion {
        val ops = listOf("+", "-", "×", "÷")
        val op = ops.random()
        val (expr, ans) = when (op) {
            "+" -> {
                val a = Random.nextInt(2, 20)
                val b = Random.nextInt(2, 20)
                "$a + $b" to (a + b)
            }
            "-" -> {
                val a = Random.nextInt(8, 25)
                val b = Random.nextInt(2, a) // non-negative result
                "$a - $b" to (a - b)
            }
            "×" -> {
                val a = Random.nextInt(2, 10)
                val b = Random.nextInt(2, 10)
                "$a × $b" to (a * b)
            }
            "÷" -> {
                val b = Random.nextInt(2, 9)
                val result = Random.nextInt(2, 10)
                val a = b * result
                "$a ÷ $b" to result
            }
            else -> "5 + 5" to 10
        }
        return MathQuestion(expr, ans, Difficulty.EASY)
    }

    private fun generateMedium(): MathQuestion {
        // Two operations: e.g. "15 ÷ 3 + 5", "8 × 3 - 7", "18 - 4 + 6"
        val pattern = Random.nextInt(0, 4)
        val (expr, ans) = when (pattern) {
            0 -> {
                // Div + Add: a ÷ b + c
                val b = Random.nextInt(2, 8)
                val quotient = Random.nextInt(2, 10)
                val a = b * quotient
                val c = Random.nextInt(3, 20)
                "$a ÷ $b + $c" to (quotient + c)
            }
            1 -> {
                // Mult - Sub: a × b - c
                val a = Random.nextInt(3, 9)
                val b = Random.nextInt(2, 8)
                val product = a * b
                val c = Random.nextInt(2, product)
                "$a × $b - $c" to (product - c)
            }
            2 -> {
                // Add - Sub: a + b - c
                val a = Random.nextInt(10, 30)
                val b = Random.nextInt(5, 25)
                val c = Random.nextInt(2, a + b)
                "$a + $b - $c" to (a + b - c)
            }
            else -> {
                // Sub + Add: a - b + c
                val a = Random.nextInt(15, 35)
                val b = Random.nextInt(3, a)
                val c = Random.nextInt(4, 20)
                "$a - $b + $c" to (a - b + c)
            }
        }
        return MathQuestion(expr, ans, Difficulty.MEDIUM)
    }

    private fun generateHard(): MathQuestion {
        // Hard: e.g. "12 × 3 - 8", "24 ÷ 4 + 17", "15 + 8 × 2", or 3 ops
        val pattern = Random.nextInt(0, 4)
        val (expr, ans) = when (pattern) {
            0 -> {
                // a + b × c (multiplication precedence)
                val a = Random.nextInt(10, 35)
                val b = Random.nextInt(4, 12)
                val c = Random.nextInt(3, 9)
                "$a + $b × $c" to (a + b * c)
            }
            1 -> {
                // a × b - c (larger numbers)
                val a = Random.nextInt(11, 20)
                val b = Random.nextInt(3, 8)
                val c = Random.nextInt(5, 25)
                val product = a * b
                "$a × $b - $c" to (product - c)
            }
            2 -> {
                // a ÷ b + c × d
                val b = Random.nextInt(3, 9)
                val quot = Random.nextInt(4, 12)
                val a = b * quot
                val c = Random.nextInt(3, 8)
                val d = Random.nextInt(2, 6)
                "$a ÷ $b + $c × $d" to (quot + c * d)
            }
            else -> {
                // a × b + c ÷ d
                val a = Random.nextInt(5, 14)
                val b = Random.nextInt(3, 7)
                val d = Random.nextInt(2, 8)
                val quot = Random.nextInt(3, 10)
                val c = d * quot
                "$a × $b + $c ÷ $d" to (a * b + quot)
            }
        }
        return MathQuestion(expr, ans, Difficulty.HARD)
    }
}
