package com.atfotiad.pokemonexplorerapp.navigation

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.atfotiad.pokemonexplorerapp.tts.TextToSpeechService
import com.atfotiad.pokemonexplorerapp.ui.homeScreen.PokemonViewModel

@Composable
        /**
         * [PokeApp] is a composable function that sets the content of the app.]
         * */
fun PokeApp() {
    val context = LocalContext.current
    var ttsService by remember { mutableStateOf<TextToSpeechService?>(null) }
    var isBound by remember { mutableStateOf(false) }
    val isTtsServiceReady by ttsService?.isReady?.collectAsStateWithLifecycle(initialValue = false)
        ?: remember { mutableStateOf(false) }

    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as TextToSpeechService.LocalBinder
                ttsService = binder.getService()
                isBound = true
                Log.i("PokeApp", "Bound to TTS Service, ready: $isTtsServiceReady")
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                ttsService = null
                isBound = false
                Log.i("PokeApp", "Unbound from TTS Service")
            }
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, TextToSpeechService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        onDispose {
            if (isBound) {
                context.unbindService(serviceConnection)
                isBound = false
                ttsService = null
            }
        }
    }



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        val pokemonViewModel: PokemonViewModel = hiltViewModel()
        PokeNavHost(
            navController,
            pokemonViewModel,
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            ttsService = ttsService,
            isTtsServiceReady = isTtsServiceReady,
            onSpeakPokemonDetails = { pokemonToSpeak ->
                if (isTtsServiceReady && ttsService != null) {
                    val typeText = if (pokemonToSpeak.types.size == 1) {
                        pokemonToSpeak.types[0].type.name + "type pokemon."
                    } else if (pokemonToSpeak.types.size == 2) {
                        pokemonToSpeak.types[0].type.name + " and " + pokemonToSpeak.types[1].type.name + "type pokemon,"
                    } else {
                        "unknown type" // Handle cases with 0 or more than 2 types (rare)
                    }
                    val statsText = ". its highest stat is " + pokemonToSpeak.stats.maxByOrNull { it.baseStat }?.stat?.name +
                            "with a base stat of " + pokemonToSpeak.stats.maxByOrNull { it.baseStat }?.baseStat + "."
                    ttsService!!.speak(
                        pokemonToSpeak.name + "is a " + typeText + pokemonToSpeak.pokeDexEntry.replace(
                            "[\n\t\u000c]".toRegex(),
                            " "
                        ) + ". " + statsText
                    )

                } else {
                    Log.w("PokeApp", "TTS Service not ready to speak ${pokemonToSpeak.name}")
                }
            }
        )
    }
}
