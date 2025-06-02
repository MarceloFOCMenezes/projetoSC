package SC.ProjetoSC.Response

data class PedidoResponse (
    val dtPedido: String? = null,
    val dtEntregaEsperada: String? = null,
    val precoTotal: Double? = null,
    val isRetirada: Boolean? = null,
    val clienteId: Int? = null,
    val enderecoId: Int? = null,
    val statusPedido: String? = null,
    val formaPagamento: String? = null,
    val itensPedido: List<ItemPedidoResponse>? = null
){

}