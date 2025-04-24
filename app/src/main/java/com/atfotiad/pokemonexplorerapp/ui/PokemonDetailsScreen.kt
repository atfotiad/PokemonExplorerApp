package com.atfotiad.pokemonexplorerapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atfotiad.pokemonexplorerapp.R
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.util.Locale

@Composable
fun PokemonDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonDetailsViewModel
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val pokemon = state ?: Pokemon(
        0,
        "",
        emptyList(),
        emptyList(),
        "",
        "",
        ""
    )
    Column(modifier.safeDrawingPadding().verticalScroll(rememberScrollState(), true)) {
        PokemonCard(pokemon = pokemon, modifier) {
            viewModel.playCry(pokemon)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PokemonCard(pokemon: Pokemon, modifier: Modifier = Modifier, onPlayCry: () -> Unit) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = modifier.align(Alignment.TopCenter),
                    text = pokemon.name.replaceFirstChar {
                        if (it.isLowerCase())
                            it.titlecase(Locale.getDefault())
                        else it.toString()
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Row(modifier = modifier.align(Alignment.TopEnd)) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "Primary Type",
                        modifier = modifier.size(30.dp)
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "Secondary Type",
                        modifier = modifier.size(30.dp)
                    )
                }
            }

            GlideImage(
                model = pokemon.imageUrl,
                contentDescription = "Pokemon Image",
                modifier = modifier
                    .fillMaxWidth()
                    .size(250.dp)
                    .padding(8.dp)
                    .border(2.dp, MaterialTheme.colorScheme.onSurface, RectangleShape),
                contentScale = ContentScale.Fit,
            ) {
                it.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
            }

            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = {
                    onPlayCry()
                }) {
                    Text("Cry")
                }
                Text(
                    pokemon.pokeDexEntry.replace("[\n\t\u000c]".toRegex(), " "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            PokemonStatsSection(modifier,stats = pokemon.stats)
        }
    }
}