package sc.projetosc.request

import sc.projetosc.dto.InformacaoBoloDTO

data class AdicionarItemPedidoRequest(
    val idCliente: Int? = null,
    val idProduto: Int? = null,
    var quantidade: Int? = 0,
    val listaIngredientes: List<Int>? = null,
    val informacaoBolo: InformacaoBoloDTO? = null,
) {

}