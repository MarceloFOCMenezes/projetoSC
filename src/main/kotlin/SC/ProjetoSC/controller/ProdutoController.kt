package sc.projetosc.controller


import sc.projetosc.services.ProdutoServices
import sc.projetosc.request.ProdutoRequest
import sc.projetosc.entity.Produto
import sc.projetosc.repository.ProdutoRepository

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Produtos", description = "Operações relacionadas a produtos do sistema")
@RestController
@RequestMapping("/produtos")
class ProdutoController(
    val repositorio: ProdutoRepository,
    val produtoServices: ProdutoServices
) {

    @PostMapping
    @Operation(summary = "Criar um novo produto", description = "Cria um novo produto no sistema com as informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso. O corpo da resposta contém os dados do produto criado.")
    fun criarProduto(@RequestBody @Valid novoProduto: ProdutoRequest): ResponseEntity<Any> {
        val produto = produtoServices.criarProduto(novoProduto)
        return ResponseEntity.status(201).body(produto)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um produto existente", description = "Atualiza as informações de um produto existente no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso. O corpo da resposta contém os dados do produto atualizado"),
        ApiResponse(responseCode = "404", description = "Produto não encontrado. O corpo da resposta estará vazio.")
    ])
    fun atualizarProduto(@PathVariable id:Int, @RequestBody @Valid produtoAtualizado: ProdutoRequest): ResponseEntity<Any> {
        return produtoServices.atualizarProduto(id, produtoAtualizado)
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Desativar ou Reativar um produto", description = "Atualiza o status de ativo de um produto existente no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Status do produto atualizado com sucesso. O corpo da resposta contém os dados do produto atualizado"),
        ApiResponse(responseCode = "404", description = "Produto não encontrado. O corpo da resposta estará vazio.")
    ])
    fun atualizarStatusProduto(@PathVariable id:Int): ResponseEntity<Any> {
        return produtoServices.atualizarStatusProduto(id)
    }

    @GetMapping("getEssenciais")
    @Operation(summary = "Listar produtos essenciais", description = "Retorna uma lista com todos os produtos essenciais registrados no sistema.")
    fun listarProdutosEssenciais(): ResponseEntity<List<Produto>> {
        val produtos = produtoServices.listarProdutosEssenciais()
        return if (produtos.isEmpty()) {
            ResponseEntity.status(204).build()
        } else {
            ResponseEntity.status(200).body(produtos)
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista com todos os produtos registrados no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso."),
        ApiResponse(responseCode = "204", description = "Nenhum produto encontrado.")
    ])
    fun listarProdutos(
        @RequestParam(required = false)descricao:String?,
        @RequestParam(required = false)ativos:Boolean?
    ): ResponseEntity<List<Produto>> {
        val produtos = produtoServices.listarProdutos(descricao,ativos)
        return if (produtos.isEmpty()) {
            ResponseEntity.status(204).build()
        } else {
            ResponseEntity.status(200).body(produtos)
        }
    }
}