package sc.projetosc.controller

import sc.projetosc.entity.Anexo
import sc.projetosc.services.AnexoServices
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Anexos", description = "Operações relacionadas aos anexos do sistema")
@RestController
@RequestMapping("/anexos")
class AnexoController(
    private val anexoService: AnexoServices
) {

    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Fazer upload de anexo", description = "Realiza o upload de um arquivo e o armazena no banco de dados")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Anexo criado com sucesso"),
        ApiResponse(responseCode = "400", description = "Arquivo vazio ou inválido"),
        ApiResponse(responseCode = "500", description = "Erro interno ao processar o upload")
    ])
    fun upload(@RequestParam("file") file: MultipartFile): ResponseEntity<Int> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().build()
        }
        val anexo = anexoService.salvar(file)
        return ResponseEntity.status(201).body(anexo.idAnexo)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Baixar anexo", description = "Recupera um anexo armazenado no banco de dados pelo seu ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Anexo encontrado e retornado com sucesso"),
        ApiResponse(responseCode = "404", description = "Anexo não encontrado"),
        ApiResponse(responseCode = "500", description = "Erro interno ao buscar o anexo")
    ])
    fun download(@PathVariable id: Int): ResponseEntity<ByteArray> {
        val anexo = anexoService.buscar(id)
        //val nomeArquivo = anexo.nomeArquivo ?: "anexo_${id}.bin"
        val contentType = when {
            anexo.nomeArquivo?.endsWith(".png", true) == true -> MediaType.IMAGE_PNG_VALUE
            anexo.nomeArquivo?.endsWith(".jpg", true) == true -> MediaType.IMAGE_JPEG_VALUE
            anexo.nomeArquivo?.endsWith(".jpeg", true) == true -> MediaType.IMAGE_JPEG_VALUE
            anexo.nomeArquivo?.endsWith(".gif", true) == true -> MediaType.IMAGE_GIF_VALUE
            anexo.nomeArquivo?.endsWith(".pdf", true) == true -> MediaType.APPLICATION_PDF_VALUE
            else -> {
                MediaType.APPLICATION_OCTET_STREAM_VALUE
                return ResponseEntity.badRequest().build()
            }

        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(anexo.imagemAnexo)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover anexo", description = "Remove um anexo do banco de dados pelo seu ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Anexo removido com sucesso"),
        ApiResponse(responseCode = "404", description = "Anexo não encontrado"),
        ApiResponse(responseCode = "500", description = "Erro interno ao remover o anexo")
    ])
    fun remover(@PathVariable id: Int): ResponseEntity<Void> {
        anexoService.deletar(id)
        return ResponseEntity.noContent().build()
    }
}
