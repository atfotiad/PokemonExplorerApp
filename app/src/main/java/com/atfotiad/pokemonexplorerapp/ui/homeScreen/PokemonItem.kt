package com.atfotiad.pokemonexplorerapp.ui.homeScreen

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atfotiad.pokemonexplorerapp.data.model.Pokemon
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun PokemonItem(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    pokemon: Pokemon,
    onPokemonClick: (Pokemon) -> Unit
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onPokemonClick(pokemon) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(),
        shape = RoundedCornerShape(15.dp)
    ) {
        Row(modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            with(sharedTransitionScope) {
                GlideImage(
                    model = pokemon.imageUrl,
                    contentDescription = pokemon.name,
                    modifier = modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                        .padding(8.dp)
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = pokemon.id),
                            animatedContentScope
                        )
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = pokemon.name),
                            animatedContentScope
                        ),
                    text = pokemon.name,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}