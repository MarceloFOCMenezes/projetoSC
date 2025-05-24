package SC.ProjetoSC.entity

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class DataComemorativa(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do usuário")
    val idDataComemorativa:Int? = null,

    @Schema(description = "Data comemorativa")
    @field:NotNull
    val dataComemorativa: LocalDateTime? = null,


    @field:NotNull
    @Schema(description = "FK do usuário que registrou a data comemorativa")
    @ManyToOne
    @JoinColumn(name = "fkUsuario", nullable = false)
    val usuario:Usuario? = null,

    @field:NotNull
    @Schema(description = "FK da data comemorativa")
    @ManyToOne
    @JoinColumn(name = "fkTipoData", nullable = false)
    val tipoData:TipoData? = null,
){constructor() : this(null, null, null, null)}
