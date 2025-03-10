package SC.ProjetoSC

import com.fasterxml.jackson.annotation.JsonIgnore

data class Usuario(
    var Nome: String? = null,
    var Email: String? = null,
    var Telefone: String? = null,
    var Senha: String? = null,
    var Tipo: Int? = 0
)
