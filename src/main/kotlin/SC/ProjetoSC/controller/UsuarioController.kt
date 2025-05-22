package SC.ProjetoSC.controller

import SC.ProjetoSC.entity.Usuario
import SC.ProjetoSC.repository.UsuarioRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@Tag(name = "Usuários", description = "Operações relacionadas aos usuários do sistema")
@RestController
@RequestMapping("/usuarios")
class UsuarioController (val repositorio: UsuarioRepository) {

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

    @GetMapping("/login")
    @Operation(summary = "Realizar login", description = "Retorna o usuário logado.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Usuário logado com sucesso. O corpo da resposta contém o usuário logado."),
        ApiResponse(responseCode = "401", description = "Credenciais incorretas. O corpo da resposta estará vazio."),
        ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado. O corpo da resposta estará vazio.")
    ])
    fun login(@RequestParam email:String, @RequestParam senha: String):ResponseEntity<Usuario> {
        if (!repositorio.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(404).build()
        }
        val usuarioEncontrado = repositorio.findByEmailIgnoreCase(email)
        if (usuarioEncontrado.senha != senha) {
            return ResponseEntity.status(401).build()
        }

        // Atualiza o status de login e a data do último login
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
        if (repositorio.existsByEmailIgnoreCase(novoUsuario.email!!)) {
            //para lembrar: O !! é o operador de not-null forcado. Ele diz para o compilador: "Confia em mim, essa variável não é nula. Pode usar."
            return ResponseEntity.status(409).build()
        }
        val usuario = repositorio.save(novoUsuario)
        return ResponseEntity.status(201).body(usuario)
    }

    @PatchMapping
    @Operation(summary = "Alterar senha", description = "Altera a senha de um usuário.")
    @ApiResponses( value  = [
        ApiResponse(responseCode = "200", description = "Senha alterada com sucesso. O corpo da resposta contém os dados do usuário."),
        ApiResponse(responseCode = "409", description = "Senha nova igual a senha atual. O corpo da resposta estará vazio."),
        ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado. O corpo da resposta estará vazio.")
    ])
    fun alterarSenha(@RequestParam @Email email: String, @RequestParam @Size(min = 8, max = 45) senha:String):ResponseEntity<Usuario>{
        // como o @Valid só funciona pra @RequestBody, aqui validamos usando as validações lá da classe mesmo!

        val usuario = repositorio.findByEmailIgnoreCase(email)
            ?: return ResponseEntity.status(404).build()
        if (usuario.senha == senha) {
            return ResponseEntity.status(409).build()
        }
        usuario.senha = senha
        repositorio.save(usuario)
        return ResponseEntity.status(200).body(usuario)
    }

    @PutMapping
    @Operation(summary = "Alterar usuário", description = "Altera a conta do usuário logado no sistema.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Usuário alterado com sucesso. O corpo da resposta contém o novo usuário logado."),
        ApiResponse(responseCode = "404", description = "Nenhum usuário encontrado. O corpo da resposta estará vazio.")
    ])
    fun alterarUsuario(@RequestParam id: Int, @RequestBody @Valid novoUsuario: Usuario):ResponseEntity<Usuario>{
        // verificar se o usuário existe
        if (!repositorio.existsById(id)) {
            return ResponseEntity.status(404).build()
        }
        val usuarioAtualizado = novoUsuario.copy(id = id)
        //garante que o ID na URL será usado, mesmo se novoUsuario.id vier diferente ou nulo, assim evita sobreescrever outro usuário sem querer
        repositorio.save(usuarioAtualizado)
        return ResponseEntity.status(200).body(usuarioAtualizado)
    }

    @DeleteMapping
    @Operation(summary = "Excluir usuário", description = "Exclui um usuário do sistema")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Usuário excluído com sucesso. O corpo da resposta estará vazio."),
        ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado. O corpo da resposta estará vazio.")
    ])
    fun apagarUsuario(@RequestParam id: Int): ResponseEntity<Void> {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.status(404).build()
        }
        repositorio.deleteById(id)
        return ResponseEntity.status(200).build()
    }
}