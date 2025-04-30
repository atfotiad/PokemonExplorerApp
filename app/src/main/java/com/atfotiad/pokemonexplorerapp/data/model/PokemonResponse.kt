package com.atfotiad.pokemonexplorerapp.data.model

data class PokemonResponse(
    val id: Int,
    val name: String,
    val types: List<Type>,
    val stats: List<Stat>,
    val cries: Cry,
    val sprites: Sprites,
    val species: Species
)
