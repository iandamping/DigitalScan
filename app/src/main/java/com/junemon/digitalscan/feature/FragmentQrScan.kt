package com.junemon.digitalscan.feature

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.junemon.digitalscan.MainActivity
import com.junemon.digitalscan.core.presentation.base.BaseFragmentViewBinding
import com.junemon.digitalscan.core.presentation.clicks
import com.junemon.digitalscan.core.presentation.helper.CameraxHelper
import com.junemon.digitalscan.core.presentation.listener.ScanListener
import com.junemon.digitalscan.databinding.FragmentQrScanBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Ian Damping on 19,September,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */

@AndroidEntryPoint
class FragmentQrScan : BaseFragmentViewBinding<FragmentQrScanBinding>() {

    private val cameraVm:CameraViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentQrScanBinding
        get() = FragmentQrScanBinding::inflate

    private fun observeListener() {
        lifecycleScope.launchWhenStarted {
            cameraVm.getQrImage().onEach {
                cameraVm.unbindCamera()
                val intent: Intent = Intent(requireActivity(), MainActivity::class.java)
                intent.putExtra("result", it.text)
                startActivity(intent)
                requireActivity().finish()
            }.launchIn(this)
        }
    }

    private fun observeCameraLens(){
        lifecycleScope.launchWhenStarted {
            cameraVm.lensStateFlow.onEach { lensState ->
                // Re-bind use cases to update selected camera
                cameraVm.startCamera(
                    lifecycleOwner = this@FragmentQrScan as LifecycleOwner,
                    lens = lensState,
                    preview = cameraVm.providePreview(
                        view = binding.viewFinder
                    )
                ) { camera ->
                    with(cameraVm){
                        autoFocusPreview(view = binding.viewFinder, camera = camera)
                        tapToFocusPreview(view = binding.viewFinder, camera = camera)
                    }
                }
            }.launchIn(this)
        }

    }

    /** Enabled or disabled a button to switch cameras depending on the available cameras */
    private fun FragmentQrScanBinding.updateCameraSwitchButton() {
        try {
            cameraSwitchButton.isEnabled = cameraVm.hasBackCamera() && cameraVm.hasFrontCamera()
        } catch (exception: CameraInfoUnavailableException) {
            cameraSwitchButton.isEnabled = false
        }
    }

    override fun viewCreated() {
        with(binding){
            updateCameraSwitchButton()
            // Setup for button used to switch cameras
            clicks(cameraSwitchButton){
                if (CameraSelector.LENS_FACING_FRONT == cameraVm.lensStateFlow.value) {
                    cameraVm.setLens(cameraVm.changeIntoBackCamera())
                } else {
                    cameraVm.setLens(cameraVm.changeIntoFrontCamera())
                }
            }
        }
    }

    override fun activityCreated() {
        observeCameraLens()
        observeListener()
    }
}