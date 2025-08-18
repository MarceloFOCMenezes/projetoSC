package SC.ProjetoSC.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

@Tag(name = "Anexos", description = "Operações relacionadas aos anexos do sistema")
@RestController
@RequestMapping("/anexos")
class AnexoController {

    private val logger = LoggerFactory.getLogger(AnexoController::class.java)
    private val uploadDir = Path.of("uploads")

    init {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }
    }

    @PostMapping
    @Operation(summary = "Fazer upload de anexo", description = "Realiza o upload de um arquivo para o sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Anexo criado com sucesso."),
        ApiResponse(responseCode = "400", description = "Arquivo vazio ou nome inválido."),
        ApiResponse(responseCode = "500", description = "Erro interno ao processar o upload.")
    ])
    fun uploadAnexo(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        return try {

            if (file.isEmpty) {
                return ResponseEntity.badRequest().body("O arquivo está vazio.")
            }

            val fileName = file.originalFilename?.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
                ?: return ResponseEntity.badRequest().body("Nome do arquivo inválido.")

            val filePath = uploadDir.resolve(fileName)
            Files.copy(file.inputStream, filePath, StandardCopyOption.REPLACE_EXISTING)

            logger.info("Arquivo '${fileName}' salvo com sucesso em '${filePath}'.")
            ResponseEntity.status(201).body("Anexo '${fileName}' criado com sucesso.")

        } catch (e: Exception) {

            logger.error("Erro ao fazer upload do anexo: ${e.message}", e)
            ResponseEntity.status(500).body("Erro ao fazer upload do anexo: ${e.message}")

        }
    }

    @DeleteMapping
    @Operation(summary = "Remover anexo", description = "Remove um arquivo do sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Anexo removido com sucesso."),
        ApiResponse(responseCode = "404", description = "Anexo não encontrado."),
        ApiResponse(responseCode = "500", description = "Erro interno ao remover o anexo.")
    ])
    fun removerAnexo(@RequestParam("fileName") fileName: String): ResponseEntity<String> {
        return try {

            val filePath = uploadDir.resolve(fileName)

            if (!Files.exists(filePath)) {
                return ResponseEntity.status(404).body("Anexo '${fileName}' não encontrado.")
            }
            Files.delete(filePath)

            logger.info("Arquivo '${fileName}' removido com sucesso.")
            ResponseEntity.status(204).body("Anexo '${fileName}' removido com sucesso.")

        } catch (e: Exception) {
            logger.error("Erro ao remover o anexo: ${e.message}", e)
            ResponseEntity.status(500).body("Erro ao remover o anexo: ${e.message}")
        }
    }
}
