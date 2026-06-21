package com.rtech.klarnapay

import android.app.Application
import com.rtech.klarnapay.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application entry-point.
 * Starts the Koin dependency-injection container with all modules.
 */
class KlarnaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@KlarnaApp)
            modules(appModule)
        }
    }
}