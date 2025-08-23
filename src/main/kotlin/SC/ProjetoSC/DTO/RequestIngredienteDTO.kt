package sc.projetosc.dto

data class RequestIngredienteDto(
    var idTipoIngrediente: Int? = null,
    var nome: String? = null,
    var is_premium: Boolean? = null,
    var Ativo: Boolean? = null,
) {
    constructor() : this(null, null, false, true,)
}
// Esse DTO é usado para receber os dados de um ingrediente quando um novo ingrediente é criado