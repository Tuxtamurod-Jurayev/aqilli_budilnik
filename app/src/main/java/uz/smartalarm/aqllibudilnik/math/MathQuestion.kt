package uz.smartalarm.aqllibudilnik.math

import uz.smartalarm.aqllibudilnik.data.model.Difficulty

data class MathQuestion(
    val expression: String,
    val answer: Int,
    val difficulty: Difficulty
)
