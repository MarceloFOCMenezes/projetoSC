package SC.ProjetoSC.controller

import SC.ProjetoSC.Services.EnderecoServices
import SC.ProjetoSC.dto.RequestEnderecoDTO
import SC.ProjetoSC.entity.Endereco
import SC.ProjetoSC.entity.Usuario
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.ResponseEntity

class EnderecoControllerTest {

    private val enderecoServices = mock(EnderecoServices::class.java)
    private val controller = EnderecoController(enderecoServices)

    lateinit var usuario: Usuario
    lateinit var endereco: Endereco

    @BeforeEach
    fun setup() {
        usuario = Usuario(
            id = 1,
            nome = "Usuário Teste",
            email = "teste@email.com",
            telefone = "123456789",
            senha = "senha123",
            tipo = SC.ProjetoSC.Enum.TipoUsuarioEnum.cliente,
            logado = false
        )
        endereco = Endereco(
            idEndereco = 1,
            nomeEndereco = "Casa",
            cep = "12345678",
            logradouro = "Rua Teste",
            numero = "100",
            complemento = "Apto 10",
            bairro = "Centro",
            cidade = "Cidade Teste",
            estado = "SP",
            pontoReferencia = "Próximo à praça",
            usuario = usuario,
            ativo = true
        )
    }

    @Test
    @DisplayName("criarEndereco: deve retornar status 201 e o endereço criado")
    fun criarEndereco() {
        val dto = RequestEnderecoDTO(
            nomeEndereco = "Casa",
            cep = "12345678",
            logradouro = "Rua Teste",
            numero = "100",
            bairro = "Centro",
            cidade = "Cidade Teste",
            estado = "SP",
            usuarioId = 1,
            ativo = true
        )
        `when`(enderecoServices.criarEndereco(dto)).thenReturn(endereco)

        val response = controller.criarEndereco(dto)

        assertEquals(201, response.statusCode.value())
        assertEquals(endereco, response.body)
    }

    @Test
    @DisplayName("atualizarEndereco: deve retornar status 200 e o endereço atualizado")
    fun atualizarEndereco() {
        val dto = RequestEnderecoDTO(
            nomeEndereco = "Casa",
            cep = "12345678",
            logradouro = "Rua Teste",
            numero = "100",
            bairro = "Centro",
            cidade = "Cidade Teste",
            estado = "SP",
            usuarioId = 1,
            ativo = true
        )
        `when`(enderecoServices.atualizarEndereco(1, dto)).thenReturn(ResponseEntity.status(200).body(endereco))

        val response = controller.atualizarEndereco(1, dto)

        assertEquals(200, response.statusCode.value())
        assertEquals(endereco, response.body)
    }

    @Test
    @DisplayName("atualizarEndereco: endereço não encontrado = status 404")
    fun atualizarEnderecoNaoEncontrado() {
        val dto = RequestEnderecoDTO()
        `when`(enderecoServices.atualizarEndereco(99, dto)).thenReturn(ResponseEntity.status(404).build())

        val response = controller.atualizarEndereco(99, dto)

        assertEquals(404, response.statusCode.value())
        assertNull(response.body)
    }

    @Test
    @DisplayName("desativarEndereco: deve retornar status 200 e o endereço desativado")
    fun desativarEndereco() {
        val enderecoDesativado = endereco.copy(ativo = false)
        `when`(enderecoServices.desativarEndereco(1)).thenReturn(ResponseEntity.status(200).body(enderecoDesativado))

        val response = controller.desativarEndereco(1)

        assertEquals(200, response.statusCode.value())
        assertEquals(enderecoDesativado, response.body)
    }

    @Test
    @DisplayName("desativarEndereco: endereço não encontrado = status 404")
    fun desativarEnderecoNaoEncontrado() {
        `when`(enderecoServices.desativarEndereco(99)).thenReturn(ResponseEntity.status(404).build())

        val response = controller.desativarEndereco(99)

        assertEquals(404, response.statusCode.value())
        assertNull(response.body)
    }

    @Test
    @DisplayName("listarEnderecosPorUsuario: deve retornar lista de endereços do usuário")
    fun listarEnderecosPorUsuario() {
        val lista = listOf(endereco)
        `when`(enderecoServices.listarEnderecosPorUsuario(1)).thenReturn(lista)

        val response = controller.listarEnderecosPorUsuario(1)

        assertEquals(1, response.size)
        assertEquals(endereco, response[0])
    }

    @Test
    @DisplayName("listarEnderecosPorUsuario: usuário sem endereços = lista vazia")
    fun listarEnderecosPorUsuarioVazio() {
        `when`(enderecoServices.listarEnderecosPorUsuario(2)).thenReturn(emptyList())

        val response = controller.listarEnderecosPorUsuario(2)

        assertTrue(response.isEmpty())
    }
}