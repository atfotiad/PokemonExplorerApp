package com.atfotiad.pokemonexplorerapp.ui.homeScreen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
/**
 *  [PokemonList] is a composable function that displays a list of Pokemon.
 *  @param sharedTransitionScope is an instance of [SharedTransitionScope]
 *  @param animatedContentScope is an instance of [AnimatedContentScope]
 *  @param modifier is an instance of [Modifier]
 *  @param list is a list of [Pokemon]
 *  @param listState is an instance of [LazyListState]
 *  @param onClick is a lambda function that is called when a Pokemon is clicked
 *  @param isLoading is a boolean value that indicates whether the list is loading
 *  @param onLoadMore is a lambda function that is called when the user reaches the end of the list
 *  @param shouldShowLoadMoreButton is a [State] that indicates whether the load more button should be shown
 *  @param shouldShowEndOfResults is a [State] that indicates whether the end of results message should be shown
 *  @param shouldShowNoPokemonFound is a [State] that indicates whether the no pokemon found message should be shown
 *  @param stateUI is a [StateUI] that indicates the state of the UI
 * */
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
    shouldShowNoPokemonFound: State<Boolean>,
    stateUI: StateUI<List<Pokemon>>
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
                if (stateUI is StateUI.Success) {
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
                } else if (stateUI is StateUI.Error) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    }
                    if (!isLoading) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No internet connection")
                            Button(onClick = onLoadMore) { Text("Retry") }
                        }
                    }

                } else if (stateUI is StateUI.Empty) {
                    Column {
                        Text("No Pokemon Found")
                    }
                }
            }
        }
    }
}