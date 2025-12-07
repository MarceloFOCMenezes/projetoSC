package sc.projetosc.services

import sc.projetosc.entity.ItemPedidoIngrediente
import sc.projetosc.entity.ItemPedidoIngredienteId
import sc.projetosc.repository.IngredienteRepository
import sc.projetosc.repository.ItemPedidoIngredienteRepository
import sc.projetosc.repository.ItemPedidoRepository

class ItemPedidoIngredienteService(
    private val itemPedidoRepository: ItemPedidoRepository,
    private val itemPedidoIngredienteRepository: ItemPedidoIngredienteRepository,
    private val ingredienteRepository: IngredienteRepository
) {

    fun insereItemPedidoIngrediente(idItemPedido:Int, idIngrediente: Int): ItemPedidoIngrediente {
        val itemPedidoIngredienteId = ItemPedidoIngredienteId(
            itemPedidoId = idItemPedido,
            ingredienteId = idIngrediente
        )

        val itemPedido = itemPedidoRepository.findById(idItemPedido).orElseThrow {
            RuntimeException("ItemPedido not found with id: $idItemPedido")
        }

        val ingrediente = ingredienteRepository.findById(idIngrediente).orElseThrow {
            RuntimeException("Ingrediente not found with id: $idIngrediente")
        }
        val itemPedidoIngrediente = ItemPedidoIngrediente(
            id = itemPedidoIngredienteId,
            itemPedido = itemPedido!!,
            ingrediente = ingrediente
        )

        return itemPedidoIngredienteRepository.save(itemPedidoIngrediente)
    }
}