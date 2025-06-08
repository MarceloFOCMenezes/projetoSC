package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
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

    @field:NotBlank
    @Column(name = "cep", length = 8, nullable = false)
    @Schema(description = "CEP do endereço")
    val cep: String,

    @field:NotBlank
    @Column(name = "logradouro", length = 100, nullable = false)
    @Schema(description = "Logradouro, rua ou avenida")
    val logradouro: String,

    @field:NotBlank
    @Column(name = "numero", length = 10, nullable = false)
    @Schema(description = "Número do endereço")
    val numero: String,

    @Column(name = "complemento", length = 50)
    @Schema(description = "Complemento do endereço (ex: apto, bloco)")
    val complemento: String? = null,

    @field:NotBlank
    @Column(name = "bairro", length = 60, nullable = false)
    @Schema(description = "Bairro")
    val bairro: String,

    @field:NotBlank
    @Column(name = "cidade", length = 60, nullable = false)
    @Schema(description = "Cidade")
    val cidade: String,

    @field:NotBlank
    @Pattern(regexp = "[A-Z]{2}", message = "Estado deve ser a sigla, como SP, RJ, MG...")
    @Column(name = "estado", length = 2, nullable = false)
    @Schema(description = "Estado (UF)")
    val estado: String,

    @Column(name = "ponto_referencia", length = 100)
    @Schema(description = "Ponto de referência")
    val pontoReferencia: String? = null,

    @field:NotNull(message = "O campo ativo é obrigatório.")
    val ativo: Boolean? = null,

    @ManyToOne
    @JoinColumn(name = "fk_usuario")
    @JsonIgnore
    @Schema(description = "Usuário dono do endereço")
    val usuario: Usuario? = null


){
    constructor() : this(
        idEndereco = null,
        nomeEndereco = null,
        cep = "",
        logradouro = "",
        numero = "",
        complemento = null,
        bairro = "",
        cidade = "",
        estado = "",
        pontoReferencia = null,
        ativo = null,
        usuario = null
    )
}