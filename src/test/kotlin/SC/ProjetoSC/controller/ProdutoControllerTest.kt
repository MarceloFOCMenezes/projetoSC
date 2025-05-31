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

class ProdutoControllerTest {

 val repository = mock(ProdutoRepository::class.java)
 val service = ProdutoServices(repository, mock()) // mock do UsuarioRepository
 val controller = ProdutoController(repository, service)

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
}