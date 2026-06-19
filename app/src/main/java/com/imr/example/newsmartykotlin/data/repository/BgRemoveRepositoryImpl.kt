package com.imr.example.newsmartykotlin.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.huawei.hmf.tasks.Task
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer
import com.imr.example.newsmartykotlin.domain.repository.BgRemoveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import androidx.core.net.toUri

class BgRemoveRepositoryImpl(
    private val context: Context,
    private val analyzer: MLImageSegmentationAnalyzer
) : BgRemoveRepository {

    override suspend fun removeBackground(
        imageUri: String
    ): String {
        return withContext(Dispatchers.IO) {
            val bitmap = context.contentResolver
                .openInputStream(imageUri.toUri())
                .use { input ->
                    BitmapFactory.decodeStream(input)
                } ?: throw IllegalStateException("Image decode failed")

            val segmentation = analyseImage(bitmap)

            val foregroundBitmap = segmentation.foreground
                ?: throw IllegalStateException("Foreground segmentation failed")

            val outputDir = File(context.cacheDir, "bg_removed")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }

            val outputFile = File(
                outputDir,
                "bg_removed_${System.currentTimeMillis()}.png"
            )

            FileOutputStream(outputFile).use { output ->
                foregroundBitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    output
                )
            }

            bitmap.recycle()

            Uri.fromFile(outputFile).toString()
        }
    }

    private suspend fun analyseImage(
        bitmap: Bitmap
    ): MLImageSegmentation {
        return suspendCancellableCoroutine { continuation ->

            val frame = MLFrame.fromBitmap(bitmap)

            val task: Task<MLImageSegmentation> =
                analyzer.asyncAnalyseFrame(frame)

            task.addOnSuccessListener { segmentation ->
                if (continuation.isActive) {
                    continuation.resume(segmentation)
                }
            }

            task.addOnFailureListener { exception ->
                Log.d("ErrorTesting", "analyseImage: bg remover error is ==== >>>>> ${exception.message}")
                if (continuation.isActive) {
                    continuation.resumeWithException(exception)
                }
            }
        }
    }
}