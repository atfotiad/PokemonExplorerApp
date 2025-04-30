package com.atfotiad.pokemonexplorerapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Type(
    val slot: Int,
    val type: TypeX
) : Parcelable


