package com.atfotiad.pokemonexplorerapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Type(
    val slot: Int,
    val type: TypeX
): Parcelable


