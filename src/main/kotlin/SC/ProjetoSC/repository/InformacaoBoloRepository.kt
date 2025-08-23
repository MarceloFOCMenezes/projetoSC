package sc.projetosc.repository

import sc.projetosc.entity.InformacaoBolo
import org.springframework.data.jpa.repository.JpaRepository

interface InformacaoBoloRepository: JpaRepository<InformacaoBolo, Int> {
    // Aqui você pode adicionar métodos específicos para InformacaoBolo, se necessário
    // Por exemplo, buscar informações de bolo por algum critério específico
}