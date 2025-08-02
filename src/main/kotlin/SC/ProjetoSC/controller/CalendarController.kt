package sc.projetosc.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sc.projetosc.Services.GoogleCalendarServices

@Tag(name = "Calendário", description = "Operações relacionadas ao Google Calendar para agendamentos de pedidos personalizados")
@RestController
@RequestMapping("/calendario")
class CalendarController (
    private val calendarService: GoogleCalendarServices
) {
    @PostMapping("/teste")
    fun criarAgendamento():ResponseEntity<Any> {
        calendarService.criarAgendamento()
        return ResponseEntity.ok("Evento criado com sucesso no Google Calendar. Verifique o calendário!")
    }
}