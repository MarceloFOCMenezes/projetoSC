package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
data class Endereco (
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val idEndereco: Int? = null,

    @field:NotBlank @field:Size(min = 2, max = 45)
    @Schema(description = "Nome do endereço")
    val nomeEndereco: String? = null,

    @field:NotBlank @field:Size(min = 2, max = 10)
    @Schema(description = "Número do endereço")
    val numeroEndereco: String? = null,

    @field:NotBlank @field:Size(min = 8, max = 8)
    @Schema(description = "CEP do endereço, apenas números")
    val cepEndereco: String? = null,
){
}