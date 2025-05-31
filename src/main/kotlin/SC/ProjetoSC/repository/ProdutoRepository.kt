package SC.ProjetoSC.repository

import SC.ProjetoSC.entity.Produto
import org.springframework.data.jpa.repository.JpaRepository

interface ProdutoRepository: JpaRepository<Produto, Int> {

    //todo
    // buscar produto por nome
    fun findByNome(nome: String): List<Produto>
}