package com.junemon.digitalscan.core.presentation.analyzer

import androidx.camera.core.ImageAnalysis

/**
 * Created by Ian Damping on 28,May,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
interface ScanAnalyzer {

    fun provideAnalyzer(): ImageAnalysis.Analyzer
}