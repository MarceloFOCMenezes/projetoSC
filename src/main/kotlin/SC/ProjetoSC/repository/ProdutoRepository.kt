package SC.ProjetoSC.repository

import SC.ProjetoSC.entity.Produto
import org.springframework.data.jpa.repository.JpaRepository

interface ProdutoRepository : JpaRepository<Produto, Int> {
    // Aqui você pode adicionar métodos específicos para Produto, se necessário
    // Por exemplo, buscar produtos por nome, categoria, etc.
}