package com.atfotiad.pokemonexplorerapp.ui

import androidx.lifecycle.ViewModel
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class PokemonViewModel : ViewModel(){

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    val pokemonList: StateFlow<List<Pokemon>> = _pokemonList

}
