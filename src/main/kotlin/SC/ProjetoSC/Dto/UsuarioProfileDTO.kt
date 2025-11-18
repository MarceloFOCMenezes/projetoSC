package sc.projetosc.dto

import java.time.LocalDate
import java.time.LocalDateTime

// Adicionada essa DTO para retornar os dados do perfil do usuário
data class UsuarioProfileDTO(
    val id: Int?,
    val nome: String?,
    val email: String?,
    val telefone: String?,
    val dataNascimento: LocalDate?, // Campo que adicionei
    val avatarUrl: String?,      // Campo que adicionei
    val dataUltimoLogin: LocalDateTime?, // Campo que adicionei
    val totalPedidos: Long, // Novo campo para a contagem
    val dataCadastro: LocalDateTime? // Campo que adicionei
)