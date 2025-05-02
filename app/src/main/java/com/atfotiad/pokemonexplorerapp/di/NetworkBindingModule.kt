package com.atfotiad.pokemonexplorerapp.di

import com.atfotiad.pokemonexplorerapp.utils.network.AndroidNetworkConnectivityChecker
import com.atfotiad.pokemonexplorerapp.utils.network.NetworkConnectivityChecker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
/*** [NetworkBindingModule] is a module that provides bindings for network-related dependencies.]
 * */
abstract class NetworkBindingModule {

    @Binds
    /**
     * [bindNetworkConnectivityChecker] is a method that binds the [AndroidNetworkConnectivityChecker]
     * */
    abstract fun bindNetworkConnectivityChecker(
        androidNetworkConnectivityChecker: AndroidNetworkConnectivityChecker
    ): NetworkConnectivityChecker
}