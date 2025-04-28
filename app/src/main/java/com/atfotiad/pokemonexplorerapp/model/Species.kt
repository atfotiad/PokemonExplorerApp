package com.atfotiad.pokemonexplorerapp.model

import android.os.Parcelable
import com.atfotiad.pokemonexplorerapp.data.UrlSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Species(
    val name: String,
    @Serializable(with = UrlSerializer::class)
    val url: String
) : Parcelable


