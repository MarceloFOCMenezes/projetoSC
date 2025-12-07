package sc.projetosc.repository

import sc.projetosc.entity.Usuario
import org.springframework.data.jpa.repository.JpaRepository

/*Uma Repository abstrai os métodos de acesso a dados.
Ex: findById() abstrai um "select * from musica where id = ?"
save() abstrai um "insert into musica values (?, ?, ?)"
       ou um "update musica set nome = ?, interprete = ? where id = ?"

Note que é uma interface, não uma classe.
O Spring vai criar uma implementação dessa classe cujos métodos
irão usar instruções SQL apropriadas para o banco de dados
configurado para o projeto

Dentro de <> temos 2 tipos
1o. é o tipo da Entidade
2o. é o tipo do Id (PK)   */

interface UsuarioRepository : JpaRepository<Usuario, Int> {
    fun existsByEmailIgnoreCase(email:String):Boolean

    fun findByEmailIgnoreCase(email:String): Usuario

}