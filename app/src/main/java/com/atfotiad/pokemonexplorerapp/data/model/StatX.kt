package com.atfotiad.pokemonexplorerapp.data.model

import android.os.Parcelable
import com.atfotiad.pokemonexplorerapp.data.UrlSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class StatX(
    val name: String,
    @Serializable(with = UrlSerializer::class)
    val url: String
) : Parcelable


