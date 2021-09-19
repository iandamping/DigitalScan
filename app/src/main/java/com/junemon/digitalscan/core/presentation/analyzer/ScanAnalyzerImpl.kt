package com.junemon.digitalscan.core.presentation.analyzer

import androidx.camera.core.ImageAnalysis
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.junemon.digitalscan.core.presentation.listener.ScanListener
import java.nio.ByteBuffer
import javax.inject.Inject

/**
 * Created by Ian Damping on 28,May,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
class ScanAnalyzerImpl @Inject constructor(
    private val listener: ScanListener
) : ScanAnalyzer {

    private val reader = MultiFormatReader().apply {
        val map = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)
        )
        setHints(map)
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    override fun provideAnalyzer(): ImageAnalysis.Analyzer {
        return ImageAnalysis.Analyzer {
            val buffer = it.planes[0].buffer
            val data = buffer.toByteArray()

            /**we parse qrscan image here using zxing*/
            val source = PlanarYUVLuminanceSource(
                data,
                it.width,
                it.height,
                0,
                0,
                it.width,
                it.height,
                false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            // listener.setBinaryImage(binaryBitmap)

            try {
                // Whenever reader fails to detect a QR code in image
                // it throws NotFoundException
                val result = reader.decode(binaryBitmap)
                if (result != null) {
                    listener.setQrImage(result)
                }
            } catch (e: NotFoundException) {
                e.printStackTrace()
            }
            it.close()
        }
    }
}