package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull

@Entity
data class Ingrediente(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do usuário")
    var idIngrediente:Int?,

    @field:NotBlank @field:Size(min = 2, max = 45)
    @Schema(description = "Nome do ingrediente")
    var nome: String? = null,

    @Schema(description = "Indica se o produto é premium ou não")
    var premium: Boolean = false,

    @Schema(description = "Indica se o produto está ativo ou não")
    var ativo: Boolean = false,


    @field:NotNull
    @Schema(description = "Indica o tipo de ingrediente")
    @ManyToOne
    @JoinColumn(name = "fkTipoIngrediente", nullable = false) // indica o nome do atributo na tabela, nullable false indica que não pode ser nulo
    var tipoIngrediente: TipoIngrediente? = null,

){}
