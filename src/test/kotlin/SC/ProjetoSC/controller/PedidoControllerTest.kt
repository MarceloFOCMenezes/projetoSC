package SC.ProjetoSC.controller

import RequestPedidoDTO
import SC.ProjetoSC.Enum.FormaPagamentoEnum
import SC.ProjetoSC.Services.PedidoServices
import SC.ProjetoSC.controller.PedidoController
import SC.ProjetoSC.entity.Endereco
import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.entity.StatusPedido
import SC.ProjetoSC.entity.Usuario
import SC.ProjetoSC.repository.PedidoRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.util.*

class PedidoControllerTest {

    private val pedidoRepository = mock(PedidoRepository::class.java)
    private val pedidoServices = mock(PedidoServices::class.java)
    private val controller = PedidoController(pedidoRepository, pedidoServices)

    lateinit var usuario: Usuario
    lateinit var endereco: Endereco
    lateinit var statusPedido: StatusPedido
    lateinit var pedido: Pedido

    @BeforeEach
    fun setup() {
        usuario = Usuario(
            id = 1,
            nome = "Cliente Teste",
            email = "cliente@email.com",
            telefone = "123456789",
            senha = "senha123",
            tipo = SC.ProjetoSC.Enum.TipoUsuarioEnum.cliente,
            logado = false
        )
        endereco = Endereco(
            idEndereco = 1,
            nomeEndereco = "Casa",
            cep = "12345678",
            logradouro = "Rua Teste",
            numero = "100",
            complemento = "Apto 10",
            bairro = "Centro",
            cidade = "Cidade Teste",
            estado = "SP",
            pontoReferencia = "Próximo à praça",
            usuario = usuario // se necessário, senão pode ser null
        )
        statusPedido = StatusPedido(
            idStatusPedido = 1,
            descricao = "Em aberto"
        )
        pedido = Pedido(
            id = 1,
            dtPedido = LocalDateTime.now(),
            dtEntrega = LocalDateTime.now().plusDays(2),
            precoTotal = 100.0,
            isRetirada = false,
            cliente = usuario,
            endereco = endereco,
            statusPedido = statusPedido,
            forma_Pagamento = FormaPagamentoEnum.pix
        )
    }

    @Test
    @DisplayName("lista: COM dados = status 200 com lista de pedidos")
    fun lista() {
        val pedidos = listOf(pedido)
        `when`(pedidoServices.listarPedidos(null, null)).thenReturn(pedidos)

        val response = controller.lista(null, null)

        assertEquals(200, response.statusCode.value())
        assertEquals(1, response.body?.size)
        assertEquals(pedido, response.body?.get(0))
    }

    @Test
    @DisplayName("lista: SEM dados = status 204 sem corpo")
    fun listaVazio() {
        `when`(pedidoServices.listarPedidos(null, null)).thenReturn(emptyList())

        val response = controller.lista(null, null)

        assertEquals(204, response.statusCode.value())
        assertNull(response.body)
    }

    @Test
    @DisplayName("criarPedido: COM dados = status 201 com o pedido criado")
    fun criarPedido() {
        val dto = RequestPedidoDTO(
            precoTotal = 100.0,
            isRetirada = false,
            clienteId = 1,
            enderecoId = 1,
            formaPagamento = FormaPagamentoEnum.pix
        )
        `when`(pedidoServices.criarPedido(dto)).thenReturn(pedido)

        val response = controller.criarPedido(dto)

        assertEquals(201, response.statusCode.value())
        val body = response.body as Pedido
        assertEquals(pedido.id, body.id)
        assertEquals(usuario, body.cliente)
        assertEquals(endereco, body.endereco)
        assertEquals(statusPedido, body.statusPedido)
    }

    @Test
    @DisplayName("atualizar: COM dados = status 200 com o pedido atualizado")
    fun atualizar() {
        `when`(pedidoRepository.existsById(1)).thenReturn(true)
        `when`(pedidoRepository.save(any(Pedido::class.java))).thenReturn(pedido)

        val response = controller.atualizar(1, pedido)

        assertEquals(200, response.statusCode.value())
        assertEquals(pedido, response.body)
    }

    @Test
    @DisplayName("atualizar: pedido não encontrado = status 404 sem corpo")
    fun atualizarVazio() {
        `when`(pedidoRepository.existsById(99)).thenReturn(false)

        val response = controller.atualizar(99, pedido)

        assertEquals(404, response.statusCode.value())
        assertNull(response.body)
    }

    @Test
    @DisplayName("excluir: pedido encontrado = status 204 sem corpo")
    fun excluir() {
        `when`(pedidoRepository.existsById(1)).thenReturn(true)
        doNothing().`when`(pedidoRepository).deleteById(1)

        val response = controller.excluir(1)

        assertEquals(204, response.statusCode.value())
        assertNull(response.body)
        verify(pedidoRepository, times(1)).deleteById(1)
    }

    @Test
    @DisplayName("excluir: pedido não encontrado = status 404 sem corpo")
    fun excluirVazio() {
        `when`(pedidoRepository.existsById(99)).thenReturn(false)

        val response = controller.excluir(99)

        assertEquals(404, response.statusCode.value())
        assertNull(response.body)
    }
}