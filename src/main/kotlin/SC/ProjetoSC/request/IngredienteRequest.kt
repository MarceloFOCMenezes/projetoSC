package sc.projetosc.request


data class IngredienteRequest(
    var idTipoIngrediente: Int? = null,
    var nome: String? = null,
    var is_premium: Boolean? = null,
    var ativo: Boolean? = null,
) {
    constructor() : this(null, null, false, true,)
}
// Esse DTO é usado para receber os dados de um ingrediente quando um novo ingrediente é criado