package com.atfotiad.pokemonexplorerapp.data.model

import com.google.gson.annotations.SerializedName

data class Other(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork,
    val home: Home
)
