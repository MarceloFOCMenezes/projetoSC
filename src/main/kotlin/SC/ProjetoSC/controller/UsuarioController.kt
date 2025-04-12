package SC.ProjetoSC.controller

import SC.ProjetoSC.entity.Usuario
import SC.ProjetoSC.repository.UsuarioRepository
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

@RestController
@RequestMapping("/usuarios")
class UsuarioController (val repositorio: UsuarioRepository) {

    @GetMapping
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
    fun login(@RequestParam email:String, @RequestParam senha: String):ResponseEntity<Usuario> {
        if (!repositorio.existsByEmailIgnoreCase(email)) {
            return ResponseEntity.status(404).build()
        }
        val usuarioEncontrado = repositorio.findByEmailIgnoreCase(email)
        if (usuarioEncontrado.senha != senha) {
            return ResponseEntity.status(401).build()
        }
        return ResponseEntity.status(200).body(usuarioEncontrado)
    }

    @PostMapping
    fun cadastrarUsuario(@RequestBody @Valid @NotNull novoUsuario: Usuario): ResponseEntity<Usuario> {
        if (repositorio.existsByEmailIgnoreCase(novoUsuario.email!!)) {
            //para lembrar: O !! é o operador de not-null forcado. Ele diz para o compilador: "Confia em mim, essa variável não é nula. Pode usar."
            return ResponseEntity.status(409).build()
        }
        val usuario = repositorio.save(novoUsuario)
        return ResponseEntity.status(201).body(usuario)
    }

    @PatchMapping
    fun alterarSenha(@RequestParam @Email email: String, @RequestParam @Size(min = 8, max = 45) senha:String):ResponseEntity<Usuario>{
        // como o @Valid só funciona pra @RequestBody, aqui validamos usando as validações lá da classe mesmo!

        val usuario = repositorio.findByEmailIgnoreCase(email)
            ?: return ResponseEntity.status(404).build()
        usuario.senha = senha
        repositorio.save(usuario)
        return ResponseEntity.status(200).body(usuario)
    }

    @PutMapping
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
    fun apagarUsuario(@RequestParam id: Int): ResponseEntity<Void> {
        if (!repositorio.existsById(id)) {
            return ResponseEntity.status(404).build()
        }
        repositorio.deleteById(id)
        return ResponseEntity.status(200).build()
    }
}