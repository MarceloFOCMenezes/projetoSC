package SC.ProjetoSC.Services

import SC.ProjetoSC.DTO.RequestIngredienteDTO
import SC.ProjetoSC.entity.Ingrediente
import SC.ProjetoSC.repository.IngredienteRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class IngredienteServices(
    private val ingredienteRepository: IngredienteRepository
) {
    fun criarIngrediente(novoIngrediente: RequestIngredienteDTO): Ingrediente {
        val ingrediente = Ingrediente(
            ativo = novoIngrediente.ativo ?: true,
            descricao = novoIngrediente.descricao,
            precoUnitario = novoIngrediente.precoUnitario,
            categoria = novoIngrediente.categoria,
            observacao = novoIngrediente.observacao,
            idIngrediente = 1
        )
        ingredienteRepository.save(ingrediente)
        return ingrediente
    }


    fun atualizarIngrediente(id: Int, dto: RequestIngredienteDTO): ResponseEntity<Any> {
        val ingredienteOpt = ingredienteRepository.findById(id)
        if (ingredienteOpt.isEmpty) return ResponseEntity.status(404).build()

        val ingredienteExist = ingredienteOpt.get()
        val ingredienteAtt = ingredienteExist.copy(
            descricao = dto.descricao,
            precoUnitario = dto.precoUnitario,
            categoria = dto.categoria,
            ativo = dto.ativo == true,
            observacao = dto.observacao
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


    fun listarIngredientes(descricao: String?, ativos: Boolean?): List<Ingrediente> {
        return when {
            // Buscar apenas inativos e por nome
            ativos == false && !descricao.isNullOrBlank() ->
                ingredienteRepository.findByDescricaoContainsIgnoreCaseAndAtivoFalse(descricao)

            // Buscar apenas inativos, sem nome
            ativos == false ->
                ingredienteRepository.findByAtivoFalse()

            // Buscar apenas ativos e por nome
            ativos == true && !descricao.isNullOrBlank() ->
                ingredienteRepository.findByDescricaoContainsIgnoreCaseAndAtivoTrue(descricao)

            // Buscar apenas ativos, sem nome
            ativos == true ->
                ingredienteRepository.findByAtivoTrue()

            // Buscar todos (ativos e inativos) por nome
            !descricao.isNullOrBlank() ->
                ingredienteRepository.findByDescricaoContainsIgnoreCase(descricao)

            // Buscar todos (ativos e inativos), sem nome
            else -> ingredienteRepository.findAll()
        }
    }


     fun listarIngredientesPorTipo(tipo: String, ativos: Boolean?): List<Ingrediente> {
        return when {
            // Buscar apenas inativos e por tipo
            ativos == false && !tipo.isNullOrBlank() ->
                ingredienteRepository.findByTipoContainsIgnoreCaseAndAtivoFalse(tipo)

            // Buscar apenas ativos e por tipo
            ativos == true && !tipo.isNullOrBlank() ->
                ingredienteRepository.findByTipoContainsIgnoreCaseAndAtivoTrue(tipo)

            // Buscar todos (ativos e inativos) por tipo
            !tipo.isNullOrBlank() ->
                ingredienteRepository.findByTipoContainsIgnoreCase(tipo)

            // Buscar todos sem tipo
            else -> ingredienteRepository.findAll()
        }
    }

}