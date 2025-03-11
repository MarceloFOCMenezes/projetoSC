package SC.ProjetoSC

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
class UsuarioController {
    var listaUsuario = mutableListOf<Usuario>()

    @GetMapping
    fun listarUsuario(): ResponseEntity<Any> {
        if (listaUsuario.isNotEmpty())
            return ResponseEntity.ok(listaUsuario)
        return ResponseEntity.status(204).body("Nenhum usuário foi cadastrado!")
    }

    @GetMapping("/login")
    fun login(@RequestParam email:String, @RequestParam senha: String):ResponseEntity<String>{
        val usuario = listaUsuario.find {it.Email == email}

        if(usuario == null)
            return ResponseEntity.status(409).body("Nenhum usuário encontrado com esse email")
        if(usuario.Senha != senha)
            return ResponseEntity.status(401).body("Senha incorreta")
        return ResponseEntity.ok("Login realizado com sucesso!")

    }

    @PostMapping
    fun CadastrarUsuario(@RequestBody novoUsuario: Usuario): ResponseEntity<String> {
        if (novoUsuario.Nome.isNullOrBlank() ||
            novoUsuario.Email.isNullOrBlank() ||
            novoUsuario.Telefone.isNullOrBlank() ||
            novoUsuario.Senha.isNullOrBlank() ||
            novoUsuario.Tipo == null
        ) {
        return ResponseEntity.status(400).body("Campos Faltando!")
        }
            if (listaUsuario.any { it.Email == novoUsuario.Email }) {
                return ResponseEntity.status(409).body("Usuario já cadastrado!")
            }
            listaUsuario.add(novoUsuario)
            return ResponseEntity.ok("Usuario Cadastrado com sucesso!")

    }

    @PatchMapping
    fun alterarSenha(@RequestParam email: String, @RequestParam senha:String):ResponseEntity<String>{
        var usuario = listaUsuario.find { it.Email == email }

        if (usuario == null)
            return ResponseEntity.status(404).body("Usuário não encontrado com o Email: $email")
        usuario.Senha = senha
        return ResponseEntity.ok("Senha alterada com sucesso!")
    }

    @PutMapping
    fun alterarUsuario(@RequestParam id: Int, @RequestBody novoUsuario: Usuario):ResponseEntity<String>{
        if(id < 0 || id >= listaUsuario.size)
            return ResponseEntity.status(404).body("Usuário não encontrado com o ID: $id")

        if (novoUsuario.Nome.isNullOrBlank() ||
            novoUsuario.Email.isNullOrBlank() ||
            novoUsuario.Telefone.isNullOrBlank() ||
            novoUsuario.Senha.isNullOrBlank() ||
            novoUsuario.Tipo == null
        ) {
            return ResponseEntity.status(400).body("Campos Faltando!")
        }
        listaUsuario[id] = novoUsuario
        return ResponseEntity.ok("Usuário alterado com sucesso!")
    }

    @DeleteMapping
    fun ApagarUsuario(@RequestParam id: Int): ResponseEntity<String> {
        if (id < 0 || id >= listaUsuario.size) {
            return ResponseEntity.status(404).body("Usuário não encontrado com o ID: $id")
        }
            listaUsuario.removeAt(id)
            return ResponseEntity.ok("Usuário removido com sucesso!")
    }
}