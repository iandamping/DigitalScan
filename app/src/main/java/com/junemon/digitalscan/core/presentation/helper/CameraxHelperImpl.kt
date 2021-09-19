package com.junemon.digitalscan.core.presentation.helper

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.SurfaceHolder
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.MeteringPointFactory
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.junemon.digitalscan.core.presentation.afterMeasured
import com.junemon.digitalscan.core.presentation.analyzer.ScanAnalyzer
import com.junemon.digitalscan.di.qualifier.LensFacingBack
import com.junemon.digitalscan.di.qualifier.LensFacingFront
import timber.log.Timber
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Ian Damping on 29,May,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
class CameraxHelperImpl @Inject constructor(
    private val cameraExecutor: ExecutorService,
    private val cameraMainExecutor: Executor,
    private val cameraFuture: ListenableFuture<ProcessCameraProvider>,
    private val cameraProcessProvider: ProcessCameraProvider,
    @LensFacingBack private val backCameraSelector: CameraSelector,
    @LensFacingFront private val frontCameraSelector: CameraSelector,
    private val imageAnalyzer: ScanAnalyzer,
) : CameraxHelper {

    override fun startCameraForScan(
        lifecycleOwner: LifecycleOwner,
        cameraSelector: CameraSelector,
        preview: Preview,
        camera: (Camera) -> Unit
    ) {
            cameraFuture.addListener({
                try {
                    // Unbind use cases before rebinding
                    unbindCamera()
                    // Bind use cases to camera
                    camera(
                        cameraProcessProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, provideImageAnalysis()
                        )
                    )
                } catch (exc: Exception) {
                    Timber.e("Use case binding failed : $exc")
                }

            }, cameraMainExecutor)
    }

    override fun providePreview(view: PreviewView, aspectRatio: Int): Preview {
        return Preview.Builder()
            .setTargetAspectRatio(aspectRatio)
            .build()
            .also {
                it.setSurfaceProvider(view.surfaceProvider)
            }
    }

    override fun providePreview(view: PreviewView, aspectRatio: Int, rotation: Int): Preview {
        return Preview.Builder()
            .setTargetAspectRatio(aspectRatio)
            .setTargetRotation(rotation)
            .build().also {
                it.setSurfaceProvider(view.surfaceProvider)
            }
    }

    override fun provideAspectRatio(view: PreviewView): Int {
        val metrics = DisplayMetrics().also { view.display.getRealMetrics(it) }
        return aspectRatio(metrics.widthPixels, metrics.heightPixels)
    }

    override fun provideRotation(view: PreviewView): Int {
        return view.display.rotation
    }

    override fun drawOverlay(holder: SurfaceHolder, heightCropPercent: Int, widthCropPercent: Int) {
        val canvas = holder.lockCanvas()
        val bgPaint = Paint().apply {
            alpha = 140
        }
        canvas.drawPaint(bgPaint)
        val rectPaint = Paint()
        rectPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        rectPaint.style = Paint.Style.FILL
        rectPaint.color = Color.WHITE
        val outlinePaint = Paint()
        outlinePaint.style = Paint.Style.STROKE
        outlinePaint.color = Color.WHITE
        outlinePaint.strokeWidth = 4f
        val surfaceWidth = holder.surfaceFrame.width()
        val surfaceHeight = holder.surfaceFrame.height()

        val cornerRadius = 25f
        // Set rect centered in frame
        val rectTop = surfaceHeight * heightCropPercent / 2 / 100f
        val rectLeft = surfaceWidth * widthCropPercent / 2 / 100f
        val rectRight = surfaceWidth * (1 - widthCropPercent / 2 / 100f)
        val rectBottom = surfaceHeight * (1 - heightCropPercent / 2 / 100f)
        val rect = RectF(rectLeft, rectTop, rectRight, rectBottom)
        canvas.drawRoundRect(
            rect, cornerRadius, cornerRadius, rectPaint
        )
        canvas.drawRoundRect(
            rect, cornerRadius, cornerRadius, outlinePaint
        )
        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 50F

        val overlayText = "Scan QR in box"
        val textBounds = Rect()
        textPaint.getTextBounds(overlayText, 0, overlayText.length, textBounds)
        val textX = (surfaceWidth - textBounds.width()) / 2f
        val textY = rectBottom + textBounds.height() + 15f // put text below rect and 15f padding
        canvas.drawText("Scan QR in box", textX, textY, textPaint)
        holder.unlockCanvasAndPost(canvas)
    }

    override fun provideOverlayCallback(): SurfaceHolder.Callback {
        return object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                drawOverlay(
                    holder,
                    DESIRED_HEIGHT_CROP_PERCENT,
                    DESIRED_WIDTH_CROP_PERCENT
                )
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        }
    }


    override fun provideImageAnalysis(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(cameraExecutor, imageAnalyzer.provideAnalyzer())
            }
    }

    override fun provideLensFacingFrontState(): Int {
        return CameraSelector.LENS_FACING_FRONT
    }

    override fun provideLensFacingBackState(): Int {
        return CameraSelector.LENS_FACING_BACK
    }

    override fun provideFrontCameraSelector(): CameraSelector {
       return frontCameraSelector
    }

    override fun provideBackCameraSelector(): CameraSelector {
        return backCameraSelector
    }

    override fun autoFocusPreview(view: PreviewView, camera: Camera) {
        view.afterMeasured {
            val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1f, 1f)
                .createPoint(.5f, .5f)
            try {
                val autoFocusAction = FocusMeteringAction.Builder(
                    autoFocusPoint,
                    FocusMeteringAction.FLAG_AF
                ).apply {
                    //start auto-focusing after 2 seconds
                    setAutoCancelDuration(2, TimeUnit.SECONDS)
                }.build()
                camera.cameraControl.startFocusAndMetering(autoFocusAction)
            } catch (e: CameraInfoUnavailableException) {
                Timber.e("cannot access camera because $e")
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun tapToFocusPreview(view: PreviewView, camera: Camera) {
        view.afterMeasured {
            view.setOnTouchListener { _, event ->
                return@setOnTouchListener when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                            view.width.toFloat(), view.height.toFloat()
                        )
                        val autoFocusPoint = factory.createPoint(event.x, event.y)
                        try {
                            camera.cameraControl.startFocusAndMetering(
                                FocusMeteringAction.Builder(
                                    autoFocusPoint,
                                    FocusMeteringAction.FLAG_AF
                                ).apply {
                                    //focus only when the user tap the preview
                                    disableAutoCancel()
                                }.build()
                            )
                        } catch (e: CameraInfoUnavailableException) {
                            Timber.e("cannot access camera because $e")
                        }
                        true
                    }
                    else -> false // Unhandled event.
                }
            }
        }
    }

    override fun hasBackCamera(): Boolean {
        return cameraProcessProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
    }

    override fun hasFrontCamera(): Boolean {
        return cameraProcessProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
    }

    override fun providePreview(view: PreviewView): Preview {
        return Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(view.surfaceProvider)
            }
    }

    override fun unbindCamera() {
        cameraProcessProvider.unbindAll()
    }

    override fun shutdownExecutor() {
        cameraExecutor.shutdown()
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    companion object {
        // We only need to analyze the part of the image that has text, so we set crop percentages
        // to avoid analyze the entire image from the live camera feed.
        const val DESIRED_WIDTH_CROP_PERCENT = 25
        const val DESIRED_HEIGHT_CROP_PERCENT = 25

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }
}