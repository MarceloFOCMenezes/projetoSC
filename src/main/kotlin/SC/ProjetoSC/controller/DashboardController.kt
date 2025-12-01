package sc.projetosc.controller

import sc.projetosc.dto.DashboardOverviewDTO
import sc.projetosc.dto.EstatisticasGeraisDTO
import sc.projetosc.dto.PedidosPeriodoDTO
import sc.projetosc.dto.TopClienteDTO
import sc.projetosc.dto.RankingIngredientesPorTipoDTO
import sc.projetosc.services.DashboardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "Endpoints para dados do dashboard operacional")
@CrossOrigin(origins = ["*"])
class DashboardController(
    private val dashboardService: DashboardService
) {
    private val logger = LoggerFactory.getLogger(DashboardController::class.java)

    @GetMapping("/overview")
    @Operation(
        summary = "Buscar dados do overview do dashboard",
        description = "Retorna informações sobre pedidos hoje, pendentes, produzindo e concluídos"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Dados do overview retornados com sucesso"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    fun getDashboardOverview(): ResponseEntity<DashboardOverviewDTO> {
        return try {
            val overview = dashboardService.getDashboardOverview()
            ResponseEntity.ok(overview)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            println("Erro ao buscar overview do dashboard: ${e.message}")
            e.printStackTrace()
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/top-ingredientes")
    @Operation(
        summary = "Ranking dos Top 5 Ingredientes por Categoria",
        description = """
            Retorna o ranking dos 5 ingredientes mais utilizados em pedidos, organizados por categoria:
            
            **Categorias:**
            - **Massa**: Top 5 ingredientes do tipo 'Massa'
            - **Recheio**: Top 5 ingredientes do tipo 'Recheio' 
            - **Adicional**: Top 5 ingredientes do tipo 'Adicional'
            
            **Dados retornados para cada ingrediente:**
            - ID e nome do ingrediente
            - Tipo/categoria do ingrediente
            - Quantidade de pedidos que utilizam o ingrediente
            - Percentual de uso dentro da categoria
            - Posição no ranking (1-5)
            
            **Critérios:**
            - Baseado em pedidos com status: Produzindo, Pronto ou Entregue
            - Ordenado por quantidade de pedidos (decrescente)
            - Limitado a 5 ingredientes por categoria
            - Se uma categoria tiver menos de 5 ingredientes com pedidos, serão exibidos todos disponíveis
            - Ingredientes sem pedidos aparecem com quantidade 0
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", 
                description = "Ranking de ingredientes por categoria retornado com sucesso"
            ),
            ApiResponse(
                responseCode = "500", 
                description = "Erro interno do servidor ao processar ranking de ingredientes"
            )
        ]
    )
    fun getTop5Ingredientes(): ResponseEntity<RankingIngredientesPorTipoDTO> {
        return try {
            logger.info("Requisição recebida para top 5 ingredientes por tipo")
            val ranking = dashboardService.getTop5Ingredientes()
            logger.info("Top 5 ingredientes por tipo retornado com sucesso - Massa: ${ranking.massa.size}, Recheio: ${ranking.recheio.size}, Adicional: ${ranking.adicional.size}")
            ResponseEntity.ok(ranking)
        } catch (e: Exception) {
            logger.error("Erro ao buscar top 5 ingredientes por tipo", e)
            ResponseEntity.status(500).body(
                RankingIngredientesPorTipoDTO(
                    massa = emptyList(),
                    recheio = emptyList(),
                    adicional = emptyList()
                )
            )
        }
    }

    @GetMapping("/estatisticas")
    @Operation(
        summary = "Buscar estatísticas gerais do dashboard",
        description = "Retorna estatísticas como total de pedidos do período, receita total, ticket médio e taxa de conclusão"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Estatísticas retornadas com sucesso"),
            ApiResponse(responseCode = "400", description = "Parâmetros de data inválidos"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    fun getEstatisticasGerais(
        @RequestParam dataInicio: String,
        @RequestParam dataFim: String
    ): ResponseEntity<EstatisticasGeraisDTO> {
        return try {
            // Validar formato das datas
            if (!isValidDateFormat(dataInicio) || !isValidDateFormat(dataFim)) {
                return ResponseEntity.badRequest().build()
            }
            
            // Validar se data início é anterior à data fim
            if (dataInicio > dataFim) {
                return ResponseEntity.badRequest().build()
            }
            
            val estatisticas = dashboardService.getEstatisticasGerais(dataInicio, dataFim)
            ResponseEntity.ok(estatisticas)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            println("Erro ao buscar estatísticas: ${e.message}")
            e.printStackTrace()
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/pedidos-periodo")
    @Operation(
        summary = "Buscar pedidos por período",
        description = "Retorna a quantidade de pedidos por dia no período especificado baseado na data de entrega esperada"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Dados do período retornados com sucesso"),
            ApiResponse(responseCode = "400", description = "Parâmetros de data inválidos"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    fun getPedidosPorPeriodo(
        @RequestParam dataInicio: String,
        @RequestParam dataFim: String
    ): ResponseEntity<List<PedidosPeriodoDTO>> {
        return try {
            // Validar formato das datas
            if (!isValidDateFormat(dataInicio) || !isValidDateFormat(dataFim)) {
                return ResponseEntity.badRequest().build()
            }
            
            // Validar se data início é anterior à data fim
            if (dataInicio > dataFim) {
                return ResponseEntity.badRequest().build()
            }
            
            val dados = dashboardService.getPedidosPorPeriodo(dataInicio, dataFim)
            ResponseEntity.ok(dados)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            println("Erro ao buscar pedidos por período: ${e.message}")
            e.printStackTrace()
            ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/top-clientes")
    @Operation(
        summary = "Buscar top clientes por período",
        description = "Retorna os 10 clientes que mais gastaram no período especificado baseado na data de entrega esperada"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Top clientes retornados com sucesso"),
            ApiResponse(responseCode = "400", description = "Parâmetros de data inválidos"),
            ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        ]
    )
    fun getTopClientes(
        @RequestParam dataInicio: String,
        @RequestParam dataFim: String
    ): ResponseEntity<List<TopClienteDTO>> {
        return try {
            // Validar formato das datas
            if (!isValidDateFormat(dataInicio) || !isValidDateFormat(dataFim)) {
                return ResponseEntity.badRequest().build()
            }
            
            // Validar se data início é anterior à data fim
            if (dataInicio > dataFim) {
                return ResponseEntity.badRequest().build()
            }
            
            val clientes = dashboardService.getTopClientes(dataInicio, dataFim)
            ResponseEntity.ok(clientes)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (e: Exception) {
            println("Erro ao buscar top clientes: ${e.message}")
            e.printStackTrace()
            ResponseEntity.internalServerError().build()
        }
    }
    
    private fun isValidDateFormat(date: String): Boolean {
        return try {
            val pattern = Regex("^\\d{4}-\\d{2}-\\d{2}$")
            if (!pattern.matches(date)) return false
            
            val parts = date.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()
            
            // Validações básicas
            if (year < 2000 || year > 2100) return false
            if (month < 1 || month > 12) return false
            if (day < 1 || day > 31) return false
            
            // Validação mais específica seria com LocalDate.parse, mas isso pode lançar exceção
            java.time.LocalDate.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }
}