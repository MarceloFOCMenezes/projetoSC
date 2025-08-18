package SC.ProjetoSC.Request

import SC.ProjetoSC.Dto.InformacaoBoloDTO
import SC.ProjetoSC.entity.InformacaoBolo

data class AdicionarItemPedidoRequest(
    val idCliente: Int? = null,
    val idProduto: Int? = null,
    var quantidade: Int? = 0,
    val listaIngredientes: List<Int>? = null,
    val informacaoBolo: InformacaoBoloDTO? = null,
) {

}