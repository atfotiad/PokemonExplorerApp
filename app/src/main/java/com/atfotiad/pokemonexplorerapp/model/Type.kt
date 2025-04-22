package com.atfotiad.pokemonexplorerapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Type(
    val slot: Int,
    val type: TypeX
) : Parcelable


