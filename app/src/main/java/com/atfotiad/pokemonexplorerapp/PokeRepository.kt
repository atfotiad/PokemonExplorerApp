package com.atfotiad.pokemonexplorerapp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.atfotiad.pokemonexplorerapp.api.PokeClient
import com.atfotiad.pokemonexplorerapp.paging.PokeDataSource
import com.atfotiad.pokemonexplorerapp.paging.loadSize
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class PokeRepository @Inject constructor(
    private val pokeApi: PokeClient
) {
    fun getPokeResults() = Pager(
        config = PagingConfig(
            pageSize = loadSize,
            initialLoadSize = loadSize,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { PokeDataSource(pokeApi) }
    ).flow
}