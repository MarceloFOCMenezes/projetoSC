package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
@Entity
@Table(name = "anexo")
data class Anexo(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do anexo")
    @Column(name = "id_anexo", nullable = false, unique = true)
    val idAnexo: Int? = null,

    @Column(name = "imagem_anexo",length = 100*1024*1024, nullable = true)
    @JsonIgnore
    var imagemAnexo: ByteArray? = null

){constructor() : this(null, null)}
