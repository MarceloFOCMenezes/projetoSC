package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "Status_Pedido")
data class StatusPedido(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_Pedido")
    val idStatusPedido: Int? = null,

    @field:NotBlank @field:Size(min = 2, max = 255)
    @Schema(description = "Descrição do status do pedido")
    val descricao: String? = null
) {
}