package SC.ProjetoSC.repository

import SC.ProjetoSC.entity.ItemPedidoIngrediente
import SC.ProjetoSC.entity.ItemPedidoIngredienteId
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ItemPedidoIngredienteRepository : JpaRepository<ItemPedidoIngrediente, Int> {
    // Aqui você pode adicionar métodos específicos para ItemPedidoIngrediente, se necessário
     fun findById(id: ItemPedidoIngredienteId): Optional<ItemPedidoIngrediente>
}