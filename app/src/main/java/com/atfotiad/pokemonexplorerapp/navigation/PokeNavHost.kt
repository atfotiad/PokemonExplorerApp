package com.atfotiad.pokemonexplorerapp.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.atfotiad.pokemonexplorerapp.data.model.Pokemon
import com.atfotiad.pokemonexplorerapp.ui.homeScreen.HomeScreen
import com.atfotiad.pokemonexplorerapp.ui.detailsScreen.PokemonDetailsScreen
import com.atfotiad.pokemonexplorerapp.ui.detailsScreen.PokemonDetailsViewModel
import com.atfotiad.pokemonexplorerapp.ui.homeScreen.PokemonViewModel
import com.atfotiad.pokemonexplorerapp.utils.navigation.toNavType
import kotlin.reflect.typeOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
/***
 * [PokeNavHost] is a composable function that sets up the navigation graph for the app.
 */
fun PokeNavHost(
    navController: NavHostController,
    pokemonViewModel: PokemonViewModel,
    modifier: Modifier = Modifier
) {

    SharedTransitionLayout {
        NavHost(
            navController,
            startDestination = PokemonDestination.MainScreen,
            modifier
        ) {
            composable<PokemonDestination.MainScreen> {
                HomeScreen(
                    this@SharedTransitionLayout,
                    this@composable,
                    modifier,
                    pokemonViewModel
                ) { pokemon ->
                    navController.navigateToDetails(pokemon)
                }
            }
            composable<PokemonDestination.DetailScreen>(
                typeMap = mapOf(typeOf<Pokemon>() to NavType.toNavType<Pokemon>())
            ) {
                val pokemon = it.toRoute<PokemonDestination.DetailScreen>().pokemon
                val pokemonDetailsViewModel: PokemonDetailsViewModel = hiltViewModel()
                it.savedStateHandle["pokemon"] = pokemon
                PokemonDetailsScreen(
                    this@SharedTransitionLayout,
                    this@composable,
                    viewModel = pokemonDetailsViewModel
                )
            }
        }
    }
}
/**
 * [navigateSingleTopTo] is an extension function that navigates to a destination in the navigation graph.
 * */
fun NavHostController.navigateSingleTopTo(route: Any) = this.navigate(route) {
    popUpTo(
        this@navigateSingleTopTo.graph.findStartDestination().id
    ) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
/**
 *  [navigateToDetails] is an extension function that navigates to the details screen.
 * */
private fun NavHostController.navigateToDetails(pokemon: Pokemon) {
    this.navigateSingleTopTo(PokemonDestination.DetailScreen(pokemon))
}