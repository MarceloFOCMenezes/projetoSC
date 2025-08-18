package SC.ProjetoSC.dto

data class RequestEnderecoDTO(
    val nomeEndereco: String? = null,
    val cep: String? = null,
    val logradouro: String? = null,
    val numero: String? = null,
    val complemento: String? = null,
    val bairro: String? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val pontoReferencia: String? = null,
    val usuarioId: Int? = null,
    val ativo: Boolean? = null,
) {
    constructor() : this(
        nomeEndereco = null,
        cep = null,
        logradouro = null,
        numero = null,
        complemento = null,
        bairro = null,
        cidade = null,
        estado = null,
        pontoReferencia = null,
        usuarioId = null,
        ativo = null
    )
}