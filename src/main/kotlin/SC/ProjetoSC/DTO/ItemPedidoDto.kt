package SC.ProjetoSC.DTO

import SC.ProjetoSC.entity.InformacaoBolo
import SC.ProjetoSC.entity.ItemPedidoIngrediente

data class ItemPedidoDto(
    val descricao: String? = null,
    val quantidade: Int? = null,
    val precoUnitario: Double? = null,
    val informacaoBolo: InformacaoBolo? = null,
    val ingredientes: List<ItemPedidoIngrediente>? = null

    ) {


}