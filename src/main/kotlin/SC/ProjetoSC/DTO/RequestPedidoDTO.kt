import SC.ProjetoSC.Enum.FormaPagamentoEnum
import java.time.LocalDateTime

data class RequestPedidoDTO(
    val precoTotal: Double? = null,
    val isRetirada: Boolean? = null,
    val clienteId: Int? = null,
    val enderecoId: Int? = null,
    val formaPagamento: FormaPagamentoEnum? = null
)
{
    constructor() : this(null, null, null, null, null)
}