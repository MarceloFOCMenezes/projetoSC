package SC.ProjetoSC.controller

import SC.ProjetoSC.Request.EnviarPedidoRequest
import SC.ProjetoSC.Response.PedidoResponse
import SC.ProjetoSC.Services.PedidoServices
import SC.ProjetoSC.entity.*
import SC.ProjetoSC.repository.PedidoRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

import org.springframework.http.HttpStatus
import java.util.*

class PedidoControllerTest {

  val repository = mock(PedidoRepository::class.java)
    val pedidoServices = mock(PedidoServices::class.java)
    val controller = PedidoController(repository, pedidoServices)

 // criando um pedido de teste
 lateinit var fkCliente: Usuario
 lateinit var fkEndereco: Endereco
 lateinit var fkStatusPedido: StatusPedido
 lateinit var pedido: Pedido


    @Test
    @DisplayName("Listar pedidos com ID de usuário válido retorna pedido")
    fun listarPedidosComIdUsuarioValido() {
        val idUsuario = 1
        val pedidoResponse = PedidoResponse(idPedido = 1)
        `when`(pedidoServices.listarPedidoAtual(idUsuario)).thenReturn(pedidoResponse)

        val response = controller.listarPedidos(idUsuario)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(pedidoResponse, response.body)
    }

    @Test
    @DisplayName("Listar pedidos sem ID de usuário retorna BAD_REQUEST")
    fun listarPedidosSemIdUsuario() {
        val response = controller.listarPedidos(null)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    @DisplayName("Atualizar status de pedido existente retorna pedido atualizado")
    fun atualizarStatusPedidoExistente() {
        val idPedido = 1
        val idStatus = 2
        val pedidoAtualizado = PedidoResponse(idPedido = idPedido, statusPedido = "Atualizado")
        `when`(repository.findById(idPedido)).thenReturn(Optional.of(Pedido()))
        `when`(pedidoServices.atualizarStatusPedido(idPedido, idStatus)).thenReturn(pedidoAtualizado)

        val response = controller.atualizarStatus(idPedido, idStatus)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(pedidoAtualizado, response.body)
    }

    @Test
    @DisplayName("Atualizar status de pedido inexistente retorna NOT_FOUND")
    fun atualizarStatusPedidoInexistente() {
        val idPedido = 999
        val idStatus = 2
        `when`(repository.findById(idPedido)).thenReturn(Optional.empty())

        val response = controller.atualizarStatus(idPedido, idStatus)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    @DisplayName("Enviar pedido com dados válidos retorna pedido atualizado")
    fun enviarPedidoComDadosValidos() {
        val enviarPedidoRequest = EnviarPedidoRequest(idPedido = 1)
        val pedidoAtualizado = PedidoResponse(idPedido = 1, statusPedido = "Enviado")
        `when`(repository.findById(enviarPedidoRequest.idPedido!!)).thenReturn(Optional.of(Pedido()))
        `when`(pedidoServices.enviarPedido(enviarPedidoRequest)).thenReturn(pedidoAtualizado)

        val response = controller.enviarPedido(enviarPedidoRequest)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(pedidoAtualizado, response.body)
    }

    @Test
    @DisplayName("Enviar pedido com ID inexistente retorna NOT_FOUND")
    fun enviarPedidoComIdInexistente() {
        val enviarPedidoRequest = EnviarPedidoRequest(idPedido = 999)
        `when`(repository.findById(enviarPedidoRequest.idPedido!!)).thenReturn(Optional.empty())

        val response = controller.enviarPedido(enviarPedidoRequest)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}