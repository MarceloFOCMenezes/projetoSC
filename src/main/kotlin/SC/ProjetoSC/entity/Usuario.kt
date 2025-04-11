package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull

/*
Com @Entity, o Spring vai supor que essa classe 'espelha',
'mapeia' uma tabela chamada 'Usuario'
 */
@Entity
data class Usuario(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Int?,

    @field:NotBlank @field:Size(min = 2, max = 45)
    var nome: String? = null,

    @field:NotBlank @field:Size(min = 6, max = 150) @field:Email
    var email: String? = null,

    @field:NotBlank @field:Size(min = 11, max = 11)
    var telefone: String? = null,

    @field:NotBlank @field:Size(min = 8, max = 45)
    var senha: String? = null,

    @field:NotNull @field:PositiveOrZero
    var tipo: Int? = 0
) {

    // O JPA exige que exista um construtor vazio nas Entidades
    constructor() : this(null, null, null, null, null, null)
    /*
    Aqui dizemos que sempre que o construtor vazio for invocado,
    será criado um Usuario com todos os campos nulos
     */
}
