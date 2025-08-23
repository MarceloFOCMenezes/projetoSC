package sc.projetosc.repository

import sc.projetosc.entity.Ingrediente
import org.springframework.data.jpa.repository.JpaRepository

interface IngredienteRepository:JpaRepository<Ingrediente, Int> {
    fun findByTipoIngrediente_DescricaoContainsIgnoreCase(descricao: String): List<Ingrediente>
    fun findByTipoIngrediente_DescricaoContainsIgnoreCaseAndAtivoTrue(descricao: String): List<Ingrediente>
    fun findByTipoIngrediente_DescricaoContainsIgnoreCaseAndAtivoFalse (descricao: String): List<Ingrediente>
    fun findByAtivoTrue(): List<Ingrediente>
    fun findByAtivoFalse(): List<Ingrediente>
    fun findByNomeContainsIgnoreCaseAndAtivoFalse(nome: String): List<Ingrediente>
    fun findByNomeContainsIgnoreCaseAndAtivoTrue (nome: String): List<Ingrediente>
    fun findByNomeContainsIgnoreCase (nome: String): List<Ingrediente>

}