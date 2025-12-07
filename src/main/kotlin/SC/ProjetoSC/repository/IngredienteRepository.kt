package sc.projetosc.repository

import sc.projetosc.entity.Ingrediente
import sc.projetosc.dto.IngredienteRankingDTO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface IngredienteRepository:JpaRepository<Ingrediente, Int> {
    fun findByTipoIngrediente_DescricaoContainsIgnoreCase(descricao: String): List<Ingrediente>
    fun findByTipoIngrediente_DescricaoContainsIgnoreCaseAndAtivoTrue(descricao: String): List<Ingrediente>
    fun findByTipoIngrediente_DescricaoContainsIgnoreCaseAndAtivoFalse (descricao: String): List<Ingrediente>
    fun findByAtivoTrue(): List<Ingrediente>
    fun findByAtivoFalse(): List<Ingrediente>
    fun findByNomeContainsIgnoreCaseAndAtivoFalse(nome: String): List<Ingrediente>
    fun findByNomeContainsIgnoreCaseAndAtivoTrue (nome: String): List<Ingrediente>
    fun findByNomeContainsIgnoreCase (nome: String): List<Ingrediente>

    @Query("""
        SELECT i.id_ingrediente as id,
               i.nome as nome,
               i.premium as isPremium
               COUNT(DISTINCT p.id_pedido) as quantidadePedidos
        FROM ingrediente i
        JOIN item_pedido_ingrediente ipi ON i.id_ingrediente = ipi.fk_ingrediente
        JOIN item_pedido ip ON ipi.fk_item_pedido = ip.id_item_pedido
        JOIN pedido p ON ip.fk_pedido = p.id_pedido
        WHERE p.fk_status_pedido IN (3, 4, 5)
        GROUP BY i.id_ingrediente, i.nome
        ORDER BY quantidadePedidos DESC
        LIMIT 5
    """, nativeQuery = true)
    fun findTop5IngredientesMaisUsados(): List<Array<Any>>

    @Query("""
        SELECT 
            i.id_ingrediente as id,
            i.nome as nome,
            ti.descricao as tipoIngrediente,
            i.is_premium as premium,
            COUNT(DISTINCT p.id_pedido) as quantidadePedidos,
            ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT p.id_pedido) DESC) as posicao
        FROM ingrediente i
        JOIN tipo_ingrediente ti ON i.fk_tipo_ingrediente = ti.id_tipo_ingrediente
        LEFT JOIN item_pedido_ingrediente ipi ON i.id_ingrediente = ipi.fk_ingrediente
        LEFT JOIN item_pedido ip ON ipi.fk_item_pedido = ip.id_item_pedido
        LEFT JOIN pedido p ON ip.fk_pedido = p.id_pedido AND p.fk_status_pedido IN (3, 4, 5)
        WHERE ti.descricao = :tipoIngrediente
        GROUP BY i.id_ingrediente, i.nome, ti.descricao
        ORDER BY quantidadePedidos DESC
        LIMIT 5
    """, nativeQuery = true)
    fun findTop5IngredientesPorTipo(tipoIngrediente: String): List<Array<Any>>

    @Query("""
        SELECT 
            i.id_ingrediente as id,
            i.nome as nome,
            ti.descricao as tipoIngrediente,
            i.premium as premium,
            0 as quantidadePedidos
        FROM ingrediente i
        JOIN tipo_ingrediente ti ON i.fk_tipo_ingrediente = ti.id_tipo_ingrediente
        WHERE ti.descricao = :tipoIngrediente
        ORDER BY i.nome ASC
        LIMIT 5
    """, nativeQuery = true)
    fun findIngredientesPorTipoSemPedidos(tipoIngrediente: String): List<Array<Any>>


    @Query("""
    SELECT 
        i.id_ingrediente as id,
        i.nome as nome,
        ti.descricao as tipoIngrediente,
        i.is_premium as premium,
        COUNT(DISTINCT p.id_pedido) as quantidadePedidos,
        ROW_NUMBER() OVER (ORDER BY COUNT(DISTINCT p.id_pedido) DESC) as posicao
    FROM ingrediente i
    JOIN tipo_ingrediente ti ON i.fk_tipo_ingrediente = ti.id_tipo_ingrediente
    LEFT JOIN item_pedido_ingrediente ipi ON i.id_ingrediente = ipi.fk_ingrediente
    LEFT JOIN item_pedido ip ON ipi.fk_item_pedido = ip.id_item_pedido
    LEFT JOIN pedido p ON ip.fk_pedido = p.id_pedido 
        AND p.fk_status_pedido = 7
        AND DATE(p.dt_pedido) BETWEEN :dataInicio AND :dataFim
    WHERE ti.descricao = :tipoIngrediente
    GROUP BY i.id_ingrediente, i.nome, ti.descricao, i.is_premium
    ORDER BY quantidadePedidos DESC
    LIMIT 5
""", nativeQuery = true)
    fun findTop5IngredientesPorTipoComPeriodo(
        tipoIngrediente: String,
        dataInicio: String,
        dataFim: String
    ): List<Array<Any>>
}

