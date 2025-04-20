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
        override val route = "detail"
        private const val POKEMON = "pokemonName"
        val arguments = listOf(navArgument(POKEMON) {
            type = NavType.ParcelableType(Pokemon::class.java)
        })
    }
}