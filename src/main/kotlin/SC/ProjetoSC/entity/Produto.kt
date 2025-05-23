package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

@Entity
data class Produto(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idProduto: Int? = null,

    @field:NotBlank(message = "A descrição do produto é obrigatória e não pode ser vazia.")
    @field:Size(min = 1, max = 100, message = "A descrição deve ter entre 1 e 100 caracteres.")
    val descricao: String? = null,

    @field:NotNull(message = "O preço unitário é obrigatório.")
    @field:PositiveOrZero(message = "O preço unitário deve ser zero ou positivo.")
    val precoUnitario: BigDecimal? = null,

    @field:NotBlank(message = "A categoria é obrigatória e não pode ser vazia.")
    @field:Size(min = 1, max = 50, message = "A categoria deve ter entre 1 e 50 caracteres.")
    val categoria: String? = null,

    @field:NotNull(message = "O campo ativo é obrigatório.")
    val ativo: Boolean? = null,

    @field:NotNull(message = "O campo temIngrediente é obrigatório.")
    val temIngrediente: Boolean? = null,

    @field:Size(max = 255, message = "A observação deve ter no máximo 255 caracteres, preenchimento opcional.")
    val observacao: String? = null,


    @ManyToOne
    @JoinColumn(name = "fkunidadeMedida", nullable = false)
    @field:NotNull(message = "A unidade de medida é obrigatória.")
    val unidadeMedida: UnidadeMedida? = null
) {
    constructor() : this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )
}
