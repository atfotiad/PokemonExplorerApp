package com.atfotiad.pokemonexplorerapp.ui.detailsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atfotiad.pokemonexplorerapp.data.model.Stat

@Composable
/**
 *  [PokemonStatsSection] is a composable function that displays the stats of a Pokemon.]
 *  @param modifier is an instance of [Modifier]
 *  @param stats is a list of [Stat]
 * */
fun PokemonStatsSection(modifier: Modifier = Modifier, stats: List<Stat>) {
    Column(
        modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
    ) {
        stats.forEach {
            PokemonStatBar(statValue = it)
            Spacer(modifier.padding(top = 16.dp))
        }
    }
}
