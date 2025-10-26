package sc.projetosc.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sc.projetosc.entity.Anexo


@Repository
interface AnexoRepository : JpaRepository<Anexo, Int>{

    fun findAnexoByIdAnexo(anexoId: Int):List<Anexo>
    @Query( value = """
    SELECT imagem_anexo FROM anexo LIMIT 10
""", nativeQuery = true)
    fun getDefaultImages(

    ): List<Array<ByteArray>>


}


