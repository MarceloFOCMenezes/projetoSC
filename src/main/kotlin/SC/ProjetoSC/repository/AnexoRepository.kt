package sc.projetosc.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sc.projetosc.entity.Anexo


@Repository
interface AnexoRepository : JpaRepository<Anexo, Int>{}
