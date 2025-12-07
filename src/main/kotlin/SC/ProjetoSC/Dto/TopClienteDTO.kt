package sc.projetosc.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para cliente no ranking do dashboard")
data class TopClienteDTO(
    @Schema(description = "ID do cliente")
    val id: Int,
    
    @Schema(description = "Nome do cliente")
    val nome: String,
    
    @Schema(description = "Email do cliente")
    val email: String,
    
    @Schema(description = "Quantidade de pedidos no período")
    val quantidadePedidos: Int,
    
    @Schema(description = "Valor total gasto no período")
    val valorTotal: Double,
    
    @Schema(description = "Posição no ranking")
    val posicao: Int
)