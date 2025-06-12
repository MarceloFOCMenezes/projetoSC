package SC.ProjetoSC.Response

import SC.ProjetoSC.entity.Endereco

data class PedidoResponse (
    val idPedido: Int? = null,
    val dtPedido: String? = null,
    val dtEntregaEsperada: String? = null,
    val precoTotal: Double? = null,
    val isRetirada: Boolean? = null,
    val clienteId: Int? = null,
    val endereco: Endereco? = null,
    val statusPedido: String? = null,
    val formaPagamento: String? = null,
    val itensPedido: List<ItemPedidoResponse>? = null
){

}