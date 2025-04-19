package com.atfotiad.pokemonexplorerapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pokemon(
    val id: Int,
    val name: String,
    val types: List<Type>,
    val stats: List<Stat>,
    val cry: String,
    val imageUrl: String,
    val pokeDexEntry: String
): Parcelable


