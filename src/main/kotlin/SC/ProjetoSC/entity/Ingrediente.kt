package sc.projetosc.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull

@Table(name = "ingrediente")
@Entity
data class Ingrediente(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do usuário")
    @Column(name = "id_ingrediente", nullable = false, unique = true)
    val idIngrediente:Int? = null,

    @field:NotBlank @field:Size(min = 2, max = 45)
    @Schema(description = "Nome do ingrediente")
    @Column(name = "nome")
    val nome: String? = null,

    @Schema(description = "Indica se o produto é premium ou não")
    @Column(name = "is_premium", nullable = false)
    val premium: Boolean = false,

    @Schema(description = "Indica se o produto está ativo ou não")
    val ativo: Boolean = false,


    @field:NotNull
    @Schema(description = "Indica o tipo de ingrediente")
    @ManyToOne
    @JoinColumn(name = "fk_tipo_ingrediente", nullable = false) // indica o nome do atributo na tabela, nullable false indica que não pode ser nulo
    val tipoIngrediente: TipoIngrediente? = null,

    )
