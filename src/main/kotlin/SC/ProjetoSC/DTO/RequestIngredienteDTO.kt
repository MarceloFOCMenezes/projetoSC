package SC.ProjetoSC.dto

data class RequestIngredienteDto(
    val idTipoIngrediente: Int? = null,
    val nome: String? = null,
    val is_premium: Boolean? = null,
    val Ativo: Boolean? = null,
) {
    constructor() : this(null, null, false, true,)
}
// Esse DTO é usado para receber os dados de um ingrediente quando um novo ingrediente é criado