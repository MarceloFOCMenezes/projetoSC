package sc.projetosc.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "DTO para alertas operacionais do dashboard")
data class AlertaOperacionalDTO(
    @Schema(description = "Tipo do alerta")
    val tipo: String,
    
    @Schema(description = "Título do alerta")
    val titulo: String,
    
    @Schema(description = "Descrição do alerta")
    val descricao: String,
    
    @Schema(description = "Nível de prioridade (LOW, MEDIUM, HIGH)")
    val prioridade: String,
    
    @Schema(description = "Data relacionada ao alerta, se aplicável")
    val dataReferencia: LocalDate? = null
)

@Schema(description = "DTO para dias com alta demanda")
data class DiaAltaDemandaDTO(
    @Schema(description = "Data do dia")
    val data: LocalDate,
    
    @Schema(description = "Quantidade de pedidos")
    val quantidadePedidos: Int,
    
    @Schema(description = "Indica se está acima da capacidade normal")
    val acimaCapacidade: Boolean
)