package sc.projetosc.controller

import  sc.projetosc.Response.PedidoSemanaResponse
import sc.projetosc.request.AdicionarItemPedidoRequest
import sc.projetosc.request.EnviarPedidoRequest
import sc.projetosc.Response.PedidoResponse
import sc.projetosc.services.PedidoServices
import sc.projetosc.repository.PedidoRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sc.projetosc.services.GoogleCalendarServices

@Tag(name = "Pedidos", description = "Operações relacionadas a pedidos do sistema")
@RestController
@RequestMapping("/pedidos")
class PedidoController(
    val repositorio: PedidoRepository,
    val pedidoServices: PedidoServices,
    val googleCalendarServices: GoogleCalendarServices
) {


    @GetMapping("/PedidosData")
    @Operation(summary = "Lista pedidos por data")
    @ApiResponses(value =[
        ApiResponse(responseCode = "200", description = "Pedidos por Data"),
    ])
    fun listarPedidosData(@RequestParam data: String): ResponseEntity<List<PedidoResponse>> {
        return try {
            val pedidosData = pedidoServices.listarPedidosPorData(data)
            ResponseEntity.status(HttpStatus.OK).body(pedidosData)
        }
        catch (e:Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/PedidosStatus")
    @Operation(summary = "Lista pedidos por data")
    @ApiResponses(value =[
        ApiResponse(responseCode = "200", description = "Pedidos por Data"),
    ])
    fun listarPedidosStatus(@RequestParam idStatus: Int): ResponseEntity<List<PedidoResponse>> {
        return try {
            val pedidosStatus = pedidoServices.listarPedidosPorStatus(idStatus)
            ResponseEntity.status(HttpStatus.OK).body(pedidosStatus)
        }
        catch (e:Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
    @GetMapping("/PedidosSemanaData")
    @Operation(summary = "Lista pedidos semana com data")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Pedidos Semana")
    ])
    fun listarPedidosSemana(@RequestParam data:String): ResponseEntity<List<PedidoSemanaResponse>> {
        return try{
            val semanaPedido = pedidoServices.getSemana(data)
            ResponseEntity.status(HttpStatus.OK).body((semanaPedido))
        }
        catch (e: Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }


    @GetMapping("/PedidosSemana")
    @Operation(summary = "Lista pedidos semana")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Pedidos Semana")
    ])
    fun listarPedidosSemana(): ResponseEntity<List<PedidoSemanaResponse>> {
        return try{
            val semanaPedido = pedidoServices.PedidoSemana()
            ResponseEntity.status(HttpStatus.OK).body((semanaPedido))
        }
        catch (e: Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/DiasPedidos")
    @Operation(summary = "Listar dias com pedidos", description = "Retorna uma lista de dias que possuem pedidos registrados com o id 4.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de dias retornada com sucesso. O corpo da resposta contém os dias com pedidos."),
        ApiResponse(responseCode = "204", description = "Nenhum dia com pedidos encontrado. O corpo da resposta estará vazio.")
    ])
    fun listarDiasComPedidos(): ResponseEntity<List<String>> {
        return try {
            val diasComPedidos = pedidoServices.findDiasLotados()
            if (diasComPedidos.isEmpty()) {
                ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            } else {
                ResponseEntity.status(HttpStatus.OK).body(diasComPedidos)
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }


    @GetMapping("/carrinho")
    @Operation(summary = "Listar pedidos", description = "Retorna uma lista de pedidos, podendo filtrar por ID do usuário.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso. O corpo da resposta contém os dados dos pedidos."),
        ApiResponse(responseCode = "204", description = "Nenhum pedido encontrado. O corpo da resposta estará vazio.")
    ])
    fun listarPedidos(@RequestParam(required = false) idUsuario: Int?): ResponseEntity<PedidoResponse> {
        try {
            if (idUsuario == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(PedidoResponse())
            }
            val pedido = pedidoServices.listarPedidoAtual(idUsuario)
            return if (pedido.idPedido == 0) {
                ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            } else {
                ResponseEntity.status(HttpStatus.OK).body(pedido)
            }
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(PedidoResponse())
        }
    }

    @DeleteMapping("/desabilitarItemPedido/{idItemPedido}")
    @Operation(summary = "Desabilitar Item Pedido", description = "Desabilita um item do pedido específico.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Item do pedido desabilitado com sucesso."),
        ApiResponse(responseCode = "204", description = "Nenhum item do pedido encontrado. O corpo da resposta estará vazio.")
    ])
    fun desabilidarItemPedido(@PathVariable idItemPedido: Int): ResponseEntity<Void> {
        pedidoServices.desabilitarItemPedido(idItemPedido)
        return ResponseEntity.status(HttpStatus.OK).build()
    }




    @GetMapping("/pendentes")
    @Operation(summary = "Listar pedidos pendentes", description = "Retorna uma lista de pedidos pendentes para a confeiteira (status 2, 3 e 4).")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de pedidos pendentes retornada com sucesso. O corpo da resposta contém os dados dos pedidos (pode ser um array vazio se não houver pedidos pendentes).")
    ])
    fun listarPedidosPendentes(): ResponseEntity<List<PedidoResponse>> {
        try {
            val pedidosPendentes = pedidoServices.listarPedidosPendentes()
            // Sempre retorna 200 com um array (mesmo que vazio) para facilitar o tratamento no frontend
            return ResponseEntity.status(HttpStatus.OK).body(pedidosPendentes)
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/{idPedido}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedido específico pelo seu ID.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Pedido encontrado com sucesso. O corpo da resposta contém os dados do pedido."),
        ApiResponse(responseCode = "404", description = "Pedido não encontrado. O corpo da resposta estará vazio.")
    ])
    fun buscarPedidoPorId(@PathVariable idPedido: Int): ResponseEntity<PedidoResponse> {
        return try {
            val pedido = pedidoServices.listarPedidosPorId(idPedido)
            ResponseEntity.status(HttpStatus.OK).body(pedido)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PatchMapping("/alterarStatus/{idPedido}/status/{idStatus}")
    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido específico.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Status do pedido atualizado com sucesso. O corpo da resposta contém os dados do pedido atualizado."),
        ApiResponse(responseCode = "404", description = "Pedido não encontrado. O corpo da resposta estará vazio.")
    ])
    fun atualizarStatus(@PathVariable idPedido: Int, @PathVariable idStatus: Int): ResponseEntity<PedidoResponse> {
        val pedido = repositorio.findById(idPedido)
        if (pedido.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        val pedidoAtualizado = pedidoServices.atualizarStatusPedido(idPedido, idStatus)
        return ResponseEntity.status(HttpStatus.OK).body(pedidoAtualizado)
    }

//    @PostMapping
//    @Operation(summary = "Adicionar um novo pedido", description = "Retorna o pedido criado.")
//    @ApiResponse(responseCode = "201", description = "Pedido adicionado com sucesso. O corpo da resposta contém os dados do pedido criado.")
//    fun criarPedido(@RequestBody @Valid novoPedido: RequestPedidoDTO): ResponseEntity<Any> {
//        val pedido = pedidoServices.criarPedido(novoPedido)
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(pedido)
//    }

    @PostMapping("/adicionarProduto")
    @Operation(summary = "Adicionar produto ao pedido", description = "Adiciona um produto a um pedido existente.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Produto adicionado ao pedido com sucesso. O corpo da resposta contém os dados do pedido atualizado." +
                " e o status atualizado."),
        ApiResponse(responseCode = "404", description = "Pedido ou produto não encontrado. O corpo da resposta estará vazio." +
                " Verifique se o ID do pedido e do produto estão corretos."),
    ])
    fun adicionarProdutoAoPedido(@RequestBody adicionarItemPedidoRequest: AdicionarItemPedidoRequest): ResponseEntity<Any>{
        try {
            val pedido = pedidoServices.adicionarItemPedido(adicionarItemPedidoRequest)
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido)
        }
        catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.localizedMessage)
        }
    }

    @DeleteMapping("/{idPedido}")
    @Operation(summary = "Excluir pedido", description = "Retorna o status 204 caso pedido excluído com sucesso.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Pedido excluído com sucesso. O corpo da resposta estará vazio." ),
        ApiResponse(responseCode = "404", description = "Pedido não encontrado. O corpo da resposta estará vazio." +
                " Verifique se o ID do pedido está correto."),
    ])
    fun cancelarPedido(@PathVariable idPedido: Int): ResponseEntity<Void>{
        val pedido = repositorio.findById(idPedido)
        if (pedido.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        try {
            googleCalendarServices.excluirEvento(idPedido)// Exclui o evento do Google Calendar
            pedidoServices.atualizarStatusPedido(idPedido, 8) // 8 é o ID do status "Cancelado"
            return ResponseEntity.status(HttpStatus.OK).build()

        }
        catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PatchMapping("/enviarPedido")
    @Operation(
        summary = "Enviar pedido para processamento",
        description = "Atualiza o pedido para o status de enviado, processando a entrega ou retirada conforme informado."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Pedido enviado com sucesso. O corpo da resposta contém os dados do pedido atualizado." +
                    " e o status atualizado."),
            ApiResponse(responseCode = "404", description = "Pedido não encontrado. O corpo da resposta estará vazio." +
                    " Verifique se o ID do pedido está correto."),
            ApiResponse(responseCode = "500", description = "Erro interno ao processar o envio do pedido." +
                    " O corpo da resposta contém a mensagem de erro detalhada.")
        ]
    )

    fun enviarPedido(@RequestBody EnviarPedidoRequest: EnviarPedidoRequest): ResponseEntity<Any> {
        val pedido = repositorio.findById(EnviarPedidoRequest.idPedido!!)
        if (pedido.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        try {
            val pedidoAtualizado = pedidoServices.enviarPedido(EnviarPedidoRequest)
            return ResponseEntity.status(HttpStatus.OK).body(pedidoAtualizado)
        }
        catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.localizedMessage)
        }
    }

    @GetMapping("/listar")
    @Operation(
        summary = "Listar todos os pedidos do usuário",
        description = "Retorna uma lista de todos os pedidos associados ao ID do usuário fornecido."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso. O corpo da resposta contém os dados dos pedidos."),
            ApiResponse(responseCode = "204", description = "Nenhum pedido encontrado para o usuário. O corpo da resposta estará vazio."),
            ApiResponse(responseCode = "500", description = "Erro interno ao processar a solicitação.")
        ]
    )
    fun listarPedidosDoUsuario(@RequestParam idUsuario: Int): ResponseEntity<List<PedidoResponse>> {
        return try {
            val pedidos = pedidoServices.listarPedidosPorUsuario(idUsuario)
            if (pedidos.isEmpty()) {
                ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            } else {
                ResponseEntity.status(HttpStatus.OK).body(pedidos)
            }
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}