package com.atfotiad.pokemonexplorerapp.di

import com.atfotiad.pokemonexplorerapp.utils.network.AndroidNetworkConnectivityChecker
import com.atfotiad.pokemonexplorerapp.utils.network.NetworkConnectivityChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindingModule {

    @Binds
    abstract fun bindNetworkConnectivityChecker(
        androidNetworkConnectivityChecker: AndroidNetworkConnectivityChecker
    ): NetworkConnectivityChecker
}