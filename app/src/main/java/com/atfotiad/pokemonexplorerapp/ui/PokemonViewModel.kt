package com.atfotiad.pokemonexplorerapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atfotiad.pokemonexplorerapp.PokeRepository
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Set<String>>(emptySet())
    val selectedFilters: StateFlow<Set<String>> = _selectedFilters.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _allPokemon = MutableStateFlow<List<Pokemon>>(emptyList())
    val totalPokemonCount: StateFlow<Int> = repository.totalPokemonCount

    @OptIn(FlowPreview::class)
    val displayPokemonItems: StateFlow<List<Pokemon>> = combine(
        _searchQuery, _selectedFilters, _allPokemon,
    ) { query, filters, allPokemon ->
        filterPokemon(query, filters, allPokemon)
    }.debounce(500).stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private var offset = 0
    private val limit = 10

    init {
        load()
    }


    fun setSearchQuery(query: String) {
        _searchQuery.update { query }
    }

    fun updateFilters(selectedFilters: Set<String>) {
        _selectedFilters.update { selectedFilters }
    }

    fun loadMore() {
        offset += 10
        load()
    }

    private fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            repository.getPokemonList(offset, limit).collect { newPokemon ->
                val currentList = _allPokemon.value.toMutableList()
                currentList.addAll(newPokemon)
                _allPokemon.update {
                    currentList
                }
                _isLoading.value = false
            }
        }
    }

    private fun filterPokemon(
        query: String,
        filters: Set<String>,
        allPokemon: List<Pokemon>
    ): List<Pokemon> {

        val lowerCaseFilters = filters.map { it.lowercase() }
        return allPokemon.filter { pokemon ->
            val pokemonTypes = pokemon.types.map { it.type.name }
            val matchesQuery = query.isBlank() || pokemon.name.contains(query, ignoreCase = true)
            val matchesFilters =
                lowerCaseFilters.isEmpty() || pokemonTypes.any { it in lowerCaseFilters }

            matchesQuery && matchesFilters
        }
    }
}
