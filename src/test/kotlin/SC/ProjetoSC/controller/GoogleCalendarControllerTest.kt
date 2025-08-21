package SC.ProjetoSC.controller

import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.entity.Usuario
import SC.ProjetoSC.repository.PedidoRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.util.*
import kotlin.test.Test
import sc.projetosc.controller.GoogleCalendarController
import sc.projetosc.Services.GoogleCalendarServices

class GoogleCalendarControllerTest {

 private val calendarService = mock(GoogleCalendarServices::class.java)
 private val pedidoRepository = mock(PedidoRepository::class.java)
 private val controller = GoogleCalendarController(calendarService, pedidoRepository)

 @Test
 @DisplayName("GET /ocupados - Deve retornar 200 OK com lista quando houver horários")
 fun `listarHorariosOcupados deve retornar 200 com lista`() {
  // Cenário (Arrange)
  val horarios = listOf("Evento 1 - Início: 2025-08-21T10:00:00", "Evento 2 - Início: 2025-08-21T14:00:00")
  // Correção: Usando `when` com crases
  `when`(calendarService.listarHorariosOcupados()).thenReturn(horarios)

  // Ação (Act)
  val resposta = controller.listarHorariosOcupados()

  // Verificação (Assert)
  assertEquals(HttpStatus.OK, resposta.statusCode)
  assertEquals(horarios, resposta.body)
  assertTrue((resposta.body as List<*>).isNotEmpty())
 }

 @Test
 @DisplayName("GET /ocupados - Deve retornar 204 No Content quando a lista estiver vazia")
 fun `listarHorariosOcupados deve retornar 204 sem corpo`() {
  // Cenário (Arrange)
  `when`(calendarService.listarHorariosOcupados()).thenReturn(emptyList())

  // Ação (Act)
  val resposta = controller.listarHorariosOcupados()

  // Verificação (Assert)
  assertEquals(HttpStatus.NO_CONTENT, resposta.statusCode)
  assertNull(resposta.body)
 }

 @Test
 @DisplayName("POST /{idPedido} - Deve retornar 200 OK ao agendar com sucesso")
 fun `agendamento deve retornar 200 com sucesso`() {
  // Cenário (Arrange)
  val idPedido = 1
  val pedidoMock = Pedido(
   id = idPedido,
   cliente = Usuario(id = 1, nome = "Cliente Teste", telefone = "11999999999"),
   isRetirada = false,
   dtPedido = LocalDateTime.now()
  )
  `when`(pedidoRepository.findById(idPedido)).thenReturn(Optional.of(pedidoMock))
  // Correção para doNothing
  doNothing().`when`(calendarService).agendarEvento(idPedido)

  // Ação (Act)
  val resposta = controller.agendamento(idPedido)

  // Verificação (Assert)
  assertEquals(HttpStatus.OK, resposta.statusCode)
  assertEquals("Agendamento criado com sucesso.", resposta.body)
 }

