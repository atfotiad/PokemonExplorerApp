package com.atfotiad.pokemonexplorerapp.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.atfotiad.pokemonexplorerapp.model.Species
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun PokemonDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonDetailsViewModel
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isMediaPlayerReady by viewModel.isMediaPlayerReady.collectAsStateWithLifecycle()
    val pokemon = state ?: Pokemon(
        0,
        "",
        emptyList(),
        emptyList(),
        Species("", ""),
        "",
        "",
        ""
    )
    var mediaPlayer = remember { MediaPlayer() }

    LaunchedEffect(key1 = state) {
        withContext(Dispatchers.IO) {
            viewModel.resetMediaPlayerReady()
            if (state != null) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(state?.cry)
                    prepare()
                }
                viewModel.setMediaPlayer(mediaPlayer)
            }
        }
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Column(
        modifier
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState(), true)
    ) {
        PokemonCard(pokemon = pokemon, modifier, isMediaPlayerReady) {
            viewModel.playCry()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PokemonCard(
    pokemon: Pokemon,
    modifier: Modifier = Modifier,
    isMediaPlayerReady: Boolean,
    onPlayCry: () -> Unit
) {
    val typeToResourceMap = mapOf(
        "fire" to R.drawable.fire,
        "water" to R.drawable.water,
        "grass" to R.drawable.grass,
        "electric" to R.drawable.electric,
        "poison" to R.drawable.poison,
        "normal" to R.drawable.normal,
        "psychic" to R.drawable.psychic,
        "rock" to R.drawable.rock,
        "ghost" to R.drawable.ghost,
        "ice" to R.drawable.ice,
        "ground" to R.drawable.ground,
        "flying" to R.drawable.flying,
        "fighting" to R.drawable.fighting,
        "bug" to R.drawable.bug,
        "steel" to R.drawable.steel,
        "dragon" to R.drawable.dragon,
        "dark" to R.drawable.dark,
        "fairy" to R.drawable.fairy
    )

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
                    for (type in pokemon.types) {
                        typeToResourceMap[type.type.name]?.let {
                            Image(
                                painter = painterResource(it),
                                contentDescription = type.type.name,
                                modifier = modifier.size(30.dp)
                            )
                        }
                    }
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
                Button(
                    onClick = {
                        onPlayCry()
                    }, modifier = modifier.padding(start = 8.dp),
                    enabled = isMediaPlayerReady
                ) {
                    Text("Cry")
                }
                Text(
                    pokemon.pokeDexEntry.replace("[\n\t\u000c]".toRegex(), " "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            PokemonStatsSection(modifier, stats = pokemon.stats)
        }
    }
}