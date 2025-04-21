package com.atfotiad.pokemonexplorerapp.model

import com.google.gson.annotations.SerializedName

data class Other(
    @SerializedName("official-artwork")
    val officialArtwork: OfficialArtwork
)
