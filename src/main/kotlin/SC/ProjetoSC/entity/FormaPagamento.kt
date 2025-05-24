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
data class FormaPagamento(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do tipo de pagamento")
    val idPagamento:Int? = null,

    @field:NotBlank @field:Size(min = 2, max = 45)
    @Schema(description = "Descrição do tipo de pagamento")
    val descricao: String? = null

){ constructor() : this(null, null) }
