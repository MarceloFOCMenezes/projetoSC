package sc.projetosc.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para ranking de clientes por valor gasto")
data class ClienteRankingDTO(
    @Schema(description = "ID do cliente")
    val clienteId: Int,
    
    @Schema(description = "Nome do cliente")
    val clienteNome: String,
    
    @Schema(description = "Email do cliente")
    val clienteEmail: String,
    
    @Schema(description = "Total gasto pelo cliente no período")
    val totalGasto: Double,
    
    @Schema(description = "Quantidade de pedidos do cliente no período")
    val quantidadePedidos: Int
)
