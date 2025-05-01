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
import com.atfotiad.pokemonexplorerapp.data.repository.PokeRepository
import com.atfotiad.pokemonexplorerapp.data.source.remote.PokeRemoteDataSource
import com.atfotiad.pokemonexplorerapp.ui.homeScreen.emptyPokemon
import com.atfotiad.pokemonexplorerapp.utils.repository.Result.Success
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import javax.inject.Inject

@HiltAndroidTest
@RunWith(JUnit4::class)
class PokeRepositoryImplTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val remoteDataSource: PokeRemoteDataSource = mock()

    @Inject
    lateinit var repository: PokeRepository // Inject the interface

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun getPokemonByNameReturnsSuccessWithPokemonOnSuccessfulRemoteFetch() =
        runBlocking {
            println("Hello from Hilt Test")

            // Arrange
            val pokemonName = "bulbasaur"
            val pokemonId = 1
            val expectedFlavorText = "A grass-type Pokémon."
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
            whenever(remoteDataSource.getPokemonInfo(pokemonName)).thenReturn(
                Success(
                    pokemonResponse
                )
            )
            whenever(remoteDataSource.getSpeciesEntry(pokemonId)).thenReturn(Success(speciesEntry))

            val expectedFlow = flowOf(
                Success(
                    emptyPokemon.copy(
                        id = pokemonId,
                        name = pokemonName,
                        pokeDexEntry = expectedFlavorText
                    )
                )
            )

            whenever(repository.getPokemonByName(pokemonName)).thenReturn(expectedFlow)

            // Act
            val result = repository.getPokemonByName(pokemonName).first()

            // Assert
            verify(remoteDataSource).getPokemonInfo(pokemonName)
            assertTrue(result is Success)
            val pokemon = (result as Success).data
            assertNotNull(pokemon)
            println("Actual pokeDexEntry: ${pokemon?.pokeDexEntry}")
            assertEquals(pokemonId, pokemon?.id)
            assertEquals(pokemonName, pokemon?.name)
            assertEquals(expectedFlavorText, pokemon?.pokeDexEntry)
        }
}