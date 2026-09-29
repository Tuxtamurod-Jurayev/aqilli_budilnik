package uz.smartalarm.aqllibudilnik.ui.ringing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.smartalarm.aqllibudilnik.AqlliBudilnikApp
import uz.smartalarm.aqllibudilnik.alarm.AlarmService
import uz.smartalarm.aqllibudilnik.data.model.Difficulty
import uz.smartalarm.aqllibudilnik.data.repository.AlarmRepository
import uz.smartalarm.aqllibudilnik.math.MathQuestion
import uz.smartalarm.aqllibudilnik.math.MathQuestionGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class RingingUiState(
    val alarmId: Long = -1L,
    val label: String = "",
    val currentTimeString: String = "",
    val currentStep: Int = 1,
    val totalSteps: Int = 3,
    val questions: List<MathQuestion> = emptyList(),
    val userAnswer: String = "",
    val feedbackMessage: String = "",
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val isCompleted: Boolean = false
)

class RingingViewModel(
    private val repository: AlarmRepository = AqlliBudilnikApp.instance.alarmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RingingUiState())
    val uiState: StateFlow<RingingUiState> = _uiState.asStateFlow()

    init {
        updateCurrentTime()
    }

    private fun updateCurrentTime() {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        _uiState.update { it.copy(currentTimeString = timeFormat.format(Date())) }
    }

    fun initAlarm(alarmId: Long, label: String, difficultyName: String) {
        if (_uiState.value.questions.isNotEmpty()) return // Already initialized

        val diff = try {
            Difficulty.valueOf(difficultyName)
        } catch (e: Exception) {
            Difficulty.MEDIUM
        }

        val generated = MathQuestionGenerator.generateQuestions(diff, 3)

        _uiState.update {
            it.copy(
                alarmId = alarmId,
                label = label,
                questions = generated,
                currentStep = 1,
                userAnswer = ""
            )
        }
    }

    fun onAnswerChange(input: String) {
        // Accept only digits and leading minus
        if (input.isEmpty() || input == "-" || input.matches(Regex("^-?\\d+$"))) {
            _uiState.update { it.copy(userAnswer = input, isError = false, feedbackMessage = "") }
        }
    }

    fun appendDigit(digit: String) {
        val current = _uiState.value.userAnswer
        if (current.length < 6) {
            onAnswerChange(current + digit)
        }
    }

    fun backspace() {
        val current = _uiState.value.userAnswer
        if (current.isNotEmpty()) {
            onAnswerChange(current.dropLast(1))
        }
    }

    fun checkAnswer(context: Context) {
        val state = _uiState.value
        val input = state.userAnswer.trim()

        if (input.isBlank() || input == "-") {
            _uiState.update {
                it.copy(
                    isError = true,
                    feedbackMessage = "Javobni kiriting!"
                )
            }
            return
        }

        val parsedAnswer = input.toIntOrNull()
        val currentQ = state.questions.getOrNull(state.currentStep - 1) ?: return

        if (parsedAnswer == currentQ.answer) {
            // Correct answer
            if (state.currentStep >= state.totalSteps) {
                // All 3 answered correctly! Alarm stops!
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        isError = false,
                        feedbackMessage = "Ajoyib! Barcha misollar yechildi.",
                        isCompleted = true
                    )
                }

                // Stop Alarm Service (audio & vibration)
                AlarmService.stop(context)

                // Reschedule or update alarm in repository
                viewModelScope.launch {
                    if (state.alarmId > 0) {
                        repository.onAlarmFired(state.alarmId)
                    }
                }
            } else {
                // Move to next step
                _uiState.update {
                    it.copy(
                        isSuccess = true,
                        isError = false,
                        feedbackMessage = "To‘g‘ri! Keyingi misol..."
                    )
                }

                viewModelScope.launch {
                    delay(700)
                    _uiState.update {
                        it.copy(
                            currentStep = it.currentStep + 1,
                            userAnswer = "",
                            isSuccess = false,
                            feedbackMessage = ""
                        )
                    }
                }
            }
        } else {
            // Incorrect answer
            _uiState.update {
                it.copy(
                    isError = true,
                    isSuccess = false,
                    feedbackMessage = "Noto‘g‘ri javob! Qayta urinib ko‘ring."
                )
            }
        }
    }
}
