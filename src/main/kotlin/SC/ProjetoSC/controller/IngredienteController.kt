package sc.projetosc.controller


import sc.projetosc.services.IngredienteServices
import sc.projetosc.request.IngredienteRequest
import sc.projetosc.entity.Ingrediente
import sc.projetosc.repository.IngredienteRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Ingredientes", description = "Operações relacionadas aos ingredientes do sistema")
@RestController
@RequestMapping("/ingredientes")
class IngredienteController(
    val repositorio: IngredienteRepository,
    val ingredienteServices: IngredienteServices
) {
    @PostMapping
    @Operation(summary = "Criar um novo ingrediente", description = "Cria um novo ingrediente no sistema com as informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "Ingrediente criado com sucesso. O corpo da resposta contém os dados do ingrediente criado.")
    fun criarIngrediente(@RequestBody novoIngrediente: IngredienteRequest): ResponseEntity<Any> {
        val ingrediente = ingredienteServices.criarIngrediente(novoIngrediente)
        return ResponseEntity.status(201).body(ingrediente)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um ingrediente existente", description = "Atualiza as informações de um ingrediente existente no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Ingrediente atualizado com sucesso. O corpo da resposta contém os dados do ingrediente atualizado"),
        ApiResponse(responseCode = "404", description = "Ingrediente não encontrado. O corpo da resposta estará vazio.")
    ])
    fun atualizarIngrediente(@PathVariable id: Int, @RequestBody @Valid ingredienteAtualizado: IngredienteRequest): ResponseEntity<Any> {
        return ingredienteServices.atualizarIngrediente(id, ingredienteAtualizado)
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Desativar ou Reativar um ingrediente", description = "Atualiza o status de ativo de um ingrediente existente no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Status do ingrediente atualizado com sucesso. O corpo da resposta contém os dados do ingrediente atualizado"),
        ApiResponse(responseCode = "404", description = "Ingrediente não encontrado. O corpo da resposta estará vazio.")
    ])
    fun atualizarStatusIngrediente(@PathVariable id: Int): ResponseEntity<Any> {
        return ingredienteServices.atualizarIngrediente(id, IngredienteRequest())
    }

    @GetMapping
    @Operation(summary = "Listar todos os ingredientes", description = "Retorna uma lista com todos os ingredientes registrados no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de ingredientes retornada com sucesso."),
        ApiResponse(responseCode = "204", description = "Nenhum ingrediente encontrado.")
    ])
    fun listarIngredientes(
        @RequestParam(required = false) descricao: String?,
        @RequestParam(required = false) ativos: Boolean?
    ): ResponseEntity<List<Ingrediente>> {
        val ingrediente = ingredienteServices.listarIngredientes(descricao, ativos)
        return if (ingrediente.isEmpty()) {
            ResponseEntity.status(204).build()
        } else {
            ResponseEntity.status(200).body(ingrediente)
        }
    }


    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar ingredientes por tipo", description = "Retorna uma lista de ingredientes filtrados pelo tipo especificado.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de ingredientes por tipo retornada com sucesso."),
        ApiResponse(responseCode = "204", description = "Nenhum ingrediente encontrado para o tipo especificado.")
    ])
    fun listarIngredientesPorTipo(
        @PathVariable tipo: String,
        @RequestParam(required = false) ativos: Boolean?
    ): ResponseEntity<List<Ingrediente>> {
        val ingredientes = ingredienteServices.listarIngredientesPorTipo(tipo, ativos)
        return if (ingredientes.isEmpty()) {
            ResponseEntity.status(204).build()
        } else {
            ResponseEntity.status(200).body(ingredientes)
        }
    }

}