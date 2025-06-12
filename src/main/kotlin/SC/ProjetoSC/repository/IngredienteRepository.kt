package SC.ProjetoSC.repository

import SC.ProjetoSC.entity.Ingrediente
import org.springframework.data.jpa.repository.JpaRepository

interface IngredienteRepository:JpaRepository<Ingrediente, Int> {
    // Define any additional query methods if needed
    // For example, you can find ingredients by name or type
    // fun findByName(name: String): List<IngredienteRepository>

    // listar ingredientes com filtro de nome
    fun findByDescricaoContainsIgnoreCase(descricao: String): List<Ingrediente>

    //listar ingredientes com filtro de nome e somente ativos (que vem como padrão)
    fun findByDescricaoContainsIgnoreCaseAndAtivoTrue(descricao: String): List<Ingrediente>

    // listar todos os ingredientes com filtro de ativo que sejam true
    fun findByAtivoTrue(): List<Ingrediente>

    //listar todos os ingredientes com filtro de ativo que sejam false
    fun findByAtivoFalse(): List<Ingrediente>

    //listar ingredientes com filtro de nome e somente inativos
    fun findByDescricaoContainsIgnoreCaseAndAtivoFalse(descricao: String): List<Ingrediente>

    //listar ingredientes por tipo
    fun findByTipoContainsIgnoreCase(tipo: String): List<Ingrediente>

    //listar ingredientes por tipo e somente ativos
    fun findByTipoContainsIgnoreCaseAndAtivoTrue(tipo: String): List<Ingrediente>

    //listar ingredientes por tipo e somente inativos
    fun findByTipoContainsIgnoreCaseAndAtivoFalse(tipo: String): List<Ingrediente>


}