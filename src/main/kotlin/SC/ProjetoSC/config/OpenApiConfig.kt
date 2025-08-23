package sc.projetosc.config

import io.swagger.v3.oas.models.ExternalDocumentation
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("API de Gestão de Pedidos - Elê Doces")
                    .version("1.0.0")
                    .description("Esta API permite o gerenciamento de pedidos personalizados e controle de produção de um ateliê de confeitaria. As funcionalidades incluem cadastro de usuários, registro e acompanhamento de pedidos, e integração com o sistema interno da confeiteira.")
                    .contact(
                        Contact().name("Equipe Elê Doces").email("eduardo.azevedo@sptech.school")
                    )
//                    .license(
//                        License().name("MIT License").url("https://opensource.org/licenses/MIT")
//                    )
            )
            .externalDocs(
                ExternalDocumentation()
                    .description("Documentação completa no GitHub")
                    .url("https://github.com/SquadSC/projetoSC_back")
            )
    }
}