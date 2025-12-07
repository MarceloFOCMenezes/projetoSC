package sc.projetosc.repository

import sc.projetosc.entity.StatusPedido
import org.springframework.data.jpa.repository.JpaRepository

interface StatusPedidoRepository : JpaRepository<StatusPedido, Int> {
    fun findByDescricaoIgnoreCase(descricao: String): StatusPedido?
    fun existsByDescricaoIgnoreCase(descricao: String): Boolean
}