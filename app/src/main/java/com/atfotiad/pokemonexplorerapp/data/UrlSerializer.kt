package com.atfotiad.pokemonexplorerapp.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
/**
 * [UrlSerializer] is a serializer for URL strings.
 * It is used to serialize and deserialize URL strings to and from the network.
 * */
object UrlSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("UrlEncodedString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        return URLDecoder.decode(decoder.decodeString(), StandardCharsets.UTF_8.toString())
    }

    override fun serialize(encoder: Encoder, value: String) {
        val encodedString = URLEncoder.encode(value, StandardCharsets.UTF_8.toString())
        encoder.encodeString(encodedString)
    }

}

