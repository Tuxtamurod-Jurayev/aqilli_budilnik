package uz.smartalarm.aqllibudilnik.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import uz.smartalarm.aqllibudilnik.AqlliBudilnikApp
import uz.smartalarm.aqllibudilnik.data.model.Difficulty

data class SettingsUiState(
    val defaultDifficulty: Difficulty = Difficulty.MEDIUM,
    val defaultQuestionsCount: Int = 3,
    val defaultVibration: Boolean = true,
    val appTheme: String = "Tizim sozlamasi (System)"
)

class SettingsViewModel : ViewModel() {

    private val prefs = AqlliBudilnikApp.instance.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val diffName = prefs.getString("default_difficulty", Difficulty.MEDIUM.name) ?: Difficulty.MEDIUM.name
        val diff = try { Difficulty.valueOf(diffName) } catch (e: Exception) { Difficulty.MEDIUM }
        val vibration = prefs.getBoolean("default_vibration", true)

        _uiState.update {
            it.copy(
                defaultDifficulty = diff,
                defaultVibration = vibration
            )
        }
    }

    fun setDefaultDifficulty(difficulty: Difficulty) {
        prefs.edit().putString("default_difficulty", difficulty.name).apply()
        _uiState.update { it.copy(defaultDifficulty = difficulty) }
    }

    fun setDefaultVibration(enabled: Boolean) {
        prefs.edit().putBoolean("default_vibration", enabled).apply()
        _uiState.update { it.copy(defaultVibration = enabled) }
    }
}
