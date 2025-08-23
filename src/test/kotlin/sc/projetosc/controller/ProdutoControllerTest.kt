package sc.projetosc.controller

import sc.projetosc.Enum.UnidadeMedidaEnum
import sc.projetosc.dto.RequestProdutoDTO
import sc.projetosc.entity.Produto
import sc.projetosc.repository.ProdutoRepository
import sc.projetosc.services.ProdutoServices
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
   precoUnitario = BigDecimal("10.50"),
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
   precoUnitario = BigDecimal("10.50"),
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
   unidadeMedida = UnidadeMedidaEnum.kg
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
  assertEquals(UnidadeMedidaEnum.kg, body.unidadeMedida)
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
   unidadeMedida = UnidadeMedidaEnum.kg
  )

  `when`(repository.findById(2)).thenReturn(Optional.empty())

  val response = service.atualizarProduto(2, dto)

  assertEquals(404, response.statusCode.value())
  assertNull(response.body)
 }

 // ----------------------------------------------------------------------------------------------

// TESTES DA FUNÇÃO atualizarStatusProduto

 @Test
 @DisplayName("atualizarStatusProduto: desativa produto ativo")
 fun atualizarStatusProduto_desativaProdutoAtivo() {
  val produtoAtivo = Produto(
   idProduto = 1,
   descricao = "Produto Ativo",
   precoUnitario = BigDecimal("10.00"),
   categoria = "Lanche",
   ativo = true,
   temIngrediente = true,
   observacao = "Ativo",
   unidadeMedida = UnidadeMedidaEnum.unidade
  )
  `when`(repository.findById(1)).thenReturn(Optional.of(produtoAtivo))
  `when`(repository.save(any(Produto::class.java))).thenAnswer { it.getArgument(0) }

  val response = service.atualizarStatusProduto(1)
  assertEquals(200, response.statusCode.value())
  val body = response.body as Produto
  assertEquals(false, body.ativo)
 }

 @Test
 @DisplayName("atualizarStatusProduto: ativa produto inativo")
 fun atualizarStatusProduto_ativaProdutoInativo() {
  val produtoInativo = Produto(
   idProduto = 2,
   descricao = "Produto Inativo",
   precoUnitario = BigDecimal("15.00"),
   categoria = "Bebida",
   ativo = false,
   temIngrediente = false,
   observacao = "Inativo",
   unidadeMedida = UnidadeMedidaEnum.kg
  )
  `when`(repository.findById(2)).thenReturn(Optional.of(produtoInativo))
  `when`(repository.save(any(Produto::class.java))).thenAnswer { it.getArgument(0) }

  val response = service.atualizarStatusProduto(2)
  assertEquals(200, response.statusCode.value())
  val body = response.body as Produto
  assertEquals(true, body.ativo)
 }

 @Test
 @DisplayName("atualizarStatusProduto: produto não encontrado = status 404")
 fun atualizarStatusProduto_naoEncontrado() {
  `when`(repository.findById(99)).thenReturn(Optional.empty())
  val response = service.atualizarStatusProduto(99)
  assertEquals(404, response.statusCode.value())
  assertNull(response.body)
 }

 // ----------------------------------------------------------------------------------------------

 // TESTES DA FUNÇÃO listarProdutos
 @Test
 @DisplayName("listarProdutos: padrão (ativos) = status 200 com lista de ativos")
 fun listarProdutos_ativosPadrao() {
  val produto1 = Produto(1, "Coca-Cola", BigDecimal("5.00"), "Bebida", true, false, "Gelada", UnidadeMedidaEnum.unidade)
  val produto2 = Produto(2, "Hamburguer", BigDecimal("15.00"), "Lanche", true, true, null, UnidadeMedidaEnum.unidade)
  `when`(repository.findByAtivoTrue()).thenReturn(listOf(produto1, produto2))

  val response = controller.listarProdutos(null, true) // <-- use true explicitamente
  assertEquals(200, response.statusCode.value())
  assertEquals(2, response.body?.size)
  assertTrue(response.body!!.all { it.ativo == true })
 }

 @Test
 @DisplayName("listarProdutos: padrão (ativos) sem resultados = status 204")
 fun listarProdutos_ativosPadrao_semResultados() {
  `when`(repository.findByAtivoTrue()).thenReturn(emptyList())

  val response = controller.listarProdutos(null, null)
  assertEquals(204, response.statusCode.value())
  assertNull(response.body)
 }

 @Test
 @DisplayName("listarProdutos: todos os produtos = status 200 com lista completa")
 fun listarProdutos_todos() {
  val produto1 = Produto(1, "Coca-Cola", BigDecimal("5.00"), "Bebida", true, false, "Gelada", UnidadeMedidaEnum.unidade)
  val produto2 = Produto(2, "Hamburguer", BigDecimal("15.00"), "Lanche", false, true, null, UnidadeMedidaEnum.unidade)
  `when`(repository.findAll()).thenReturn(listOf(produto1, produto2))

  val response = controller.listarProdutos(null, null) // ativos = null para buscar todos
  assertEquals(200, response.statusCode.value())
  assertEquals(2, response.body?.size)
 }

 @Test
 @DisplayName("listarProdutos: todos os produtos sem resultados = status 204")
 fun listarProdutos_todos_semResultados() {
  `when`(repository.findAll()).thenReturn(emptyList())

  val response = controller.listarProdutos(null, false)
  assertEquals(204, response.statusCode.value())
  assertNull(response.body)
 }

 @Test
 @DisplayName("listarProdutos: busca por nome (ativos) = status 200 com lista correta")
 fun listarProdutos_buscaNomeAtivos() {
  val produto = Produto(3, "Suco de Laranja", BigDecimal("7.00"), "Bebida", true, false, null, UnidadeMedidaEnum.unidade)
  `when`(repository.findByDescricaoContainsIgnoreCaseAndAtivoTrue("Suco")).thenReturn(listOf(produto))

  val response = controller.listarProdutos("Suco", true) // <-- use true explicitamente
  assertEquals(200, response.statusCode.value())
  assertEquals(1, response.body?.size)
  assertEquals("Suco de Laranja", response.body?.get(0)?.descricao)
 }

 @Test
 @DisplayName("listarProdutos: busca por nome (ativos) sem resultados = status 204")
 fun listarProdutos_buscaNomeAtivos_semResultados() {
  `when`(repository.findByDescricaoContainsIgnoreCaseAndAtivoTrue("Inexistente")).thenReturn(emptyList())

  val response = controller.listarProdutos("Inexistente", null)
  assertEquals(204, response.statusCode.value())
  assertNull(response.body)
 }

 @Test
 @DisplayName("listarProdutos: busca por nome (todos) = status 200 com lista correta")
 fun listarProdutos_buscaNomeTodos() {
  val produto = Produto(4, "Suco de Uva", BigDecimal("8.00"), "Bebida", false, false, null, UnidadeMedidaEnum.unidade)
  `when`(repository.findByDescricaoContainsIgnoreCase("Suco")).thenReturn(listOf(produto))

  val response = controller.listarProdutos("Suco", null) // ativos = null para buscar todos por nome
  assertEquals(200, response.statusCode.value())
  assertEquals(1, response.body?.size)
  assertEquals("Suco de Uva", response.body?.get(0)?.descricao)
 }

 @Test
 @DisplayName("listarProdutos: busca por nome (todos) sem resultados = status 204")
 fun listarProdutos_buscaNomeTodos_semResultados() {
  `when`(repository.findByDescricaoContainsIgnoreCase("Nada")).thenReturn(emptyList())

  val response = controller.listarProdutos("Nada", false)
  assertEquals(204, response.statusCode.value())
  assertNull(response.body)
 }

 @Test
 @DisplayName("listarProdutos: somente inativos = status 200 com lista de inativos")
 fun listarProdutos_inativos() {
  val produto1 = Produto(1, "Produto Inativo 1", BigDecimal("5.00"), "Bebida", false, false, "Obs", UnidadeMedidaEnum.unidade)
  val produto2 = Produto(2, "Produto Inativo 2", BigDecimal("15.00"), "Lanche", false, true, null, UnidadeMedidaEnum.unidade)
  `when`(repository.findByAtivoFalse()).thenReturn(listOf(produto1, produto2))

  val response = controller.listarProdutos(null, false)
  assertEquals(200, response.statusCode.value())
  assertEquals(2, response.body?.size)
  assertTrue(response.body!!.all { it.ativo == false })
 }

 @Test
 @DisplayName("listarProdutos: somente inativos sem resultados = status 204")
 fun listarProdutos_inativos_semResultados() {
  `when`(repository.findByAtivoFalse()).thenReturn(emptyList())

  val response = controller.listarProdutos(null, false)
  assertEquals(204, response.statusCode.value())
  assertNull(response.body)
 }

 @Test
 @DisplayName("listarProdutos: busca por nome (inativos) = status 200 com lista correta")
 fun listarProdutos_buscaNomeInativos() {
  val produto = Produto(3, "Suco de Goiaba", BigDecimal("7.00"), "Bebida", false, false, null, UnidadeMedidaEnum.unidade)
  `when`(repository.findByDescricaoContainsIgnoreCaseAndAtivoFalse("Suco")).thenReturn(listOf(produto))

  val response = controller.listarProdutos("Suco", false)
  assertEquals(200, response.statusCode.value())
  assertEquals(1, response.body?.size)
  assertEquals("Suco de Goiaba", response.body?.get(0)?.descricao)
  assertEquals(false, response.body?.get(0)?.ativo)
 }

 @Test
 @DisplayName("listarProdutos: busca por nome (inativos) sem resultados = status 204")
 fun listarProdutos_buscaNomeInativos_semResultados() {
  `when`(repository.findByDescricaoContainsIgnoreCaseAndAtivoFalse("Nada")).thenReturn(emptyList())

  val response = controller.listarProdutos("Nada", false)
  assertEquals(204, response.statusCode.value())
  assertNull(response.body)
 }

}

