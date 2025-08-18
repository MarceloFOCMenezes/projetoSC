package SC.ProjetoSC.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Files
import java.nio.file.Path

class AnexoControllerTest {

    private lateinit var controller: AnexoController
    private lateinit var uploadDir: Path

    @BeforeEach
    fun setup() {
        controller = AnexoController()
        uploadDir = Path.of("uploads")
    }

    @Test
    @DisplayName("Upload: arquivo válido = status 201 com mensagem de sucesso")
    fun uploadAnexoValido() {
        val mockFile = MockMultipartFile(
            "file",
            "teste.txt",
            "text/plain",
            "conteúdo teste".toByteArray()
        )

        val resposta = controller.uploadAnexo(mockFile)

        assertEquals(201, resposta.statusCode.value())
        assertTrue(resposta.body!!.contains("teste.txt"))
        assertTrue(Files.exists(uploadDir.resolve("teste.txt")))
    }

    @Test
    @DisplayName("Upload: arquivo vazio = status 400 com mensagem de erro")
    fun uploadAnexoVazio() {
        val mockFile = MockMultipartFile(
            "file",
            "vazio.txt",
            "text/plain",
            ByteArray(0)
        )

        val resposta = controller.uploadAnexo(mockFile)

        assertEquals(400, resposta.statusCode.value())
        assertEquals("O arquivo está vazio.", resposta.body)
    }

    @Test
    @DisplayName("Upload: nome do arquivo inválido = status 400 com mensagem de erro")
    fun uploadAnexoNomeInvalido() {
        val mockFile = MockMultipartFile(
            "file",
            null,
            "text/plain",
            "conteúdo".toByteArray()
        )

        val resposta = controller.uploadAnexo(mockFile)

        assertEquals(400, resposta.statusCode.value())
        assertEquals("Nome do arquivo inválido.", resposta.body)
    }

    @Test
    @DisplayName("Remover: arquivo existente = status 204")
    fun removerAnexoExistente() {
        val arquivo = uploadDir.resolve("para_remover.txt")
        Files.createDirectories(uploadDir)
        Files.write(arquivo, "teste".toByteArray())

        val resposta = controller.removerAnexo("para_remover.txt")

        assertEquals(204, resposta.statusCode.value())
        assertFalse(Files.exists(arquivo))
    }

    @Test
    @DisplayName("Remover: arquivo não existente = status 404 com mensagem")
    fun removerAnexoInexistente() {
        val resposta = controller.removerAnexo("nao_existe.txt")

        assertEquals(404, resposta.statusCode.value())
        assertTrue(resposta.body!!.contains("não encontrado"))
    }
}