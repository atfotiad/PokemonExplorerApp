package com.atfotiad.pokemonexplorerapp.di

import com.atfotiad.pokemonexplorerapp.data.repository.PokeRepository
import com.atfotiad.pokemonexplorerapp.data.repository.PokeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {

    @Binds
    @ActivityRetainedScoped
    abstract fun bindPokeRepository(
        pokeRepositoryImpl: PokeRepositoryImpl
    ): PokeRepository
}