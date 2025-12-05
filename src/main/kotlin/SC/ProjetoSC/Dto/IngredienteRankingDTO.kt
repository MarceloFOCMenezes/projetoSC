package sc.projetosc.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para ranking de ingredientes")
data class IngredienteRankingDTO(
    @Schema(description = "ID do ingrediente")
    val ingredienteId: Int,
    
    @Schema(description = "Nome do ingrediente")
    val ingredienteNome: String,
    
    @Schema(description = "Quantidade de vezes que o ingrediente foi pedido")
    val quantidadePedidos: Int
)