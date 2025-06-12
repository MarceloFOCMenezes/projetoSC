package SC.ProjetoSC.DTO

data class RequestIngredienteDTO(
    val descricao: String? = null,
    val precoUnitario: Double? = null,
    val categoria: String? = null,
    val ativo: Boolean? = null,
    val observacao: String? = null
) {
    constructor() : this(null, null, null, true, null)
}
// Esse DTO é usado para receber os dados de um ingrediente quando um novo ingrediente é criado