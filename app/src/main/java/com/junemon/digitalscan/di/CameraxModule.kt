package com.junemon.digitalscan.di

import com.junemon.digitalscan.core.presentation.helper.CameraxHelper
import com.junemon.digitalscan.core.presentation.helper.CameraxHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

/**
 * Created by Ian Damping on 29,May,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
@Module
@InstallIn(ActivityComponent::class)
interface CameraxModule {

    @Binds
    @ActivityScoped
    fun bindCameraxHelper(cameraxHelper: CameraxHelperImpl): CameraxHelper
}