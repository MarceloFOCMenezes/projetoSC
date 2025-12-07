package sc.projetosc.dto

data class InformacaoBoloDTO(
    var tema: String? = null,
    var detalhes: String? = null,
    var anexo: Int? = null,
) { constructor() : this(null, null) 

}