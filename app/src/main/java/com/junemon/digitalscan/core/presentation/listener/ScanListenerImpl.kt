package com.junemon.digitalscan.core.presentation.listener

import com.google.zxing.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Ian Damping on 28,May,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
class ScanListenerImpl @Inject constructor(private val scope: CoroutineScope) : ScanListener {

    private val channel = Channel<Result>(CONFLATED)

    override fun setQrImage(data: Result) {
        scope.launch {
            channel.send(data)
        }
    }

    override val qrImage: Flow<Result>
        get() = channel.receiveAsFlow().distinctUntilChanged { old, new ->
            new.text == old.text
        }

}