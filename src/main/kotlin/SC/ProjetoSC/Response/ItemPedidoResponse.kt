package SC.ProjetoSC.Response

import SC.ProjetoSC.entity.InformacaoBolo

data class ItemPedidoResponse(
    val descricao: String? = null,
    val quantidade: Int? = null,
    val precoUnitario: Double? = null,
    val informacaoBolo: InformacaoBolo? = null,
    val ingredientes: List<ItemPedidoIngredienteResponse>? = null
    ) {


}