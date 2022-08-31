package ds.tetris.android

import android.app.Application
import ds.tetris.di.gameModule
import ds.tetris.di.initKoin
import ds.tetris.di.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(gameModule, platformModule)
        }
    }
}