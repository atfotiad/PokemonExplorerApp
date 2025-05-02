package com.atfotiad.pokemonexplorerapp.utils.navigation

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
/**
 *  [toNavType] is an extension function that converts a Parcelable type to a NavType
 *  Useful to pass [Parcelable] in object based destinations in navigation
 * */
inline fun <reified T : Parcelable> NavType.Companion.toNavType(): NavType<T> {

    return object : NavType<T>(
        isNullableAllowed = false
    ) {
        override fun get(bundle: Bundle, key: String): T? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(key, T::class.java)
            } else {
                @Suppress("DEPRECATION")
                bundle.getParcelable(key)
            }
        }

        override fun parseValue(value: String): T {
            return Json.decodeFromString<T>(value)
        }

        override fun put(bundle: Bundle, key: String, value: T) {
            bundle.putParcelable(key, value)
        }

        override fun serializeAsValue(value: T) = Json.encodeToString(value)

        override val name = T::class.java.name

    }
}