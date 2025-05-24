package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull

@Entity
data class TipoIngrediente(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idTipoIngrediente: Int? = null,

    @field:NotBlank @field:Size(min = 2, max = 45)
    @Schema(description = "Descrição do tipo de ingrediente")
    val descricao: String? = null,

    @field:NotNull
    @field:PositiveOrZero(message = "A quantidade máxima deve ser zero ou positiva.")
    val quantidadeMaxima: Int? = null,
){
    constructor(): this(null, null){}
}
