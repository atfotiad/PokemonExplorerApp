package com.atfotiad.pokemonexplorerapp.ui.detailsScreen

import android.media.MediaPlayer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.atfotiad.pokemonexplorerapp.data.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
/**
 *  [PokemonDetailsViewModel] is a class that handles the state for the Pokemon details screen.
 *  @param savedStateHandle is an instance of [SavedStateHandle]
 * */
class PokemonDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow<Pokemon?>(null)
    val state: StateFlow<Pokemon?> = _state
    private var mediaPlayer: MediaPlayer? = null
    private val _isMediaPlayerReady = MutableStateFlow(false)
    val isMediaPlayerReady: StateFlow<Boolean> = _isMediaPlayerReady

    init {
        if (savedStateHandle.contains("pokemon")) {
            _state.value = savedStateHandle["pokemon"]
        }
    }

    fun setMediaPlayer(mediaPlayer: MediaPlayer) {
        this.mediaPlayer = mediaPlayer
        _isMediaPlayerReady.update { true }
    }

    fun resetMediaPlayerReady() {
        _isMediaPlayerReady.update { false }
    }

    fun playCry() {
        try {
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}