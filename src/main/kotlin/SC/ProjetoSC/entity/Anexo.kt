package sc.projetosc.entity

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
