package SC.ProjetoSC.controller

import SC.ProjetoSC.Enum.FormaPagamentoEnum
import SC.ProjetoSC.entity.*
import SC.ProjetoSC.repository.*
import SC.ProjetoSC.Services.PedidoServices
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.mockito.Mockito.*
import java.util.*

class PedidoControllerTest {

    val pedidoRepository = mock(PedidoRepository::class.java)
    val usuarioRepository = mock(UsuarioRepository::class.java)
    val enderecoRepository = mock(EnderecoRepository::class.java)
    val statusPedidoRepository = mock(StatusPedidoRepository::class.java)
    val pedidoServices = PedidoServices(pedidoRepository, usuarioRepository, enderecoRepository, statusPedidoRepository)
    val controller = PedidoController(pedidoRepository, pedidoServices)

    lateinit var cliente: Usuario
    lateinit var endereco: Endereco
    lateinit var statusPedido: StatusPedido
    lateinit var pedido: Pedido

    @BeforeEach
    fun setup() {
        cliente = Usuario(id = 1, nome = "Cliente Teste", email = "cliente@gmail.com")
        endereco = Endereco(idEndereco = 1,
            nomeEndereco = "Rua Teste",
            cep = "12345678",
            logradouro = "Rua Teste",
            numero = "123",
            complemento = null,
            bairro = "Centro",
            cidade = "Cidade Teste",
            estado = "SP",
            pontoReferencia = null,
            usuario = cliente)
        statusPedido = StatusPedido(idStatusPedido = 1, descricao = "Pedido em andamento")
        pedido = Pedido(
            id = 1,
            dtPedido = null,
            dtEntrega = null,
            precoTotal = 10.0,
            isRetirada = false,
            forma_Pagamento = FormaPagamentoEnum.pix,
            cliente = cliente,
            endereco = endereco,
            statusPedido = statusPedido
        )
    }

