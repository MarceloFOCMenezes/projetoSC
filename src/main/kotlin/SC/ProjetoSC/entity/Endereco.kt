package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
@Table(name = "Endereco")
@Entity
data class Endereco (
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco", nullable = false, unique = true)
    val idEndereco: Int? = null,

    @field:NotBlank @field:Size(min = 2, max = 45)
    @Schema(description = "Nome do endereço")
    @Column(name = "nome_endereco")
    val nomeEndereco: String? = null,

    @field:NotBlank @field:Size(min = 2, max = 10)
    @Schema(description = "Número do endereço")
    @Column(name = "numero_endereco")
    val numeroEndereco: String? = null,

    @field:NotBlank @field:Size(min = 8, max = 8)
    @Schema(description = "CEP do endereço, apenas números")
    @Column(name = "cep_endereco")
    val cepEndereco: String? = null,
){
}