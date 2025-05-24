package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime

/*
Com @Entity, o Spring vai supor que essa classe 'espelha',
'mapeia' uma tabela chamada 'Usuario'
 */
@Entity
data class Usuario(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do usuário")
    val id:Int? = null,

    @field:NotBlank @field:Size(min = 2, max = 45)
    @Schema(description = "Nome do usuário")
    val nome: String? = null,

    @field:NotBlank @field:Size(min = 6, max = 150) @field:Email
    @Schema(description = "E-mail do usuário")
    val email: String? = null,

    @field:NotBlank @field:Size(min = 11, max = 11)
    @Schema(description = "Número de telefone do usuário")
    val telefone: String? = null,

    @JsonIgnore
    @field:NotBlank @field:Size(min = 8, max = 45)
    @Schema(description = "Senha da conta do usuário")
    val senha: String? = null,


    @field:NotNull
    @Schema(description = "Indica o tipo de usuário: 0 - Administrador/Confeiteiro, 1 - Cliente")
    @ManyToOne
    @JoinColumn(name = "fkTipoUsuario", nullable = false) // indica o nome do atributo na tabela, nullable false indica que não pode ser nulo
    val tipo: TipoUsuario? = null,

    @Schema(description = "Indica se o usuário está logado")
    val logado: Boolean = false,

    @Schema(description = "Data e hora do último login do usuário")
    val dataUltimoLogin: LocalDateTime? = null
) {

    // O JPA exige que exista um construtor vazio nas Entidades
    constructor() : this(null, null, null, null, null, null)
    /*
    Aqui dizemos que sempre que o construtor vazio for invocado,
    será criado um Usuario com todos os campos nulos
     */
}
