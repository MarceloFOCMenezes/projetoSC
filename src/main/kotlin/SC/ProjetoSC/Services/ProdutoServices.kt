package sc.projetosc.services

import sc.projetosc.dto.RequestProdutoDTO
import sc.projetosc.entity.Produto
import sc.projetosc.repository.ProdutoRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ProdutoServices (
    private val produtoRepository: ProdutoRepository
) {
    fun criarProduto(requestProduto: RequestProdutoDTO): Produto {
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

    fun atualizarStatusProduto(id:Int): ResponseEntity<Any> {
        val produtoOpt = produtoRepository.findById(id)
        if (produtoOpt.isEmpty) return ResponseEntity.status(404).build()

        val produto = produtoOpt.get()
        val novoStatus = !(produto.ativo ?: false)
        val produtoAtualizado = produto.copy(ativo = novoStatus)
        produtoRepository.save(produtoAtualizado)
        return ResponseEntity.status(200).body(produtoAtualizado)
    }

    fun listarProdutos(descricao:String?, ativos:Boolean?): List<Produto> {
        val filtroAtivos = ativos ?: true // por padrão, vai exibir somente produtos ativos

        return when {
            // Buscar apenas inativos e por nome
            ativos == false && !descricao.isNullOrBlank() ->
                produtoRepository.findByDescricaoContainsIgnoreCaseAndAtivoFalse(descricao)

            // Buscar apenas inativos, sem nome
            ativos == false ->
                produtoRepository.findByAtivoFalse()

            // Buscar apenas ativos e por nome
            ativos == true && !descricao.isNullOrBlank() ->
                produtoRepository.findByDescricaoContainsIgnoreCaseAndAtivoTrue(descricao)

                // Buscar apenas ativos, sem nome
            ativos == true ->
                produtoRepository.findByAtivoTrue()

            // Buscar todos (ativos e inativos) por nome
            !descricao.isNullOrBlank() ->
                produtoRepository.findByDescricaoContainsIgnoreCase(descricao)

            // Buscar todos (ativos e inativos), sem nome
            else -> produtoRepository.findAll()
        }
    }
}