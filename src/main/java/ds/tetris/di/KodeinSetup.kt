package ds.tetris.di

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import ds.tetris.game.Game

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

private fun provideMainComponent() = Kodein {
    bind<Game>() with singleton { Game() }
}

val mainComponent = provideMainComponent()