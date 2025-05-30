package SC.ProjetoSC.Services

import SC.ProjetoSC.DTO.ItemPedidoDto
import SC.ProjetoSC.DTO.PedidoDto
import SC.ProjetoSC.entity.InformacaoBolo
import SC.ProjetoSC.entity.ItemPedidoIngrediente
import SC.ProjetoSC.entity.ItemPedidoIngredienteId
import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.repository.*
import org.springframework.stereotype.Service

@Service
class PedidoServices(
    private val pedidoRepository: PedidoRepository,
    private val itemPedidoRepository: ItemPedidoRepository,
    private val informacaoBoloRepository: InformacaoBoloRepository,
    private val produtoRepository: ProdutoRepository,
    private val itemPedidoIngredienteRepository: ItemPedidoIngredienteRepository
) {


    fun listarPedidos(idUsuario: Int?): PedidoDto {
            val pedidosUsuario: List<Pedido> = if (idUsuario == 0 || idUsuario == null) {
                pedidoRepository.findAll() // Retorna todos os pedidos se o ID do usuário for 0
            } else {
                pedidoRepository.findByClienteIdAndStatusPedidoIdStatusPedidoOrderByDtPedidoDesc(idUsuario, 1) // Retorna os pedidos do usuário específico
            }

        val listaPedidos = mutableListOf<PedidoDto>()
        pedidosUsuario.forEach { pedido ->

            val ListaItensPedido = mutableListOf<ItemPedidoDto>();
            val ListaIngredientes = mutableListOf<ItemPedidoIngrediente>();
            val ItensPedido = itemPedidoRepository.findByPedidoId(pedido.id!!);



            ItensPedido.forEach { item ->
                val produto = item.produto
                var informacaoBolo: InformacaoBolo? = null
                var ingrediente: ItemPedidoIngrediente? = null

                if (produto != null) {
                    if(produto.temIngrediente == true){
                        // Busca a informação do bolo associada ao item do pedido
                        informacaoBolo = informacaoBoloRepository.findById(item.idItemPedido!!)
                            .orElse(null)

                        val ingredienteId = ItemPedidoIngredienteId(
                            itemPedidoId = item.idItemPedido!!,
                            ingredienteId = item.produto?.idProduto ?: 0
                        )
                        ingrediente = itemPedidoIngredienteRepository.findById(ingredienteId)
                            .orElse(null)

                        ListaIngredientes.add(ingrediente)
                    }
                }

                ListaItensPedido.add(
                    ItemPedidoDto(
                        descricao = produto?.descricao,
                        quantidade = item.quantidade,
                        precoUnitario = produtoRepository.findById(item.produto?.idProduto ?: 0)
                            .orElse(null)?.precoUnitario!!.toDouble(),
                        informacaoBolo = informacaoBolo,
                        ingredientes = ListaIngredientes
                    )
                )
            }


            val Pedido = PedidoDto(
                dtPedido = pedido.dtPedido.toString(),
                dtEntregaEsperada = pedido.dtEntregaEsperada.toString(),
                precoTotal = pedido.precoTotal,
                isRetirada = pedido.isRetirada,
                clienteId = pedido.cliente?.id,
                enderecoId = pedido.endereco?.idEndereco,
                statusPedidoId = pedido.statusPedido?.idStatusPedido,
                formaPagamento = pedido.formaPagamento.toString(),
                itensPedido = ListaItensPedido // Mapeia os produtos dos itens do pedido
            )

            listaPedidos.add(Pedido)
        }
        return listaPedidos.first();
    }
}