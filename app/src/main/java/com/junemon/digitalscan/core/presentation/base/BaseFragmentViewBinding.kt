package com.junemon.digitalscan.core.presentation.base

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

/**
 * Created by Ian Damping on 18,February,2020
 * Github https://github.com/iandamping
 * Indonesia.
 */
abstract class BaseFragmentViewBinding<out VB : ViewBinding> : BaseFragment() {
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB

    private var baseJob: Job? = null
    private lateinit var alert: AlertDialog

    protected fun navigate(destination: NavDirections) = baseNavigate(destination)

    protected fun backPressed(func: () -> Unit) = fragmentBackPressed(func)

    protected fun navigateUp() =
        with(findNavController()) {
            navigateUp()
        }

    protected fun consumeSuspend(func: suspend () -> Unit) {
        baseJob?.cancel()
        baseJob = lifecycleScope.launchWhenStarted {
            func.invoke()
        }
    }

    abstract fun viewCreated()

    abstract fun activityCreated()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreated()
        activityCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        baseJob?.cancel()
    }
}
