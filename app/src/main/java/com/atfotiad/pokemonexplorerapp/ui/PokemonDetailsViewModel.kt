package com.atfotiad.pokemonexplorerapp.ui

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PokemonDetailsViewModel @Inject constructor(
   savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow<Pokemon?>(null)
    val state: StateFlow<Pokemon?> = _state
    private var mediaPlayer: MediaPlayer? = null

    init {
        if (savedStateHandle.contains("pokemon")) {
            _state.value = savedStateHandle["pokemon"]
        }
    }

    fun playCry(pokemon: Pokemon) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(pokemon.cry)
                prepare()
                start()
            }
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