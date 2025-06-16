package SC.ProjetoSC.controller

import SC.ProjetoSC.Services.IngredienteServices
import SC.ProjetoSC.dto.RequestIngredienteDto
import SC.ProjetoSC.entity.Ingrediente
import SC.ProjetoSC.repository.IngredienteRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito.*
import kotlin.test.assertEquals

class IngredienteControllerTest {
 private val repository = mock(IngredienteRepository::class.java)
 private val service = IngredienteServices(repository)
 private val controller = IngredienteController(repository, service)

 lateinit var ingrediente: Ingrediente

 @BeforeEach
    fun setUp() {
        ingrediente = Ingrediente(
            idIngrediente = 1,
            descricao = "Recheio de Jujuba",
            precoUnitario = 50.0,
            categoria = "Recheio",
            ativo = true,
            observacao = "Observação do produto"
        )
    }

@Test
@DisplayName("Testando criação de ingrediente com status 201")
 fun criarIngrediente() {
  val dto = RequestIngredienteDto(
    descricao = "Recheio de Jujuba",
    precoUnitario = 50.0,
    categoria = "Recheio",
    ativo = true,
    observacao = "Observação do produto"
  )
    // Simula o comportamento do repositório para retornar o ingrediente criado
    `when`(repository.save(any(Ingrediente::class.java))).thenReturn(ingrediente)
    val response = controller.criarIngrediente(dto)
    // Verifica se o status da resposta é 201 Created e se o corpo contém os dados do ingrediente criado
    assertEquals(201, response.statusCode.value())
    assertEquals("Recheio de Jujuba", (response.body as Ingrediente).descricao)
    assertEquals(50.0, (response.body as Ingrediente).precoUnitario)
    assertEquals("Recheio", (response.body as Ingrediente).categoria)
    assertEquals(true, (response.body as Ingrediente).ativo)
    assertEquals("Observação do produto", (response.body as Ingrediente).observacao)

    // Verifica se o método save foi chamado uma vez com qualquer objeto Ingrediente
    verify(repository, times(1)).save(any(Ingrediente::class.java))
 }

@Test
@DisplayName("Testando atualização de ingrediente com status 200")
 fun atualizarIngrediente() {
    val dto = RequestIngredienteDto(
        descricao = "Recheio de Jujuba Atualizado",
        precoUnitario = 60.0,
        categoria = "Recheio",
        ativo = true,
        observacao = "Observação do produto atualizada"
    )
    // Simula o comportamento do repositório para retornar o ingrediente atualizado
    `when`(repository.findById(1)).thenReturn(java.util.Optional.of(ingrediente))
    `when`(repository.save(any(Ingrediente::class.java))).thenAnswer { it.getArgument(0) }

    val response = controller.atualizarIngrediente(1, dto)

    // Verifica se o status da resposta é 200 OK e se o corpo contém os dados do ingrediente atualizado
    assertEquals(200, response.statusCode.value())
    val body = response.body as Ingrediente
    assertEquals("Recheio de Jujuba Atualizado", body.descricao)
    assertEquals(60.0, body.precoUnitario)
    assertEquals("Recheio", body.categoria)
    assertEquals(true, body.ativo)
    assertEquals("Observação do produto atualizada", body.observacao)

    // Verifica se os métodos findById e save foram chamados corretamente
    verify(repository, times(1)).findById(1)
    verify(repository, times(1)).save(any(Ingrediente::class.java))

 }

@Test
@DisplayName("Testando atualização de status de ingrediente para desativa-lo")
 fun atualizarStatusIngrediente() {
  val ingredienteAtivo = Ingrediente(
    idIngrediente = 1,
    descricao = "Recheio de Jujuba",
    precoUnitario = 50.0,
    categoria = "Recheio",
    ativo = true,
    observacao = "Observação do produto"
  )
    // Simula o comportamento do repositório para retornar o ingrediente ativo
   `when`(repository.findById(1)).thenReturn(java.util.Optional.of(ingredienteAtivo))

    // Simula o comportamento do repositório para retornar o ingrediente atualizado
    `when`(repository.save(any(Ingrediente::class.java))).thenAnswer { it.getArgument(0) }

    val response = service.atualizarStatusIngrediente(1)

    // Verifica se o status da resposta é 200 OK e se o corpo contém os dados do ingrediente atualizado
    assertEquals(200, response.statusCode.value())
    val body = response.body as Ingrediente
    assertEquals(false, body.ativo)

    // Verifica se os métodos findById e save foram chamados corretamente
    verify(repository, times(1)).findById(1)
    verify(repository, times(1)).save(any(Ingrediente::class.java))
 }

@Test
@DisplayName("Testando listagem de ingredientes com status 200")
 fun listarIngredientes() {
    // Simula o comportamento do repositório para retornar uma lista de ingredientes
    val ingredientes = listOf(ingrediente)
    `when`(repository.findByAtivoTrue()).thenReturn(ingredientes)

    val response = controller.listarIngredientes(null, true)

    // Verifica se o status da resposta é 200 OK e se o corpo contém a lista de ingredientes
    assertEquals(200, response.statusCode.value())
    assertEquals(1, response.body?.size)
    assertEquals("Recheio de Jujuba", response.body?.get(0)?.descricao)

    // Verifica se o método findByAtivoTrue foi chamado uma vez
    verify(repository, times(1)).findByAtivoTrue()
 }


}