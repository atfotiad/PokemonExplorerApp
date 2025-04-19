package com.atfotiad.pokemonexplorerapp.model

import com.google.gson.annotations.SerializedName

data class PokeDexEntryText(
    @SerializedName("flavor_text_entries")
    val flavorTextEntries: List<FlavorText>,
)

