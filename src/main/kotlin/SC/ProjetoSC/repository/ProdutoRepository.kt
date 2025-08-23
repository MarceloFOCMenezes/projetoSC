package sc.projetosc.repository

import sc.projetosc.entity.Produto
import org.springframework.data.jpa.repository.JpaRepository
interface ProdutoRepository: JpaRepository<Produto, Int> {

    // listar produtos com filtro de nome
    fun findByDescricaoContainsIgnoreCase(nome: String): List<Produto>

    //listar produtos com filtro de nome e somente ativos (que vem como padrão)
    fun findByDescricaoContainsIgnoreCaseAndAtivoTrue(nome:String): List<Produto>

    // listar todos os produtos com filtro de ativo que sejam true
    fun findByAtivoTrue(): List<Produto>

    //listar todos os produtos com filtro de ativo que sejam false
    fun findByAtivoFalse(): List<Produto>

    //listar produtos com filtro de nome e somente inativos
    fun findByDescricaoContainsIgnoreCaseAndAtivoFalse(descricao: String): List<Produto>
}