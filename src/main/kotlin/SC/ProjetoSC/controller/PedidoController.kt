package SC.ProjetoSC.controller

import SC.ProjetoSC.entity.StatusPagamento
import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.repository.PedidoRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/pedidos")
class PedidoController (val repositorio: PedidoRepository) {

//    var maisCaro = Pedido(3, LocalDateTime.parse("2025-05-21T09:00:00"), LocalDateTime.parse("2025-05-30T10:00:00"),
//        StatusPagamento.PAGO, 600.00, false)
//    @GetMapping("/mais-caro")
//    fun maisCaro(): Pedido {
//        return maisCaro
//    }


    // lista todos os pedidos ou filtra por data do pedido realizado ou data de entrega do pedido!
    @GetMapping
    fun lista(@RequestParam(required = false) dtPedido: LocalDateTime?, @RequestParam(required = false) dtEntrega: LocalDateTime?):
            ResponseEntity<List<Pedido>> {

        // definindo o conteúdo da lista
        val pedidos: List<Pedido> = when {
            dtPedido != null && dtEntrega != null -> repositorio.findByDtPedidoAndDtEntregaGreaterThanEqual(dtPedido, dtEntrega)

            dtPedido != null -> repositorio.findByDtPedido(dtPedido)

            dtEntrega != null -> repositorio.findByDtEntregaGreaterThanEqual(dtEntrega)

            else -> repositorio.findAll()
        }

        // definindo o resultado retornado de acordo com oq tem na lista
        return if (pedidos.isEmpty()) {
            ResponseEntity.status(204).build()
        } else {
            ResponseEntity.status(200).body(pedidos)
        }
    }

    @PostMapping
    fun criarPedido(@RequestBody @Valid novoPedido: Pedido): ResponseEntity<Pedido> {
        val pedidoCriado = repositorio.save(novoPedido)
        return ResponseEntity.status(201).body(pedidoCriado)
    }

    @PutMapping("/{id}")
    fun atualizar(@PathVariable id: Int, @RequestBody @Valid pedidoAtualizado: Pedido): ResponseEntity<Pedido> {

        // verificar se o pedido existe
        if (!repositorio.existsById(id)) {
            return ResponseEntity.status(404).build()
        }
        // garante que o ID na url será uasdo, mesmo se pedidoAtualizado.id vier diferente ou nulo, assim evita sobreescrever outro pedido sem querer
        val pedidoAtt = pedidoAtualizado.copy(id = id)
        repositorio.save(pedidoAtt)
        return ResponseEntity.status(200).body(pedidoAtt)
    }

    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Int): ResponseEntity<Void>{
        if (!repositorio.existsById(id)){
            return ResponseEntity.status(404).build()
        }
        repositorio.deleteById(id)
        return ResponseEntity.status(200).build()
    }
}