package com.atfotiad.pokemonexplorerapp.model

import com.google.gson.annotations.SerializedName

data class Home(
    @SerializedName("front_default")
    val frontDefault: String?
)
