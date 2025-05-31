package SC.ProjetoSC.controller

import SC.ProjetoSC.Enum.UnidadeMedidaEnum
import SC.ProjetoSC.dto.RequestProdutoDTO
import SC.ProjetoSC.entity.Produto
import SC.ProjetoSC.repository.ProdutoRepository
import SC.ProjetoSC.Services.ProdutoServices
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.util.*

class ProdutoControllerTest {

 private val repository = mock(ProdutoRepository::class.java)
 private val service = ProdutoServices(repository)
 private val controller = ProdutoController(repository, service)

 lateinit var produto: Produto

 @BeforeEach
 fun setup() {
  produto = Produto(
   idProduto = 1,
   descricao = "Produto Teste",
   precoUnitario = java.math.BigDecimal("10.50"),
   categoria = "Bebida",
   ativo = true,
   temIngrediente = false,
   observacao = "Observar o produto",
   unidadeMedida = UnidadeMedidaEnum.unidade
  )
 }

 // TESTES DA FUNÇÃO criarProduto

 @Test
 @DisplayName("criarProduto: COM dados = status 201 com o produto criado")
 fun criarProduto() {
  val dto = RequestProdutoDTO(
   descricao = "Produto Teste",
   precoUnitario = java.math.BigDecimal("10.50"),
   categoria = "Bebida",
   ativo = true,
   temIngrediente = false,
   observacao = "Sem observação",
   unidadeMedida = UnidadeMedidaEnum.unidade
  )

  `when`(repository.save(any(Produto::class.java))).thenReturn(produto)

  val retorno = controller.criarProduto(dto)

  assertEquals(201, retorno.statusCode.value())
  val body = retorno.body as Produto
  assertEquals("Produto Teste", body.descricao)
  assertEquals(java.math.BigDecimal("10.50"), body.precoUnitario)
  assertEquals("Bebida", body.categoria)
  assertEquals(true, body.ativo)
  assertEquals(false, body.temIngrediente)
  assertEquals("Sem observação", body.observacao)
  assertEquals(UnidadeMedidaEnum.unidade, body.unidadeMedida)
 }

 // ----------------------------------------------------------------------------------------------
 // TESTES DA FUNÇÃO atualizarProduto

 @Test
 @DisplayName("atualizarProduto: produto encontrado = status 200 com produto atualizado")
 fun atualizarProduto_sucesso() {
  val dto = RequestProdutoDTO(
   descricao = "Produto Novo",
   precoUnitario = BigDecimal("10.00"),
   categoria = "Bebida",
   ativo = false,
   temIngrediente = false,
   observacao = "Novo",
   unidadeMedida = UnidadeMedidaEnum.quilo
  )

  `when`(repository.findById(1)).thenReturn(Optional.of(produto))
  `when`(repository.save(any(Produto::class.java))).thenAnswer { it.getArgument(0) }

  val response = service.atualizarProduto(1, dto)

  assertEquals(200, response.statusCode.value())
  val body = response.body as Produto
  assertEquals("Produto Novo", body.descricao)
  assertEquals(BigDecimal("10.00"), body.precoUnitario)
  assertEquals("Bebida", body.categoria)
  assertEquals(false, body.ativo)
  assertEquals(false, body.temIngrediente)
  assertEquals("Novo", body.observacao)
  assertEquals(UnidadeMedidaEnum.quilo, body.unidadeMedida)
 }

 @Test
 @DisplayName("atualizarProduto: produto não encontrado = status 404 sem corpo")
 fun atualizarProduto_naoEncontrado() {
  val dto = RequestProdutoDTO(
   descricao = "Produto Novo",
   precoUnitario = BigDecimal("10.00"),
   categoria = "Bebida",
   ativo = false,
   temIngrediente = false,
   observacao = "Novo",
   unidadeMedida = UnidadeMedidaEnum.quilo
  )

  `when`(repository.findById(2)).thenReturn(Optional.empty())

  val response = service.atualizarProduto(2, dto)

  assertEquals(404, response.statusCode.value())
  assertNull(response.body)
 }

 // ----------------------------------------------------------------------------------------------

// TESTES DA FUNÇÃO desativarProduto


 // ----------------------------------------------------------------------------------------------

 // TESTES DA FUNÇÃO listarProdutos
}

