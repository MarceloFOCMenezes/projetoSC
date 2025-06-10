package SC.ProjetoSC.repository

import SC.ProjetoSC.entity.ItemPedidoIngrediente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ItemPedidoIngredienteRepository : JpaRepository<ItemPedidoIngrediente, Int> {
    // Aqui você pode adicionar métodos específicos para ItemPedidoIngrediente, se necessário
     @Query( value = """
         SELECT i.nome, (CASE WHEN i.is_premium = 1 THEN true ELSE false END), ti.descricao 
        FROM item_pedido_ingrediente ipi
        JOIN item_pedido ip ON ipi.fk_item_pedido = ip.id_item_pedido
        JOIN ingrediente i ON ipi.fk_ingrediente = i.id_ingrediente
        JOIN tipo_ingrediente ti ON i.fk_tipo_ingrediente = ti.id_tipo_ingrediente
        WHERE ipi.fk_item_pedido = ?1
     """,
     nativeQuery = true)
     fun findIngredienteInItemPedido(
        idItemPedido: Int
    ): List<Array<Any>>
}