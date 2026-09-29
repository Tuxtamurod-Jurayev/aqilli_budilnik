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

    /**
     * Generates a list of unique mathematical questions.
     * Guaranteed to return exactly [count] questions (default 3).
     */
    fun generateQuestions(difficulty: Difficulty, count: Int = 3): List<MathQuestion> {
        val questions = mutableListOf<MathQuestion>()
        val expressions = mutableSetOf<String>()
        var attempts = 0

        while (questions.size < count && attempts < 100) {
            attempts++
            val q = generate(difficulty)
            if (!expressions.contains(q.expression)) {
                expressions.add(q.expression)
                questions.add(q)
            }
        }

        // Fallback if needed to guarantee exact count
        while (questions.size < count) {
            questions.add(generate(difficulty))
        }

        return questions
    }

    private fun generateEasy(): MathQuestion {
        val pattern = Random.nextInt(0, 16)
        val (expr, ans) = when (pattern) {
            0 -> {
                // a + b
                val a = Random.nextInt(3, 30)
                val b = Random.nextInt(2, 30)
                "$a + $b" to (a + b)
            }
            1 -> {
                // a - b
                val a = Random.nextInt(12, 40)
                val b = Random.nextInt(2, a)
                "$a - $b" to (a - b)
            }
            2 -> {
                // a × b
                val a = Random.nextInt(2, 10)
                val b = Random.nextInt(2, 10)
                "$a × $b" to (a * b)
            }
            3 -> {
                // a ÷ b
                val b = Random.nextInt(2, 9)
                val res = Random.nextInt(2, 10)
                val a = b * res
                "$a ÷ $b" to res
            }
            4 -> {
                // a + b + c
                val a = Random.nextInt(2, 15)
                val b = Random.nextInt(2, 15)
                val c = Random.nextInt(2, 15)
                "$a + $b + $c" to (a + b + c)
            }
            5 -> {
                // a + b - c
                val a = Random.nextInt(6, 20)
                val b = Random.nextInt(5, 20)
                val c = Random.nextInt(2, a + b)
                "$a + $b - $c" to (a + b - c)
            }
            6 -> {
                // a × b + c
                val a = Random.nextInt(2, 7)
                val b = Random.nextInt(2, 7)
                val c = Random.nextInt(2, 20)
                "$a × $b + $c" to (a * b + c)
            }
            7 -> {
                // a - b + c
                val a = Random.nextInt(15, 30)
                val b = Random.nextInt(3, a)
                val c = Random.nextInt(2, 20)
                "$a - $b + $c" to (a - b + c)
            }
            8 -> {
                // a × b - c
                val a = Random.nextInt(3, 8)
                val b = Random.nextInt(2, 6)
                val p = a * b
                val c = Random.nextInt(1, p)
                "$a × $b - $c" to (p - c)
            }
            9 -> {
                // a + b × c
                val a = Random.nextInt(3, 20)
                val b = Random.nextInt(2, 6)
                val c = Random.nextInt(2, 6)
                "$a + $b × $c" to (a + b * c)
            }
            10 -> {
                // a - b - c
                val a = Random.nextInt(20, 45)
                val b = Random.nextInt(3, 15)
                val rem = a - b
                val c = Random.nextInt(2, rem)
                "$a - $b - $c" to (a - b - c)
            }
            11 -> {
                // a + b + c + d
                val a = Random.nextInt(2, 10)
                val b = Random.nextInt(2, 10)
                val c = Random.nextInt(2, 10)
                val d = Random.nextInt(2, 10)
                "$a + $b + $c + $d" to (a + b + c + d)
            }
            12 -> {
                // a × 10 + b
                val a = Random.nextInt(2, 9)
                val b = Random.nextInt(1, 10)
                "$a × 10 + $b" to (a * 10 + b)
            }
            13 -> {
                // a ÷ b + c
                val b = Random.nextInt(2, 6)
                val q = Random.nextInt(2, 8)
                val a = b * q
                val c = Random.nextInt(3, 15)
                "$a ÷ $b + $c" to (q + c)
            }
            14 -> {
                // (a + b) × c
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 6)
                val c = Random.nextInt(2, 5)
                "($a + $b) × $c" to ((a + b) * c)
            }
            else -> {
                // (a - b) × c
                val a = Random.nextInt(6, 12)
                val b = Random.nextInt(2, a)
                val c = Random.nextInt(2, 5)
                "($a - $b) × $c" to ((a - b) * c)
            }
        }
        return MathQuestion(expr, ans, Difficulty.EASY)
    }

    private fun generateMedium(): MathQuestion {
        val pattern = Random.nextInt(0, 20)
        val (expr, ans) = when (pattern) {
            0 -> {
                // a ÷ b + c
                val b = Random.nextInt(2, 9)
                val quot = Random.nextInt(3, 12)
                val a = b * quot
                val c = Random.nextInt(4, 25)
                "$a ÷ $b + $c" to (quot + c)
            }
            1 -> {
                // a × b - c
                val a = Random.nextInt(3, 10)
                val b = Random.nextInt(3, 9)
                val prod = a * b
                val c = Random.nextInt(3, prod)
                "$a × $b - $c" to (prod - c)
            }
            2 -> {
                // a + b × c (precedence)
                val b = Random.nextInt(3, 9)
                val c = Random.nextInt(3, 9)
                val a = Random.nextInt(5, 30)
                "$a + $b × $c" to (a + b * c)
            }
            3 -> {
                // a × b + c
                val a = Random.nextInt(4, 11)
                val b = Random.nextInt(3, 9)
                val c = Random.nextInt(5, 30)
                "$a × $b + $c" to (a * b + c)
            }
            4 -> {
                // a - b + c - d
                val a = Random.nextInt(20, 50)
                val b = Random.nextInt(5, 18)
                val c = Random.nextInt(5, 20)
                val d = Random.nextInt(2, 15)
                "$a - $b + $c - $d" to (a - b + c - d)
            }
            5 -> {
                // a ÷ b × c
                val b = Random.nextInt(2, 8)
                val quot = Random.nextInt(2, 9)
                val a = b * quot
                val c = Random.nextInt(2, 8)
                "$a ÷ $b × $c" to (quot * c)
            }
            6 -> {
                // a - b ÷ c
                val c = Random.nextInt(2, 8)
                val quot = Random.nextInt(2, 10)
                val b = c * quot
                val a = Random.nextInt(quot + 5, quot + 35)
                "$a - $b ÷ $c" to (a - quot)
            }
            7 -> {
                // a + b - c + d
                val a = Random.nextInt(12, 35)
                val b = Random.nextInt(8, 28)
                val c = Random.nextInt(3, a + b)
                val d = Random.nextInt(4, 20)
                "$a + $b - $c + $d" to (a + b - c + d)
            }
            8 -> {
                // a × b - c × d
                val a = Random.nextInt(4, 9)
                val b = Random.nextInt(4, 8)
                val c = Random.nextInt(2, 5)
                val d = Random.nextInt(2, 5)
                val prod1 = a * b
                val prod2 = c * d
                val (first, second) = if (prod1 >= prod2) (prod1 to prod2) else (prod2 to prod1)
                val (ea, eb, ec, ed) = if (prod1 >= prod2) listOf(a, b, c, d) else listOf(c, d, a, b)
                "$ea × $eb - $ec × $ed" to (first - second)
            }
            9 -> {
                // a ÷ b + c ÷ d
                val b = Random.nextInt(2, 7)
                val q1 = Random.nextInt(2, 9)
                val a = b * q1
                val d = Random.nextInt(2, 7)
                val q2 = Random.nextInt(2, 9)
                val c = d * q2
                "$a ÷ $b + $c ÷ $d" to (q1 + q2)
            }
            10 -> {
                // (a + b) ÷ c
                val c = Random.nextInt(2, 6)
                val q = Random.nextInt(3, 10)
                val sum = c * q
                val a = Random.nextInt(2, sum)
                val b = sum - a
                "($a + $b) ÷ $c" to q
            }
            11 -> {
                // (a - b) × c
                val b = Random.nextInt(3, 12)
                val diff = Random.nextInt(2, 8)
                val a = b + diff
                val c = Random.nextInt(3, 7)
                "($a - $b) × $c" to (diff * c)
            }
            12 -> {
                // a × (b + c)
                val a = Random.nextInt(3, 7)
                val b = Random.nextInt(2, 6)
                val c = Random.nextInt(2, 6)
                "$a × ($b + $c)" to (a * (b + c))
            }
            13 -> {
                // a × b + c × d
                val a = Random.nextInt(3, 8)
                val b = Random.nextInt(3, 7)
                val c = Random.nextInt(2, 6)
                val d = Random.nextInt(2, 6)
                "$a × $b + $c × $d" to (a * b + c * d)
            }
            14 -> {
                // a + b × c - d
                val b = Random.nextInt(3, 8)
                val c = Random.nextInt(3, 8)
                val mult = b * c
                val a = Random.nextInt(5, 25)
                val d = Random.nextInt(2, a + mult)
                "$a + $b × $c - $d" to (a + mult - d)
            }
            15 -> {
                // a - b × c + d
                val b = Random.nextInt(2, 6)
                val c = Random.nextInt(2, 6)
                val mult = b * c
                val a = Random.nextInt(mult + 5, mult + 30)
                val d = Random.nextInt(3, 15)
                "$a - $b × $c + $d" to (a - mult + d)
            }
            16 -> {
                // a × b ÷ c
                val c = Random.nextInt(2, 6)
                val quot = Random.nextInt(2, 8)
                val b = Random.nextInt(2, 5)
                val a = c * quot
                "$a × $b ÷ $c" to (quot * b)
            }
            17 -> {
                // (a + b + c) ÷ d
                val d = Random.nextInt(2, 6)
                val q = Random.nextInt(3, 9)
                val total = d * q
                val a = Random.nextInt(1, total - 2)
                val rem = total - a
                val b = Random.nextInt(1, rem)
                val c = rem - b
                "($a + $b + $c) ÷ $d" to q
            }
            18 -> {
                // a × b - c + d
                val a = Random.nextInt(4, 9)
                val b = Random.nextInt(3, 7)
                val p = a * b
                val c = Random.nextInt(3, p)
                val d = Random.nextInt(2, 15)
                "$a × $b - $c + $d" to (p - c + d)
            }
            else -> {
                // a ÷ b + c - d
                val b = Random.nextInt(2, 8)
                val q = Random.nextInt(3, 10)
                val a = b * q
                val c = Random.nextInt(5, 20)
                val d = Random.nextInt(2, q + c)
                "$a ÷ $b + $c - $d" to (q + c - d)
            }
        }
        return MathQuestion(expr, ans, Difficulty.MEDIUM)
    }

    private fun generateHard(): MathQuestion {
        val pattern = Random.nextInt(0, 20)
        val (expr, ans) = when (pattern) {
            0 -> {
                // a + b × c - d
                val a = Random.nextInt(15, 50)
                val b = Random.nextInt(4, 12)
                val c = Random.nextInt(3, 9)
                val mult = b * c
                val d = Random.nextInt(3, a + mult)
                "$a + $b × $c - $d" to (a + mult - d)
            }
            1 -> {
                // a × b - c × d
                val a = Random.nextInt(7, 15)
                val b = Random.nextInt(4, 11)
                val c = Random.nextInt(3, 8)
                val d = Random.nextInt(3, 7)
                val p1 = a * b
                val p2 = c * d
                val (f, s) = if (p1 >= p2) (p1 to p2) else (p2 to p1)
                val (fa, fb, fc, fd) = if (p1 >= p2) listOf(a, b, c, d) else listOf(c, d, a, b)
                "$fa × $fb - $fc × $fd" to (f - s)
            }
            2 -> {
                // a ÷ b + c × d - e
                val b = Random.nextInt(3, 9)
                val q = Random.nextInt(4, 14)
                val a = b * q
                val c = Random.nextInt(4, 10)
                val d = Random.nextInt(3, 8)
                val mult = c * d
                val e = Random.nextInt(2, q + mult)
                "$a ÷ $b + $c × $d - $e" to (q + mult - e)
            }
            3 -> {
                // a × b + c ÷ d
                val a = Random.nextInt(6, 16)
                val b = Random.nextInt(4, 9)
                val d = Random.nextInt(2, 8)
                val q = Random.nextInt(3, 12)
                val c = d * q
                "$a × $b + $c ÷ $d" to (a * b + q)
            }
            4 -> {
                // a × b × c (multiplication of 3 numbers)
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(2, 6)
                val c = Random.nextInt(3, 8)
                "$a × $b × $c" to (a * b * c)
            }
            5 -> {
                // a × b - c ÷ d
                val a = Random.nextInt(6, 14)
                val b = Random.nextInt(4, 8)
                val d = Random.nextInt(2, 8)
                val q = Random.nextInt(2, 9)
                val c = d * q
                val p = a * b
                "$a × $b - $c ÷ $d" to (p - q)
            }
            6 -> {
                // a + b × c + d × e
                val a = Random.nextInt(10, 30)
                val b = Random.nextInt(3, 8)
                val c = Random.nextInt(2, 6)
                val d = Random.nextInt(3, 7)
                val e = Random.nextInt(2, 6)
                "$a + $b × $c + $d × $e" to (a + b * c + d * e)
            }
            7 -> {
                // a × b + c - d × e
                val a = Random.nextInt(8, 14)
                val b = Random.nextInt(4, 8)
                val c = Random.nextInt(10, 30)
                val d = Random.nextInt(3, 7)
                val e = Random.nextInt(2, 6)
                val p1 = a * b
                val p2 = d * e
                "$a × $b + $c - $d × $e" to (p1 + c - p2)
            }
            8 -> {
                // a ÷ b × c + d
                val b = Random.nextInt(3, 8)
                val q = Random.nextInt(3, 9)
                val a = b * q
                val c = Random.nextInt(4, 9)
                val d = Random.nextInt(10, 40)
                "$a ÷ $b × $c + $d" to (q * c + d)
            }
            9 -> {
                // a × b - c + d × e
                val a = Random.nextInt(7, 13)
                val b = Random.nextInt(4, 7)
                val c = Random.nextInt(5, 25)
                val d = Random.nextInt(4, 9)
                val e = Random.nextInt(2, 6)
                val p1 = a * b
                val p2 = d * e
                "$a × $b - $c + $d × $e" to (p1 - c + p2)
            }
            10 -> {
                // (a + b) × (c - d)
                val a = Random.nextInt(5, 20)
                val b = Random.nextInt(5, 20)
                val d = Random.nextInt(2, 8)
                val c = d + Random.nextInt(2, 6)
                "($a + $b) × ($c - $d)" to ((a + b) * (c - d))
            }
            11 -> {
                // a × b + c × d - e
                val a = Random.nextInt(4, 10)
                val b = Random.nextInt(4, 9)
                val c = Random.nextInt(3, 8)
                val d = Random.nextInt(3, 7)
                val total = a * b + c * d
                val e = Random.nextInt(5, total - 5)
                "$a × $b + $c × $d - $e" to (total - e)
            }
            12 -> {
                // a × (b + c) - d × e
                val a = Random.nextInt(4, 9)
                val b = Random.nextInt(3, 8)
                val c = Random.nextInt(2, 6)
                val d = Random.nextInt(2, 6)
                val e = Random.nextInt(2, 5)
                val term1 = a * (b + c)
                val term2 = d * e
                "$a × ($b + $c) - $d × $e" to (term1 - term2)
            }
            13 -> {
                // (a - b) × (c + d)
                val b = Random.nextInt(3, 10)
                val a = b + Random.nextInt(3, 9)
                val c = Random.nextInt(3, 8)
                val d = Random.nextInt(2, 7)
                "($a - $b) × ($c + $d)" to ((a - b) * (c + d))
            }
            14 -> {
                // a × b × c - d
                val a = Random.nextInt(3, 6)
                val b = Random.nextInt(2, 5)
                val c = Random.nextInt(2, 5)
                val prod = a * b * c
                val d = Random.nextInt(5, prod)
                "$a × $b × $c - $d" to (prod - d)
            }
            15 -> {
                // (a + b + c) × d - e
                val a = Random.nextInt(2, 8)
                val b = Random.nextInt(2, 8)
                val c = Random.nextInt(2, 8)
                val d = Random.nextInt(2, 6)
                val sum = (a + b + c) * d
                val e = Random.nextInt(3, sum)
                "($a + $b + $c) × $d - $e" to (sum - e)
            }
            16 -> {
                // a × (b - c) + d × e
                val c = Random.nextInt(2, 8)
                val b = c + Random.nextInt(2, 7)
                val a = Random.nextInt(3, 8)
                val d = Random.nextInt(3, 7)
                val e = Random.nextInt(2, 6)
                "$a × ($b - $c) + $d × $e" to (a * (b - c) + d * e)
            }
            17 -> {
                // a ÷ b + c × d + e × f
                val b = Random.nextInt(2, 7)
                val q = Random.nextInt(2, 8)
                val a = b * q
                val c = Random.nextInt(2, 6)
                val d = Random.nextInt(2, 5)
                val e = Random.nextInt(2, 6)
                val f = Random.nextInt(2, 5)
                "$a ÷ $b + $c × $d + $e × $f" to (q + c * d + e * f)
            }
            18 -> {
                // (a × b - c) ÷ d + e
                val d = Random.nextInt(2, 6)
                val quot = Random.nextInt(2, 8)
                val target = d * quot
                val c = Random.nextInt(2, 10)
                val prod = target + c
                // find factors for prod if possible, else direct
                val a = 5
                val b = (prod + 4) / a
                val actualProd = a * b
                val actualC = actualProd - target
                val e = Random.nextInt(4, 20)
                "($a × $b - $actualC) ÷ $d + $e" to (quot + e)
            }
            else -> {
                // a × b + (c - d) × e
                val a = Random.nextInt(4, 10)
                val b = Random.nextInt(3, 8)
                val d = Random.nextInt(2, 7)
                val c = d + Random.nextInt(2, 6)
                val e = Random.nextInt(2, 6)
                "$a × $b + ($c - $d) × $e" to (a * b + (c - d) * e)
            }
        }
        return MathQuestion(expr, ans, Difficulty.HARD)
    }
}
