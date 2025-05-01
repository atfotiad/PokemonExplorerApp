package com.atfotiad.pokemonexplorerapp.di


import com.atfotiad.pokemonexplorerapp.data.repository.PokeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import org.mockito.Mockito.mock
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object TestRepositoryModule {

    @Provides
    @Singleton
    fun provideTestPokeRepository(): PokeRepository = mock()


    @Provides
    @Singleton
    fun provideString(): String = "test"


}