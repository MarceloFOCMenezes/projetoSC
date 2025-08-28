package SC.ProjetoSC.entity

import SC.ProjetoSC.Enum.TipoUsuarioEnum
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

@Entity
@Table(name = "anexo")
data class Anexo(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anexo", nullable = false, unique = true)
    val idAnexo: Int? = null,

    @Column(name = "nome_arquivo", nullable = false)
    var nomeArquivo: String? = null,

    @Lob
    @Column(name = "imagem_anexo", columnDefinition = "LONGBLOB")
    var imagemAnexo: ByteArray? = null
)

{constructor() : this(null, null)}
