package sc.projetosc.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
@Table(name = "tipo_ingrediente")
@Entity
data class TipoIngrediente(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_ingrediente", nullable = false, unique = true)
    val idTipoIngrediente: Int? = null,

    @field:NotBlank @field:Size(min = 2, max = 45)
    @Schema(description = "Descrição do tipo de ingrediente")
    val descricao: String? = null,

    @field:NotNull
    @field:PositiveOrZero(message = "A quantidade máxima deve ser zero ou positiva.")
    @Schema(description = "Quantidade máxima de ingredientes deste tipo")
    @Column(name = "quantidade_maxima", nullable = true)
    val quantidadeMaxima: Int? = null,
){
    constructor(): this(null, null){}
}
