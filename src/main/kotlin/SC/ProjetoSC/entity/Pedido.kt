package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDateTime

@Entity
data class Pedido (
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do pedido")
    var id: Int = 0,

    @field:NotNull
    @Schema(description = "Data em que o pedido foi realizado")
    var dtPedido: LocalDateTime? = null,

    @field:NotNull @FutureOrPresent // não pode cadastrar uma entrega pra uma data que já foi
    @Schema(description = "Data de entrega do pedido")
    var dtEntrega: LocalDateTime? = null,

    @field:NotNull
    @Schema(description = "Status atual do pagamento do pedido")
    var statusPagamento: StatusPagamento = StatusPagamento.NAO_PAGO,

    @field:NotNull @field:PositiveOrZero
    @Schema(description = "Preço total do pedido")
    var precoTotal: Double? = 0.0,

    @field:NotNull
    @Schema(description = "Indica se o pedido é para retirada ou entrega")
    var isRetirada: Boolean? = null
)