package uz.smartalarm.aqllibudilnik.math

import org.junit.Assert.assertEquals
import org.junit.Test

class MathEvaluatorTest {

    @Test
    fun testSimpleAdditionAndSubtraction() {
        assertEquals(15, MathEvaluator.evaluate("8 + 7"))
        assertEquals(18, MathEvaluator.evaluate("25 - 7"))
        assertEquals(0, MathEvaluator.evaluate("10 - 10"))
    }

    @Test
    fun testMultiplicationAndDivision() {
        assertEquals(28, MathEvaluator.evaluate("4 × 7"))
        assertEquals(28, MathEvaluator.evaluate("4 * 7"))
        assertEquals(6, MathEvaluator.evaluate("36 ÷ 6"))
        assertEquals(6, MathEvaluator.evaluate("36 / 6"))
    }

    @Test
    fun testOperatorPrecedence() {
        // Multiplication before addition: 2 + (3 * 4) = 14
        assertEquals(14, MathEvaluator.evaluate("2 + 3 × 4"))
        // Division before subtraction: 20 - (12 / 3) = 16
        assertEquals(16, MathEvaluator.evaluate("20 - 12 ÷ 3"))
        // Combined precedence: (36 / 6) + 5 = 11
        assertEquals(11, MathEvaluator.evaluate("36 ÷ 6 + 5"))
        // Mixed: 10 + 2 * 6 - 4 = 10 + 12 - 4 = 18
        assertEquals(18, MathEvaluator.evaluate("10 + 2 × 6 - 4"))
    }

    @Test
    fun testUnaryNegativeNumbers() {
        assertEquals(-5, MathEvaluator.evaluate("-5"))
        assertEquals(5, MathEvaluator.evaluate("10 + -5"))
    }

    @Test
    fun testDivisionByZeroHandledSafely() {
        assertEquals(0, MathEvaluator.evaluate("10 ÷ 0"))
    }

    @Test
    fun testParenthesesExpressions() {
        assertEquals(48, MathEvaluator.evaluate("6 × (4 + 4)"))
        assertEquals(45, MathEvaluator.evaluate("(2 + 3) × (4 + 5)"))
        assertEquals(18, MathEvaluator.evaluate("(5 × 4 - 2) ÷ 1"))
        assertEquals(24, MathEvaluator.evaluate("2 × (3 + (4 + 5))"))
    }
}
