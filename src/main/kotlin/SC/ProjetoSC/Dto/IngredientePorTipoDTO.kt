package sc.projetosc.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para ingrediente no ranking por tipo")
data class IngredientePorTipoDTO(
    @Schema(description = "ID do ingrediente")
    val id: Int,
    
    @Schema(description = "Nome do ingrediente")
    val nome: String,
    
    @Schema(description = "Tipo do ingrediente")
    val tipoIngrediente: String,
    
    @Schema(description = "Número de pedidos que contém este ingrediente")
    val quantidadePedidos: Int,

    @Schema(description = "Posição no ranking do tipo (1-5 ou menos)")
    val posicao: Int
)

@Schema(description = "DTO para ranking de ingredientes por categoria")
data class RankingIngredientesPorTipoDTO(
    @Schema(description = "Ingredientes tipo Massa")
    val massa: List<IngredientePorTipoDTO>,
    
    @Schema(description = "Ingredientes tipo Recheio")
    val recheio: List<IngredientePorTipoDTO>,
    
    @Schema(description = "Ingredientes tipo Adicional")
    val adicional: List<IngredientePorTipoDTO>
)