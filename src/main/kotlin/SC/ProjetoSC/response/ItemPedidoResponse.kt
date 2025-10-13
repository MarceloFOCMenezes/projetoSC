package sc.projetosc.Response

import sc.projetosc.Response.ItemPedidoIngredienteResponse
import sc.projetosc.entity.InformacaoBolo

data class ItemPedidoResponse(
    val descricao: String? = null,
    val quantidade: Int? = null,
    val precoUnitario: Double? = null,
    val informacaoBolo: InformacaoBolo? = null,
    val ingredientes: List<ItemPedidoIngredienteResponse>? = null,










    
    val precoItem: Double? = null
    ) {


}