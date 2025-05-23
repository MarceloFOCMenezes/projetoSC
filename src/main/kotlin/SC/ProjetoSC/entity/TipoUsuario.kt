package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
data class TipoUsuario(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idTipo: Int? = null,

    @field:NotBlank(message = "O nome é obrigatório e não pode ser vazio.")
    @field:Size(min = 1, max = 20, message = "O nome deve ter entre 1 e 20 caracteres.")
    val nomeTipo: String? = null
){
    constructor() : this(null, null)
}