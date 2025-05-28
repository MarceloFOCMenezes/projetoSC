package SC.ProjetoSC.Services

import RequestPedidoDTO
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
    fun criarPedido(requestPedido: RequestPedidoDTO) {
       val cliente = usuarioRepository.findById(requestPedido.clienteId!!).orElseThrow { Exception("Cliente não encontrado") }
        val endereco = enderecoRepository.findById(requestPedido.enderecoId!!).orElseThrow { Exception("Endereço não encontrado") };
        val statusPedido = statusPedidoRepository.findById(requestPedido.statusPedidoId!!).orElseThrow { Exception("Status do pedido não encontrado") }
        val pedido = Pedido(
            dtPedido = requestPedido.dtPedido,
            dtEntrega = requestPedido.dtEntrega,
            precoTotal = requestPedido.precoTotal,
            isRetirada = requestPedido.isRetirada,
            cliente = cliente,
            endereco = endereco,
            statusPedido = statusPedido
        )
    }
}