    // todo: corrigir testes

//    @Test
//    @DisplayName("criarProduto: COM dados = status 201 com o produto criado")
//    fun criarProduto() {
//        val dto = RequestProdutoDTO(
//            descricao = "Produto Teste",
//            precoUnitario = java.math.BigDecimal("10.50"),
//            categoria = "Bebida",
//            ativo = true,
//            temIngrediente = false,
//            observacao = "Sem observação",
//            unidadeMedida = UnidadeMedidaEnum.unidade
//        )
//
//        `when`(repository.save(any(Produto::class.java))).thenReturn(produto)
//
//        val retorno = controller.criarProduto(dto)
//
//        assertEquals(201, retorno.statusCode.value())
//        val body = retorno.body as Produto
//        assertEquals("Produto Teste", body.descricao)
//        assertEquals(java.math.BigDecimal("10.50"), body.precoUnitario)
//        assertEquals("Bebida", body.categoria)
//        assertEquals(true, body.ativo)
//        assertEquals(false, body.temIngrediente)
//        assertEquals("Sem observação", body.observacao)
//        assertEquals(UnidadeMedidaEnum.unidade, body.unidadeMedida)
//    }
//}
//
// // -----------------------------------------------------------------------
// // TESTES DA FUNÇÃO: listar pedidos
//
//@Test
//@DisplayName("lista: COM dados = status 200 com a lista correta")
// fun lista() {
//    // programando o mock pra se comportar como se houvesse dados na tabela
//    `when`(repository.findAll()).thenReturn(mutableListOf(pedido))
//
//    val retorno = controller.lista(null, null)
//
//    // verificando se o status da resposta é 200
//    assertEquals(200, retorno.statusCode.value())
//    // verificando se o corpo da resposta tem 1 elemento
//    assertEquals(1, retorno.body?.size)
// }
//
//    @Test
//    @DisplayName("lista: SEM dados = status 204 sem corpo de resposta")
//    fun listaVazio() {
//        // programando o mock pra se comportar como se NÂO houvesse dados na tabela
//        `when`(repository.findAll()).thenReturn(mutableListOf())
//
//        val retorno = controller.lista(null, null)
//
//        // verificando se o status da resposta é 204
//        assertEquals(204, retorno.statusCode.value())
//        // verificando se o corpo da resposta é nulo
//        assertNull(retorno.body)
//    }
//
//
// // -----------------------------------------------------------------------
// // TESTES DA FUNÇÃO: criarPedido
//
//@Test
//@DisplayName("criarPedido: COM dados = status 201 com o pedido criado")
// fun criarPedido() {
//    // programando o mock pra se comportar como se houvesse dados na tabela
//    `when`(repository.save(pedido)).thenReturn(pedido)
//    val retorno = controller.criarPedido(pedido)
//
//    // verificando se o status da resposta é 201
//    assertEquals(201, retorno.statusCode.value())
//    // verificando se o corpo da resposta tem 1 elemento
//    assertEquals(1, retorno.body?.id)
//    assertEquals(fkCliente, retorno.body?.fkCliente)
//    assertEquals(fkEndereco, retorno.body?.fkEndereco)
//    assertEquals(fkStatusPedido, retorno.body?.fkStatusPedido)
// }
//
// // -----------------------------------------------------------------------
// // TESTES DA FUNÇÃO: atualizar pedido
//
//@Test
//@DisplayName("atualizar: COM dados = status 200 com o pedido atualizado")
// fun atualizar() {
//    // programando o mock pra se comportar como se houvesse dados na tabela
//    `when`(repository.existsById(1)).thenReturn(true)
//    `when`(repository.save(pedido)).thenReturn(pedido)
//
//    val retorno = controller.atualizar(1, pedido)
//
//    // verificando se o status da resposta é 200
//    assertEquals(200, retorno.statusCode.value())
//    // verificando se o corpo da resposta tem 1 elemento
//    assertEquals(1, retorno.body?.id)
//    assertEquals(fkCliente, retorno.body?.fkCliente)
//    assertEquals(fkEndereco, retorno.body?.fkEndereco)
//    assertEquals(fkStatusPedido, retorno.body?.fkStatusPedido)
//
// }
//
//    @Test
//    @DisplayName("atualizar: SEM dados = status 404 sem corpo de resposta")
//    fun atualizarVazio() {
//        // programando o mock pra se comportar como se NÃO houvesse dados na tabela
//        `when`(repository.existsById(1)).thenReturn(false)
//
//        val retorno = controller.atualizar(1, pedido)
//
//        // verificando se o status da resposta é 404
//        assertEquals(404, retorno.statusCode.value())
//        // verificando se o corpo da resposta é nulo
//        assertNull(retorno.body)
//    }
//
// // -----------------------------------------------------------------------
// // TESTES DA FUNÇÃO: excluir pedido
//
//@Test
//@DisplayName("excluir: COM dados = status 204 sem corpo de resposta")
// fun excluir() {
//    // programando o mock pra se comportar como se houvesse dados na tabela
//    `when`(repository.existsById(1)).thenReturn(true)
//
//    val retorno = controller.excluir(1)
//
//    // verificando se o status da resposta é 204
//    assertEquals(204, retorno.statusCode.value())
//    // verificando se o corpo da resposta é nulo
//    assertNull(retorno.body)
//    // verificando se o método deleteById foi chamado com o id correto
//    verify(repository).deleteById(1)
// }
//
//    @Test
//    @DisplayName("excluir: COM dados = status 404 sem corpo de resposta")
//    fun excluirVazio() {
//        // programando o mock pra se comportar como se NÃO houvesse dados na tabela
//        `when`(repository.existsById(1)).thenReturn(false)
//
//        val retorno = controller.excluir(1)
//
//        // verificando se o status da resposta é 404
//        assertEquals(404, retorno.statusCode.value())
//        // verificando se o corpo da resposta é nulo
//        assertNull(retorno.body)
//    }
 }

