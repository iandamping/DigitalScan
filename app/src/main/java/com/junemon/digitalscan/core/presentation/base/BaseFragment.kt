package com.junemon.digitalscan.core.presentation.base

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

/**
 * Created by Ian Damping on 30,July,2020
 * Github https://github.com/iandamping
 * Indonesia.
 */
abstract class BaseFragment : Fragment() {

    protected fun baseNavigate(destination: NavDirections) =
        with(findNavController()) {
            currentDestination?.getAction(destination.actionId)
                ?.let { navigate(destination) }
        }

    protected fun fragmentBackPressed(func: () -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() { // Handle the back button event
                    func.invoke()
                }
            }
        )
    }
}