package SC.ProjetoSC.entity

import SC.ProjetoSC.Enum.TipoDataEnum
import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
@Table(name = "data_comemorativa")
@Entity
data class DataComemorativa(
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador do usuário")
    @Column(name = "id_data_comemorativa", nullable = false, unique = true)
    val idDataComemorativa:Int? = null,

    @Schema(description = "Data comemorativa")
    @field:NotNull
    @Column(name = "data_comemorativa", nullable = false)
    val dataComemorativa: LocalDateTime? = null,


    @field:NotNull
    @Schema(description = "FK do usuário que registrou a data comemorativa")
    @ManyToOne
    @JoinColumn(name = "fk_usuario", nullable = false)
    val usuario:Usuario? = null,

    @field:NotNull
    @Schema(description = "Tipo da data comemorativa")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_data", nullable = false)
    val tipoData:TipoDataEnum? = null,
){constructor() : this(null, null, null, null)}
