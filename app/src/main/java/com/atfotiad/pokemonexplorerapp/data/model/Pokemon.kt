package com.atfotiad.pokemonexplorerapp.data.model

import android.os.Parcelable
import com.atfotiad.pokemonexplorerapp.data.UrlSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Pokemon(
    val id: Int,
    val name: String,
    val types: List<Type>,
    val stats: List<Stat>,
    val species: Species,
    @Serializable(with = UrlSerializer::class)
    val cry: String,
    @Serializable(with = UrlSerializer::class)
    val imageUrl: String,
    val pokeDexEntry: String
) : Parcelable


