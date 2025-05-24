package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

@Entity
data class Anexo(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do anexo")
    val idAnexo: Int? = null,

    @Column(length = 100*1024*1024)
    @JsonIgnore
    var imagemAnexo: ByteArray? = null

){constructor() : this(null, null)}
