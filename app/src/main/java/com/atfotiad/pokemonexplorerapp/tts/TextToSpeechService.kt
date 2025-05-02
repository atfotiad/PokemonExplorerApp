package com.atfotiad.pokemonexplorerapp.tts

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class TextToSpeechService : Service(), TextToSpeech.OnInitListener {

    private var ttsEngine: TextToSpeech? = null
    private val binder = LocalBinder()
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    inner class LocalBinder : Binder() {
        fun getService(): TextToSpeechService = this@TextToSpeechService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        ttsEngine = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        Log.i("TTS Service", "onInit() called with status: $status")
        if (status == TextToSpeech.SUCCESS) {
            val result = ttsEngine?.setLanguage(Locale.ENGLISH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS Service", "Language not supported")
            } else {
                Log.i("TTS Service", "TTS engine initialized successfully")
                _isReady.value = true
                Log.i("TTS Service", "onInit: $_isReady")
            }
        } else {
            Log.e("TTS Service", "Initialization failed with status: $status")
        }
    }

    fun speak(text: String) {
        if (_isReady.value && ttsEngine != null) {
            ttsEngine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            Log.w("TTS Service", "TTS engine not ready to speak: $text")
        }
    }

    override fun onDestroy() {
        ttsEngine?.stop()
        ttsEngine?.shutdown()
        ttsEngine = null
        _isReady.value = false
        Log.i("TTS Service", "TTS engine destroyed")
        super.onDestroy()
    }
}