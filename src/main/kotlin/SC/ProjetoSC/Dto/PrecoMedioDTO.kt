package sc.projetosc.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para preço médio dos pedidos")
data class PrecoMedioDTO(
    @Schema(description = "Valor médio dos pedidos concluídos")
    val precoMedio: Double,
    
    @Schema(description = "Total de pedidos concluídos no período")
    val totalPedidos: Int
)
