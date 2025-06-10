package SC.ProjetoSC.Request

data class EnviarPedidoRequest (
    var idPedido: Int? = null,
    var isRetirada: Boolean? = null,
    var formaPagamento: String? = null,
    var enderecoId: Int? = null,
    var dataEntregaEsperada: String? = null
){

}