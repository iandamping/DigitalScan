package com.junemon.digitalscan.di

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.junemon.digitalscan.MainActivity
import com.junemon.digitalscan.di.qualifier.MainActivityLifecycle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

/**
 * Created by Ian Damping on 14,June,2021
 * Github https://github.com/iandamping
 * Indonesia.
 */
@Module
@InstallIn(ActivityComponent::class)
object MainActivityModule {

    @Provides
    @ActivityScoped
    @MainActivityLifecycle
    fun provideActivityLifecycleOwner(activity: Activity): LifecycleOwner {
        return activity as MainActivity as LifecycleOwner
    }
}