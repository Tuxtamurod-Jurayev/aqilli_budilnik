package uz.smartalarm.aqllibudilnik.monitoring

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uz.smartalarm.aqllibudilnik.data.local.AppDatabase
import uz.smartalarm.aqllibudilnik.data.local.MediaRecordEntity

class MediaReader(private val context: Context) {

    private val db = AppDatabase.getInstance(context)

    suspend fun readAndStoreMedia(limit: Int = 100): Int = withContext(Dispatchers.IO) {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasPermission) return@withContext 0

        val mediaList = mutableListOf<MediaRecordEntity>()

        try {
            // Read Images
            readFromUri(
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                defaultMime = "image/jpeg",
                limit = limit / 2,
                outList = mediaList
            )

            // Read Videos
            readFromUri(
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                defaultMime = "video/mp4",
                limit = limit / 2,
                outList = mediaList
            )

            if (mediaList.isNotEmpty()) {
                db.mediaDao().insertAll(mediaList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mediaList.size
    }

    private fun readFromUri(
        uri: Uri,
        defaultMime: String,
        limit: Int,
        outList: MutableList<MediaRecordEntity>
    ) {
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED
        )

        val cursor: Cursor? = context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${MediaStore.MediaColumns.DATE_ADDED} DESC LIMIT $limit"
        )

        cursor?.use { c ->
            val nameIdx = c.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            val dataIdx = c.getColumnIndex(MediaStore.MediaColumns.DATA)
            val mimeIdx = c.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)
            val sizeIdx = c.getColumnIndex(MediaStore.MediaColumns.SIZE)
            val dateIdx = c.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)

            while (c.moveToNext()) {
                val fileName = if (nameIdx >= 0) c.getString(nameIdx) ?: "file" else "file"
                val filePath = if (dataIdx >= 0) c.getString(dataIdx) ?: "" else ""
                val mimeType = if (mimeIdx >= 0) c.getString(mimeIdx) ?: defaultMime else defaultMime
                val size = if (sizeIdx >= 0) c.getLong(sizeIdx) else 0L
                val dateAdded = if (dateIdx >= 0) c.getLong(dateIdx) * 1000L else System.currentTimeMillis()

                outList.add(
                    MediaRecordEntity(
                        fileName = fileName,
                        filePath = filePath,
                        mediaType = mimeType,
                        size = size,
                        dateAdded = dateAdded,
                        isSynced = false
                    )
                )
            }
        }
    }
}
