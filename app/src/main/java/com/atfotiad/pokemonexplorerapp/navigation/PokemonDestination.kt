package com.atfotiad.pokemonexplorerapp.navigation

import com.atfotiad.pokemonexplorerapp.data.model.Pokemon
import kotlinx.serialization.Serializable

sealed interface PokemonDestination {
    @Serializable
    data object MainScreen: PokemonDestination

    @Serializable
    data class DetailScreen(
        val pokemon: Pokemon
    ): PokemonDestination
}