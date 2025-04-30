package com.atfotiad.pokemonexplorerapp.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import com.atfotiad.pokemonexplorerapp.utils.typeColorMap
import com.atfotiad.pokemonexplorerapp.utils.types
import kotlinx.coroutines.FlowPreview


@SuppressLint("ConfigurationScreenWidthHeight", "UnrememberedMutableState")
@OptIn(ExperimentalLayoutApi::class, FlowPreview::class, ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    transitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    pokemonViewModel: PokemonViewModel,
    onPokemonClick: (Pokemon) -> Unit
) {

    val totalCount by pokemonViewModel.totalPokemonCount.collectAsStateWithLifecycle()
    val text by pokemonViewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilters by pokemonViewModel.selectedFilters.collectAsStateWithLifecycle()
    val isLoading by pokemonViewModel.isLoading.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()
    val isSearchingByName by pokemonViewModel.isSearchingByName.collectAsStateWithLifecycle()
    val stateUI by pokemonViewModel.stateUI.collectAsStateWithLifecycle()

    // Derive displayPokemonItems directly from stateUI
    val displayPokemonItems: List<Pokemon> by remember(stateUI) {
        derivedStateOf {
            if (stateUI is StateUI.Success) {
                (stateUI as StateUI.Success<List<Pokemon>>).data
            } else {
                emptyList()
            }
        }
    }

    val shouldShowLoadMoreButton by rememberUpdatedState(derivedStateOf {
        !isLoading &&
                displayPokemonItems.size < totalCount &&
                text.isEmpty() &&
                !isSearchingByName &&
                stateUI !is StateUI.Error &&
                stateUI !is StateUI.Loading
    })

    val shouldShowEndOfResults by rememberUpdatedState(derivedStateOf {
        !isLoading &&
                displayPokemonItems.isNotEmpty() &&
                displayPokemonItems.size == totalCount &&
                text.isEmpty() &&
                selectedFilters.isEmpty()
    })

    val shouldShowNoPokemonFound by rememberUpdatedState(derivedStateOf {
        !isLoading &&
                displayPokemonItems.isEmpty() &&
                text.isNotEmpty() &&
                !isSearchingByName
    })

    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(imeBottom) {
        if (imeBottom == 0 && isFocused) {
            isFocused = false
            focusManager.moveFocus(FocusDirection.Next)
            focusManager.clearFocus()
        }
    }

    Scaffold { paddingValues ->
        ConstraintLayout(
            modifier = modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(paddingValues)
                .focusable()
        ) {
            val (searchBox, filterRow, listContent) = createRefs()

            val searchBoxWidth: Dp by animateDpAsState(
                targetValue = if (isFocused) LocalConfiguration.current.screenWidthDp.dp - 64.dp else 300.dp,
                animationSpec = spring()
            )

            SearchField(
                text = text,
                onTextChange = pokemonViewModel::setSearchQuery,
                onTrailingIconClick = {
                    pokemonViewModel.setSearchQuery("")
                    pokemonViewModel.performSearch()
                    focusManager.clearFocus()
                },
                onSearch = {
                    pokemonViewModel.performSearch()
                    focusManager.clearFocus()
                },
                onFocusChanged = { isFocused = it.isFocused },
                modifier = Modifier
                    .constrainAs(searchBox) {
                        top.linkTo(parent.top, margin = 8.dp)
                        start.linkTo(parent.start, margin = 8.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                        width = Dimension.value(searchBoxWidth)
                    }
                    .focusRequester(focusRequester)
            )

            FlowRow(
                modifier = Modifier
                    .constrainAs(filterRow) {
                        top.linkTo(searchBox.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedFilters.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Filters:", style = MaterialTheme.typography.bodySmall)
                        IconButton(onClick = pokemonViewModel::clearAllFilters) {
                            Icon(Icons.Default.ClearAll, contentDescription = "Clear All Filters")
                        }
                    }
                }
                for (type in types) {
                    FilterChip(
                        selected = selectedFilters.contains(type),
                        onClick = {
                            pokemonViewModel.updateFilters(
                                if (selectedFilters.contains(type)) selectedFilters - type else selectedFilters + type
                            )
                        },
                        label = { Text(text = type) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = typeColorMap[type]!!,
                            containerColor = Color.Transparent,
                            labelColor = typeColorMap[type]!!,
                            selectedLabelColor = Color.White
                        ),
                        border = BorderStroke(1.dp, typeColorMap[type]!!),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .constrainAs(listContent) {
                        top.linkTo(filterRow.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    }
            ) {
                when (stateUI) {
                    is StateUI.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is StateUI.Success -> {
                        PokemonList(
                            sharedTransitionScope = transitionScope,
                            animatedContentScope = animatedContentScope,
                            list = displayPokemonItems,
                            listState = listState,
                            onClick = onPokemonClick,
                            isLoading = isLoading,
                            onLoadMore = pokemonViewModel::loadMore,
                            shouldShowLoadMoreButton = shouldShowLoadMoreButton,
                            shouldShowEndOfResults = shouldShowEndOfResults,
                            shouldShowNoPokemonFound = shouldShowNoPokemonFound,
                        )
                    }

                    is StateUI.Error -> {
                        Log.i("Error", "HomeScreen: ${(stateUI as StateUI.Error)}")
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (stateUI as StateUI.Error).message ?: "An error occurred"
                            )
                        }
                    }

                    is StateUI.Empty -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (text.isNotEmpty()) {
                                Text(text = "No Pokemon found matching your search.")
                            } else if (selectedFilters.isNotEmpty() && displayPokemonItems.isEmpty()) {
                                Text(text = "No Pokemon found with the selected filters.")
                            } else {
                                Text(text = "No Pokemon available.")
                            }
                        }
                    }
                }
            }
        }
    }
}