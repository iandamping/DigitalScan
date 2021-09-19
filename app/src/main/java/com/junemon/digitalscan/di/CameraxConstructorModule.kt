package com.junemon.digitalscan.di

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.junemon.digitalscan.di.qualifier.LensFacingBack
import com.junemon.digitalscan.di.qualifier.LensFacingFront
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Ian Damping on 29,May,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
@Module
@InstallIn(SingletonComponent::class)
object CameraxConstructorModule {

    @Provides
    fun provideCameraXExecutor(): ExecutorService = Executors.newSingleThreadExecutor()

    @Provides
    fun provideCameraMainExecutor(@ApplicationContext context: Context): Executor =
        ContextCompat.getMainExecutor(context)

    @Provides
    fun provideProcessCameraProviderFuture(@ApplicationContext context: Context): ListenableFuture<ProcessCameraProvider> =
        ProcessCameraProvider.getInstance(context)

    @Provides
    fun provideProcessCameraProvider(future: ListenableFuture<ProcessCameraProvider>): ProcessCameraProvider =
        future.get()

    @Provides
    @LensFacingBack
    fun provideBackCameraSelector(): CameraSelector =
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

    @Provides
    @LensFacingFront
    fun provideFrontCameraSelector(): CameraSelector =
        CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
}