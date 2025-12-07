package sc.projetosc.repository

import sc.projetosc.entity.ItemPedido
import org.springframework.data.jpa.repository.JpaRepository

interface ItemPedidoRepository : JpaRepository<ItemPedido, Int> {
    // Aqui você pode adicionar métodos específicos para ItemPedido, se necessário
    // Por exemplo, buscar itens por pedido, etc.
    fun findByPedidoId(pedidoId: Int): List<ItemPedido>

}