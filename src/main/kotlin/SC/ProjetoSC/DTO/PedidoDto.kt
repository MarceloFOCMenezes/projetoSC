package SC.ProjetoSC.DTO

import SC.ProjetoSC.entity.Produto

data class PedidoDto (
    val dtPedido: String? = null,
    val dtEntregaEsperada: String? = null,
    val precoTotal: Double? = null,
    val isRetirada: Boolean? = null,
    val clienteId: Int? = null,
    val enderecoId: Int? = null,
    val statusPedidoId: Int? = null,
    val formaPagamento: String? = null,
    val itensPedido: List<ItemPedidoDto>? = null
){

}