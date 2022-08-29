package ds.tetris.di

import ds.tetris.game.Game
import ds.tetris.game.Soundtrack
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val gameModule = module {
    singleOf(::Game)
    singleOf(::Soundtrack)
    single {
        Napier.base(DebugAntilog())
        Napier
    }
}

val platformModule = module {

}