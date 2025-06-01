package SC.ProjetoSC.controller

import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.Enum.TipoUsuarioEnum
import SC.ProjetoSC.entity.Usuario
import SC.ProjetoSC.repository.UsuarioRepository
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*

class UsuarioControllerTest {

    // criando um dublê do tipo mock para UsuarioRepository e fazendo as configurações iniciais
    val repository = mock(UsuarioRepository::class.java)
    val controller = UsuarioController(repository)

    lateinit var tipoUsuario: TipoUsuarioEnum

    lateinit var usuario: Usuario
    lateinit var usuario2: Usuario
    lateinit var usuario3: Usuario


    @BeforeEach
    fun setup() {
        tipoUsuario = TipoUsuarioEnum.cliente

        // usuario logado
        usuario = Usuario(1, "Paula", "paula@email.com", "123456789", "12345678", logado = true)
        // usuario não logado
        usuario2 = Usuario(2, "Zé", "zezinho@gmail.com", "123456789", "12345678", logado = false)
        // usuario não existente
        usuario3 = Usuario(9, "Luana", "luana@outlook.com", "123456789", "12345678", logado = false)
        // aqui vamos seguir o seguinte: 1-> existe | 9-> não existe
        `when`(repository.existsById(1)).thenReturn(true)
        `when`(repository.findById(1)).thenReturn(Optional.of(usuario))
        `when`(repository.existsById(9)).thenReturn(false)
    }

    // TESTES DA FUNÇÃO: listarUsuarios

    @Test
    @DisplayName("ListarUsuarios: COM dados = status 200 com a lista correta")
    fun get_listarUsuarios() {
        `when`(repository.findAll()).thenReturn(mutableListOf(mock(Usuario::class.java), mock(Usuario::class.java)))
        val retorno = controller.listarUsuarios()

        // verificando se o status da resposta é 200
        assertEquals(200, retorno.statusCode.value())
        // verificando se o corpo da resposta tem 2 elementos
        assertEquals(2, retorno.body?.size)
    }

