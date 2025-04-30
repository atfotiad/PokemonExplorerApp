package com.atfotiad.pokemonexplorerapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atfotiad.pokemonexplorerapp.PokeRepository
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import com.atfotiad.pokemonexplorerapp.model.Species
import com.atfotiad.pokemonexplorerapp.utils.repository.RepoUtils.getOrErrorMessage
import com.atfotiad.pokemonexplorerapp.utils.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _stateUI = MutableStateFlow<StateUI<List<Pokemon>>>(StateUI.Loading)
    val stateUI: StateFlow<StateUI<List<Pokemon>>> = _stateUI.asStateFlow()

    private val _isSearchingByName = MutableStateFlow(false)
    val isSearchingByName: StateFlow<Boolean> = _isSearchingByName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _displayPokemonItems = MutableStateFlow<List<Pokemon>>(emptyList())
    private val _allPokemon = MutableStateFlow<List<Pokemon>>(emptyList())
    val totalPokemonCount: StateFlow<Int> = repository.totalPokemonCount

    private var offset = 0
    private val limit = 10

    init {
        load()
    }


    fun setSearchQuery(query: String) {
        _searchQuery.update { query }
        _isSearchingByName.value = query.isNotBlank()
    }

    fun updateFilters(selectedFilters: Set<String>) {
        _selectedFilters.update { selectedFilters }
        _stateUI.value = filterPokemon(_searchQuery.value, selectedFilters, _allPokemon.value)
    }

    fun clearAllFilters() {
        _selectedFilters.update { emptySet() }
        _stateUI.value = filterPokemon(_searchQuery.value, emptySet(), _allPokemon.value)
    }

    fun loadMore() {
        offset += 10
        load()
    }


    fun performSearch() {
        val currentQuery = _searchQuery.value.trim()
        if (currentQuery.isNotBlank()) {
            _stateUI.value = filterPokemon(currentQuery, _selectedFilters.value, _allPokemon.value)
            if ((stateUI.value as? StateUI.Success)?.data?.isEmpty() == true) {
                _isSearchingByName.update { true }
                searchPokemonByName(currentQuery)
            } else {
                _isSearchingByName.update { false }
            }
        } else {
            _stateUI.value = filterPokemon("", _selectedFilters.value, _allPokemon.value)
            _isSearchingByName.update { false }
        }
    }

    fun searchPokemonByName(pokemonName: String) {
        var displayPokemon: Pokemon? = null
        viewModelScope.launch{
            _isLoading.value = true
            repository.getPokemonByName(pokemonName).collect { result ->
                val fetchedPokemon = result.getOrErrorMessage()
                    if (result is Result.Success) {
                        displayPokemon = fetchedPokemon!!
                        if (_searchQuery.value.trim() == pokemonName.trim()) {
                                //Check for duplicates and add to allPokemon list and sort by id
                                if (!_allPokemon.value.any { it.id == displayPokemon.id }) {
                                    val updatedList = _allPokemon.value.toMutableList()
                                    updatedList.add(displayPokemon)
                                    _allPokemon.update { updatedList.sortedBy { it.id } }
                                }
                                val filteredResult = listOf(displayPokemon).filter { pokemon ->
                                    val pokemonTypes = pokemon.types.map { it.type.name }
                                    val lowerCaseFilters =
                                        _selectedFilters.value.map { it.lowercase() }
                                    lowerCaseFilters.isEmpty() || pokemonTypes.any { it in lowerCaseFilters }
                                }
                                _displayPokemonItems.value = filteredResult
                                _stateUI.value = StateUI.Success(filteredResult)
                                _isSearchingByName.update { false }

                        }
                        _isLoading.value = false
                    } else if (result is Result.Error) {

                        Log.i("Error", "searchPokemonByName: Fetched Pokemon: $fetchedPokemon")
                        _stateUI.value = StateUI.Empty
                        _isLoading.value = false
                        _isSearchingByName.update { false }
                    }
            }
        }
    }

    private fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.update { true }
            repository.getPokemonList(offset, limit).collect { result ->
                if (result is Result.Success ){
                    val newPokemonList = result.data ?: emptyList()
                    _allPokemon.update {
                        (it + newPokemonList).distinctBy { it.id }.sortedBy { it.id }
                    }
                    _stateUI.value = filterPokemon(
                        _searchQuery.value,
                        _selectedFilters.value,
                        _allPokemon.value
                    )
                } else if (result is Result.Error) {
                    _stateUI.value = StateUI.Error(null)
                }
            }
            _isLoading.update { false }
            Log.i("isSearchingByName", "load: ${_isSearchingByName.value}")
        }
    }

    private fun filterPokemon(
        query: String,
        filters: Set<String>,
        allPokemon: List<Pokemon>
    ): StateUI<List<Pokemon>> {

        val lowerCaseFilters = filters.map { it.lowercase() }
        val filteredList = allPokemon.filter { pokemon ->
            val pokemonTypes = pokemon.types.map { it.type.name }
            val matchesQuery =
                query.isBlank() || pokemon.name.contentEquals(query, ignoreCase = true)
            val matchesFilters =
                lowerCaseFilters.isEmpty() || pokemonTypes.any { it in lowerCaseFilters }

            matchesQuery && matchesFilters
        }
       return StateUI.Success(filteredList)
    }
}

sealed class StateUI<out T> {
    object Loading : StateUI<Nothing>()
    data class Success<T>(val data: T) : StateUI<T>()
    data class Error(val message: String?) : StateUI<Nothing>()
    object Empty : StateUI<Nothing>()
}

val emptyPokemon = Pokemon(
    0,
    "",
    emptyList(),
    emptyList(),
    Species("", ""),
    "",
    "",
    ""
)