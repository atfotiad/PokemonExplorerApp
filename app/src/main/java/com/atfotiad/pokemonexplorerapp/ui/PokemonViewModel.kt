package com.atfotiad.pokemonexplorerapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.atfotiad.pokemonexplorerapp.PokeRepository
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokeRepository
) : ViewModel() {

    val pokemonList: Flow<PagingData<Pokemon>> =
        repository.getPokeResults().cachedIn(viewModelScope)

}
