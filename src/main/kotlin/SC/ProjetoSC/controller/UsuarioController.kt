package sc.projetosc.controller

import sc.projetosc.entity.Usuario
import sc.projetosc.repository.UsuarioRepository
import sc.projetosc.Enum.TipoUsuarioEnum
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import sc.projetosc.dto.LoginDTO

@Tag(name = "Usuários", description = "Operações relacionadas aos usuários do sistema")
@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = ["http://localhost:5173"])
class UsuarioController (
    val repositorio: UsuarioRepository,
    val encoder: PasswordEncoder
) {
    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista com todos os usuários registrados no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado. O corpo da resposta estará vazio."),
        ApiResponse(responseCode = "200", description = "Usuário(s) encontrado(s). O corpo da resposta contém a lista de usuários encontrados.  ")
    ])
    fun listarUsuarios(): ResponseEntity<List<Usuario>> {
        // faz um "select * from usuario"
        val usuarios = repositorio.findAll()

        return if (usuarios.isEmpty()) {
            ResponseEntity.status(204).build()
        } else {
            ResponseEntity.status(200).body(usuarios)
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Retorna o usuário logado.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Usuário logado com sucesso. O corpo da resposta contém o usuário logado."),
        ApiResponse(responseCode = "401", description = "Credenciais incorretas. O corpo da resposta estará vazio."),
        ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado. O corpo da resposta estará vazio.")
    ])
    fun login(@RequestBody loginData: LoginDTO):ResponseEntity<Usuario> {
        val email = loginData.email
        val senha = loginData.senha

        if (!repositorio.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(404).build()
        }

        val usuarioEncontrado = repositorio.findByEmailIgnoreCase(email)

        val senhaValida = encoder.matches(senha, usuarioEncontrado.senha)

        if (!senhaValida) {
            return ResponseEntity.status(401).build()
        }

        usuarioEncontrado.logado = true
        usuarioEncontrado.dataUltimoLogin = LocalDateTime.now()
        repositorio.save(usuarioEncontrado)

        return ResponseEntity.status(200).body(usuarioEncontrado)
    }

    @PatchMapping("/logoff")
    @Operation(summary = "Realizar logoff", description = "Realiza o logoff do usuário no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Logoff realizado com sucesso. O corpo da resposta contém o usuário deslogado."),
        ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado. O corpo da resposta estará vazio.")
    ])
    fun logoff(@RequestParam email: String): ResponseEntity<Usuario> {
        val usuarioEncontrado = repositorio.findByEmailIgnoreCase(email)
            ?: return ResponseEntity.status(404).build()

        // Atualiza o status de login
        usuarioEncontrado.logado = false
        repositorio.save(usuarioEncontrado)

        return ResponseEntity.status(200).body(usuarioEncontrado)
    }


    @PostMapping
    @Operation(summary = "Cadastrar um novo usuário", description = "Cadastrar um novo usuário no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "409", description = "Usuário já cadastrado. O corpo da resposta estará vazio."),
        ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso. O corpo da resposta contém os dados do usuário criado.")
    ])
    fun cadastrarUsuario(@RequestBody @Valid @NotNull novoUsuario: Usuario): ResponseEntity<Usuario> {
        if (novoUsuario.email.isNullOrBlank() || !novoUsuario.email!!.contains('@'))
            return ResponseEntity.status(400).body(null)
        if (novoUsuario.telefone.isNullOrBlank() || !novoUsuario.telefone!!.matches(Regex("^[0-9]{10,15}$")))
            return ResponseEntity.status(400).body(null)
        if (novoUsuario.senha.isNullOrBlank() || novoUsuario.senha!!.length < 8)
            return ResponseEntity.status(400).body(null)
        if (repositorio.existsByEmailIgnoreCase(novoUsuario.email!!))
            return ResponseEntity.status(409).build()

        // Modificando o objeto recebido para evitar criar um novo desnecessariamente
        novoUsuario.senha = encoder.encode(novoUsuario.senha)
        novoUsuario.tipo = TipoUsuarioEnum.cliente

        val usuarioSalvo = repositorio.save(novoUsuario)
        return ResponseEntity.status(201).body(usuarioSalvo)
    }

    @PatchMapping
    @Operation(summary = "Alterar senha", description = "Altera a senha de um usuário.")
    @ApiResponses( value  = [
        ApiResponse(responseCode = "200", description = "Senha alterada com sucesso. O corpo da resposta contém os dados do usuário."),
        ApiResponse(responseCode = "409", description = "Senha nova igual a senha atual. O corpo da resposta estará vazio."),
        ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado. O corpo da resposta estará vazio.")
    ])
    fun alterarSenha(@RequestParam @Email email: String, @RequestParam @Size(min = 8, max = 45) senha:String):ResponseEntity<Usuario>{
        val usuario = repositorio.findByEmailIgnoreCase(email)
            ?: return ResponseEntity.status(404).build()
        if (encoder.matches(senha, usuario.senha)) {
            return ResponseEntity.status(409).build()
        }
        usuario.senha = encoder.encode(senha)
        repositorio.save(usuario)
        return ResponseEntity.status(200).body(usuario)
    }

    @PatchMapping("/recuperar-senha")
    @Operation(summary = "Recuperar senha", description = "Gera uma nova senha e retorna para o usuário.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Senha gerada com sucesso."),
        ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    ])
    fun recuperarSenha(@RequestParam @Email email: String): ResponseEntity<String> {
        val usuario = repositorio.findByEmailIgnoreCase(email)
            ?: return ResponseEntity.status(404).build() // Adicionando validação

        val novaSenha = (10000000..99999999).random().toString() // Aumentando para 8 dígitos

        usuario.senha = encoder.encode(novaSenha)
        repositorio.save(usuario)

        // Retornando a senha em texto plano para o usuário (em um app real, isso seria enviado por email)
        return ResponseEntity.status(200).body("Sua nova senha é: $novaSenha")
    }

    @PutMapping("/desativar/{id}")
    fun desativarUsuario(@PathVariable id: Int): ResponseEntity<Usuario> {
        return if (repositorio.existsById(id)) {
            val usuario = repositorio.findById(id).get()
            usuario.logado = false
            repositorio.save(usuario)
            ResponseEntity.ok(usuario)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PutMapping("/{id}")
    fun atualizarUsuario(@PathVariable id: Int, @RequestBody novosDados: Usuario): ResponseEntity<Usuario> {
        return if (repositorio.existsById(id)) {
            val usuarioExistente = repositorio.findById(id).get()

            usuarioExistente.nome = novosDados.nome ?: usuarioExistente.nome
            usuarioExistente.email = novosDados.email ?: usuarioExistente.email
            usuarioExistente.telefone = novosDados.telefone ?: usuarioExistente.telefone

            repositorio.save(usuarioExistente)
            ResponseEntity.ok(usuarioExistente)
        } else {
            ResponseEntity.notFound().build()
        }
    }


}