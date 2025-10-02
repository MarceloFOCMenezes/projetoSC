package sc.projetosc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.awt.Desktop
import java.net.URI

@SpringBootApplication
class ProjetoScApplication

fun main(args: Array<String>) {
	runApplication<ProjetoScApplication>(*args)
}
@Component
class SwaggerOpener {
	@EventListener(ApplicationReadyEvent::class)
	fun openSwaggerUi() {
		if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
			try {
				Desktop.getDesktop().browse(URI("http://localhost:8080/swagger-ui/index.html"))
			} catch (e: Exception) {
				println("Não foi possível abrir o navegador: ${e.message}")
			}
		}
	}
}

@Configuration
class CorsConfig {
	@Bean
	fun corsConfigurer(): WebMvcConfigurer {
		return object : WebMvcConfigurer {
			override fun addCorsMappings(registry: CorsRegistry) {
				registry.addMapping("/**")
					.allowedOrigins("http://localhost:5173")
					.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
					.allowedHeaders("*")
			}
		}
	}
}

