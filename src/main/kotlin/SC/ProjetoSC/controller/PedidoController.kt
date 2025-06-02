package SC.ProjetoSC.controller

import SC.ProjetoSC.Request.AdicionarItemPedidoRequest
import SC.ProjetoSC.Response.PedidoResponse
import SC.ProjetoSC.Services.PedidoServices
import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.repository.PedidoRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Pedidos", description = "Operações relacionadas a pedidos do sistema")
@RestController
@RequestMapping("/pedidos")
class PedidoController(
    val repositorio: PedidoRepository,
    val pedidoServices: PedidoServices
) {

    // exemplo de pedido mais caro, só para teste

//    var maisCaro = Pedido(3, LocalDateTime.parse("2025-05-21T09:00:00"), LocalDateTime.parse("2025-05-30T10:00:00"),
//        StatusPagamento.PAGO, 600.00, false)
//    @GetMapping("/mais-caro")
//    fun maisCaro(): Pedido {
//        return maisCaro
//    }



    // lista todos os pedidos ou filtra por data do pedido realizado ou data de entrega do pedido!
//    @GetMapping
//    @Operation(summary = "Listar todos os pedidos", description = "Retorna uma lista com todos os pedidos registrados no sistema.")
//    @ApiResponses(value = [
//        ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso. O corpo da resposta contém os dados dos pedidos."),
//        ApiResponse(responseCode = "204", description = "Nenhum pedido encontrado com os critérios informados. O corpo da resposta estará vazio.")
//    ])
//    fun lista(@RequestParam(required = false) dtPedido: LocalDateTime?, @RequestParam(required = false) dtEntrega: LocalDateTime?):
//            ResponseEntity<List<Pedido>> {
//        val listaPedidos = pedidoServices.listarPedidos(dtPedido, dtEntrega);
//        return if (listaPedidos.isEmpty()) {
//            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
//        } else {
//            ResponseEntity.status(HttpStatus.OK).body(listaPedidos)
//        }
//    }

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Retorna uma lista de pedidos, podendo filtrar por ID do usuário.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Lista de pedidos retornada com sucesso. O corpo da resposta contém os dados dos pedidos."),
        ApiResponse(responseCode = "204", description = "Nenhum pedido encontrado. O corpo da resposta estará vazio.")
    ])
    fun listarPedidos(@RequestParam(required = false) idUsuario: Int?): ResponseEntity<PedidoResponse> {
        val pedido = pedidoServices.getPedidoAtual(idUsuario)
        return if (pedido.clienteId == null) {
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        } else {
            ResponseEntity.status(HttpStatus.OK).body(pedido)
        }
    }

    @PatchMapping("/{idPedido}/status/{idStatus}")
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
        ApiResponse(responseCode = "201", description = "Produto adicionado ao pedido com sucesso. O corpo da resposta contém os dados do pedido atualizado."),
        ApiResponse(responseCode = "404", description = "Pedido ou produto não encontrado. O corpo da resposta estará vazio.")
    ])
    fun adicionarProdutoAoPedido(@RequestBody adicionarItemPedidoRequest: AdicionarItemPedidoRequest): ResponseEntity<Any>{
        try {
            pedidoServices.adicionarItemPedido(adicionarItemPedidoRequest)
            return ResponseEntity.status(HttpStatus.CREATED).build()
        }
        catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.localizedMessage)
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido", description = "Retorna o pedido atualizado.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Pedido atualizado com sucesso. O corpo da resposta contém os dados do pedido atualizado"),
        ApiResponse(responseCode = "404", description = "Pedido não encontrado. O corpo da resposta estará vazio.")
    ])
    fun atualizar(@PathVariable id: Int, @RequestBody @Valid pedidoAtualizado: Pedido): ResponseEntity<Pedido> {

        // verificar se o pedido existe
        if (!repositorio.existsById(id)) {
            return ResponseEntity.status(404).build()
        }
        // garante que o ID na url será usado, mesmo se pedidoAtualizado.id vier diferente ou nulo, assim evita sobreescrever outro pedido sem querer
        val pedidoAtt = pedidoAtualizado.copy(id = id)
        repositorio.save(pedidoAtt)
        return ResponseEntity.status(200).body(pedidoAtt)
    }

    @DeleteMapping("/{idPedido}")
    @Operation(summary = "Excluir pedido", description = "Retorna o status 204 caso pedido excluído com sucesso.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Pedido excluído com sucesso. O corpo da resposta estará vazio."),
        ApiResponse(responseCode = "404", description = "Pedido não encontrado. O corpo da resposta estará vazio.")
    ])
    fun cancelarPedido(@PathVariable idPedido: Int): ResponseEntity<Void>{
        val pedido = repositorio.findById(idPedido)
        if (pedido.isEmpty) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        pedidoServices.atualizarStatusPedido(idPedido, 8) // 8 é o ID do status "Cancelado"
        return ResponseEntity.status(HttpStatus.OK).build()
    }
}