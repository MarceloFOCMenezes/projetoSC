package sc.projetosc.controller

import sc.projetosc.entity.Usuario
import sc.projetosc.repository.UsuarioRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import sc.projetosc.Enum.TipoUsuarioEnum
import sc.projetosc.dto.LoginDTO
import java.util.*

class UsuarioControllerTest {

    val repository = mock(UsuarioRepository::class.java)
    val encoder: PasswordEncoder = BCryptPasswordEncoder()
    val controller = UsuarioController(repository, encoder)
    lateinit var usuario: Usuario

    @BeforeEach
    fun setup() {
        usuario = Usuario(
            id = 1,
            nome = "Paula",
            email = "paula@email.com",
            telefone = "1234567890",
            senha = encoder.encode("12345678"),
            tipo = TipoUsuarioEnum.cliente,
            logado = false
        )
        `when`(repository.existsById(1)).thenReturn(true)
        `when`(repository.findById(1)).thenReturn(Optional.of(usuario))
        `when`(repository.existsByEmailIgnoreCase(usuario.email!!)).thenReturn(true)
        `when`(repository.findByEmailIgnoreCase(usuario.email!!)).thenReturn(usuario)
    }

    @Test
    @DisplayName("ListarUsuarios: COM dados = status 200 com a lista correta")
    fun listarUsuarios() {
        `when`(repository.findAll()).thenReturn(listOf(usuario))
        val retorno = controller.listarUsuarios()
        assertEquals(200, retorno.statusCode.value())
        assertEquals(1, retorno.body?.size)
    }

    @Test
    @DisplayName("ListarUsuarios: SEM dados = status 204 sem corpo")
    fun listarUsuariosVazio() {
        `when`(repository.findAll()).thenReturn(emptyList())
        val retorno = controller.listarUsuarios()
        assertEquals(204, retorno.statusCode.value())
        assertNull(retorno.body)
    }

    @Test
    @DisplayName("Login: credenciais corretas = status 200")
    fun loginCorreto() {
        val loginDTO = LoginDTO(email = usuario.email!!, senha = "12345678")
        val response = controller.login(loginDTO)
        assertEquals(200, response.statusCode.value())
        assertEquals(usuario.email, response.body?.email)
        assertTrue(response.body?.logado ?: false)
    }

    @Test
    @DisplayName("Login: usuário não encontrado = status 404")
    fun loginUsuarioNaoEncontrado() {
        val loginDTO = LoginDTO(email = "naoexiste@email.com", senha = "12345678")
        `when`(repository.existsByEmailIgnoreCase(loginDTO.email)).thenReturn(false)
        val response = controller.login(loginDTO)
        assertEquals(404, response.statusCode.value())
        assertNull(response.body)
    }

    @Test
    @DisplayName("Login: senha incorreta = status 401")
    fun loginSenhaIncorreta() {
        val loginDTO = LoginDTO(email = usuario.email!!, senha = "senhaErrada")
        val response = controller.login(loginDTO)
        assertEquals(401, response.statusCode.value())
        assertNull(response.body)
    }

    @Test
    @DisplayName("CadastrarUsuario: telefone inválido = status 400")
    fun cadastrarUsuarioTelefoneInvalido() {
        val novoUsuario = usuario.copy(telefone = "abc123")
        val response = controller.cadastrarUsuario(novoUsuario)
        assertEquals(400, response.statusCode.value())
        assertNull(response.body)
    }

    @Test
    @DisplayName("CadastrarUsuario: senha curta = status 400")
    fun cadastrarUsuarioSenhaCurta() {

        val novoUsuario = usuario.copy(senha = "123")
        val response = controller.cadastrarUsuario(novoUsuario)
        assertEquals(400, response.statusCode.value())
        assertNull(response.body)
    }

    @Test
    @DisplayName("CadastrarUsuario: já cadastrado = status 409")
    fun cadastrarUsuarioJaCadastrado() {
        val novoUsuario = usuario.copy(email = usuario.email)
        `when`(repository.existsByEmailIgnoreCase(novoUsuario.email!!)).thenReturn(true)
        val response = controller.cadastrarUsuario(novoUsuario)
        assertEquals(409, response.statusCode.value())
        assertNull(response.body)
    }

    @Test
    @DisplayName("CadastrarUsuario: sucesso = status 201")
    fun cadastrarUsuarioSucesso() {
        val novoUsuario = usuario.copy(email = "novo@email.com", telefone = "11999999999", senha = "12345678")
        `when`(repository.existsByEmailIgnoreCase(novoUsuario.email!!)).thenReturn(false)
        `when`(repository.save(any(Usuario::class.java))).thenReturn(novoUsuario)
        val response = controller.cadastrarUsuario(novoUsuario)
        assertEquals(201, response.statusCode.value())
        assertEquals(novoUsuario.email, response.body?.email)
    }

    @Test
    @DisplayName("AlterarSenha: sucesso = status 200")
    fun alterarSenhaSucesso() {
        val novaSenha = "novaSenha123"
        val response = controller.alterarSenha(usuario.email!!, novaSenha)
        assertEquals(200, response.statusCode.value())
        assertTrue(encoder.matches(novaSenha, response.body?.senha ?: ""))
    }

    @Test
    @DisplayName("AlterarSenha: senha igual = status 409")
    fun alterarSenhaIgual() {
        val response = controller.alterarSenha(usuario.email!!, "12345678")
        assertEquals(409, response.statusCode.value())
        assertNull(response.body)
    }

    @Test
    @DisplayName("AlterarSenha: usuário não encontrado = status 404")
    fun alterarSenhaUsuarioNaoEncontrado() {
        val response = controller.alterarSenha("naoexiste@email.com", "novaSenha123")
        assertEquals(404, response.statusCode.value())
        assertNull(response.body)
    }
}