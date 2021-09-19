package com.junemon.digitalscan.feature

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.junemon.digitalscan.core.presentation.Constant.REQUEST_CODE_PERMISSIONS
import com.junemon.digitalscan.core.presentation.Constant.REQUIRED_PERMISSIONS
import com.junemon.digitalscan.core.presentation.base.BaseFragmentViewBinding
import com.junemon.digitalscan.core.presentation.clicks
import com.junemon.digitalscan.databinding.FragmentHomeBinding
import timber.log.Timber

/**
 * Created by Ian Damping on 19,September,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
class FragmentHome : BaseFragmentViewBinding<FragmentHomeBinding>() {

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity().baseContext,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun viewCreated() {
        with(binding) {
            clicks(btnScan) {
                // Request camera permissions
                if (allPermissionsGranted()) {
                    navigate(FragmentHomeDirections.actionFragmentHomeToFragmentQrScan())
                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                    )
                }
            }
        }
    }

    override fun activityCreated() {
        // Request camera permissions
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Timber.e("called onRequestPermissionsResult")
                navigate(FragmentHomeDirections.actionFragmentHomeToFragmentQrScan())
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}