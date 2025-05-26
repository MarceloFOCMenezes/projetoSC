package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDateTime

@Entity
@Table(name = "Pedido")
data class Pedido (
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do pedido")
    @field:Column(name = "idPedido", nullable = false, unique = true)
    val id: Int? = null,

    @field:NotNull
    @Schema(description = "Data em que o pedido foi realizado")
    @Column(name = "Dt_Pedido", nullable = false)
    val dtPedido: LocalDateTime? = null,

    @field:NotNull @FutureOrPresent // não pode cadastrar uma entrega pra uma data que já foi
    @Schema(description = "Data de entrega do pedido")
    @Column(name = "Dt_Entrega", nullable = false)
    val dtEntrega: LocalDateTime? = null,

    @field:NotNull
    @Schema(description = "Status atual do pagamento do pedido")
    @Column(name = "fkStatus_pagamento", nullable = false)
    val statusPagamento: StatusPagamento = StatusPagamento.NAO_PAGO,

    @field:NotNull @field:PositiveOrZero
    @Schema(description = "Preço total do pedido")
    @Column(name = "Preco_Total", nullable = false)
    val precoTotal: Double? = 0.0,

    @field:NotNull
    @Schema(description = "Indica se o pedido é para retirada ou entrega")
    @Column(nullable = false)
    val isRetirada: Boolean? = null,



    @field:NotNull (message = "A FK do cliente respectivo é obrigatória.")
    @Schema(description = "FK do cliente que fez o pedido - usuário")
    @ManyToOne
    @JoinColumn(name = "fkCliente", nullable = false)
    val cliente:Usuario? = null,

    @field:NotNull(message = "A FK do endereço respectivo é obrigatória.")
    @Schema(description = "FK do endereço de entrega do pedido")
    @ManyToOne
    @JoinColumn(name = "fkEndereco", nullable = true)
    val endereco:Endereco? = null,

    @field:NotNull (message = "A FK do status do pedido é obrigatória.")
    @Schema(description = "FK do status atual do pedido")
    @ManyToOne
    @JoinColumn(name = "fkStatus_pedido", nullable = false)
    val statusPedido:StatusPedido? = null,

    @field:NotNull (message = "A FK do status do pedido é obrigatória.")
    @Schema(description = "FK do status atual do pedido")
    @ManyToOne
    @JoinColumn(name = "fkForma_pagamento", nullable = false)
    val fkForma_Pagamento:FormaPagamento? = null,


)