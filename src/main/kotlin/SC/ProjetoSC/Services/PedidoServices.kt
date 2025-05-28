package SC.ProjetoSC.Services

import RequestPedidoDTO
import SC.ProjetoSC.entity.Endereco
import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.repository.EnderecoRepository
import SC.ProjetoSC.repository.PedidoRepository
import SC.ProjetoSC.repository.StatusPedidoRepository
import SC.ProjetoSC.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class PedidoServices(
    private val pedidoRepository: PedidoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val enderecoRepository: EnderecoRepository,
    private val statusPedidoRepository: StatusPedidoRepository
) {
    fun criarPedido(requestPedido: RequestPedidoDTO): Pedido {
        val cliente = usuarioRepository.findById(requestPedido.clienteId!!).orElseThrow { Exception("Cliente não encontrado") }
        val statusPedido = statusPedidoRepository.findById(requestPedido.statusPedidoId!!).orElseThrow { Exception("Status do pedido não encontrado") }
        val pedido = Pedido(
            precoTotal = requestPedido.precoTotal,
            isRetirada = requestPedido.isRetirada,
            cliente = cliente,
            endereco = delivery(requestPedido.isRetirada!!, requestPedido.enderecoId!!),
            statusPedido = statusPedido
        )
        pedidoRepository.save(pedido)
        return pedido
    }

    fun delivery(isRetirada : Boolean, id : Int): Endereco?
    {
        if (isRetirada) {
            return null
        }
        val endereco = enderecoRepository.findById(id)
        if (endereco.isPresent) {
            return endereco.get()
        } else {
            throw Exception("Endereço não encontrado")
        }

    }
}