 @Test
 @DisplayName("POST /{idPedido} - Deve retornar 500 quando pedido não for encontrado")
 fun `agendamento deve retornar 500 para pedido nao encontrado`() {
  // Cenário (Arrange)
  val idPedido = 99
  `when`(pedidoRepository.findById(idPedido)).thenReturn(Optional.empty())

  // Ação (Act)
  val resposta = controller.agendamento(idPedido)

  // Verificação (Assert)
  assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.statusCode)
  assertTrue((resposta.body as String).contains("Pedido não encontrado."))
 }

 @Test
 @DisplayName("DELETE /{idPedido} - Deve retornar 200 OK ao excluir com sucesso")
 fun `excluirEvento deve retornar 200 com sucesso`() {
  // Cenário (Arrange)
  val idPedido = 1
  doNothing().`when`(calendarService).excluirEvento(idPedido)

  // Ação (Act)
  val resposta = controller.excluirEvento(idPedido)

  // Verificação (Assert)
  assertEquals(HttpStatus.OK, resposta.statusCode)
  assertEquals("Evento excluído com sucesso.", resposta.body)
 }

 @Test
 @DisplayName("DELETE /{idPedido} - Deve retornar 500 quando serviço de exclusão falhar")
 fun `excluirEvento deve retornar 500 em caso de falha`() {
  // Cenário (Arrange)
  val idPedido = 99
  val mensagemErro = "Evento não encontrado para o pedido #$idPedido."
  `when`(calendarService.excluirEvento(idPedido)).thenThrow(Exception(mensagemErro))

  // Ação (Act)
  val resposta = controller.excluirEvento(idPedido)

  // Verificação (Assert)
  assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.statusCode)
  assertEquals("Erro ao excluir evento: $mensagemErro", resposta.body)
 }

 @Test
 @DisplayName("GET /livre/{periodo} - Deve retornar 200 OK com horários para período válido")
 fun `listarHorariosDisponiveisPorPeriodo deve retornar 200 com sucesso`() {
  // Cenário (Arrange)
  val periodo = "semana"
  val horariosDisponiveis = mapOf("21/08/2025" to listOf("[10:00] [10:30]"))
  `when`(calendarService.listarHorariosDisponiveisPorPeriodo(periodo)).thenReturn(horariosDisponiveis)

  // Ação (Act)
  val resposta = controller.listarHorariosDisponiveisPorPeriodo(periodo)

  // Verificação (Assert)
  assertEquals(HttpStatus.OK, resposta.statusCode)
  assertEquals(horariosDisponiveis, resposta.body)
 }

 @Test
 @DisplayName("GET /livre/{periodo} - Deve retornar 400 Bad Request para período inválido")
 fun `listarHorariosDisponiveisPorPeriodo deve retornar 400 para periodo invalido`() {
  // Cenário (Arrange)
  val periodoInvalido = "ano"
  val mensagemErro = "Período inválido."
  `when`(calendarService.listarHorariosDisponiveisPorPeriodo(periodoInvalido)).thenThrow(IllegalArgumentException(mensagemErro))

  // Ação (Act)
  val resposta = controller.listarHorariosDisponiveisPorPeriodo(periodoInvalido)

  // Verificação (Assert)
  assertEquals(HttpStatus.BAD_REQUEST, resposta.statusCode)
  assertEquals("Erro: $mensagemErro", resposta.body)
 }

 @Test
 @DisplayName("GET /ocupados/{periodo} - Deve retornar 200 OK com pedidos para período válido")
 fun `listarPedidosPorPeriodo deve retornar 200 com sucesso`() {
  // Cenário (Arrange)
  val periodo = "dia"
  val pedido = Pedido(id = 1, dtEntregaEsperada = LocalDateTime.of(2025, 8, 21, 10, 0))
  val pedidosAgrupados = mapOf("2025-08-21" to listOf(pedido))
  `when`(calendarService.listarPedidosPorPeriodo(periodo)).thenReturn(pedidosAgrupados)

  // Ação (Act)
  val resposta = controller.listarPedidosPorPeriodo(periodo)

  // Verificação (Assert)
  assertEquals(HttpStatus.OK, resposta.statusCode)
  assertEquals(pedidosAgrupados, resposta.body)
 }

 @Test
 @DisplayName("GET /ocupados/{periodo} - Deve retornar 400 Bad Request para período inválido")
 fun `listarPedidosPorPeriodo deve retornar 400 para periodo invalido`() {
  // Cenário (Arrange)
  val periodoInvalido = "trimestre"
  val mensagemErro = "Período inválido."
  `when`(calendarService.listarPedidosPorPeriodo(periodoInvalido)).thenThrow(IllegalArgumentException(mensagemErro))

  // Ação (Act)
  val resposta = controller.listarPedidosPorPeriodo(periodoInvalido)

  // Verificação (Assert)
  assertEquals(HttpStatus.BAD_REQUEST, resposta.statusCode)
  assertEquals("Erro: $mensagemErro", resposta.body)
 }
 }