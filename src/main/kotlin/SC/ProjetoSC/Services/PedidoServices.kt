package SC.ProjetoSC.Services

import SC.ProjetoSC.Enum.FormaPagamentoEnum
import SC.ProjetoSC.Request.AdicionarItemPedidoRequest
import SC.ProjetoSC.Request.AlterarPedidoRequest
import SC.ProjetoSC.Request.EnviarPedidoRequest
import SC.ProjetoSC.Response.ItemPedidoResponse
import SC.ProjetoSC.Response.ItemPedidoIngredienteResponse
import SC.ProjetoSC.Response.PedidoResponse
import SC.ProjetoSC.entity.*
import SC.ProjetoSC.repository.*
import org.springframework.stereotype.Service

@Service
class PedidoServices(
    private val pedidoRepository: PedidoRepository,
    private val itemPedidoRepository: ItemPedidoRepository,
    private val informacaoBoloRepository: InformacaoBoloRepository,
    private val produtoRepository: ProdutoRepository,
    private val itemPedidoIngredienteRepository: ItemPedidoIngredienteRepository,
    private val statusPedidoRepository: StatusPedidoRepository,
    private val ingredienteRepository: IngredienteRepository,
    private val usuarioRepository: UsuarioRepository
) {


    fun getPedidoAtual(idUsuario: Int?): PedidoResponse {
            val pedidosUsuario: List<Pedido> = if (idUsuario == 0 || idUsuario == null) {
                pedidoRepository.findAll() // Retorna todos os pedidos se o ID do usuário for 0
            } else {
                pedidoRepository.findByClienteIdAndStatusPedidoIdStatusPedidoOrderByDtPedidoDesc(idUsuario, 1) // Retorna os pedidos do usuário específico
            }

        var pedidos = PedidoResponse()
        pedidosUsuario.forEach { pedido ->

            val ListaItensPedido = mutableListOf<ItemPedidoResponse>();
            val ItensPedido = itemPedidoRepository.findByPedidoId(pedido.id!!);



            ItensPedido.forEach { item ->
                val ListaIngredientes = mutableListOf<ItemPedidoIngredienteResponse>();
                val produto = item.produto
                var informacaoBolo: InformacaoBolo? = null
                var ingredienteDTO: ItemPedidoIngredienteResponse? =   null

                if (produto != null) {
                    if(produto.temIngrediente == true){
                        // Busca a informação do bolo associada ao item do pedido
                        informacaoBolo = informacaoBoloRepository.findById(item.idItemPedido!!)
                            .orElse(null)


                        val ingredientes = itemPedidoIngredienteRepository.findIngredienteInItemPedido(item.idItemPedido)
                        ?.map { row ->
                            ItemPedidoIngredienteResponse(
                                nome = row[0] as String,
                                isPremium = (row[1] as Long) == 1L,
                                descricao = row[2] as String
                            )
                        }
                        if (ingredientes!!.isNotEmpty() ) {
                            ingredientes.forEach{ingrediente ->
                            ListaIngredientes.add(ingredienteDTO ?: ItemPedidoIngredienteResponse(
                                nome = ingrediente.nome,
                                isPremium = ingrediente.isPremium,
                                descricao = ingrediente.descricao
                            ))

                            }
                        }
                    }
                }

                ListaItensPedido.add(
                    ItemPedidoResponse(
                        descricao = produto?.descricao,
                        quantidade = item.quantidade,
                        precoUnitario = produtoRepository.findById(item.produto?.idProduto ?: 0)
                            .orElse(null)?.precoUnitario!!.toDouble(),
                        informacaoBolo = informacaoBolo,
                        ingredientes = ListaIngredientes
                    )
                )
            }


            pedidos = PedidoResponse(
                idPedido = pedido.id,
                dtPedido = pedido.dtPedido.toString(),
                dtEntregaEsperada = pedido.dtEntregaEsperada.toString(),
                precoTotal = pedido.precoTotal,
                isRetirada = pedido.isRetirada,
                clienteId = pedido.cliente?.id,
                enderecoId = pedido.endereco?.idEndereco,
                statusPedido = pedido.statusPedido?.descricao,
                formaPagamento = pedido.formaPagamento.toString(),
                itensPedido = ListaItensPedido // Mapeia os produtos dos itens do pedido
            )

        }
        return pedidos
    }

    fun atualizarStatusPedido(idPedido: Int, idStatusPedido: Int): PedidoResponse {
        val pedido = pedidoRepository.findById(idPedido).orElseThrow { Exception("Pedido não encontrado") }


        pedido.statusPedido = idStatusPedido?.let {
            statusPedidoRepository.findById(it).orElseThrow { Exception("Status do pedido não encontrado") }
        }
        pedidoRepository.save(pedido)

        return PedidoResponse(
            dtPedido = pedido.dtPedido.toString(),
            dtEntregaEsperada = pedido.dtEntregaEsperada.toString(),
            precoTotal = pedido.precoTotal,
            isRetirada = pedido.isRetirada,
            clienteId = pedido.cliente?.id,
            enderecoId = pedido.endereco?.idEndereco,
            statusPedido = pedido.statusPedido?.descricao,
            formaPagamento = pedido.formaPagamento.toString(),
            itensPedido = itemPedidoRepository.findByPedidoId(pedido.id!!).map { item ->
                ItemPedidoResponse(
                    descricao = item.produto?.descricao,
                    quantidade = item.quantidade,
                    precoUnitario = item.produto?.precoUnitario?.toDouble(),
                    informacaoBolo = informacaoBoloRepository.findById(item.idItemPedido!!).orElse(null),
                    ingredientes = itemPedidoIngredienteRepository.findIngredienteInItemPedido(item.idItemPedido)
                        .map { row ->
                            ItemPedidoIngredienteResponse(
                                nome = row[0] as String,
                                isPremium = (row[1] as Long) == 1L,
                                descricao = row[2] as String
                            )
                        }
                )
            }
        )
    }

    fun listarPedidosPorId(idPedido: Int): PedidoResponse {
        val pedido = pedidoRepository.findById(idPedido).orElseThrow { Exception("Pedido não encontrado") }
        return PedidoResponse(
            dtPedido = pedido.dtPedido.toString(),
            dtEntregaEsperada = pedido.dtEntregaEsperada.toString(),
            precoTotal = pedido.precoTotal,
            isRetirada = pedido.isRetirada,
            clienteId = pedido.cliente?.id,
            enderecoId = pedido.endereco?.idEndereco,
            statusPedido = pedido.statusPedido?.descricao,
            formaPagamento = pedido.formaPagamento.toString(),
            itensPedido = itemPedidoRepository.findByPedidoId(pedido.id!!).map { item ->
                ItemPedidoResponse(
                    descricao = item.produto?.descricao,
                    quantidade = item.quantidade,
                    precoUnitario = item.produto?.precoUnitario?.toDouble(),
                    informacaoBolo = informacaoBoloRepository.findById(item.idItemPedido!!).orElse(null),
                    ingredientes = itemPedidoIngredienteRepository.findIngredienteInItemPedido(item.idItemPedido)
                        .map { row ->
                            ItemPedidoIngredienteResponse(
                                nome = row[0] as String,
                                isPremium = (row[1] as Long) == 1L,
                                descricao = row[2] as String
                            )
                        }
                )
            }
        )
    }

    fun adicionarItemPedido(adicionarItemPedidoRequest: AdicionarItemPedidoRequest) {
        try{
            var pedido = pedidoRepository.findByClienteIdAndStatusPedidoIdStatusPedidoOrderByDtPedidoDesc(adicionarItemPedidoRequest.idCliente!!, 1).firstOrNull()
            if(pedido == null) {
                val cliente = usuarioRepository.findById(adicionarItemPedidoRequest.idCliente!!).orElseThrow { Exception("Cliente não encontrado") }
                pedido = Pedido(
                    dtPedido = java.time.LocalDateTime.now(),
                    cliente = cliente,
                    statusPedido = statusPedidoRepository.findById(1).orElseThrow { Exception("Status do pedido não encontrado") },
                )
                pedidoRepository.save(pedido)
            }

            val produto = produtoRepository.findById(adicionarItemPedidoRequest.idProduto!!).orElseThrow { Exception("Produto não encontrado") }

            val itemPedido = ItemPedido(
                pedido = pedido,
                produto = produto,
                quantidade = adicionarItemPedidoRequest.quantidade // Defina a quantidade padrão como 1, ou ajuste conforme necessário
            )
            pedido.precoTotal = pedido.precoTotal?.plus(produto.precoUnitario!!.toDouble() * adicionarItemPedidoRequest.quantidade!!)
            itemPedidoRepository.save(itemPedido)



            if(produto.temIngrediente!!){
                if(adicionarItemPedidoRequest.listaIngredientes!!.isNotEmpty()){
                    adicionarItemPedidoRequest.listaIngredientes?.forEach { idIngrediente ->
                        adicionarIngredienteAoItemPedido(itemPedido.idItemPedido!!, idIngrediente)
                    }
                }
            }
        }
        catch (e: Exception) {
            throw Exception("Erro ao adicionar item ao pedido: ${e.message}")
        }


    }

    fun adicionarIngredienteAoItemPedido(idItemPedido: Int, idIngrediente: Int): ItemPedidoIngredienteResponse {
        try {
            val itemPedido = itemPedidoRepository.findById(idItemPedido).orElseThrow { Exception("Item de pedido não encontrado") }
            val ingrediente = ingredienteRepository.findById(idIngrediente).orElseThrow { Exception("Ingrediente não encontrado") }

            val itemPedidoIngredienteId = ItemPedidoIngredienteId(
                itemPedidoId = itemPedido.idItemPedido!!,
                ingredienteId = ingrediente.idIngrediente!!
            )

            val itemPedidoIngrediente = ItemPedidoIngrediente(
                id = itemPedidoIngredienteId,
                itemPedido = itemPedido,
                ingrediente = ingrediente
            )
            itemPedidoIngredienteRepository.save(itemPedidoIngrediente)

            return ItemPedidoIngredienteResponse(
                nome = ingrediente.nome,
                isPremium = ingrediente.premium,
                descricao = ingrediente.nome
            )
        }
        catch (e: Exception) {
            throw Exception("Erro ao adicionar ingrediente ao item de pedido: ${e.message}")
        }


    }

    fun atualizarPedido(alterarPedidoRequest: AlterarPedidoRequest): Pedido {
        val pedido = pedidoRepository.findById(alterarPedidoRequest.idPedido!!)
            .orElseThrow { Exception("Pedido não encontrado") }

        pedido.dtEntregaEsperada = alterarPedidoRequest.dtEsperada.let {
            java.time.LocalDateTime.parse(it!!)
        }
        pedido.precoTotal = alterarPedidoRequest.precoTotal
        pedido.isRetirada = alterarPedidoRequest.retirada
        pedido.formaPagamento = alterarPedidoRequest.formaPagamento.let {
            FormaPagamentoEnum.valueOf(it!!)
        }

        return pedidoRepository.save(pedido)
    }

    fun enviarPedido(enviarPedidoRequest: EnviarPedidoRequest):Boolean{
        val pedido = pedidoRepository.findById(enviarPedidoRequest.idPedido!!)
            .orElseThrow { Exception("Pedido não encontrado") }

        val linhasAfetadas = pedidoRepository.enviarPedido(
            PedidoResponse(
                idPedido = pedido.id,
                precoTotal = enviarPedidoRequest.precoTotal,
                isRetirada = enviarPedidoRequest.isRetirada,
                formaPagamento = enviarPedidoRequest.formaPagamento?.let { FormaPagamentoEnum.valueOf(it) }.toString()
            )
        )
        return linhasAfetadas > 0;
    }
}