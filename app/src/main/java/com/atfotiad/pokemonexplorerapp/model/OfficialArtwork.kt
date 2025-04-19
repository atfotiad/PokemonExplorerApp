package com.atfotiad.pokemonexplorerapp.model

import com.google.gson.annotations.SerializedName

data class OfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String
)
