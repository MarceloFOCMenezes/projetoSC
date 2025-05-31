package SC.ProjetoSC.Services

import SC.ProjetoSC.dto.RequestProdutoDTO
import SC.ProjetoSC.entity.Produto
import SC.ProjetoSC.repository.ProdutoRepository
import SC.ProjetoSC.repository.UsuarioRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ProdutoServices (
    private val produtoRepository: ProdutoRepository
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

    fun atualizarProduto(id:Int, dto: RequestProdutoDTO): ResponseEntity<Any> {
        val produtoOpt = produtoRepository.findById(id)
        if (produtoOpt.isEmpty) return ResponseEntity.status(404).build()

        val produtoExist = produtoOpt.get()
        val produtoAtt = produtoExist.copy(
            descricao = dto.descricao,
            precoUnitario = dto.precoUnitario,
            categoria = dto.categoria,
            ativo = dto.ativo,
            temIngrediente = dto.temIngrediente,
            observacao = dto.observacao,
            unidadeMedida = dto.unidadeMedida
        )
        produtoRepository.save(produtoAtt)
        return ResponseEntity.status(200).body(produtoAtt)
    }

}