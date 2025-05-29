package SC.ProjetoSC.Services

import RequestPedidoDTO
import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.repository.EnderecoRepository
import SC.ProjetoSC.repository.PedidoRepository
import SC.ProjetoSC.repository.StatusPedidoRepository
import SC.ProjetoSC.repository.UsuarioRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PedidoServices(
    private val pedidoRepository: PedidoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val enderecoRepository: EnderecoRepository,
    private val statusPedidoRepository: StatusPedidoRepository
) {
    fun criarPedido(requestPedido: RequestPedidoDTO):Pedido {
       val cliente = usuarioRepository.findById(requestPedido.clienteId!!).orElseThrow { Exception("Cliente não encontrado") }
        val endereco = enderecoRepository.findById(requestPedido.enderecoId!!).orElseThrow { Exception("Endereço não encontrado") };
        val statusPedido = statusPedidoRepository.findById(1).orElseThrow { Exception("Status do pedido não encontrado") }
        val pedido = Pedido(
            precoTotal = requestPedido.precoTotal,
            isRetirada = requestPedido.isRetirada,
            forma_Pagamento = requestPedido.formaPagamento,
            cliente = cliente,
            endereco = endereco,
            statusPedido = statusPedido
        )
        pedidoRepository.save(pedido)
        return pedido

    }

    fun listarPedidos(dtPedido: LocalDateTime?, dtEntrega: LocalDateTime?): List<Pedido> {
        return when {
            dtPedido != null && dtEntrega != null -> pedidoRepository.findByDtPedidoAndDtEntregaGreaterThanEqual(dtPedido, dtEntrega)
            dtPedido != null -> pedidoRepository.findByDtPedido(dtPedido)
            dtEntrega != null -> pedidoRepository.findByDtEntrega(dtEntrega)
            else -> pedidoRepository.findAll()
        }
    }
}