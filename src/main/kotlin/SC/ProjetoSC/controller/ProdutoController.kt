package SC.ProjetoSC.controller

import SC.ProjetoSC.Services.ProdutoServices
import SC.ProjetoSC.dto.RequestProdutoDTO
import SC.ProjetoSC.repository.ProdutoRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    fun criarProduto(@RequestBody @Valid novoProduto: RequestProdutoDTO): ResponseEntity<Any> {
        val produto = produtoServices.criarProduto(novoProduto)
        return ResponseEntity.status(201).body(produto)
    }

}