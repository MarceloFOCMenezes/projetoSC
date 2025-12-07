package sc.projetosc.controller



import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sc.projetosc.services.EnderecoServices
import sc.projetosc.entity.Endereco
import sc.projetosc.request.EnderecoRequest

@Tag(name = "Endereço", description = "Operações relacionadas a endereços do sistema")
@RestController
@RequestMapping("/enderecos")
class EnderecoController(
    private val enderecoServices: EnderecoServices
) {
    @PostMapping
    @Operation(summary = "Criar um novo endereço", description = "Cria um novo endereço no sistema com as informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso. O corpo da resposta contém os dados do endereço criado.")
    fun criarEndereco(@RequestBody dto: EnderecoRequest): ResponseEntity<Endereco> {
        val endereco = enderecoServices.criarEndereco(dto)
        return ResponseEntity.status(201).body(endereco)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um endereço existente", description = "Atualiza as informações de um endereço existente no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso. O corpo da resposta contém os dados do endereço atualizado"),
        ApiResponse(responseCode = "404", description = "Endereço não encontrado. O corpo da resposta estará vazio.")
    ])
    fun atualizarEndereco(@PathVariable id: Int, @RequestBody dto: EnderecoRequest): ResponseEntity<Any> {
        return enderecoServices.atualizarEndereco(id, dto)
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar um endereço", description = "Desativa um endereço existente no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Endereço desativado com sucesso. O corpo da resposta contém os status do endereço atualizado"),
        ApiResponse(responseCode = "404", description = "Endereço não encontrado. O corpo da resposta estará vazio.")
    ])
    fun desativarEndereco(@PathVariable id: Int): ResponseEntity<Any> =
        enderecoServices.desativarEndereco(id)

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar endereços por usuário", description = "Retorna todos os endereços cadastrados para um usuário específico.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de endereços por usuário retornada com sucesso."),
        ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    ])
    fun listarEnderecosPorUsuario(@PathVariable usuarioId: Int): List<Endereco> =
        enderecoServices.listarEnderecosPorUsuario(usuarioId)
}