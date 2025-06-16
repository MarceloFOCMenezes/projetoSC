package SC.ProjetoSC.controller

import SC.ProjetoSC.Request.AdicionarItemPedidoRequest
import SC.ProjetoSC.Request.EnviarPedidoRequest
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
}