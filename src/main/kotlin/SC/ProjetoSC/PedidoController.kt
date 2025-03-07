package SC.ProjetoSC

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/pedidos")

class PedidoController {

    val pedidos = mutableListOf<Pedido>(
        Pedido(LocalDateTime.parse("2020-10-02T18:13:00"), LocalDateTime.parse("2020-10-15T15:00:00"),
            false, 300.00, true),
        Pedido(LocalDateTime.parse("2025-04-01T10:00:00"), LocalDateTime.parse("2025-04-11T16:00:00"),
            true, 450.00, false),
        Pedido(LocalDateTime.parse("2025-03-16T12:00:00"), LocalDateTime.parse("2025-03-21T10:00:00"),
            true, 350.00, false)
    )

    var maisCaro = Pedido(LocalDateTime.parse("2025-05-21T09:00:00"), LocalDateTime.parse("2025-05-30T10:00:00"),
        true, 600.00, false)


    @GetMapping("/mais-caro")
    fun maisCaro(): Pedido {
        return maisCaro
    }

    @GetMapping  // /herois ou /herois?classe=A ou /herois?classe=C&forcaMinima=7000 ou /herois?forcaMinima=7000&classe=C ou /herois?forcaMinima=7000
    fun lista(@RequestParam(required = false) dtPedido: LocalDateTime?, @RequestParam(required = false) dtEntrega: LocalDateTime?): ResponseEntity<List<Pedido>> {


        if (dtPedido == null && dtEntrega == null) {
            if (pedidos.isEmpty()) {
                return ResponseEntity.status(204).build()
                // Aqui eu estou retornando uma resposta e Sem corpo de resposta buil()
            }
            return ResponseEntity.status(200).body(pedidos)
            /*
            Retornando com o status 200 e a lista 'herois' no comando
             */
        }
        return ResponseEntity.status(200).body(pedidos)
    }


}