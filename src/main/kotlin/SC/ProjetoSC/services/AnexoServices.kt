package sc.projetosc.services

import sc.projetosc.entity.Anexo
import sc.projetosc.repository.AnexoRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AnexoServices(
    private val anexoRepository: AnexoRepository
) {
    fun salvar(file: MultipartFile): Anexo {
        val anexo = Anexo(
            nomeArquivo = file.originalFilename,
            imagemAnexo = file.bytes
        )
        return anexoRepository.save(anexo)
    }

    fun buscar(id: Int): Anexo =
        anexoRepository.findById(id).orElseThrow { RuntimeException("Anexo não encontrado $id") }

    fun deletar(id: Int) {
        if (!anexoRepository.existsById(id)) {
            throw RuntimeException("Anexo não encontrado")
        }
        anexoRepository.deleteById(id)
    }
}
