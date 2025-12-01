package sc.projetosc.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "DTO para dados de pedidos por período")
data class PedidosPeriodoDTO(
    @Schema(description = "Data do período")
    val data: LocalDate,
    
    @Schema(description = "Quantidade de pedidos no período")
    val quantidade: Int
)

@Schema(description = "DTO para estatísticas gerais do dashboard")
data class EstatisticasGeraisDTO(
    @Schema(description = "Total de pedidos no período")
    val totalPedidosPeriodo: Int,
    
    @Schema(description = "Receita total no período")
    val receitaTotalPeriodo: Double,
    
    @Schema(description = "Ticket médio")
    val ticketMedio: Double,
    
    @Schema(description = "Taxa de conclusão de pedidos")
    val taxaConclusao: Double
)