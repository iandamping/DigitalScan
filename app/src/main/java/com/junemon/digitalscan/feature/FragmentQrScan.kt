package com.junemon.digitalscan.feature

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.junemon.digitalscan.MainActivity
import com.junemon.digitalscan.core.presentation.base.BaseFragmentViewBinding
import com.junemon.digitalscan.core.presentation.helper.CameraxHelper
import com.junemon.digitalscan.core.presentation.listener.ScanListener
import com.junemon.digitalscan.databinding.FragmentQrScanBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Created by Ian Damping on 19,September,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */

@AndroidEntryPoint
class FragmentQrScan : BaseFragmentViewBinding<FragmentQrScanBinding>() {
    @Inject
    lateinit var listener: ScanListener

    @Inject
    lateinit var cameraxHelper: CameraxHelper

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentQrScanBinding
        get() = FragmentQrScanBinding::inflate

    private fun observeListener() {
        lifecycleScope.launchWhenStarted {
            listener.qrImage.onEach {
                cameraxHelper.unbindCamera()
                val intent: Intent = Intent(requireActivity(), MainActivity::class.java)
                intent.putExtra("result", it.text)
                startActivity(intent)
                requireActivity().finish()
            }.launchIn(this)
        }
    }

    private fun startCamera() {
        cameraxHelper.startCameraForScan(
            lifecycleOwner = this as LifecycleOwner,
            preview = cameraxHelper.providePreview(
                view = binding.viewFinder
            )
        ) { camera ->
            with(cameraxHelper){
                autoFocusPreview(view = binding.viewFinder, camera = camera)
                tapToFocusPreview(view = binding.viewFinder, camera = camera)
            }

        }
    }

    override fun viewCreated() {
        startCamera()
    }

    override fun activityCreated() {
        observeListener()
    }
}