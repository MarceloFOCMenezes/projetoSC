package sc.projetosc.repository

import sc.projetosc.entity.TipoIngrediente
import org.springframework.data.jpa.repository.JpaRepository

interface TipoIngredienteRepository:JpaRepository<TipoIngrediente, Int>  {
}