package ds.tetris.di

import org.koin.core.context.startKoin

fun initKoin(){
    startKoin {
        modules(gameModule, platformModule)
    }
}