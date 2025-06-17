package SC.ProjetoSC.Services




import SC.ProjetoSC.dto.RequestIngredienteDto
import SC.ProjetoSC.entity.Ingrediente
import SC.ProjetoSC.repository.IngredienteRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import SC.ProjetoSC.repository.TipoIngredienteRepository

@Service
class IngredienteServices(
    private val ingredienteRepository: IngredienteRepository,
    private val TipoIngredienteRepository: TipoIngredienteRepository
) {
    fun criarIngrediente(novoIngrediente: RequestIngredienteDto): Ingrediente {
        val tipoIngrediente = TipoIngredienteRepository.findById(novoIngrediente.idTipoIngrediente!!).orElse(null)
        val ingrediente = Ingrediente(
            tipoIngrediente = tipoIngrediente,
            nome = novoIngrediente.nome,
            premium = novoIngrediente.is_premium ?: false,
            ativo = novoIngrediente.Ativo ?: true,
        )

        return ingredienteRepository.save(ingrediente) // Retorna o objeto salvo
    }


    fun atualizarIngrediente(id: Int, dto: RequestIngredienteDto): ResponseEntity<Any> {
        val ingredienteOpt = ingredienteRepository.findById(id)
        if (ingredienteOpt.isEmpty) return ResponseEntity.status(404).build()
        val tipoIngrediente = TipoIngredienteRepository.findById(dto.idTipoIngrediente!!).orElse(null)

        val ingredienteExist = ingredienteOpt.get()
        val ingredienteAtt = ingredienteExist.copy(
            nome = dto.nome,
            ativo = dto.Ativo!!,
            premium = dto.is_premium!!,
            tipoIngrediente = tipoIngrediente
        )
        ingredienteRepository.save(ingredienteAtt)
        return ResponseEntity.status(200).body(ingredienteAtt)
    }


    fun atualizarStatusIngrediente(id: Int): ResponseEntity<Any>{
        val ingredienteOpt = ingredienteRepository.findById(id)
        if (ingredienteOpt.isEmpty) return ResponseEntity.status(404).build()

        val ingrediente = ingredienteOpt.get()
        val novoStatus = !(ingrediente.ativo)
        val ingredienteAtualizado = ingrediente.copy(ativo = novoStatus)
        ingredienteRepository.save(ingredienteAtualizado)
        return ResponseEntity.status(200).body(ingredienteAtualizado)
    }


    fun listarIngredientes(nome: String?, ativos: Boolean?): List<Ingrediente> {
        return when {
            // Buscar apenas inativos e por nome
            ativos == false && !nome.isNullOrBlank() ->
                ingredienteRepository.findByNomeContainsIgnoreCaseAndAtivoFalse(nome)

            // Buscar apenas inativos, sem nome
            ativos == false ->
                ingredienteRepository.findByAtivoFalse()

            // Buscar apenas ativos e por nome
            ativos == true && !nome.isNullOrBlank() ->
                ingredienteRepository.findByNomeContainsIgnoreCaseAndAtivoTrue(nome)

            // Buscar apenas ativos, sem nome
            ativos == true ->
                ingredienteRepository.findByAtivoTrue()

            // Buscar todos (ativos e inativos) por nome
            !nome.isNullOrBlank() ->
                ingredienteRepository.findByNomeContainsIgnoreCase(nome)

            // Buscar todos (ativos e inativos), sem nome
            else -> ingredienteRepository.findAll()
        }
    }


     fun listarIngredientesPorTipo(tipo: String, ativos: Boolean?): List<Ingrediente> {
        return when {
            // Buscar apenas inativos e por tipo
            ativos == false && !tipo.isNullOrBlank() ->
                ingredienteRepository.findByTipoIngrediente_DescricaoContainsIgnoreCase(tipo)

            // Buscar apenas ativos e por tipo
            ativos == true && !tipo.isNullOrBlank() ->
                ingredienteRepository.findByTipoIngrediente_DescricaoContainsIgnoreCaseAndAtivoTrue(tipo)

            // Buscar todos (ativos e inativos) por tipo
            !tipo.isNullOrBlank() ->
                ingredienteRepository.findByTipoIngrediente_DescricaoContainsIgnoreCaseAndAtivoFalse(tipo)

            // Buscar todos sem tipo
            else -> ingredienteRepository.findAll()
        }
    }

}