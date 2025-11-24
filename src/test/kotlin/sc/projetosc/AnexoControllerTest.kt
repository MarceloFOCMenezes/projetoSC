package sc.projetosc

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import sc.projetosc.controller.AnexoController
import sc.projetosc.entity.Anexo
import sc.projetosc.services.AnexoServices
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AnexoControllerTest {
    @Mock
    private lateinit var anexoService: AnexoServices

    @InjectMocks
    private lateinit var controller: AnexoController

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @DisplayName("Upload: arquivo válido = status 201 e retorna o anexo salvo")
    fun uploadAnexoValido() {
        val mockFile = MockMultipartFile(
            "file",
            "teste.txt",
            "text/plain",
            "conteúdo teste".toByteArray()
        )
        val anexo = Anexo(idAnexo = 1, imagemAnexo = mockFile.bytes)

        `when`(anexoService.salvar(mockFile)).thenReturn(anexo)

        val resposta = controller.upload(mockFile)

        assertEquals(HttpStatus.CREATED, resposta.statusCode)
        assertEquals(anexo, resposta.body)
    }

    @Test
    @DisplayName("Upload: arquivo vazio = status 400")
    fun uploadAnexoVazio() {
        val mockFile = MockMultipartFile(
            "file",
            "vazio.txt",
            "text/plain",
            ByteArray(0)
        )

        val resposta = controller.upload(mockFile)

        assertEquals(HttpStatus.BAD_REQUEST, resposta.statusCode)
    }

    @Test
    @DisplayName("Download: anexo existente = status 200 e retorna bytes")
    fun downloadAnexoExistente() {
        val anexo = Anexo(idAnexo = 1, imagemAnexo = "conteúdo".toByteArray())

        `when`(anexoService.buscar(1)).thenReturn(anexo)

        val resposta = controller.download(1)

        assertEquals(HttpStatus.OK, resposta.statusCode)
        assertTrue(resposta.body!!.isNotEmpty())
    }

    @Test
    @DisplayName("Remover: anexo existente = status 204")
    fun removerAnexoExistente() {
        doNothing().`when`(anexoService).deletar(1)

        val resposta = controller.remover(1)

        assertEquals(HttpStatus.NO_CONTENT, resposta.statusCode)
        verify(anexoService, times(1)).deletar(1)
    }
}