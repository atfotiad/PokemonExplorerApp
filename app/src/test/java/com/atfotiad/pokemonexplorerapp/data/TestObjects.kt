package com.atfotiad.pokemonexplorerapp.data

import com.atfotiad.pokemonexplorerapp.data.model.Cry
import com.atfotiad.pokemonexplorerapp.data.model.FlavorText
import com.atfotiad.pokemonexplorerapp.data.model.Home
import com.atfotiad.pokemonexplorerapp.data.model.Language
import com.atfotiad.pokemonexplorerapp.data.model.OfficialArtwork
import com.atfotiad.pokemonexplorerapp.data.model.Other
import com.atfotiad.pokemonexplorerapp.data.model.PokeDexEntryText
import com.atfotiad.pokemonexplorerapp.data.model.PokemonResponse
import com.atfotiad.pokemonexplorerapp.data.model.Species
import com.atfotiad.pokemonexplorerapp.data.model.Sprites
import com.atfotiad.pokemonexplorerapp.data.model.Stat
import com.atfotiad.pokemonexplorerapp.data.model.StatX
import com.atfotiad.pokemonexplorerapp.data.model.Type
import com.atfotiad.pokemonexplorerapp.data.model.TypeX

const val pokemonName = "bulbasaur"
const val pokemonId = 1
const val expectedFlavorText = "A grass-type Pokémon."
val pokemonResponse = PokemonResponse(
    id = pokemonId,
    name = pokemonName,
    types = listOf(Type(1, TypeX("grass", "url"))),
    stats = listOf(Stat(45, 1, StatX("hp", ""))),
    species = Species("bulbasaur-species", "url/pokemon-species/$pokemonId/"),
    cries = Cry("", ""),
    sprites = Sprites(Other(OfficialArtwork("url"), Home("url"))),
)
val speciesEntry = PokeDexEntryText(
    flavorTextEntries = listOf(
        FlavorText(
            flavorText = expectedFlavorText,
            language = Language("en")
        )
    )
)