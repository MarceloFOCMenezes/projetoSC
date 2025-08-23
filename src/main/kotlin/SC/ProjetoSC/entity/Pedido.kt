package sc.projetosc.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import sc.projetosc.Enum.FormaPagamentoEnum
import sc.projetosc.entity.Endereco
import java.time.LocalDateTime

@Entity
@Table(name = "Pedido")
data class Pedido(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do pedido")
    @Column(name = "id_pedido", nullable = false, unique = true)
    val id: Int? = null,


    @Schema(description = "Data em que o pedido foi realizado")
    @Column(name = "dt_pedido", nullable = false)
    val dtPedido: LocalDateTime? = null,

    @FutureOrPresent // não pode cadastrar uma entrega pra uma data que já foi
    @Schema(description = "Data de entrega do pedido")
    @Column(name = "dt_entrega", nullable = true)
    val dtEntrega: LocalDateTime? = null,

    @FutureOrPresent // não pode cadastrar uma entrega pra uma data que já foi
    @Schema(description = "Data de entrega do pedido")
    @Column(name = "dt_entrega_esperada", nullable = true)
    var dtEntregaEsperada: LocalDateTime? = null,

    @field:NotNull @field:PositiveOrZero
    @Schema(description = "Preço total do pedido")
    @Column(name = "preco_total", nullable = false)
    var precoTotal: Double? = 0.0,

    @Schema(description = "Indica se o pedido é para retirada ou entrega")
    @Column(name = "is_retirada", nullable = false)
    var isRetirada: Boolean? = null,
    @field:NotNull (message = "A FK do cliente respectivo é obrigatória.")
    @Schema(description = "FK do cliente que fez o pedido - usuário")
    @ManyToOne
    @JoinColumn(name = "fk_cliente", nullable = false)
    val cliente: Usuario? = null,


    @Schema(description = "FK do endereço de entrega do pedido")
    @ManyToOne
    @JoinColumn(name = "fk_endereco", nullable = true)
    var endereco: Endereco? = null,

    @field:NotNull(message = "A FK do status do pedido é obrigatória.")
    @Schema(description = "FK do status atual do pedido")
    @ManyToOne
    @JoinColumn(name = "fk_status_pedido", nullable = false)
    var statusPedido: StatusPedido? = null,


    @Schema(description = "FK do status atual do pedido")
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento")
    var formaPagamento:FormaPagamentoEnum? = null,


    )