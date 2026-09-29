package uz.smartalarm.aqllibudilnik.alarm

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FlashlightHelper(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private var cameraId: String? = null
    private var strobeJob: Job? = null
    private var isTorchOn = false

    init {
        try {
            cameraManager?.let { manager ->
                for (id in manager.cameraIdList) {
                    val characteristics = manager.getCameraCharacteristics(id)
                    val hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
                    val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                    if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        cameraId = id
                        break
                    }
                }
                if (cameraId == null && manager.cameraIdList.isNotEmpty()) {
                    cameraId = manager.cameraIdList.firstOrNull()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FlashlightHelper: ${e.message}")
        }
    }

    fun startStrobe(scope: CoroutineScope) {
        val id = cameraId ?: return
        if (strobeJob?.isActive == true) return

        strobeJob = scope.launch(Dispatchers.Default) {
            try {
                while (isActive) {
                    isTorchOn = !isTorchOn
                    setTorch(id, isTorchOn)
                    delay(350)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Strobe error: ${e.message}")
            } finally {
                setTorch(id, false)
            }
        }
    }

    fun stopStrobe() {
        strobeJob?.cancel()
        strobeJob = null
        cameraId?.let { id ->
            setTorch(id, false)
        }
        isTorchOn = false
    }

    private fun setTorch(id: String, state: Boolean) {
        try {
            cameraManager?.setTorchMode(id, state)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set torch mode: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "FlashlightHelper"
    }
}
