package com.atfotiad.pokemonexplorerapp.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.atfotiad.pokemonexplorerapp.model.Pokemon

@Composable
fun PokemonList(
    modifier: Modifier = Modifier,
    list: List<Pokemon>,
    onClick: (Pokemon) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(list) { pokemon ->
            PokemonItem(pokemon = pokemon) {
                onClick(pokemon)
            }
        }
    }
}