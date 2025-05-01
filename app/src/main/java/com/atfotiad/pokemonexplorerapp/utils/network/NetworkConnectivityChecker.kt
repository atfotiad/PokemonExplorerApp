package com.atfotiad.pokemonexplorerapp.utils.network

interface NetworkConnectivityChecker {
    fun isInternetAvailable(): Boolean
}