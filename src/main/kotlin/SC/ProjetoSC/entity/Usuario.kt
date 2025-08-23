package sc.projetosc.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import sc.projetosc.Enum.TipoUsuarioEnum
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
    @Column(name = "id_usuario")
    val id:Int? = null,

    @field:NotBlank @field:Size(min = 2, max = 45)
    @Schema(description = "Nome do usuário")
    @Column(name = "nome_usuario")
    var nome: String? = null,

    @field:NotBlank @field:Size(min = 6, max = 150) @field:Email
    @Schema(description = "E-mail do usuário")
    @Column(name = "email_usuario", unique = true) // indica que o e-mail não pode se repetir
    var email: String? = null,

    @field:NotBlank @field:Size(min = 11, max = 11)
    @Schema(description = "Número de telefone do usuário")
    @Column(name = "telefone_usuario")
    var telefone: String? = null,


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @field:Size(min = 8, max = 60)
    @Schema(description = "Senha da conta do usuário")
    @Column(name = "senha_usuario")
    var senha: String? = null,


    @Schema(description = "Indica o tipo de usuário: 0 - Administrador/Confeiteiro, 1 - Cliente")
    @Enumerated(EnumType.STRING) // para armazenar o valor como string no banco de dados
    @Column(name = "tipo_usuario", nullable = false)
    var tipo: TipoUsuarioEnum? = null,

    @Transient // não será persistido no banco de dados
    @Schema(description = "Indica se o usuário está logado")
    var logado: Boolean = true,

    @Schema(description = "Data e hora do último login do usuário")
    @Column(name = "data_ultimo_login")
    var dataUltimoLogin: LocalDateTime? = null
) {

    // O JPA exige que exista um construtor vazio nas Entidades
    constructor() : this(null, null, null, null, null, null)
    /*
    Aqui dizemos que sempre que o construtor vazio for invocado,
    será criado um Usuario com todos os campos nulos
     */
}
