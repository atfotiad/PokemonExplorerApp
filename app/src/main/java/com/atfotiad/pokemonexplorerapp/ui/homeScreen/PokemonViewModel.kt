package com.atfotiad.pokemonexplorerapp.ui.homeScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atfotiad.pokemonexplorerapp.data.model.Pokemon
import com.atfotiad.pokemonexplorerapp.data.model.Species
import com.atfotiad.pokemonexplorerapp.data.repository.PokeRepository
import com.atfotiad.pokemonexplorerapp.ui.homeScreen.StateUI.Error
import com.atfotiad.pokemonexplorerapp.ui.homeScreen.StateUI.Loading
import com.atfotiad.pokemonexplorerapp.ui.homeScreen.StateUI.Success
import com.atfotiad.pokemonexplorerapp.utils.network.NetworkConnectivityChecker
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
/**
 *  [PokemonViewModel] is a [ViewModel] that provides data for the home screen.
 *  @param repository is an instance of [PokeRepository]
 *  @param networkChecker is an instance of [NetworkConnectivityChecker]
 * */
class PokemonViewModel @Inject constructor(
    private val repository: PokeRepository,
    private val networkChecker: NetworkConnectivityChecker
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilters = MutableStateFlow<Set<String>>(emptySet())
    val selectedFilters: StateFlow<Set<String>> = _selectedFilters.asStateFlow()

    private val _stateUI = MutableStateFlow<StateUI<List<Pokemon>>>(StateUI.Empty)
    val stateUI: StateFlow<StateUI<List<Pokemon>>> = _stateUI.asStateFlow()

    private val _isSearchingByName = MutableStateFlow(false)
    val isSearchingByName: StateFlow<Boolean> = _isSearchingByName.asStateFlow()

    // Loading state for the list helps show the indicator when loading more
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _allPokemon = MutableStateFlow<List<Pokemon>>(emptyList())
    val totalPokemonCount: StateFlow<Int> = repository.totalPokemonCount

    private var offset = 0
    private val limit = 10

    init {
        if (networkChecker.isInternetAvailable()) {
            loadInitialPokemon()
        } else {
            _stateUI.value = Error(null)
        }
    }

    /**
     *  [setSearchQuery] is a function that updates the search query.
     *  @param query is a string that represents the new search query
     *  Re-filters when query is cleared
     * */
    fun setSearchQuery(query: String) {
        _searchQuery.update { query }
        _isSearchingByName.value = query.isNotBlank()
        if (query.isBlank()) {
            performSearch() // Re-filter when search query is cleared
        } else filterAndUpdateState()
    }

    /**
     *  [updateFilters] is a function that updates the selected filters.
     *  @param selectedFilters is a set of strings that represents the new selected filters
     *  Filters and updates the state accordingly
     * */
    fun updateFilters(selectedFilters: Set<String>) {
        _selectedFilters.update { selectedFilters }
        filterAndUpdateState()
    }

    /**
     *  [clearAllFilters] is a function that clears all filters.
     *  Filters and updates the state accordingly
     * */
    fun clearAllFilters() {
        _selectedFilters.update { emptySet() }
        filterAndUpdateState()
    }

    /***
     *  [loadMore] is a function that loads more data.
     *  Loads the unfiltered next 10 pokemon
     *  If filters are on,
     *  it loads the pokemon that match any filter out of the unfiltered list
     * */
    fun loadMore() {
        if (!_isLoading.value) {
            if (networkChecker.isInternetAvailable()) {
                if (_allPokemon.value.isEmpty()) {
                    loadInitialPokemon()
                } else {
                    offset += limit
                    loadPokemonList(false)
                }
            } else {
                _stateUI.value = Error(null)
            }
        }
    }

    /**
     * [performSearch] is a function that performs search in the the api
     * if query is not blank searches by name. If query is Blank filters and Updates the state.
     * */
    fun performSearch() {
        val currentQuery = _searchQuery.value.trim()
        if (currentQuery.isNotBlank()) {
            searchPokemonByName(currentQuery)
        } else {
            filterAndUpdateState()
            _isSearchingByName.update { false }
        }
    }

    /**
     *  [searchPokemonByName] is a function that searches by Name.
     *  - Requires a full name of the pokemon.
     *  @param pokemonName the Full Name of the pokemon to search.
     * */
    fun searchPokemonByName(pokemonName: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            repository.getPokemonByName(pokemonName).collect { result ->
                _isLoading.update { false }
                when (result) {
                    is Result.Success -> {
                        result.data?.let { fetchedPokemon ->
                            updatePokemonList(listOf(fetchedPokemon))
                            filterAndUpdateState()
                            _isSearchingByName.update { false }
                        } ?: run {
                            _stateUI.value = StateUI.Empty
                            _isSearchingByName.update { false }
                        }
                    }

                    is Result.NotFoundError -> {
                        _stateUI.value = StateUI.Empty
                        _isSearchingByName.update { false }
                    }

                    is Result.NetworkError -> {
                        _stateUI.value = Error(null)
                        _isSearchingByName.update { false }
                    }

                    is Result.Error -> {
                        Log.e(
                            "PokemonViewModel",
                            "Error searching by name: ${result.exception.message}"
                        )
                        _stateUI.value = Error("Search failed")
                        _isSearchingByName.update { false }
                    }

                    Result.Loading -> _isLoading.update { true }
                }
            }
        }
    }

    private fun loadInitialPokemon() {
        loadPokemonList(true)
    }

    /**
     *  [loadPokemonList] is a function that loads pokemon.
     *  @param isInitialLoad is a [Boolean] to specify if we are initial loading.
     * */
    private fun loadPokemonList(isInitialLoad: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isInitialLoad) {
                _stateUI.update { Loading }
                _isLoading.update { true }
            } else {
                _isLoading.update { true }
            }
            repository.getPokemonList(offset, limit).collect { result ->
                _isLoading.update { false }
                when (result) {
                    is Result.Success -> {
                        result.data?.let { newPokemonList ->
                            updatePokemonList(newPokemonList)
                            filterAndUpdateState()
                        } ?: run {
                            if (isInitialLoad) _stateUI.value = Success(emptyList())
                        }
                    }

                    is Result.NetworkError -> {
                        _stateUI.value = Error(null)
                    }

                    is Result.NotFoundError -> {
                        Log.i(
                            "PokemonViewModel",
                            "Pokemon list not found (unusual): ${result.exception.message}"
                        )
                        _stateUI.value = Error("Could not load Pokemon list.")
                    }

                    is Result.Error -> {
                        Log.e(
                            "PokemonViewModel",
                            "Error loading Pokemon list: ${result.exception.message}"
                        )
                        _stateUI.value = Error("Failed to load Pokemon")
                    }

                    Result.Loading -> {
                        if (isInitialLoad) _stateUI.update { Loading } else _isLoading.update { true }
                    }

                }
            }
        }
    }

    private fun updatePokemonList(newPokemonList: List<Pokemon>) {
        _allPokemon.update { (it + newPokemonList).distinctBy { it.id }.sortedBy { it.id } }
    }

    private fun filterAndUpdateState() {
        _stateUI.value =
            filterPokemon(_searchQuery.value, _selectedFilters.value, _allPokemon.value)
    }

    /**
     *  [filterPokemon] is the main function that filters pokemon by type.
     *  @param [query] The pokemon to search. Supports partial name
     *  @param [filters] A [Set] of selected filters
     *  @param [allPokemon] The pokemon list unfiltered loaded so far
     * */
    private fun filterPokemon(
        query: String,
        filters: Set<String>,
        allPokemon: List<Pokemon>
    ): StateUI<List<Pokemon>> {
        val lowerCaseQuery = query.trim().lowercase()
        val lowerCaseFilters = filters.map { it.lowercase() }

        val filteredList = allPokemon.filter { pokemon ->
            val nameMatches = pokemon.name.lowercase().contains(lowerCaseQuery)
            val typeMatches = lowerCaseFilters.isEmpty() || pokemon.types.any {
                it.type.name.lowercase() in lowerCaseFilters
            }
            nameMatches && typeMatches
        }

        return if (filteredList.isEmpty() && query.isNotBlank()) {
            StateUI.Empty
        } else {
            Success(filteredList)
        }
    }
}

/**
 *  [StateUI] is a sealed class that represents the state of the UI.
 * */
sealed class StateUI<out T> {
    object Loading : StateUI<Nothing>()
    data class Success<T>(val data: T) : StateUI<T>()
    data class Error(val message: String?) : StateUI<Nothing>()
    object Empty : StateUI<Nothing>()
}

/**
 * [emptyPokemon] is an empty instance of [Pokemon]
 * */
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