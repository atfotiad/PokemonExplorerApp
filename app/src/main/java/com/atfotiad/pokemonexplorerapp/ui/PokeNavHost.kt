package com.atfotiad.pokemonexplorerapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import com.atfotiad.pokemonexplorerapp.navigation.PokemonDestination
import com.atfotiad.pokemonexplorerapp.utils.navigation.toNavType
import kotlin.reflect.typeOf

@Composable
fun PokeNavHost(
    navController: NavHostController,
    pokemonViewModel: PokemonViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = PokemonDestination.MainScreen,
        modifier
    ) {
        composable<PokemonDestination.MainScreen> {
            HomeScreen(Modifier, pokemonViewModel) { pokemon ->
                navController.navigateToDetails(pokemon)
            }
        }
        composable<PokemonDestination.DetailScreen>(
            typeMap = mapOf(typeOf<Pokemon>() to NavType.toNavType<Pokemon>())
        ) {
            val pokemon = it.toRoute<PokemonDestination.DetailScreen>().pokemon
            val pokemonDetailsViewModel: PokemonDetailsViewModel = hiltViewModel()
            PokemonDetailsScreen(pokemon = pokemon, viewModel = pokemonDetailsViewModel)
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: Any) = this.navigate(route) {
    popUpTo(
        this@navigateSingleTopTo.graph.findStartDestination().id
    ) {
        saveState = false
    }
    launchSingleTop = true
    restoreState = true
}

private fun NavHostController.navigateToDetails(pokemon: Pokemon) {
    this.navigateSingleTopTo(PokemonDestination.DetailScreen(pokemon))
}