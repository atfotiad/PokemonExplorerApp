package com.atfotiad.pokemonexplorerapp.data.model

data class BaseResponse(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<Result>
)
