package com.atfotiad.pokemonexplorerapp.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.atfotiad.pokemonexplorerapp.model.Pokemon

@Composable
fun PokemonList(
    modifier: Modifier = Modifier,
    list: LazyPagingItems<Pokemon>,
    onClick: (Pokemon) -> Unit
) {
    LazyColumn(modifier) {
        items(list.itemCount) { index ->
            val pokemon = list[index]
            if (pokemon != null) {
                PokemonItem(modifier,pokemon) {
                    onClick(pokemon)
                }
            }
        }
    }
}