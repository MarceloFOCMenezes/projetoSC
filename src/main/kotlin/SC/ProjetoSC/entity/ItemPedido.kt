package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Entity
data class ItemPedido(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idItemPedido: Int? = null,

    @field:NotNull(message = "O pedido respectivo é obrigatório.")
    @Schema(description = "FK do pedido respectivo")
    @ManyToOne
    @JoinColumn(name = "fkPedido", nullable = false)
    val pedido: Pedido? = null,

    @field:NotNull(message = "O produto respectivo é obrigatório.")
    @Schema(description = "FK do produto respectivo")
    @ManyToOne
    @JoinColumn(name = "fkProduto", nullable = false)
    val produto: Produto? = null,
){}
