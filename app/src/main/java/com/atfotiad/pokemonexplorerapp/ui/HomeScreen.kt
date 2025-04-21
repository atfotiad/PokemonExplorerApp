package com.atfotiad.pokemonexplorerapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import com.atfotiad.pokemonexplorerapp.model.Pokemon

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    pokemonViewModel: PokemonViewModel,
    onPokemonClick: (Pokemon) -> Unit
) {
    Column(modifier.fillMaxSize()) {
        val list = pokemonViewModel.pokemonList.collectAsLazyPagingItems()
        PokemonList(list = list) { pokemon: Pokemon ->
            onPokemonClick(pokemon)
        }
    }
}