package com.atfotiad.pokemonexplorerapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atfotiad.pokemonexplorerapp.model.Pokemon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PokemonList(
    modifier: Modifier = Modifier,
    list: List<Pokemon>,
    listState: LazyListState,
    onClick: (Pokemon) -> Unit,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    isAllItemsLoaded: Boolean,
    shouldShowLoadMoreButton: Boolean
) {
    LazyColumn(modifier = modifier.animateContentSize(), state = listState) {
        items(list, key = { pokemon -> pokemon.id }) { pokemon ->
            PokemonItem(
                pokemon = pokemon,
            ) {
                onClick(pokemon)
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (shouldShowLoadMoreButton) {
                    Button(onClick = onLoadMore) {
                        Text("Load More")
                    }
                } else {
                    AnimatedVisibility(
                        visible = isAllItemsLoaded && list.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text("End of Results")
                    }
                }
            }
        }
    }
}