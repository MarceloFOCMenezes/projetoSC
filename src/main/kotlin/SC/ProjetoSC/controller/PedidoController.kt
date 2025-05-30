package SC.ProjetoSC.controller

import SC.ProjetoSC.DTO.PedidoDto
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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    fun listarPedidos(@RequestParam(required = false) idUsuario: Int?): ResponseEntity<PedidoDto> {
        val pedido = pedidoServices.listarPedidos(idUsuario)
        return if (pedido.itensPedido!!.isEmpty()) {
            ResponseEntity.status(HttpStatus.NO_CONTENT).build()
        } else {
            ResponseEntity.status(HttpStatus.OK).body(pedido)
        }
    }

//    @PostMapping
//    @Operation(summary = "Adicionar um novo pedido", description = "Retorna o pedido criado.")
//    @ApiResponse(responseCode = "201", description = "Pedido adicionado com sucesso. O corpo da resposta contém os dados do pedido criado.")
//    fun criarPedido(@RequestBody @Valid novoPedido: RequestPedidoDTO): ResponseEntity<Any> {
//        val pedido = pedidoServices.criarPedido(novoPedido)
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(pedido)
//    }

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

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pedido", description = "Retorna o status 204 caso pedido excluído com sucesso.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Pedido excluído com sucesso. O corpo da resposta estará vazio."),
        ApiResponse(responseCode = "404", description = "Pedido não encontrado. O corpo da resposta estará vazio.")
    ])
    fun excluir(@PathVariable id: Int): ResponseEntity<Void>{
        if (!repositorio.existsById(id)){
            return ResponseEntity.status(404).build()
        }
        repositorio.deleteById(id)
        return ResponseEntity.status(204).build()
    }
}