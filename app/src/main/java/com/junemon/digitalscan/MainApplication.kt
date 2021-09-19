package com.junemon.digitalscan

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by Ian Damping on 02,April,2020
 * Github https://github.com/iandamping
 * Indonesia.
 */
@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}