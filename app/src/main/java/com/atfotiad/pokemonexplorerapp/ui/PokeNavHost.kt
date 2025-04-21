package com.atfotiad.pokemonexplorerapp.ui

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import com.atfotiad.pokemonexplorerapp.navigation.PokemonDestination

@Composable
fun PokeNavHost(
    navController: NavHostController,
    pokemonViewModel: PokemonViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = "home",
        modifier
    ) {
        composable(PokemonDestination.Home.route) {
            HomeScreen(Modifier, pokemonViewModel) { pokemon ->
                navController.navigateToDetails(pokemon)
            }
        }
        composable(
            PokemonDestination.Detail.route,
            arguments = PokemonDestination.Detail.arguments
        ) {
            val pokemon = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.arguments?.getParcelable("pokemonName", Pokemon::class.java)
            } else {
                it.arguments?.getParcelable("pokemonName")
            }
            val pokemonDetailsViewModel: PokemonDetailsViewModel = hiltViewModel()
            if (pokemon != null) {
                PokemonDetailsScreen(pokemon = pokemon, viewModel = pokemonDetailsViewModel)
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) {
    popUpTo(
        this@navigateSingleTopTo.graph.findStartDestination().id
    ) {
        saveState = false
    }
    launchSingleTop = true
    restoreState = true
}

private fun NavHostController.navigateToDetails(pokemon: Pokemon) {
    this.navigateSingleTopTo("${PokemonDestination.Detail.route}?pokemonName=${pokemon.name}")
}