package com.atfotiad.pokemonexplorerapp.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.atfotiad.pokemonexplorerapp.api.PokeClient
import com.atfotiad.pokemonexplorerapp.model.Pokemon
import com.atfotiad.pokemonexplorerapp.model.Result
import retrofit2.HttpException
import java.io.IOException

internal const val loadSize = 10
private val initialPokePageIndex = PagingIndex(limit = loadSize, 0, 0)

class PokeDataSource(
    private val pokeApi: PokeClient
) : PagingSource<PagingIndex, Pokemon>() {


    override suspend fun load(params: LoadParams<PagingIndex>): LoadResult<PagingIndex, Pokemon> {
        val limit = params.key?.limit ?: initialPokePageIndex.limit
        val offset = params.key?.offset ?: initialPokePageIndex.offset

        val finalPokeList: MutableList<Pokemon> = mutableListOf()
        return try {
            val baseResponse = pokeApi.getAllPokemon(offset, limit)
            val pokemonList = baseResponse.body()?.results ?: emptyList()

            for (pokemon: Result in pokemonList) {
                val tokens = pokemon.url.split("/")
                val id = tokens[tokens.lastIndex - 1].toInt()
                val pokemonDetails = pokeApi.getPokemonInfo(id)
                val pokeDexEntryText =
                    pokeApi.getSpeciesEntry(id).body()?.flavorTextEntries?.firstOrNull {
                        it.language.name == "en"
                    }?.flavorText

                val currentPokemon = Pokemon(
                    id,
                    pokemon.name,
                    pokemonDetails.body()?.types.orEmpty(),
                    pokemonDetails.body()?.stats.orEmpty(),
                    pokemonDetails.body()?.cries?.latest.toString(),
                    pokemonDetails.body()?.sprites?.other?.officialArtwork?.frontDefault.toString(),
                    pokeDexEntryText.toString()
                )
                finalPokeList.add(currentPokemon)
            }


            val (prevKey, nextKey) = initialPokePageIndex.pagingIndexing(
                offset = offset,
                totalFromServer = baseResponse.body()?.count ?: 0,
                totalFromPaging = finalPokeList.size
            )
            LoadResult.Page(
                data = finalPokeList,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<PagingIndex, Pokemon>): PagingIndex? {
        return null
    }
}