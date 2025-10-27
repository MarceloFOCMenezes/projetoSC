package sc.projetosc.config

import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.ServiceAccountCredentials
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.InputStream

// @Configuration
class GoogleCalendarConfig {

    @Bean
    fun calendarService(): Calendar {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        val serviceAccountStream: InputStream = javaClass.getResourceAsStream("/ele-doces-calendar.json")
            ?: throw IllegalStateException("Arquivo de credencial JSON não encontrado.")

        val credentials = ServiceAccountCredentials.fromStream(serviceAccountStream)
            .createScoped(listOf(CalendarScopes.CALENDAR))
//        val credentials = ServiceAccountCredentials.fromStream(serviceAccountStream)
//            .createScoped(listOf(CalendarScopes.CALENDAR))

        return Calendar.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName("Ele Doces Agendamento")
            .build()
    }
}