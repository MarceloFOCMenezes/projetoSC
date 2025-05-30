package SC.ProjetoSC.repository

import SC.ProjetoSC.entity.Endereco
import org.springframework.data.jpa.repository.JpaRepository

interface EnderecoRepository: JpaRepository<Endereco, Int> {
    fun findByCep(cep: String): Endereco?

}