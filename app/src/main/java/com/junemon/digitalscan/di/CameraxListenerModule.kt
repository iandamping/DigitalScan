package com.junemon.digitalscan.di

import com.junemon.digitalscan.core.presentation.analyzer.ScanAnalyzer
import com.junemon.digitalscan.core.presentation.analyzer.ScanAnalyzerImpl
import com.junemon.digitalscan.core.presentation.listener.ScanListener
import com.junemon.digitalscan.core.presentation.listener.ScanListenerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

/**
 * Created by Ian Damping on 28,May,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
@Module
@InstallIn(ActivityComponent::class)
interface CameraxListenerModule {

    @Binds
    @ActivityScoped
    fun bindScanListener(scanListener: ScanListenerImpl): ScanListener

    @Binds
    @ActivityScoped
    fun bindScanAnalyzer(scanAnalyzer: ScanAnalyzerImpl): ScanAnalyzer
}