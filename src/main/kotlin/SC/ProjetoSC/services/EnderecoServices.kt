package sc.projetosc.services


import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import sc.projetosc.entity.Endereco
import sc.projetosc.repository.EnderecoRepository
import sc.projetosc.repository.UsuarioRepository
import sc.projetosc.request.EnderecoRequest

@Service
class EnderecoServices(
    private val enderecoRepository: EnderecoRepository,
    private val usuarioRepository: UsuarioRepository
) {
    fun criarEndereco(requestEndereco: EnderecoRequest): Endereco {
        try {
        val usuario = requestEndereco.usuarioId?.let { usuarioRepository.findById(it).orElse(null) }
        val endereco = Endereco(
            idEndereco = null, // Para criação, sempre null para gerar automaticamente
            nomeEndereco = requestEndereco.nomeEndereco ?: "",
            cep = requestEndereco.cep ?: "",
            logradouro = requestEndereco.logradouro ?: "",
            numero = requestEndereco.numero ?: "",
            complemento = requestEndereco.complemento,
            bairro = requestEndereco.bairro ?: "",
            cidade = requestEndereco.cidade ?: "",
            estado = requestEndereco.estado ?: "",
            pontoReferencia = requestEndereco.pontoReferencia,
            usuario = usuario,
            ativo = requestEndereco.ativo ?: true
        )
        return enderecoRepository.save(endereco)
        } catch (e: Exception) {
            throw Exception("Erro ao criar endereço: ${e.message}")
        }
    }

    fun atualizarEndereco(id: Int, requestEndereco: EnderecoRequest): ResponseEntity<Any> {
        val enderecoOpt = enderecoRepository.findById(id)
        if (enderecoOpt.isEmpty) return ResponseEntity.status(404).build()
        val usuario = requestEndereco.usuarioId?.let { usuarioRepository.findById(it).orElse(null) }
        val enderecoExist = enderecoOpt.get()
        val enderecoAtt = enderecoExist.copy(
            cep = requestEndereco.cep ?: "",
            logradouro = requestEndereco.logradouro ?: "",
            numero = requestEndereco.numero ?: "",
            complemento = requestEndereco.complemento,
            bairro = requestEndereco.bairro ?: "",
            cidade = requestEndereco.cidade ?: "",
            estado = requestEndereco.estado ?: "",
            pontoReferencia = requestEndereco.pontoReferencia,
            usuario = usuario,
            ativo = requestEndereco.ativo ?: enderecoExist.ativo
        )
        enderecoRepository.save(enderecoAtt)
        return ResponseEntity.status(200).body(enderecoAtt)
    }

    fun desativarEndereco(id: Int): ResponseEntity<Any> {
        val enderecoOpt = enderecoRepository.findById(id)
        if (enderecoOpt.isEmpty) return ResponseEntity.status(404).build()
        val endereco = enderecoOpt.get().copy(ativo = false)
        enderecoRepository.save(endereco)
        return ResponseEntity.status(200).body(endereco)
    }

    fun listarEnderecosPorUsuario(usuarioId: Int): List<Endereco> {
        return enderecoRepository.findByUsuarioIdAndAtivoTrue(usuarioId)
    }
}