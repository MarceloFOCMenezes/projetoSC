package sc.projetosc.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Table(name = "item_pedido")
@Entity
data class ItemPedido(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_pedido", nullable = false, unique = true)
    val idItemPedido: Int? = null,


    @field:NotNull(message = "O pedido respectivo é obrigatório.")
    @Schema(description = "FK do pedido respectivo")
    @ManyToOne
    @JoinColumn(name = "fk_pedido", nullable = false)
    val pedido: Pedido? = null,

    @field:NotNull(message = "O produto respectivo é obrigatório.")
    @Schema(description = "FK do produto respectivo")
    @ManyToOne
    @JoinColumn(name = "fk_produto", nullable = false)
    val produto: Produto? = null,

    @field:NotNull(message = "A quantidade do produto é obrigatória.")
    @Schema(description = "Quantidade do produto no item do pedido")
    val quantidade: Int? = null,

    @Schema(description = "Preco do item do pedido")
    val preco_item_pedido: Double? = null,
){}
