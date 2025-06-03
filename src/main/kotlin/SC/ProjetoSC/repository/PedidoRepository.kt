package SC.ProjetoSC.repository

import SC.ProjetoSC.Response.PedidoResponse
import SC.ProjetoSC.entity.Pedido
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface PedidoRepository : JpaRepository<Pedido, Int> {


    // buscar somente por data de pedido
    fun findByDtPedido(dtPedido:LocalDateTime):List<Pedido>

    //buscar somente por data de entrega
    fun findByDtEntregaGreaterThanEqual(dtEntrega:LocalDateTime):List<Pedido>

    // essa função filtra os pedidos por dtPedido e por dtEntrega
    fun findByDtPedidoAndDtEntregaGreaterThanEqual(dtPedido:LocalDateTime, dtEntrega:LocalDateTime):List<Pedido>

    fun findByDtEntrega(dtEntrega: LocalDateTime): List<Pedido>

    fun findByClienteId(clienteId: Int): List<Pedido>
    fun findByClienteIdAndStatusPedidoIdStatusPedidoOrderByDtPedidoDesc(clienteId: Int, statusPedidoId: Int): List<Pedido>

    @Query("""
        UPDATE Pedido p
        SET p.statusPedido.idStatusPedido = 2,
        p.dt_entrega_esperada = ?2,
        p.is_retirada = ?3
        p.forma_pagamento =?4
        WHERE p.id_pedido = ?1
    """, nativeQuery = true)
    fun enviarPedido(
        pedidoResponse:PedidoResponse
    ): Int

}