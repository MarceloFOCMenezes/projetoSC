package SC.ProjetoSC.Services

import SC.ProjetoSC.dto.RequestProdutoDTO
import SC.ProjetoSC.entity.Produto
import SC.ProjetoSC.repository.ProdutoRepository
import SC.ProjetoSC.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class ProdutoServices (
    private val produtoRepository: ProdutoRepository,
    private val usuarioRepository: UsuarioRepository
) {
    fun criarProduto(requestProduto:RequestProdutoDTO): Produto {
        val produto = Produto(
            descricao = requestProduto.descricao,
            precoUnitario = requestProduto.precoUnitario,
            categoria = requestProduto.categoria,
            ativo = requestProduto.ativo,
            temIngrediente = requestProduto.temIngrediente,
            observacao = requestProduto.observacao,
            unidadeMedida = requestProduto.unidadeMedida
        )
        produtoRepository.save(produto)
        return produto
    }

}