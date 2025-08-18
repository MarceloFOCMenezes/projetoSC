package SC.ProjetoSC.dto

import SC.ProjetoSC.Enum.UnidadeMedidaEnum
import java.math.BigDecimal

data class RequestProdutoDTO(
    val descricao: String? = null,
    val precoUnitario: BigDecimal? = null,
    val categoria: String? = null,
    val ativo: Boolean? = null,
    val temIngrediente: Boolean? = null,
    val observacao: String? = null,
    val unidadeMedida: UnidadeMedidaEnum? = null
){
    constructor(descricao: String, precoUnitario: Double) : this(null, null, null, false, false, null, UnidadeMedidaEnum.unidade)
}
