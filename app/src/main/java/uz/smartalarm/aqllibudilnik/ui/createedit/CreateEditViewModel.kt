package uz.smartalarm.aqllibudilnik.ui.createedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.smartalarm.aqllibudilnik.AqlliBudilnikApp
import uz.smartalarm.aqllibudilnik.data.model.Alarm
import uz.smartalarm.aqllibudilnik.data.model.Difficulty
import uz.smartalarm.aqllibudilnik.data.model.WeekDay
import uz.smartalarm.aqllibudilnik.data.repository.AlarmRepository
import java.util.Calendar

data class CreateEditUiState(
    val id: Long = 0L,
    val isEditMode: Boolean = false,
    val hour: Int = 7,
    val minute: Int = 0,
    val label: String = "",
    val repeatDays: Set<WeekDay> = emptySet(),
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val isVibrationEnabled: Boolean = true,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false
)

class CreateEditViewModel(
    private val repository: AlarmRepository = AqlliBudilnikApp.instance.alarmRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEditUiState())
    val uiState: StateFlow<CreateEditUiState> = _uiState.asStateFlow()

    init {
        // Default time to next hour
        val cal = Calendar.getInstance().apply { add(Calendar.HOUR_OF_DAY, 1) }
        _uiState.update {
            it.copy(hour = cal.get(Calendar.HOUR_OF_DAY), minute = 0)
        }
    }

    fun loadAlarm(alarmId: Long) {
        if (alarmId <= 0) return
        viewModelScope.launch {
            val alarm = repository.getAlarmById(alarmId) ?: return@launch
            _uiState.update {
                it.copy(
                    id = alarm.id,
                    isEditMode = true,
                    hour = alarm.hour,
                    minute = alarm.minute,
                    label = alarm.label,
                    repeatDays = alarm.repeatDays,
                    difficulty = alarm.difficulty,
                    isVibrationEnabled = alarm.isVibrationEnabled
                )
            }
        }
    }

    fun setTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(hour = hour, minute = minute) }
    }

    fun setLabel(label: String) {
        _uiState.update { it.copy(label = label) }
    }

    fun toggleDay(day: WeekDay) {
        _uiState.update { state ->
            val updated = if (state.repeatDays.contains(day)) {
                state.repeatDays - day
            } else {
                state.repeatDays + day
            }
            state.copy(repeatDays = updated)
        }
    }

    fun setRepeatPreset(days: Set<WeekDay>) {
        _uiState.update { it.copy(repeatDays = days) }
    }

    fun setDifficulty(difficulty: Difficulty) {
        _uiState.update { it.copy(difficulty = difficulty) }
    }

    fun setVibration(enabled: Boolean) {
        _uiState.update { it.copy(isVibrationEnabled = enabled) }
    }

    fun saveAlarm() {
        viewModelScope.launch {
            val state = _uiState.value
            val alarm = Alarm(
                id = if (state.isEditMode) state.id else 0L,
                hour = state.hour,
                minute = state.minute,
                label = state.label.trim(),
                isEnabled = true,
                repeatDays = state.repeatDays,
                difficulty = state.difficulty,
                isVibrationEnabled = state.isVibrationEnabled
            )

            if (state.isEditMode) {
                repository.updateAlarm(alarm)
            } else {
                repository.saveAlarm(alarm)
            }
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    fun deleteAlarm() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.isEditMode && state.id > 0) {
                val alarm = repository.getAlarmById(state.id)
                if (alarm != null) {
                    repository.deleteAlarm(alarm)
                }
            }
            _uiState.update { it.copy(isDeleted = true) }
        }
    }
}
