package sc.projetosc.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para ingrediente no ranking do dashboard")
data class IngredienteRankingDTO(
    @Schema(description = "ID do ingrediente")
    val id: Int,
    
    @Schema(description = "Nome do ingrediente")
    val nome: String,
    
    @Schema(description = "Número de pedidos que contém este ingrediente")
    val quantidadePedidos: Int,
    
    @Schema(description = "Percentual de uso em relação ao total")
    val percentual: Double,
    
    @Schema(description = "Posição no ranking (1-5)")
    val posicao: Int
)