package sc.projetosc.services

import sc.projetosc.dto.RequestEnderecoDTO
import sc.projetosc.entity.Endereco
import sc.projetosc.repository.EnderecoRepository
import sc.projetosc.repository.UsuarioRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class EnderecoServices(
    private val enderecoRepository: EnderecoRepository,
    private val usuarioRepository: UsuarioRepository
) {
    fun criarEndereco(requestEndereco: RequestEnderecoDTO): Endereco {
        val usuario = requestEndereco.usuarioId?.let { usuarioRepository.findById(it).orElse(null) }
        val endereco = Endereco(
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
    }

    fun atualizarEndereco(id: Int, requestEndereco: RequestEnderecoDTO): ResponseEntity<Any> {
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