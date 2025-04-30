package com.atfotiad.pokemonexplorerapp.ui.homeScreen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atfotiad.pokemonexplorerapp.data.model.Pokemon

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonList(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    list: List<Pokemon>,
    listState: LazyListState,
    onClick: (Pokemon) -> Unit,
    isLoading: Boolean,
    onLoadMore: () -> Unit,
    shouldShowLoadMoreButton: State<Boolean>,
    shouldShowEndOfResults: State<Boolean>,
    shouldShowNoPokemonFound: State<Boolean>
) {
    LazyColumn(modifier = modifier.animateContentSize(), state = listState) {
        items(list, key = { pokemon -> pokemon.id }) { pokemon ->
            PokemonItem(
                sharedTransitionScope, animatedContentScope,
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
                }
                if (shouldShowLoadMoreButton.value) {
                    Button(onClick = onLoadMore) { Text("Load More") }
                }
                if (shouldShowEndOfResults.value) {
                    Text("End of Results")
                }
                if (shouldShowNoPokemonFound.value) {
                    Text("No Pokemon Found")
                }
            }
        }
    }
}