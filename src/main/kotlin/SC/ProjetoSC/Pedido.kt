package SC.ProjetoSC

import java.time.LocalDateTime

data class Pedido (

    var id: Int = 0,
    var dtPedido: LocalDateTime? = null,
    var dtEntrega: LocalDateTime? = null,
    var statusPagamento: StatusPagamento = StatusPagamento.NAO_PAGO,
    var precoTotal: Double = 0.0,
    var isRetirada: Boolean = false

)