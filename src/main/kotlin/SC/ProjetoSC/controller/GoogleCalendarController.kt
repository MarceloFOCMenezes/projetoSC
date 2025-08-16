package sc.projetosc.controller

import SC.ProjetoSC.entity.Pedido
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
    private val pedidoRepository: PedidoRepository // Descomente se precisar acessar o repositório de pedidos
) {
//    @PostMapping("/teste")
//    fun criarAgendamento():ResponseEntity<Any> {
//        calendarService.criarAgendamento()
//        return ResponseEntity.ok("Evento criado com sucesso no Google Calendar. Verifique o calendário!")
//    }

//    @GetMapping("/carrinho")
//    @Operation(summary = "Listar pedidos", description = "Retorna uma lista de pedidos, podendo filtrar por ID do usuário.")
//    @ApiResponses(value = [
//        ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso. O corpo da resposta contém os dados dos pedidos."),
//        ApiResponse(responseCode = "204", description = "Nenhum pedido encontrado. O corpo da resposta estará vazio.")
//    ])
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

    @PostMapping("/{idPedido}/agendar")
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

    @DeleteMapping("/{idPedido}/excluir")
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
}