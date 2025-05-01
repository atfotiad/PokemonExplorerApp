package com.atfotiad.pokemonexplorerapp.data.source

import com.atfotiad.pokemonexplorerapp.api.PokeClient
import com.atfotiad.pokemonexplorerapp.data.model.BaseResponse
import com.atfotiad.pokemonexplorerapp.data.model.Cry
import com.atfotiad.pokemonexplorerapp.data.model.Home
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
import com.atfotiad.pokemonexplorerapp.data.source.remote.PokeRemoteDataSource
import com.atfotiad.pokemonexplorerapp.utils.repository.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import retrofit2.Response
import java.io.IOException

class PokeRemoteDataSourceTest {

    private lateinit var pokeApi: PokeClient
    private lateinit var remoteDataSource: PokeRemoteDataSource

    @Before
    fun setup() {
        pokeApi = mock()
        remoteDataSource = PokeRemoteDataSource(pokeApi)
    }

    @Test
    fun `getPokemonList returns Success with data on successful API call`() = runBlocking {
        // Arrange
        val expectedResponse = BaseResponse(
            count = 100,
            next = "next",
            previous = "previous",
            results = listOf()
        )
        Mockito.`when`(pokeApi.getAllPokemon(0, 20)).thenReturn(Response.success(expectedResponse))

        // Act
        val result = remoteDataSource.getPokemonList(0, 20)

        // Assert
        Assert.assertTrue(result is Result.Success)
        Assert.assertEquals(expectedResponse, (result as Result.Success).data)
    }

    @Test
    fun `getPokemonList returns Error on API exception`() = runBlocking {
        // Arrange
        val exception = IOException("API error")
        Mockito.`when`(pokeApi.getAllPokemon(0, 20)).thenThrow(exception)

        // Act
        val result = remoteDataSource.getPokemonList(0, 20)
        // Assert
        Assert.assertTrue(result is Result.NetworkError)
        Assert.assertEquals("API error", (result as Result.NetworkError).exception.message)
    }

    @Test
    fun `getPokemonInfo(name) returns Success with data on successful API call`() = runBlocking {
        // Arrange
        val expectedResponse =
            PokemonResponse(
                id = 1, name = "bulbasaur",
                types = listOf(Type(1, TypeX("grass", "url"))),
                stats = listOf(
                    Stat(45, 1, StatX("hp", "")),
                    Stat(49, 1, StatX("attack", "")),
                    Stat(49, 1, StatX("defense", "")),
                    Stat(65, 1, StatX("special-attack", "")),
                    Stat(65, 1, StatX("special-defense", "")),
                    Stat(45, 1, StatX("speed", ""))
                ),
                cries = Cry("", ""),
                sprites = Sprites(Other(OfficialArtwork("url"), Home("url"))),
                species = Species("bulbasaur-species", ""),
            )
        Mockito.`when`(pokeApi.getPokemonInfoByName("bulbasaur"))
            .thenReturn(Response.success(expectedResponse))

        // Act
        val result = remoteDataSource.getPokemonInfo("bulbasaur")

        // Assert
        Assert.assertTrue(result is Result.Success)
        Assert.assertEquals(
            expectedResponse, (result as Result.Success).data
        )
    }

    @Test
    fun `getPokemonInfo(name) returns Error on API exception`() = runBlocking {
        // Arrange
        val exception = RuntimeException("API error")
        Mockito.`when`(pokeApi.getPokemonInfoByName("pikachu")).thenThrow(exception)

        // Act
        val result = remoteDataSource.getPokemonInfo("pikachu")

        // Assert
        Assert.assertTrue(result is Result.Error)
        Assert.assertEquals("API error", (result as Result.Error).exception.message)

    }

    @Test
    fun `getPokemonInfoByName returns NotFoundError on 404 API response`() = runBlocking {
        // Arrange
        val errorResponse = Response.error<PokemonResponse>(404, mock()) // Mock a 404 response for PokemonResponse
        Mockito.`when`(pokeApi.getPokemonInfoByName("nonexistentpokemon"))
            .thenReturn(errorResponse)

        // Act
        val result = remoteDataSource.getPokemonInfo("nonexistentpokemon")

        // Assert
        Assert.assertTrue(result is Result.NotFoundError)
        Assert.assertEquals("Pokemon not found", (result as Result.NotFoundError).exception.message)
    }


    @Test
    fun `getPokemonInfo(id) returns Success with data on successful API call`() = runBlocking {
        // Arrange
        val expectedResponse =
            PokemonResponse(
                id = 25, name = "pikachu",
                types = listOf(Type(1, TypeX("grass", "url"))),
                stats = listOf(
                    Stat(45, 1, StatX("hp", "")),
                    Stat(49, 1, StatX("attack", "")),
                    Stat(49, 1, StatX("defense", "")),
                    Stat(65, 1, StatX("special-attack", "")),
                    Stat(65, 1, StatX("special-defense", "")),
                    Stat(45, 1, StatX("speed", ""))
                ),
                cries = Cry("", ""),
                sprites = Sprites(Other(OfficialArtwork("url"), Home("url"))),
                species = Species("bulbasaur-species", ""),
            )
        Mockito.`when`(pokeApi.getPokemonInfo(25))
            .thenReturn(Response.success(expectedResponse))

        // Act
        val result = remoteDataSource.getPokemonInfo(25)

        // Assert
        Assert.assertTrue(result is Result.Success)
        Assert.assertEquals(expectedResponse, (result as Result.Success).data)
    }

    @Test
    fun `getPokemonInfo(id) returns Error on API exception`() = runBlocking {
        // Arrange
        val exception = IOException("API error")
        Mockito.`when`(pokeApi.getPokemonInfo(1)).thenThrow(exception)

        // Act
        val result = remoteDataSource.getPokemonInfo(1)
        //Assert
        Assert.assertTrue(result is Result.NetworkError)
        Assert.assertEquals("API error", (result as Result.NetworkError).exception.message)
    }

    @Test
    fun `getSpeciesEntry returns Success with data on successful API call`() = runBlocking {
        // Arrange
        val expectedResponse = PokeDexEntryText(flavorTextEntries = listOf())
        Mockito.`when`(pokeApi.getSpeciesEntry(1))
            .thenReturn(Response.success(expectedResponse))

        // Act
        val result = remoteDataSource.getSpeciesEntry(1)

        // Assert
        Assert.assertTrue(result is Result.Success)
        Assert.assertEquals(expectedResponse, (result as Result.Success).data)
    }

    @Test
    fun `getSpeciesEntry returns Error on API exception`() = runBlocking {
        // Arrange
        val exception = Exception("API error")
        Mockito.`when`(pokeApi.getSpeciesEntry(1)).thenThrow(exception)

        // Act
        val result = remoteDataSource.getSpeciesEntry(1)

        //Assert
        Assert.assertTrue(result is Result.Error)
        Assert.assertEquals("API error", (result as Result.Error).exception.message)

    }
}