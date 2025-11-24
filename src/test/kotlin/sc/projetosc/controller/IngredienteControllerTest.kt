package sc.projetosc.controller


import sc.projetosc.services.IngredienteServices
import sc.projetosc.request.IngredienteRequest
import sc.projetosc.entity.Ingrediente
import sc.projetosc.entity.TipoIngrediente
import sc.projetosc.repository.IngredienteRepository
import sc.projetosc.repository.TipoIngredienteRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import kotlin.test.assertEquals

class IngredienteControllerTest {
    private val repository = mock(IngredienteRepository::class.java)
    private val tipoIngredienteRepository = mock(TipoIngredienteRepository::class.java)
    private val service = IngredienteServices(repository, tipoIngredienteRepository)
    private val controller = IngredienteController(repository, service)

    lateinit var ingrediente: Ingrediente

    @BeforeEach
    fun setUp() {
        ingrediente = Ingrediente(
            idIngrediente = 1,
            nome = "Recheio de Jujuba",
            premium = true,
            ativo = true
        )
    }

    @Test
    @DisplayName("Testando criação de ingrediente com status 201")
    fun criarIngrediente() {
        val tipoIngrediente = TipoIngrediente(
            idTipoIngrediente = 1,
            descricao = "Recheio",
            quantidadeMaxima = 10
        )
        val ingredienteCriado = Ingrediente(
            idIngrediente = 1,
            nome = "Recheio de Jujuba",
            premium = true,
            ativo = true,
            tipoIngrediente = tipoIngrediente
        )
        val dto = IngredienteRequest(
            idTipoIngrediente = 1,
            nome = "Recheio de Jujuba",
            is_premium = true,
            ativo = true
        )
        `when`(tipoIngredienteRepository.findById(1)).thenReturn(java.util.Optional.of(tipoIngrediente))
        `when`(repository.save(any(Ingrediente::class.java))).thenReturn(ingredienteCriado)

        val response = controller.criarIngrediente(dto)

        assertEquals(201, response.statusCode.value())
        val body = response.body as Ingrediente
        assertEquals(1, body.idIngrediente)
        assertEquals("Recheio de Jujuba", body.nome)
        assertEquals(true, body.ativo)
        assertEquals("Recheio", body.tipoIngrediente?.descricao)

        verify(tipoIngredienteRepository, times(1)).findById(1)
        verify(repository, times(1)).save(any(Ingrediente::class.java))
    }

    @Test
    @DisplayName("Testando atualização de ingrediente com status 200")
    fun atualizarIngrediente() {
        val dto = IngredienteRequest(
            idTipoIngrediente = 2,
            nome = "Recheio de Jujuba Atualizado",
            is_premium = true,
            ativo = true
        )
        `when`(repository.findById(1)).thenReturn(java.util.Optional.of(ingrediente))
        `when`(repository.save(any(Ingrediente::class.java))).thenAnswer { it.getArgument(0) }

        val response = controller.atualizarIngrediente(1, dto)

        assertEquals(200, response.statusCode.value())
        val body = response.body as Ingrediente
        assertEquals("Recheio de Jujuba Atualizado", body.nome)
        assertEquals(true, body.premium)
        assertEquals(true, body.ativo)

        verify(repository, times(1)).findById(1)
        verify(repository, times(1)).save(any(Ingrediente::class.java))
    }

    @Test
    @DisplayName("Testando atualização de status de ingrediente para desativá-lo")
    fun atualizarStatusIngrediente() {
        val ingredienteAtivo = Ingrediente(
            idIngrediente = 1,
            nome = "Recheio de Jujuba",
            premium = true,
            ativo = true
        )
        `when`(repository.findById(1)).thenReturn(java.util.Optional.of(ingredienteAtivo))
        `when`(repository.save(any(Ingrediente::class.java))).thenAnswer { it.getArgument(0) }

        val response = service.atualizarStatusIngrediente(1)

        assertEquals(200, response.statusCode.value())
        val body = response.body as Ingrediente
        assertEquals(false, body.ativo)

        verify(repository, times(1)).findById(1)
        verify(repository, times(1)).save(any(Ingrediente::class.java))
    }

    @Test
    @DisplayName("Testando listagem de ingredientes com status 200")
    fun listarIngredientes() {
        val ingredientes = listOf(ingrediente)
        `when`(repository.findByAtivoTrue()).thenReturn(ingredientes)

        val response = controller.listarIngredientes(null, true)

        assertEquals(200, response.statusCode.value())
        assertEquals(1, response.body?.size)
        assertEquals("Recheio de Jujuba", response.body?.get(0)?.nome)

        verify(repository, times(1)).findByAtivoTrue()
    }
}