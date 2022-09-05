package ds.tetris.di

import ds.tetris.game.Game
import ds.tetris.game.Soundtrack
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val gameModule = module {
    single { CoroutineScope(Dispatchers.Default) }
    singleOf(::Game)
    single {
        Napier.base(DebugAntilog())
        Napier
    }
    singleOf(::Soundtrack)
}

expect val platformModule: Module