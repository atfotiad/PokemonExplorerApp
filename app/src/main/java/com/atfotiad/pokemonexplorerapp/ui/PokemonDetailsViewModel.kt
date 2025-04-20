package com.atfotiad.pokemonexplorerapp.ui

import androidx.lifecycle.ViewModel
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PokemonDetailsViewModel: ViewModel() {
    private val _pokemon = MutableStateFlow<Pokemon?>(null)
    val pokemon: StateFlow<Pokemon?> = _pokemon
}