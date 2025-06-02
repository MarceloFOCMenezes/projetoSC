package SC.ProjetoSC.repository

import SC.ProjetoSC.entity.Ingrediente
import org.springframework.data.jpa.repository.JpaRepository

interface IngredienteRepository:JpaRepository<Ingrediente, Int> {
    // Define any additional query methods if needed
    // For example, you can find ingredients by name or type
    // fun findByName(name: String): List<IngredienteRepository>
}