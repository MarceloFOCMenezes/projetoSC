package SC.ProjetoSC.repository

import SC.ProjetoSC.entity.TipoIngrediente
import org.springframework.data.jpa.repository.JpaRepository

interface TipoIngredienteRepository:JpaRepository<TipoIngrediente, Int>  {
}