package com.atfotiad.pokemonexplorerapp.data.model

import com.google.gson.annotations.SerializedName

data class FlavorText(
    @SerializedName("flavor_text")
    val flavorText: String,
    val language: Language,
)

