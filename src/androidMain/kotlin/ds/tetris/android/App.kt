package ds.tetris.android

import android.app.Application
import ds.tetris.di.gameModule
import ds.tetris.di.initKoin
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}