package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import org.jetbrains.annotations.NotNull
import java.io.Serializable

// Classe que representa a chave composta da tabela de associação N:N entre ItemPedido e Ingrediente.
// Utiliza as duas FKs como identificador único da relação.
@Schema(description = "Chave composta da associação entre ItemPedido e Ingrediente")
@Embeddable
data class ItemPedidoIngredienteId(

    @Schema(description = "ID do ItemPedido")
    @Column(name = "fk_item_pedido")
    @field:NotNull
    val itemPedidoId: Int = 0,

    @Schema(description = "ID do Ingrediente")
    @Column(name = "fk_ingrediente")
    @field:NotNull
    val ingredienteId: Int = 0

) : Serializable { constructor() : this(0, 0) }

// Entidade que representa a tabela de associação N:N entre ItemPedido e Ingrediente.
// Não possui um campo ID próprio, mas sim uma chave composta (@EmbeddedId).
@Schema(description = "Associação N:N entre ItemPedido e Ingrediente")
@Table(name = "item_pedido_ingrediente")
@Entity
data class ItemPedidoIngrediente(
    // Chave composta formada pelas FKs de ItemPedido e Ingrediente
    @Schema(description = "Chave composta formada pelas FKs de ItemPedido e Ingrediente")
    @EmbeddedId
    val id: ItemPedidoIngredienteId,

    // Relacionamento com ItemPedido. O @MapsId indica que o campo itemPedidoId da chave composta
    // será preenchido automaticamente com o id do ItemPedido associado.
    @Schema(description = "Referência ao ItemPedido")
    @field:NotNull
    @ManyToOne
    @MapsId("itemPedidoId")
    @JoinColumn(name = "fk_item_pedido", nullable = false)
    val itemPedido: ItemPedido? = null,

    // Relacionamento com Ingrediente. O @MapsId indica que o campo ingredienteId da chave composta
    // será preenchido automaticamente com o id do Ingrediente associado.
    @Schema(description = "Referência ao Ingrediente")
    @field:NotNull
    @ManyToOne
    @MapsId("ingredienteId")
    @JoinColumn(name = "fk_ingrediente", nullable = false)
    val ingrediente: Ingrediente? = null

) { constructor() : this(ItemPedidoIngredienteId(0, 0), null, null) }
