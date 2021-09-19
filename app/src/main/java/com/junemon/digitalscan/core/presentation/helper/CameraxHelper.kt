package com.junemon.digitalscan.core.presentation.helper

import android.view.SurfaceHolder
import androidx.camera.core.Camera
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner

/**
 * Created by Ian Damping on 29,May,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
interface CameraxHelper {

    fun startCameraForScan(lifecycleOwner: LifecycleOwner,preview: Preview, camera: (Camera)->Unit)

    fun providePreview(view: PreviewView): Preview

    fun providePreview(view:PreviewView,aspectRatio: Int):Preview

    fun providePreview(view: PreviewView, aspectRatio: Int, rotation: Int): Preview

    fun provideAspectRatio(view: PreviewView): Int

    fun provideRotation(view: PreviewView): Int

    fun drawOverlay(holder: SurfaceHolder, heightCropPercent: Int, widthCropPercent: Int)

    fun provideOverlayCallback(): SurfaceHolder.Callback

    fun provideImageAnalysis(): ImageAnalysis

    fun autoFocusPreview(view: PreviewView,camera: Camera)

    fun tapToFocusPreview(view: PreviewView,camera: Camera)

    fun unbindCamera()

    fun shutdownExecutor()
}