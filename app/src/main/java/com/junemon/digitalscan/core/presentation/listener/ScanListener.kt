package com.junemon.digitalscan.core.presentation.listener

import com.google.zxing.Result
import kotlinx.coroutines.flow.Flow

/**
 * Created by Ian Damping on 28,May,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
interface ScanListener {

    fun setQrImage(data:Result)

    val qrImage:Flow<Result>
}