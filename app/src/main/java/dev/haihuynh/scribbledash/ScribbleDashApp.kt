package dev.haihuynh.scribbledash

import android.app.Application
import dev.haihuynh.scribbledash.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ScribbleDashApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ScribbleDashApp)
            modules(appModule)
        }
    }
}