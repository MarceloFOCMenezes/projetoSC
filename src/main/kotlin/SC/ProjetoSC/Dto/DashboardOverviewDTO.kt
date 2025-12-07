package sc.projetosc.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para dados do overview do dashboard")
data class DashboardOverviewDTO(
    @Schema(description = "Número de pedidos realizados hoje")
    val pedidosHoje: Int,
    
    @Schema(description = "Número de pedidos pendentes")
    val pendentes: Int,
    
    @Schema(description = "Número de pedidos em produção")
    val produzindo: Int,
    
    @Schema(description = "Número de pedidos concluídos hoje")
    val concluidos: Int
)