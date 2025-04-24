package com.atfotiad.pokemonexplorerapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import com.atfotiad.pokemonexplorerapp.ui.theme.Dark
import com.atfotiad.pokemonexplorerapp.ui.theme.Dragon
import com.atfotiad.pokemonexplorerapp.ui.theme.Electric
import com.atfotiad.pokemonexplorerapp.ui.theme.Fairy
import com.atfotiad.pokemonexplorerapp.ui.theme.Fire
import com.atfotiad.pokemonexplorerapp.ui.theme.Ghost
import com.atfotiad.pokemonexplorerapp.ui.theme.Grass
import com.atfotiad.pokemonexplorerapp.ui.theme.Psychic
import com.atfotiad.pokemonexplorerapp.ui.theme.Steel
import com.atfotiad.pokemonexplorerapp.ui.theme.Water


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    pokemonViewModel: PokemonViewModel,
    onPokemonClick: (Pokemon) -> Unit
) {

    val state by pokemonViewModel.state.collectAsStateWithLifecycle(emptyList())

    val types = listOf(
        "Fire",
        "Water",
        "Grass",
        "Electric",
        "Dragon",
        "Psychic",
        "Ghost",
        "Dark",
        "Steel",
        "Fairy"
    )

    val typeColorMap = mapOf(
        "Fire" to Fire,
        "Water" to Water,
        "Grass" to Grass,
        "Electric" to Electric,
        "Dragon" to Dragon,
        "Psychic" to Psychic,
        "Ghost" to Ghost,
        "Dark" to Dark,
        "Steel" to Steel,
        "Fairy" to Fairy,
    )
    val selectedFilters by pokemonViewModel.selectedFilters.collectAsStateWithLifecycle()
    val text by pokemonViewModel.searchQuery.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        val (searchBox, filerRow, list) = createRefs()

        TextField(
            value = text,
            onValueChange = { pokemonViewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(searchBox) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            placeholder = {
                Text(text = "Search")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(onClick = {
                        pokemonViewModel.setSearchQuery("")
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                }
            )
        )

        FlowRow(
            modifier = Modifier
                .constrainAs(filerRow) {
                    top.linkTo(searchBox.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (type in types) {
                FilterChip(
                    selected = selectedFilters.contains(type),
                    onClick = {

                        val newSelectedFilters = if (selectedFilters.contains(type)) {
                            selectedFilters.filter { it != type }.toSet()
                        } else {
                            selectedFilters + type
                        }
                        pokemonViewModel.updateFilters(newSelectedFilters)
                    },
                    label = {
                        Text(text = type)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = typeColorMap[type]!!,
                        containerColor = Color.Transparent,
                        labelColor = typeColorMap[type]!!,
                        selectedLabelColor = Color.White
                    ),
                    border = BorderStroke(1.dp, typeColorMap[type]!!),
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                )
            }
        }
        PokemonList(
            list = state,
            modifier = Modifier
                .constrainAs(list) {
                    top.linkTo(filerRow.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                }
                .fillMaxHeight()
        ) {
            onPokemonClick(it)
        }
    }
}