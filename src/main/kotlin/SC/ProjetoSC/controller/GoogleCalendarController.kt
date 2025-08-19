package sc.projetosc.controller

import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.repository.ItemPedidoRepository
import SC.ProjetoSC.repository.PedidoRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sc.projetosc.Services.GoogleCalendarServices

@Tag(name = "Calendário", description = "Operações relacionadas ao Google Calendar para agendamentos de pedidos personalizados")
@RestController
@RequestMapping("/calendario")
class GoogleCalendarController (
    private val calendarService: GoogleCalendarServices,
    private val pedidoRepository: PedidoRepository
){

    @GetMapping("/ocupados")
    @Operation(summary = "Listar horários ocupados", description = "Retorna uma lista de horários ocupados no Google Calendar.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de horários ocupados retornada com sucesso."),
        ApiResponse(responseCode = "204", description = "Nenhum horário ocupado encontrado. O corpo da resposta estará vazio.")
    ])
    fun listarHorariosOcupados(): ResponseEntity<Any> {
        val horariosOcupados = calendarService.listarHorariosOcupados()
        return if (horariosOcupados.isEmpty()) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.ok(horariosOcupados)
        }
    }

    @PostMapping("/{idPedido}")
    @Operation(summary = "Agendar evento", description = "Cria um agendamento no Google Calendar.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Agendamento criado com sucesso."),
        ApiResponse(responseCode = "404", description = "Pedido não encontrado."),
        ApiResponse(responseCode = "500", description = "Erro ao criar agendamento. O corpo da resposta estará vazio.")
    ])
    fun agendamento(@PathVariable idPedido: Int): ResponseEntity<Any> {
        try {
            // Buscar o pedido no banco de dados
            val pedido = pedidoRepository.findById(idPedido).orElseThrow { Exception("Pedido não encontrado.") }

            // Validar os dados do pedido
            val cliente = pedido.cliente ?: throw Exception("Cliente não encontrado no pedido.")
            val isRetirada = pedido.isRetirada ?: throw Exception("Informação de retirada não encontrada.")
            val endereco = pedido.endereco // Pode ser null se for retirada

            // Agendar o evento no Google Calendar
            calendarService.agendarEvento(idPedido)
            return ResponseEntity.ok("Agendamento criado com sucesso.")
        } catch (e: Exception) {
            return ResponseEntity.status(500).body("Erro ao criar agendamento: ${e.message}")
        }
    }

    @DeleteMapping("/{idPedido}")
    @Operation(summary = "Excluir evento", description = "Exclui um agendamento do Google Calendar com base no ID do pedido.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Evento excluído com sucesso."),
        ApiResponse(responseCode = "404", description = "Evento ou pedido não encontrado."),
        ApiResponse(responseCode = "500", description = "Erro ao excluir evento.")
    ])
    fun excluirEvento(@PathVariable idPedido: Int): ResponseEntity<Any> {
        return try {
            calendarService.excluirEvento(idPedido)
            ResponseEntity.ok("Evento excluído com sucesso.")
        } catch (e: Exception) {
            ResponseEntity.status(500).body("Erro ao excluir evento: ${e.message}")
        }
    }


    @GetMapping("/livre/{periodo}")
    @Operation(summary = "Listar horários disponíveis por período", description = "Retorna os horários ocupados, livres e possíveis em um período (semana ou mês).")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Horários retornados com sucesso."),
        ApiResponse(responseCode = "400", description = "Período inválido."),
        ApiResponse(responseCode = "500", description = "Erro ao listar horários.")
    ])
    fun listarHorariosDisponiveisPorPeriodo(@PathVariable periodo: String): ResponseEntity<Any> {
        return try {
            val horarios = calendarService.listarHorariosDisponiveisPorPeriodo(periodo)
            ResponseEntity.ok(horarios)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body("Erro: ${e.message}")
        } catch (e: Exception) {
            ResponseEntity.status(500).body("Erro ao listar horários: ${e.message}")
        }
    }

    @GetMapping("/ocupados/{periodo}")
    @Operation(summary = "Listar os e pedidos agendados agrupados pelo dia da entrega.", description = "Retorna os horários ocupados e os pedidos agendados para esses horários.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Horários ocupados retornados com sucesso."),
        ApiResponse(responseCode = "400", description = "Período inválido."),
        ApiResponse(responseCode = "500", description = "Erro ao listar horários ocupados.")
    ])
    fun listarPedidosPorPeriodo(@PathVariable periodo: String): ResponseEntity<Any> {
        return try {
            val horariosOcupados = calendarService.listarPedidosPorPeriodo(periodo)
            ResponseEntity.ok(horariosOcupados)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body("Erro: ${e.message}")
        } catch (e: Exception) {
            ResponseEntity.status(500).body("Erro ao listar horários ocupados: ${e.message}")
        }
    }


    // ENDPOINTS PARA TESTE -  NÃO SERÁ IMPLEMENTADO NA VERSÃO FINAL

//    @PostMapping("/popular-teste")
//    @Operation(summary = "Criar 60 pedidos e agendar no Google Calendar", description = "Gera 60 pedidos no banco de dados e cria os eventos correspondentes no Google Calendar.")
//    @ApiResponses(value = [
//        ApiResponse(responseCode = "200", description = "Pedidos criados e agendados com sucesso."),
//        ApiResponse(responseCode = "500", description = "Erro ao criar pedidos ou agendar eventos.")
//    ])
//    fun criarPedidosEAgendar(): ResponseEntity<Any> {
//        return try {
//            val pedidosCriados = calendarService.criarPedidosEAgendar()
//            ResponseEntity.ok("Foram criados e agendados ${pedidosCriados.size} pedidos com sucesso.")
//        } catch (e: Exception) {
//            ResponseEntity.status(500).body("Erro ao criar pedidos e agendar eventos: ${e.message}")
//        }
//    }

//    // teste - excluir todos os eventos do calendario que sao pedidos
//    @DeleteMapping("/excluir-todos")
//    @Operation(summary = "Excluir todos os pedidos", description = "Exclui todos os pedidos do banco de dados e os eventos relacionados no Google Calendar.")
//    @ApiResponses(value = [
//        ApiResponse(responseCode = "200", description = "Todos os pedidos e eventos excluídos com sucesso."),
//        ApiResponse(responseCode = "500", description = "Erro ao excluir pedidos ou eventos.")
//    ])
//    fun excluirTodosPedidos(): ResponseEntity<String> {
//        return try {
//            // Excluir apenas eventos relacionados a pedidos
//            calendarService.excluirEventosDePedidos()
//
//            // Excluir todos os itens de pedidos
//            itemPedidoRepository.deleteAll()
//
//            // Excluir todos os pedidos do banco de dados
//            pedidoRepository.deleteAll()
//
//            ResponseEntity.ok("Todos os pedidos e eventos relacionados foram excluídos com sucesso.")
//        } catch (e: Exception) {
//            ResponseEntity.status(500).body("Erro ao excluir pedidos e eventos: ${e.message}")
//        }
//    }
}