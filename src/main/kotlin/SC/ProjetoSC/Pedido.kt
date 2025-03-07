package SC.ProjetoSC

import java.time.LocalDateTime

data class Pedido (

    var dtPedido: LocalDateTime? = null,
    var dtEntrega: LocalDateTime? = null,
    var statusPagamento: Boolean = false,
    var precoTotal: Double = 0.0,
    var isRetirada: Boolean = false

)