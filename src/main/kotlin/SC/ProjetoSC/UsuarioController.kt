package SC.ProjetoSC

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("usuarios")
class UsuarioController {
    val listaUsuario = mutableListOf<Usuario>();

    @GetMapping
    fun getUser(): ResponseEntity<Any> {
        if (listaUsuario.size >0)
            return ResponseEntity.ok(listaUsuario)
        else
            return ResponseEntity.status(204).body("Nenhum usuário cadastrado!")
    }

    @PostMapping
    fun cadastrarUsuario(@RequestBody novoUsuario: Usuario): ResponseEntity<String> {
        if (novoUsuario.Email == null ||
            novoUsuario.Nome == null ||
            novoUsuario.Senha == null ||
            novoUsuario.Telefone == null ||
            novoUsuario.Tipo == null
        )
            return ResponseEntity.status(401).body("Campos faltando!")
        listaUsuario.add(novoUsuario)
        return ResponseEntity.ok("Novo usuário cadastrado!")

    }
}