    @Test
    @DisplayName("ListarUsuarios: SEM dados = status 204 sem corpo")
    fun get_listarUsuariosVazio() {
        // programando o mock pra se comportar como se NÃO houvesse dados na tabela
        `when`(repository.findAll()).thenReturn(mutableListOf())
        val retorno = controller.listarUsuarios()

        // verificando se o status da resposta é 204
        assertEquals(204, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }

    // -----------------------------------------------------------------------
    // TESTES DA FUNÇÃO: login

    @Test
    @DisplayName("Login: COM dados = status 200 com o usuário correto")
    fun get_login() {
        // programando o mock pra se comportar como se houvesse dados na tabela -> zezinho@email.com - usuario2
        `when`(repository.existsByEmailIgnoreCase("zezinho@gmail.com")).thenReturn(true)
        `when`(repository.findByEmailIgnoreCase("zezinho@gmail.com")).thenReturn(usuario2)
        val retorno = controller.login("zezinho@gmail.com", "12345678")
        // verificando se o status da resposta é 200
        assertEquals(200, retorno.statusCode.value())
        // verificando se o corpo da resposta é igual ao usuario
        assertEquals(usuario2, retorno.body)
        // verificando se o usuario está logado
        assertTrue(usuario2.logado)
        // verificando se a data do ultimo login é diferente de nulo
        assertNotNull(usuario2.dataUltimoLogin)
        // verificando se o usuario foi salvo no repositorio
        verify(repository, times(1)).save(usuario2)
        // verificando se o usuario foi encontrado no repositorio
        verify(repository, times(1)).findByEmailIgnoreCase("zezinho@gmail.com")
        verify(repository, times(1)).existsByEmailIgnoreCase("zezinho@gmail.com")
    }

    @Test
    @DisplayName("Login: SEM dados = status 404 sem corpo")
    fun get_loginVazio() {
        // programando o mock pra se comportar como se NÃO houvesse dados na tabela
        `when`(repository.existsByEmailIgnoreCase("")).thenReturn(false)
        val retorno = controller.login("", "")

        // verificando se o status da resposta é 404
        assertEquals(404, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }

    @Test
    @DisplayName("Login: COM dados e credenciais incorretas = status 401 sem corpo")
    fun get_loginIncorreto() {
        // programando o mock pra se comportar como se houvesse dados na tabela
        `when`(repository.existsByEmailIgnoreCase("zezinho@gmail.com")).thenReturn(true)
        `when`(repository.findByEmailIgnoreCase("zezinho@gmail.com")).thenReturn(usuario2)
        val retorno = controller.login("zezinho@gmail.com", "senhaErrada")

        // verificando se o status da resposta é 404
        assertEquals(401, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
        // verificando se o usuario foi encontrado no repositorio
        verify(repository, times(1)).findByEmailIgnoreCase("zezinho@gmail.com")
    }


    // -----------------------------------------------------------------------
    // TESTES DA FUNÇÃO: logoff

    @Test
    @DisplayName("Logoff: COM dados = status 200 com o usuário correto")
    fun logoff() {
        // programando o mock pra se comportar como se houvesse dados na tabela
        `when`(repository.existsByEmailIgnoreCase("paula@email.com")).thenReturn(true)
        `when`(repository.findByEmailIgnoreCase("paula@email.com")).thenReturn(usuario)
        val retorno = controller.logoff("paula@email.com")

        // verificando se o status da resposta é 200
        assertEquals(200, retorno.statusCode.value())
        // verificando se o corpo da resposta é igual ao usuario
        assertEquals(usuario, retorno.body)
        // verificando se o usuario está deslogado
        assertFalse(usuario.logado)
        // verificando se o usuario foi salvo no repositorio
        verify(repository, times(1)).save(usuario)
        // verificando se o usuario foi encontrado no repositorio
        verify(repository, times(1)).findByEmailIgnoreCase("paula@email.com")
    }

    @Test
    @DisplayName("Logoff: SEM dados = status 404 sem corpo")
    fun logoffVazio() {
        // programando o mock pra se comportar como se NÃO houvesse dados na tabela
        `when`(repository.existsByEmailIgnoreCase("")).thenReturn(false)
        val retorno = controller.logoff("")

        // verificando se o status da resposta é 404
        assertEquals(404, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }


    // -----------------------------------------------------------------------
    // TESTES DA FUNÇÃO: cadastrarUsuario

    @Test
    @DisplayName("CadastrarUsuario: ainda não cadastrado = status 201 com o usuário correto")
    fun cadastrarUsuario() {
        // programando o mock pra se comportar como se houvesse dados na tabela
        `when`(repository.existsByEmailIgnoreCase(usuario2.email!!)).thenReturn(false)
        `when`(repository.save(usuario2)).thenReturn(usuario2)
        val retorno = controller.cadastrarUsuario(usuario2)

        // verificando se o status da resposta é 201
        assertEquals(201, retorno.statusCode.value())
        // verificando se o corpo da resposta é igual ao usuario
        assertEquals(usuario2, retorno.body)
        // verificando se o usuario foi salvo no repositorio
        verify(repository, times(1)).save(usuario2)
        // verificando se o usuario foi encontrado no repositorio
        verify(repository, times(1)).existsByEmailIgnoreCase(usuario2.email!!)
    }

    @Test
    @DisplayName("CadastrarUsuario: já cadastrado = status 409 sem corpo")
    fun cadastrarUsuarioVazio() {
        // programando o mock pra se comportar como se houvesse dados na tabela
        `when`(repository.existsByEmailIgnoreCase(usuario3.email!!)).thenReturn(true)
        val retorno = controller.cadastrarUsuario(usuario3)

        // verificando se o status da resposta é 409
        assertEquals(409, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }


    // -----------------------------------------------------------------------
    // TESTES DA FUNÇÃO: alterarSenha

    @Test
    @DisplayName("AlterarSenha: senha aprovada = status 200 com o usuário correto")
    fun alterarSenha() {
        // programando o mock pra se comportar como se houvesse dados na tabela
        `when`(repository.existsByEmailIgnoreCase("paula@email.com")).thenReturn(true)
        `when`(repository.findByEmailIgnoreCase("paula@email.com")).thenReturn(usuario)
        val retorno = controller.alterarSenha("paula@email.com", "novaSenha")

        // verificando se o status da resposta é 200
        assertEquals(200, retorno.statusCode.value())
        // verificando se o corpo da resposta é igual ao usuario
        assertEquals(usuario, retorno.body)
        // verificando se a senha foi alterada
        assertEquals("novaSenha", usuario.senha)
        // verificando se o usuario foi salvo no repositorio
        verify(repository, times(1)).save(usuario)
    }

    @Test
    @DisplayName("AlterarSenha: senha nova igual a atual = status 409 sem corpo")
    fun alterarSenhaIgual() {
        // programando o mock pra se comportar como se houvesse dados na tabela
        `when`(repository.existsByEmailIgnoreCase("paula@email.com")).thenReturn(true)
        `when`(repository.findByEmailIgnoreCase("paula@email.com")).thenReturn(usuario)
        val retorno = controller.alterarSenha("paula@email.com", "12345678")

        // verificando se o status da resposta é 409
        assertEquals(409, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }

    @Test
    @DisplayName("AlterarSenha: SEM usuário = status 404 sem corpo")
    fun alterarSenhaVazio() {
        // programando o mock pra se comportar como se NÃO houvesse dados na tabela
        `when`(repository.existsByEmailIgnoreCase("luana@outlook.com")).thenReturn(false)
        val retorno = controller.alterarSenha("", "")

        // verificando se o status da resposta é 404
        assertEquals(404, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }

    // -----------------------------------------------------------------------
    // TESTES DA FUNÇÃO: alterarUsuario

    @Test
    @DisplayName("AlterarUsuario: usuário encontrado = status 200 com o usuário correto")
    fun alterarUsuario() {
        // programando o mock pra se comportar como se houvesse dados na tabela
        `when`(repository.existsById(1)).thenReturn(true)
        `when`(repository.save(usuario)).thenReturn(usuario)
        val retorno = controller.alterarUsuario(1, usuario)

        // verificando se o status da resposta é 200
        assertEquals(200, retorno.statusCode.value())
        // verificando se o corpo da resposta é igual ao usuario
        assertEquals(usuario, retorno.body)
        // verificando se o usuario foi salvo no repositorio
        verify(repository, times(1)).save(usuario)
    }

    @Test
    @DisplayName("AlterarUsuario: usuário não encontrado = status 404 sem corpo")
    fun alterarUsuarioVazio() {
        // programando o mock pra se comportar como se NÃO houvesse dados na tabela
        `when`(repository.existsById(9)).thenReturn(false)
        val retorno = controller.alterarUsuario(9, usuario3)

        // verificando se o status da resposta é 404
        assertEquals(404, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }

    // -----------------------------------------------------------------------
    // TESTES DA FUNÇÃO: apagarUsuario

    @Test
    @DisplayName("ApagarUsuario: usuário encontrado = status 204 sem corpo")
    fun apagarUsuario() {
        // programando o mock pra se comportar como se houvesse dados na tabela
        `when`(repository.existsById(1)).thenReturn(true)
        val retorno = controller.apagarUsuario(1)

        // verificando se o status da resposta é 204
        assertEquals(200, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
        // verificando se o usuario foi apagado no repositorio
        verify(repository, times(1)).deleteById(1)
    }

    @Test
    @DisplayName("ApagarUsuario: usuário não encontrado = status 404 sem corpo")
    fun apagarUsuarioVazio() {
        // programando o mock pra se comportar como se NÃO houvesse dados na tabela
        `when`(repository.existsById(9)).thenReturn(false)
        val retorno = controller.apagarUsuario(9)

        // verificando se o status da resposta é 404
        assertEquals(404, retorno.statusCode.value())
        // verificando se o corpo da resposta é nulo
        assertNull(retorno.body)
    }


    // -----------------------------------------------------------------------
    // TESTES DA FUNÇÃO: recuperarSenha

    @Test
    @DisplayName("RecuperarSenha: email encontrado = status 200 e senha redefinida")
    fun recuperarSenhaComSucesso() {
        `when`(repository.existsByEmailIgnoreCase(usuario2.email!!)).thenReturn(true)
        `when`(repository.findByEmailIgnoreCase(usuario2.email!!)).thenReturn(usuario2)

        val senhaAntiga = usuario2.senha
        val retorno = controller.recuperarSenha(usuario2.email!!)

        assertEquals(200, retorno.statusCode.value())
        assertEquals(usuario2, retorno.body)

        val novaSenha = usuario2.senha
        assertNotEquals(senhaAntiga, novaSenha)
        assertNotNull(novaSenha)
        assertEquals(12, novaSenha!!.length)
        assertTrue(novaSenha.any { it.isDigit() || it.isLetter() || !it.isLetterOrDigit() }) // Permite letras, números ou símbolos

        verify(repository, times(1)).save(usuario2)
    }

    @Test
    @DisplayName("RecuperarSenha: email não encontrado = status 404 sem corpo")
    fun recuperarSenhaEmailNaoEncontrado() {
        `when`(repository.existsByEmailIgnoreCase("naoexiste@email.com")).thenReturn(false)

        val retorno = controller.recuperarSenha("naoexiste@email.com")

        assertEquals(404, retorno.statusCode.value())
        assertNull(retorno.body)
    }

// -----------------------------------------------------------------------
// TESTES DA FUNÇÃO: recuperarSenha


    @Test
    @DisplayName("DesativarUsuario: usuário encontrado = status 200 com usuário desativado")
    fun desativarUsuarioComSucesso() {
        // usuário logado inicialmente
        usuario.logado = true

        `when`(repository.existsById(1)).thenReturn(true)
        `when`(repository.findById(1)).thenReturn(Optional.of(usuario))
        `when`(repository.save(usuario)).thenReturn(usuario)

        val retorno = controller.desativarUsuario(1)

        assertEquals(200, retorno.statusCode.value())
        assertEquals(usuario, retorno.body)
        assertFalse(usuario.logado) // Verifica se foi desativado
        verify(repository, times(1)).save(usuario)
    }

    @Test
    @DisplayName("DesativarUsuario: usuário não encontrado = status 404 sem corpo")
    fun desativarUsuarioNaoEncontrado() {
        `when`(repository.existsById(9)).thenReturn(false)
        val retorno = controller.desativarUsuario(9)

        assertEquals(404, retorno.statusCode.value())
        assertNull(retorno.body)
    }

// -----------------------------------------------------------------------
// TESTES DA FUNÇÃO: atualizarPerfil

    @Test
    @DisplayName("AtualizarUsuario: usuário encontrado = status 200 com dados atualizados")
    fun atualizarUsuarioComSucesso() {
        val usuarioAntigo = usuario.copy()
        val novosDados = Usuario(nome = "Novo Nome", email = "novo@email.com", telefone = "99999999")

        `when`(repository.existsById(1)).thenReturn(true)
        `when`(repository.findById(1)).thenReturn(Optional.of(usuario))
        `when`(repository.save(any(Usuario::class.java))).thenAnswer { it.arguments[0] }

        val retorno = controller.atualizarUsuario(1, novosDados)

        assertEquals(200, retorno.statusCode.value())
        assertEquals("Novo Nome", usuario.nome)
        assertEquals("novo@email.com", usuario.email)
        assertEquals("99999999", usuario.telefone)
        assertNotEquals(usuarioAntigo.nome, usuario.nome)
        verify(repository, times(1)).save(usuario)
    }

    @Test
    @DisplayName("AtualizarUsuario: usuário não encontrado = status 404")
    fun atualizarUsuarioNaoEncontrado() {
        val novosDados = Usuario(nome = "Novo Nome")

        `when`(repository.existsById(999)).thenReturn(false)

        val retorno = controller.atualizarUsuario(999, novosDados)

        assertEquals(404, retorno.statusCode.value())
        assertNull(retorno.body)
    }

}