package com.atfotiad.pokemonexplorerapp.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.atfotiad.pokemonexplorerapp.model.Pokemon

sealed interface PokemonDestination {
    val route: String

    data object Home : PokemonDestination {
        override val route = "home"
    }

    data object Detail : PokemonDestination {
        private const val POKEMON = "pokemonName"
        override val route = "detail/{$POKEMON}"
        val arguments = listOf(navArgument(POKEMON) {
            type = NavType.ParcelableType(Pokemon::class.java)
        })
    }
}