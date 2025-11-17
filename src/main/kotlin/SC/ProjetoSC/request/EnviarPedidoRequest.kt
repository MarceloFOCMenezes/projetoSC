package sc.projetosc.request

data class EnviarPedidoRequest (
    var idPedido: Int? = null,
    var isRetirada: Boolean? = null,
    var enderecoId: Int? = null,
    var dataEntregaEsperada: String? = null
){

}