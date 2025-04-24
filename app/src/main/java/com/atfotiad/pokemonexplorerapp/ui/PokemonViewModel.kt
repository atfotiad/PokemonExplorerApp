package com.atfotiad.pokemonexplorerapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atfotiad.pokemonexplorerapp.PokeRepository
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokeRepository
) : ViewModel() {

    private val _pokemonList = MutableStateFlow<List<Pokemon>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilters = MutableStateFlow<Set<String>>(emptySet())
    val selectedFilters: StateFlow<Set<String>> = _selectedFilters.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val state = combine(
        _pokemonList,
        _searchQuery,
        _selectedFilters
    ) { pokemonList, searchQuery, selectedFilters ->
        pokemonList.filter { pokemon ->
            val filterCondition =
                pokemon.types.any { type ->
                    selectedFilters.any {
                        it.lowercase() == type.type.name.lowercase()
                    }
                }
            val searchCondition =
                pokemon.name.contains(searchQuery, ignoreCase = true)

            when {
                selectedFilters.isEmpty() && searchQuery.isBlank() -> true
                selectedFilters.isEmpty() && searchQuery.isNotBlank() -> searchCondition
                else -> filterCondition && searchCondition
            }
        }
    }

    init {
        getPokemon()
    }

    private fun getPokemon() {
        repository.getPokemonList().onEach { pokemonList ->
            _pokemonList.value = pokemonList
        }.launchIn(viewModelScope)
    }

    fun updateFilters(selectedFilters: Set<String>) {
        _selectedFilters.update { selectedFilters }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.update { query }
    }
}
