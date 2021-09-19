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
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Ian Damping on 28,May,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
@Module
@InstallIn(SingletonComponent::class)
interface CameraxListenerModule {

    @Binds
    @Singleton
    fun bindScanListener(scanListener: ScanListenerImpl): ScanListener

    @Binds
    @Singleton
    fun bindScanAnalyzer(scanAnalyzer: ScanAnalyzerImpl): ScanAnalyzer
}