package com.atfotiad.pokemonexplorerapp.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.atfotiad.pokemonexplorerapp.navigation.PokeNavHost

@Composable
fun PokeApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        val pokemonViewModel: PokemonViewModel = hiltViewModel()
        PokeNavHost(
            navController,
            pokemonViewModel,
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
        )
    }
}
