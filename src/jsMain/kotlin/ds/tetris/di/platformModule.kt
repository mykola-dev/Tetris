/*
 * © 2022 Deviant Studio
 */

package ds.tetris.di

import ds.tetris.game.WebSoundtrack
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::WebSoundtrack)
}