package SC.ProjetoSC.repository

import SC.ProjetoSC.entity.Anexo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnexoRepository : JpaRepository<Anexo, Int>
