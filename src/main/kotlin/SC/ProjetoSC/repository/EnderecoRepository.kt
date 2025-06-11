package SC.ProjetoSC.repository

import SC.ProjetoSC.entity.Endereco
import org.springframework.data.jpa.repository.JpaRepository

interface EnderecoRepository: JpaRepository<Endereco, Int> {
    // Listar endereços por usuário
    fun findByUsuarioId(usuarioId: Int): List<Endereco>

    // Listar endereços ativos por usuário
    fun findByUsuarioIdAndAtivoTrue(usuarioId: Int): List<Endereco>

    // Listar endereços inativos por usuário
    fun findByUsuarioIdAndAtivoFalse(usuarioId: Int): List<Endereco>

    // Buscar endereço por nome e ativo
    fun findByNomeEnderecoContainsIgnoreCaseAndAtivoTrue(nome: String): List<Endereco>

}