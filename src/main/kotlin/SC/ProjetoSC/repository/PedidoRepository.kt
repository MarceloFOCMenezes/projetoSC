package sc.projetosc.repository

import sc.projetosc.Response.PedidoSemanaResponse
import sc.projetosc.Response.PedidoResponse
import sc.projetosc.entity.Pedido
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.time.LocalDateTime

interface PedidoRepository : JpaRepository<Pedido, Int> {


    // buscar somente por data de pedido
    @Query("SELECT * FROM Pedido p WHERE DATE(p.dt_entrega_esperada) = :dtPedido AND p.fk_status_pedido = 5",
        nativeQuery = true)
    fun findByDtPedido(dtPedido: String):List<Pedido>

    //buscar somente por data de entrega
    fun findByDtEntregaGreaterThanEqual(dtEntrega:LocalDateTime):List<Pedido>

    // essa função filtra os pedidos por dtPedido e por dtEntrega
    fun findByDtPedidoAndDtEntregaGreaterThanEqual(dtPedido:LocalDateTime, dtEntrega:LocalDateTime):List<Pedido>

    fun findByDtEntrega(dtEntrega: LocalDateTime): List<Pedido>
    fun findByClienteId(clienteId: Int): List<Pedido>
    fun findByClienteIdAndStatusPedidoIdStatusPedidoOrderByDtPedidoDesc(clienteId: Int, statusPedidoId: Int): List<Pedido>
    fun findByStatusPedidoIdStatusPedido(statusPedidoId: Int): List<Pedido>

    fun findAllByDtEntregaEsperadaBetween(inicio: LocalDateTime, fim: LocalDateTime): List<Pedido>

    fun findPedidoByDtEntregaEsperada(dtEntrega: LocalDateTime): List<Pedido>

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
    WITH RECURSIVE semana AS (
    SELECT DATE_SUB(
               STR_TO_DATE(:data, '%Y-%m-%d'),
               INTERVAL WEEKDAY(STR_TO_DATE(:data, '%Y-%m-%d')) DAY
           ) AS data
    UNION ALL
    SELECT DATE_ADD(data, INTERVAL 1 DAY)
    FROM semana
    WHERE data < DATE_ADD(
                    DATE_SUB(
                        STR_TO_DATE(:data, '%Y-%m-%d'),
                        INTERVAL WEEKDAY(STR_TO_DATE(:data, '%Y-%m-%d')) DAY
                    ),
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
ORDER BY sm.data;

""", nativeQuery = true)
    fun getPedidosSemanaData(data:String): List<PedidoSemanaResponse>


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

    // Dashboard queries
    @Query("SELECT COUNT(*) FROM Pedido p WHERE DATE(p.dt_entrega_esperada) = CURDATE()", nativeQuery = true)
    fun countPedidosHoje(): Int

    @Query("SELECT COUNT(*) FROM Pedido p WHERE p.fk_status_pedido = 2", nativeQuery = true)
    fun countPedidosPendentes(): Int

    @Query("SELECT COUNT(*) FROM Pedido p WHERE p.fk_status_pedido = 3", nativeQuery = true)
    fun countPedidosProduzindo(): Int

    @Query("SELECT COUNT(*) FROM Pedido p WHERE p.fk_status_pedido = 4 AND DATE(p.dt_entrega_esperada) = CURDATE()", nativeQuery = true)
    fun countPedidosConcluidos(): Int

    @Query("SELECT COUNT(*) FROM Pedido p WHERE MONTH(p.dt_entrega_esperada) = MONTH(CURDATE()) AND YEAR(p.dt_entrega_esperada) = YEAR(CURDATE())", nativeQuery = true)
    fun countPedidosMes(): Int

    @Query("SELECT COUNT(*) FROM Pedido p WHERE DATE(p.dt_entrega_esperada) BETWEEN :dataInicio AND :dataFim", nativeQuery = true)
    fun countPedidosPeriodo(dataInicio: String, dataFim: String): Int

    @Query("SELECT COALESCE(SUM(p.preco_total), 0) FROM Pedido p WHERE MONTH(p.dt_entrega_esperada) = MONTH(CURDATE()) AND YEAR(p.dt_entrega_esperada) = YEAR(CURDATE()) AND p.fk_status_pedido IN (4, 5)", nativeQuery = true)
    fun sumReceitaMes(): Double

    @Query("SELECT COALESCE(SUM(p.preco_total), 0) FROM Pedido p WHERE DATE(p.dt_entrega_esperada) BETWEEN :dataInicio AND :dataFim AND p.fk_status_pedido IN (4, 5)", nativeQuery = true)
    fun sumReceitaPeriodo(dataInicio: String, dataFim: String): Double

    @Query("SELECT COALESCE(AVG(p.preco_total), 0) FROM Pedido p WHERE MONTH(p.dt_entrega_esperada) = MONTH(CURDATE()) AND YEAR(p.dt_entrega_esperada) = YEAR(CURDATE())", nativeQuery = true)
    fun avgTicketMedio(): Double

    @Query("SELECT COALESCE(AVG(p.preco_total), 0) FROM Pedido p WHERE DATE(p.dt_entrega_esperada) BETWEEN :dataInicio AND :dataFim", nativeQuery = true)
    fun avgTicketMedioPeriodo(dataInicio: String, dataFim: String): Double

    @Query("""
        SELECT 
            (COUNT(CASE WHEN p.fk_status_pedido IN (4, 5) THEN 1 END) * 100.0) / COUNT(*)
        FROM Pedido p 
        WHERE MONTH(p.dt_entrega_esperada) = MONTH(CURDATE()) 
        AND YEAR(p.dt_entrega_esperada) = YEAR(CURDATE())
        AND p.fk_status_pedido > 1
    """, nativeQuery = true)
    fun calcTaxaConclusao(): Double

    @Query("""
        SELECT 
            (COUNT(CASE WHEN p.fk_status_pedido IN (4, 5) THEN 1 END) * 100.0) / COUNT(*)
        FROM Pedido p 
        WHERE DATE(p.dt_entrega_esperada) BETWEEN :dataInicio AND :dataFim
        AND p.fk_status_pedido > 1
    """, nativeQuery = true)
    fun calcTaxaConclusaoPeriodo(dataInicio: String, dataFim: String): Double

    @Query("""
        SELECT DATE(p.dt_entrega_esperada) as data, COUNT(*) as quantidade
        FROM Pedido p 
        WHERE DATE(p.dt_entrega_esperada) BETWEEN :dataInicio AND :dataFim
        GROUP BY DATE(p.dt_entrega_esperada)
        ORDER BY data
    """, nativeQuery = true)
    fun getPedidosPorPeriodo(dataInicio: String, dataFim: String): List<Array<Any>>

    @Query("""
        SELECT 
            u.id_usuario as id, 
            u.nome_usuario as nome, 
            u.email_usuario as email, 
            COUNT(p.id_pedido) as quantidade_pedidos,
            COALESCE(SUM(p.preco_total), 0) as valor_total
        FROM usuario u
        INNER JOIN pedido p ON u.id_usuario = p.fk_cliente
        WHERE DATE(p.dt_entrega_esperada) BETWEEN :dataInicio AND :dataFim
        AND p.fk_status_pedido IN (4, 5)
        GROUP BY u.id_usuario, u.nome_usuario, u.email_usuario
        ORDER BY valor_total DESC, quantidade_pedidos DESC
        LIMIT 10
    """, nativeQuery = true)
    fun getTopClientesPorPeriodo(dataInicio: String, dataFim: String): List<Array<Any>>

}