package sc.projetosc.entity

import sc.projetosc.entity.Anexo
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull

@Table(name = "informacao_bolo")
@Entity
data class InformacaoBolo(

    @Schema(description = "Chave composta formada pelas FKs de ItemPedido e Anexo")
    @Id
    @Column(name = "id_item_pedido")
    val idItemPedido: Int? = null,

    @Schema(description = "Tema do bolo")
    val tema: String? = null,

    @Schema(description = "Detalhes do bolo")
    val detalhes: String? = null,

    @Schema(description = "Referência ao Anexo")
    @field:NotNull
    @ManyToOne
    @JoinColumn(name = "fk_anexo", nullable = false)
    val anexo: Anexo? = null,
){constructor() : this(null, null, null, null)}