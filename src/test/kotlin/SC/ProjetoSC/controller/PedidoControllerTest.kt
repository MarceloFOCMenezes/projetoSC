package SC.ProjetoSC.controller

import SC.ProjetoSC.Enum.StatusPagamentoEnum
import SC.ProjetoSC.entity.*
import SC.ProjetoSC.repository.PedidoRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito.*

class PedidoControllerTest {

  val repository = mock(PedidoRepository::class.java)
  val controller = PedidoController(repository)

 // criando um pedido de teste
 lateinit var fkCliente: Usuario
 lateinit var fkEndereco: Endereco
 lateinit var fkStatusPedido: StatusPedido
 lateinit var pedido: Pedido

 @BeforeEach
 fun setup() {
  // pedido de teste
   fkCliente = Usuario(id = 1, nome = "Cliente Teste", email = "cliente@gmail.com")

   fkEndereco = Endereco( idEndereco = 1, nomeEndereco = "Rua Teste", numeroEndereco = "123", cepEndereco = "12345678",)

   fkStatusPedido = StatusPedido(idStatusPedido = 1, descricao = "Pedido em andamento")

  pedido = Pedido(
   id = 1,
   dtPedido = null,
   dtEntrega = null,
   statusPagamento = StatusPagamentoEnum.NAO_PAGO,
   precoTotal = 0.0,
   isRetirada = false,
   fkCliente = fkCliente,
   fkEndereco = fkEndereco,
   fkStatusPedido = fkStatusPedido,
  )
 }

 // -----------------------------------------------------------------------
 // TESTES DA FUNÇÃO: listar pedidos

@Test
@DisplayName("lista: COM dados = status 200 com a lista correta")
 fun lista() {
    // programando o mock pra se comportar como se houvesse dados na tabela
    `when`(repository.findAll()).thenReturn(mutableListOf(pedido))

    val retorno = controller.lista(null, null)

    // verificando se o status da resposta é 200
    assertEquals(200, retorno.statusCode.value())
    // verificando se o corpo da resposta tem 1 elemento
    assertEquals(1, retorno.body?.size)
 }

    @Test
    @DisplayName("lista: SEM dados = status 204 sem corpo de resposta")
    fun listaVazio() {
        // programando o mock pra se comportar como se NÂO houvesse dados na tabela
        `when`(repository.findAll()).thenReturn(mutableListOf())

        val retorno = controller.lista(null, null)

        // verificando se o status da resposta é 204
        assertEquals(204, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }


 // -----------------------------------------------------------------------
 // TESTES DA FUNÇÃO: criarPedido

@Test
@DisplayName("criarPedido: COM dados = status 201 com o pedido criado")
 fun criarPedido() {
    // programando o mock pra se comportar como se houvesse dados na tabela
    `when`(repository.save(pedido)).thenReturn(pedido)
    val retorno = controller.criarPedido(pedido)

    // verificando se o status da resposta é 201
    assertEquals(201, retorno.statusCode.value())
    // verificando se o corpo da resposta tem 1 elemento
    assertEquals(1, retorno.body?.id)
    assertEquals(fkCliente, retorno.body?.fkCliente)
    assertEquals(fkEndereco, retorno.body?.fkEndereco)
    assertEquals(fkStatusPedido, retorno.body?.fkStatusPedido)
 }

 // -----------------------------------------------------------------------
 // TESTES DA FUNÇÃO: atualizar pedido

@Test
@DisplayName("atualizar: COM dados = status 200 com o pedido atualizado")
 fun atualizar() {
    // programando o mock pra se comportar como se houvesse dados na tabela
    `when`(repository.existsById(1)).thenReturn(true)
    `when`(repository.save(pedido)).thenReturn(pedido)

    val retorno = controller.atualizar(1, pedido)

    // verificando se o status da resposta é 200
    assertEquals(200, retorno.statusCode.value())
    // verificando se o corpo da resposta tem 1 elemento
    assertEquals(1, retorno.body?.id)
    assertEquals(fkCliente, retorno.body?.fkCliente)
    assertEquals(fkEndereco, retorno.body?.fkEndereco)
    assertEquals(fkStatusPedido, retorno.body?.fkStatusPedido)

 }

    @Test
    @DisplayName("atualizar: SEM dados = status 404 sem corpo de resposta")
    fun atualizarVazio() {
        // programando o mock pra se comportar como se NÃO houvesse dados na tabela
        `when`(repository.existsById(1)).thenReturn(false)

        val retorno = controller.atualizar(1, pedido)

        // verificando se o status da resposta é 404
        assertEquals(404, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }

 // -----------------------------------------------------------------------
 // TESTES DA FUNÇÃO: excluir pedido

@Test
@DisplayName("excluir: COM dados = status 204 sem corpo de resposta")
 fun excluir() {
    // programando o mock pra se comportar como se houvesse dados na tabela
    `when`(repository.existsById(1)).thenReturn(true)

    val retorno = controller.excluir(1)

    // verificando se o status da resposta é 204
    assertEquals(204, retorno.statusCode.value())
    // verificando se o corpo da resposta é nulo
    assertNull(retorno.body)
    // verificando se o método deleteById foi chamado com o id correto
    verify(repository).deleteById(1)
 }

    @Test
    @DisplayName("excluir: COM dados = status 404 sem corpo de resposta")
    fun excluirVazio() {
        // programando o mock pra se comportar como se NÃO houvesse dados na tabela
        `when`(repository.existsById(1)).thenReturn(false)

        val retorno = controller.excluir(1)

        // verificando se o status da resposta é 404
        assertEquals(404, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }
 }

