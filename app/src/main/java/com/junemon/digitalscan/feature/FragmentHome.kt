package com.junemon.digitalscan.feature

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.junemon.digitalscan.core.presentation.Constant.REQUEST_CODE_PERMISSIONS
import com.junemon.digitalscan.core.presentation.Constant.REQUIRED_PERMISSIONS
import com.junemon.digitalscan.core.presentation.base.BaseFragmentViewBinding
import com.junemon.digitalscan.databinding.FragmentHomeBinding
import timber.log.Timber

/**
 * Created by Ian Damping on 19,September,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
class FragmentHome:BaseFragmentViewBinding<FragmentHomeBinding>() {

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate


    override fun viewCreated() {
       with(binding){
           btnScan.setOnClickListener {
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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                Timber.e("called onActivityResult")
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