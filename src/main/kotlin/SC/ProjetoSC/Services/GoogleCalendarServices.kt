package sc.projetosc.Services

import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar.Calendars
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import org.springframework.stereotype.Service
import java.util.Calendar

@Service
class GoogleCalendarServices (
    private val calendar: com.google.api.services.calendar.Calendar
) {
    fun criarAgendamento() {
        val evento = Event()
            .setSummary("Pedido ")
            .setDescription("Cliente: Ana, Produtos: Bolo de Morango")
            .setStart(
                EventDateTime()
                    .setDateTime(DateTime("2025-08-02T10:00:00-03:00"))
                    .setTimeZone("America/Sao_Paulo"))
            .setEnd(
                EventDateTime()
                    .setDateTime(DateTime("2025-08-02T11:30:00-03:00"))
                    .setTimeZone("America/Sao_Paulo"))

        calendar.events().insert("eledocesprojeto@gmail.com", evento).execute()
    }
}