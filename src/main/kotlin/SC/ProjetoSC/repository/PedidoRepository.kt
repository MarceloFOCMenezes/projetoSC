package sc.projetosc.repository

import sc.projetosc.Response.PedidoSemanaResponse
import sc.projetosc.Response.PedidoResponse
import sc.projetosc.entity.Pedido
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
    fun findByStatusPedidoIdStatusPedido(statusPedidoId: Int): List<Pedido>

    fun findAllByDtEntregaEsperadaBetween(inicio: LocalDateTime, fim: LocalDateTime): List<Pedido>

    @Query("SELECT dt_entrega_esperada FROM (SELECT dt_entrega_esperada, COUNT(*) quantidade FROM pedido where fk_status_pedido = 4 GROUP BY dt_entrega_esperada) as groupDate WHERE groupDate.quantidade >5;", nativeQuery = true)
    fun findDiasLotados(): List<String>



    @Query("""
    WITH RECURSIVE semana AS (
        SELECT DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) AS data
        UNION ALL
        SELECT DATE_ADD(data, INTERVAL 1 DAY)
        FROM semana
        WHERE data < DATE_ADD(
            DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY),
            INTERVAL 6 DAY
        )
    )
    SELECT 
        DATE_FORMAT(sm.data, '%Y-%m-%d') AS data,
        COUNT(pd.id_Pedido) AS quantidade
    FROM semana sm
    LEFT JOIN PEDIDO pd
        ON DATE(pd.dt_entrega_esperada) = sm.data
        AND pd.fk_status_pedido = 5
    GROUP BY sm.data
    ORDER BY sm.data
""", nativeQuery = true)
    fun getPedidosSemana(): List<PedidoSemanaResponse>


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