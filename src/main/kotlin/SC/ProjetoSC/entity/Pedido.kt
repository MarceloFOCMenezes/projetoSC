package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
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
    var id: Int = 0,

    @field:NotNull
    var dtPedido: LocalDateTime? = null,

    @field:NotNull @FutureOrPresent // não pode cadastrar uma entrega pra uma data que já foi
    var dtEntrega: LocalDateTime? = null,

    @field:NotNull
    var statusPagamento: StatusPagamento = StatusPagamento.NAO_PAGO,

    @field:NotNull @field:PositiveOrZero
    var precoTotal: Double? = 0.0,

    @field:NotNull
    var isRetirada: Boolean? = null
)