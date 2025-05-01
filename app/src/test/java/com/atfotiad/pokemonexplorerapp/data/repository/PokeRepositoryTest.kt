package com.atfotiad.pokemonexplorerapp.data.repository

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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock

class PokeRepositoryImplTest {

    private lateinit var remoteDataSource: PokeRemoteDataSource
    private lateinit var repository: PokeRepositoryImpl

    // Mock data
    private val mockPokemonResponse = PokemonResponse(
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
    private val mockSpeciesEntry = PokeDexEntryText(flavorTextEntries = listOf())

    @Before
    fun setup() {
        remoteDataSource = mock()
        repository = PokeRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getPokemonByName returns Error when remoteDataSource getSpeciesEntry fails`() =
        runBlocking {
            // Arrange
            val pokemonName = "bulbasaur"
            val pokemonId = 1
            val pokemonResponse = PokemonResponse(
                id = pokemonId,
                name = pokemonName,
                types = emptyList(),
                stats = emptyList(),
                species = Species("test", "url/1/"), // Simple URL
                cries = Cry("", ""),
                sprites = Sprites(Other(OfficialArtwork("url"), Home("url"))),
            )
            val speciesErrorMessage = "Failed to fetch species entry"

            `when`(remoteDataSource.getPokemonInfo(pokemonName)).thenReturn(
                Result.Success(
                    pokemonResponse
                )
            )

            // Capture the argument passed to getSpeciesEntry
            val captor = argumentCaptor<Int>()
            `when`(remoteDataSource.getSpeciesEntry(captor.capture())).thenReturn(
                Result.Error(Exception(speciesErrorMessage))
            )

            // Act
            val results =
                repository.getPokemonByName(pokemonName).toList() // Collect all emitted values

            // Assert
            assertTrue(results.any { it is Result.Error })
            val errorResult = results.find { it is Result.Error } as? Result.Error
            assertEquals(speciesErrorMessage, errorResult?.exception?.message)

            // Check if getSpeciesEntry was called
            assertEquals(pokemonId, captor.firstValue)
        }
}