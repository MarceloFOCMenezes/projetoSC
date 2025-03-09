package SC.ProjetoSC

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
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/pedidos")

class PedidoController {

    val pedidos = mutableListOf<Pedido>(
        Pedido(0, LocalDateTime.parse("2020-10-02T18:13:00"), LocalDateTime.parse("2020-10-15T15:00:00"),
            StatusPagamento.PENDENTE, 300.00, true),
        Pedido(1, LocalDateTime.parse("2025-04-01T10:00:00"), LocalDateTime.parse("2025-04-11T16:00:00"),
            StatusPagamento.PAGO, 450.00, false),
        Pedido(2, LocalDateTime.parse("2025-03-16T12:00:00"), LocalDateTime.parse("2025-03-21T10:00:00"),
            StatusPagamento.NAO_PAGO, 350.00, false)
    )

    var maisCaro = Pedido(3, LocalDateTime.parse("2025-05-21T09:00:00"), LocalDateTime.parse("2025-05-30T10:00:00"),
        StatusPagamento.PAGO, 600.00, false)


    @GetMapping("/mais-caro")
    fun maisCaro(): Pedido {
        return maisCaro
    }

    @GetMapping
    fun lista(@RequestParam(required = false) dtPedido: LocalDateTime?, @RequestParam(required = false) dtEntrega: LocalDateTime?):
            ResponseEntity<List<Pedido>> {

        if (dtPedido == null && dtEntrega == null) {
            if (pedidos.isEmpty()) {
                return ResponseEntity.status(204).build()
                
            }
            return ResponseEntity.status(200).body(pedidos)

        }

        val dtPedidoFiltrada = dtPedido != null
        val dtEntregaFiltrada = dtEntrega != null

        val listaPedido = mutableListOf<Pedido>()

        if (dtPedidoFiltrada && dtEntregaFiltrada){
            listaPedido.addAll(pedidos.filter {
                it.dtPedido == dtPedido && it.dtEntrega!! >= dtEntrega!!
            })
        } else if (dtPedidoFiltrada) {
                listaPedido.addAll(pedidos.filter { it.dtPedido == dtPedido })
        } else {
            listaPedido.addAll(pedidos.filter { it.dtEntrega!! >= dtEntrega!! })
        }
        if (listaPedido.isEmpty()) {
            return ResponseEntity.status(204).build()
        }
        return ResponseEntity.status(200).body(listaPedido)
    }


    @PostMapping
    fun criarPedido(@RequestBody novoPedido: Pedido): ResponseEntity<Pedido>{
        pedidos.add(novoPedido)
        return ResponseEntity.status(201).body(novoPedido)
    }

    @PutMapping("/{id}")
    fun atualizar(@PathVariable id: Int, @RequestBody pedidoAtualizado: Pedido): ResponseEntity<Pedido> {
        pedidos[id] = pedidoAtualizado
        return ResponseEntity.status(200).build()
    }

    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Pedido>{

        if (id <= 0){
            return ResponseEntity.status(404).build()
        }
        return ResponseEntity.status(200).build()
    }



}