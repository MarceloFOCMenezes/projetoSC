package SC.ProjetoSC

import com.fasterxml.jackson.annotation.JsonIgnore

class Usuario {
    var Nome:String? = null;
    var Email:String? = null;
    @JsonIgnore
    var Senha:String? = null;
    var Telefone:String? = null;
    var Tipo:Int? = 0;

}