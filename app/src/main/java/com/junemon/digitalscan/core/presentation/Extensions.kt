package com.junemon.digitalscan.core.presentation

import android.view.View
import android.view.ViewTreeObserver
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

/**
 * Created by Ian Damping on 19,September,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */

fun Fragment.clicks(
    view: View,
    duration: Long = 300,
    isUsingThrottle: Boolean = true,
    onBound: () -> Unit
) {
    if (isUsingThrottle) {
        view.clickListener().throttleFirst(duration).onEach {
            onBound.invoke()
        }.launchIn(this.viewLifecycleOwner.lifecycleScope)
    } else {
        view.setOnClickListener {
            onBound.invoke()
        }
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
private fun View.clickListener(): Flow<Unit> = callbackFlow {
    setOnClickListener {
        offer(Unit)
    }
    awaitClose { this@clickListener.setOnClickListener(null) }
}

@FlowPreview
@ExperimentalCoroutinesApi
fun <T> Flow<T>.throttleFirst(windowDuration: Long): Flow<T> = flow {
    var lastEmissionTime = 0L
    collect { upstream ->
        val currentTime = System.currentTimeMillis()
        val mayEmit = currentTime - lastEmissionTime > windowDuration
        if (mayEmit) {
            lastEmissionTime = currentTime
            emit(upstream)
        }
    }
}

inline fun PreviewView.afterMeasured(crossinline block: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            if (measuredWidth > 0 && measuredHeight > 0) {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                block()
            }
        }
    })
}