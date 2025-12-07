package sc.projetosc.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "DTO para comparação de pedidos entre períodos")
data class PedidosComparisonDTO(
    @Schema(description = "Total de pedidos concluídos no período atual")
    val periodoAtual: Int,
    
    @Schema(description = "Total de pedidos concluídos no período anterior")
    val periodoAnterior: Int,
    
    @Schema(description = "Percentual de crescimento/declínio entre os períodos")
    val percentualCrescimento: Double
)
