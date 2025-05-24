package SC.ProjetoSC.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Schema(description = "Chave composta da associação entre ItemPedido e Anexo")
@Embeddable
data class InformacaoBoloId(
    @Schema(description = "ID do ItemPedido")
    @Column(name = "fkItemPedido")
    @field:NotNull
    val itemPedidoId: Int = 0,

    @Schema(description = "ID do Anexo")
    @Column(name = "fkAnexo")
    @field:NotNull
    val anexoId: Int = 0
) : Serializable
{ constructor() : this(0, 0) }

@Entity
data class InformacaoBolo(

    @Schema(description = "Chave composta formada pelas FKs de ItemPedido e Anexo")
    @EmbeddedId
    val id: InformacaoBoloId,

    @field:NotBlank @field:Size(min = 2, max = 255)
    @Schema(description = "Tema do bolo")
    val tema: String? = null,

    @field:NotBlank @field:Size(min = 2, max = 255)
    @Schema(description = "Detalhes do bolo")
    val detalhes: String? = null,

    @Schema(description = "Referência ao ItemPedido")
    @field:NotNull
    @ManyToOne
    @MapsId("itemPedidoId")
    @JoinColumn(name = "fkItemPedido", nullable = false)
    val itemPedido: ItemPedido? = null,

    @Schema(description = "Referência ao Anexo")
    @field:NotNull
    @ManyToOne
    @MapsId("anexoId")
    @JoinColumn(name = "fkAnexo", nullable = false)
    val anexo: Anexo? = null,
){constructor() : this(InformacaoBoloId(0, 0), null, null, null, null)}