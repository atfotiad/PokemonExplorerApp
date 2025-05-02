package com.atfotiad.pokemonexplorerapp.ui.detailsScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atfotiad.pokemonexplorerapp.data.model.Stat
import com.atfotiad.pokemonexplorerapp.ui.theme.DarkGreen
import com.atfotiad.pokemonexplorerapp.ui.theme.LightGreen
import com.atfotiad.pokemonexplorerapp.ui.theme.Orange
import com.atfotiad.pokemonexplorerapp.ui.theme.Red
import com.atfotiad.pokemonexplorerapp.ui.theme.Teal
import com.atfotiad.pokemonexplorerapp.ui.theme.Yellow
import java.util.Locale

@Composable
/**
 *  [PokemonStatBar] is a composable function that displays the stats of a Pokemon.]
 *  @param modifier is an instance of [Modifier]
 *  @param statValue is an instance of [Stat]
 *  @param animDelay is an integer value that represents the delay of the animation
 * */
fun PokemonStatBar(
    modifier: Modifier = Modifier,
    statValue: Stat,
    animDelay: Int = 0,
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val currentPercent = animateFloatAsState(
        targetValue = if (animationPlayed) {
            statValue.baseStat.toFloat() / 255f
        } else 0f,
        label = "",
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = animDelay
        )
    )
    val statBarColor = when {
        statValue.baseStat in 0..29 -> Red
        statValue.baseStat in 30..59 -> Orange
        statValue.baseStat in 60..89 -> Yellow
        statValue.baseStat in 90..119 -> LightGreen
        statValue.baseStat in 120..149 -> DarkGreen
        statValue.baseStat >= 150 -> Teal
        else -> Color.Black
    }
    LaunchedEffect(true) {
        animationPlayed = true
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            modifier = modifier.fillMaxWidth(0.3f),
            text = when (statValue.stat.name) {
                "hp" -> "HP"
                "special-attack" -> "Sp.Atk"
                "special-defense" -> "Sp.Def"
                else -> statValue.stat.name.replaceFirstChar {
                    if (it.isLowerCase()) it.uppercase(Locale.getDefault()) else it.toString()
                }
            },
            textAlign = TextAlign.End
        )

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(0.3f)
                )
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 1.dp, end = 1.dp)
                    .fillMaxWidth(currentPercent.value)
                    .height(30.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(statBarColor)
            )

            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
                text = "${statValue.baseStat}",
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}