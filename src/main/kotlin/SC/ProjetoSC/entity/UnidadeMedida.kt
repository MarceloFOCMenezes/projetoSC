package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
data class UnidadeMedida (
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idUnidade: Int? = null,

    @field:NotBlank(message = "O nome da unidade de medida é obrigatório e não pode ser vazio.")
    @field:Size(min = 1, max = 45, message = "O nome deve ter entre 1 e 45 caracteres.")
    val unidadeMedida: String? = null,
) {
    constructor() : this(null, null)
}