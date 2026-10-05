package uz.smartalarm.aqllibudilnik.math

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.smartalarm.aqllibudilnik.data.model.Difficulty

class MathQuestionGeneratorTest {

    @Test
    fun testGenerateQuestionsReturnsExactCount() {
        val questionsEasy = MathQuestionGenerator.generateQuestions(Difficulty.EASY, 3)
        assertEquals(3, questionsEasy.size)

        val questionsMedium = MathQuestionGenerator.generateQuestions(Difficulty.MEDIUM, 3)
        assertEquals(3, questionsMedium.size)

        val questionsHard = MathQuestionGenerator.generateQuestions(Difficulty.HARD, 5)
        assertEquals(5, questionsHard.size)
    }

    @Test
    fun testGeneratedQuestionsHaveNonEmptyExpressions() {
        val questions = MathQuestionGenerator.generateQuestions(Difficulty.MEDIUM, 10)
        for (q in questions) {
            assertTrue(q.expression.isNotBlank())
            assertNotNull(q.answer)
        }
    }

    @Test
    fun testEvaluationMatchesGeneratedAnswer() {
        // Test 20 generated questions across all difficulties to ensure calculated answer matches MathEvaluator
        for (diff in Difficulty.entries) {
            val questions = MathQuestionGenerator.generateQuestions(diff, 10)
            for (q in questions) {
                val evaluated = MathEvaluator.evaluate(q.expression)
                assertEquals("Expression ${q.expression} should evaluate to ${q.answer}", q.answer, evaluated)
            }
        }
    }
}
