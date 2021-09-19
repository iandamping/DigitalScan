package com.junemon.digitalscan.feature

import androidx.camera.core.Camera
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.junemon.digitalscan.core.presentation.helper.CameraxHelper
import com.junemon.digitalscan.core.presentation.listener.ScanListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Created by Ian Damping on 19,September,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
@HiltViewModel
class CameraViewModel @Inject constructor(
    private val scanListener: ScanListener,
    private val cameraxHelper: CameraxHelper
) : ViewModel() {

    private val _lensStateFlow:MutableStateFlow<Int> = MutableStateFlow(cameraxHelper.provideLensFacingBackState())
    val lensStateFlow:StateFlow<Int> = _lensStateFlow

    fun setLens(data:Int){
        _lensStateFlow.value = data
    }

    fun changeIntoFrontCamera():Int = cameraxHelper.provideLensFacingFrontState()

    fun changeIntoBackCamera():Int = cameraxHelper.provideLensFacingBackState()

    fun unbindCamera() {
        cameraxHelper.unbindCamera()
    }

    fun startCamera(lifecycleOwner: LifecycleOwner, lens:Int, preview: Preview, camera: (Camera) -> Unit){
        when(lens){
            cameraxHelper.provideLensFacingBackState() ->{
                cameraxHelper.startCameraForScan(lifecycleOwner, cameraxHelper.provideBackCameraSelector(),preview, camera)
            }
            cameraxHelper.provideLensFacingFrontState() ->{
                cameraxHelper.startCameraForScan(lifecycleOwner,cameraxHelper.provideFrontCameraSelector(), preview, camera)
            }
        }
    }

    fun providePreview(view: PreviewView) = cameraxHelper.providePreview(view)

    fun autoFocusPreview(view: PreviewView, camera: Camera) =
        cameraxHelper.autoFocusPreview(view, camera)

    fun tapToFocusPreview(view: PreviewView, camera: Camera) =
        cameraxHelper.tapToFocusPreview(view, camera)

    fun getQrImage() = scanListener.qrImage

    fun hasBackCamera(): Boolean = cameraxHelper.hasBackCamera()

    fun hasFrontCamera(): Boolean = cameraxHelper.hasFrontCamera()
}