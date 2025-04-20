package com.atfotiad.pokemonexplorerapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.atfotiad.pokemonexplorerapp.model.Pokemon

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    pokemonViewModel: PokemonViewModel,
    onPokemonClick: (Pokemon) -> Unit
) {
    Column(modifier) {
        val list = pokemonViewModel.pokemonList.collectAsLazyPagingItems()
        PokemonList(list, onPokemonClick) {pokemon: Pokemon ->
            onPokemonClick(pokemon)
        }

    }
}