package sc.projetosc.Services

import SC.ProjetoSC.entity.Endereco
import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.entity.Usuario
import SC.ProjetoSC.repository.ItemPedidoRepository
import SC.ProjetoSC.repository.PedidoRepository
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventAttendee
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.EventReminder
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class GoogleCalendarServices (
    private val calendar: com.google.api.services.calendar.Calendar,
    private val itemPedidoRepository: ItemPedidoRepository,
    private val pedidoRepository: PedidoRepository
) {

    fun gerarLinkWhatsApp(telefone: String, nomeCliente: String): String {
        val mensagem = """
        Olá, $nomeCliente! Aqui é da Elê Doces, tudo bem? Estamos entrando em contato para confirmar os detalhes do seu 
        pedido e garantir que tudo esteja do jeitinho que você deseja.""".trimIndent()

        val mensagemEncoded = URLEncoder.encode(mensagem, StandardCharsets.UTF_8.toString())
        return "https://wa.me/$telefone?text=$mensagemEncoded"
    }

    fun listarHorariosOcupados(): List<String> {
        val eventos = calendar.events().list("eledocesprojeto@gmail.com")
            .setTimeMin(DateTime(System.currentTimeMillis()))
            .setMaxResults(20) // Limite de resultados, pode ser ajustado conforme necessidade
            .setSingleEvents(true)
            .setOrderBy("startTime")
            .execute()
            .items
        return eventos.map { evento ->
            val inicio = evento.start.dateTime ?: evento.start.date
            val fim = evento.end.dateTime ?: evento.end.date
            "${evento.summary} - Início: $inicio, Fim: $fim"
        }
    }


    fun agendarEvento(idPedido: Int) {
        val pedido = pedidoRepository.findById(idPedido)
            .orElseThrow { Exception("Pedido não encontrado.") }

        // Validar os dados do pedido
        val cliente = pedido.cliente ?: throw Exception("Cliente não encontrado no pedido.")
        val isRetirada = pedido.isRetirada ?: throw Exception("Informação de retirada não encontrada.")
        val endereco = pedido.endereco // Pode ser null se for retirada

        val telefoneCliente = cliente.telefone
        val nomeCliente = cliente.nome

        // Gerar link para contato via WhatsApp
        val linkWhatsApp = gerarLinkWhatsApp(telefoneCliente!!, nomeCliente!!)

        // Buscar os itens do pedido
        val itens = itemPedidoRepository.findByPedidoId(pedido.id!!)

        val descricaoProdutos = StringBuilder()
        itens.forEach {
            val nomeProduto = it.produto?.descricao ?: "Produto"
            val quantidade = it.quantidade ?: 1
            descricaoProdutos.append("- $nomeProduto x$quantidade\n")
        }

        val descricaoEvento = """
        Cliente: $nomeCliente
        Produtos: $descricaoProdutos
        Contato do cliente: $linkWhatsApp
        """.trimIndent()

        // Definir a data e horário
        val dataEntrega = pedido.dtEntregaEsperada
        val startDateTime = DateTime(dataEntrega.toString() + "-03:00") // adaptável ao seu formato
        val endDateTime = DateTime(dataEntrega?.plusHours(1).toString() + "-03:00")

        val event = Event()
            .setSummary("Pedido #${pedido.id}")
            .setDescription(descricaoEvento)
            .setLocation(if (isRetirada) "Retirada no ateliê" else "Entrega em: ${endereco?.logradouro}, ${endereco?.numero}")
            .setStart(EventDateTime().setDateTime(startDateTime).setTimeZone("America/Sao_Paulo"))
            .setEnd(EventDateTime().setDateTime(endDateTime).setTimeZone("America/Sao_Paulo"))
            .setColorId(if (isRetirada) "2" else "1")
            .setVisibility("private")

        // Insere no Google Calendar
        calendar.events().insert("eledocesprojeto@gmail.com", event).execute()
    }

    fun excluirEvento(idPedido: Int) {
        val eventos = calendar.events().list("eledocesprojeto@gmail.com")
            .setQ("Pedido #$idPedido") // buscar o evento a ser excluido pelo id dele, que fica no nome do evento
            .setSingleEvents(true)
            .execute()
            .items
        if (eventos.isEmpty()) { throw Exception("Evento não encontrado para o pedido #$idPedido.")}

        eventos.forEach { evento ->
            calendar.events().delete("eledocesprojeto@gmail.com", evento.id).execute()}
    }

    fun listarHorariosPorPeriodoDetalhado(periodo: String): Map<String, List<String>> {
        val hoje = LocalDate.now()
        val inicioPeriodo = when (periodo.lowercase()) {
            "semana" -> hoje.minusDays(7).atStartOfDay()
            "15dias" -> hoje.minusDays(15).atStartOfDay()
            "mes" -> hoje.minusMonths(1).atStartOfDay()
            else -> throw IllegalArgumentException("Período inválido. Use 'semana', '15dias' ou 'mes'.")
        }
        val fimPeriodo = hoje.atTime(23, 59, 59)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        val zoneId = java.time.ZoneId.of("America/Sao_Paulo")
        val inicioDateTime = DateTime(inicioPeriodo.atZone(zoneId).format(formatter))
        val fimDateTime = DateTime(fimPeriodo.atZone(zoneId).format(formatter))

        val eventos = calendar.events().list("eledocesprojeto@gmail.com")
            .setTimeMin(inicioDateTime)
            .setTimeMax(fimDateTime)
            .setSingleEvents(true)
            .setOrderBy("startTime")
            .execute()
            .items

        val horariosOcupados = eventos.map { evento ->
            val inicio = evento.start.dateTime ?: evento.start.date
            val fim = evento.end.dateTime ?: evento.end.date
            inicio.value to fim.value
        }

        val horariosLivres = mutableListOf<Pair<Long, Long>>()
        var ultimoFim = inicioDateTime.value

        for ((inicioEvento, fimEvento) in horariosOcupados) {
            if (inicioEvento > ultimoFim) {
                horariosLivres.add(ultimoFim to inicioEvento)
            }
            ultimoFim = maxOf(ultimoFim, fimEvento)
        }
        if (ultimoFim < fimDateTime.value) {
            horariosLivres.add(ultimoFim to fimDateTime.value)
        }

        val blocosDisponiveis = mutableMapOf<String, MutableList<String>>()
        val formatterDia = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formatterHora = DateTimeFormatter.ofPattern("HH:mm")

        for ((inicio, fim) in horariosLivres) {
            var blocoInicio = inicio
            while (blocoInicio < fim) {
                val blocoFim = minOf(blocoInicio + 30 * 60 * 1000, fim) // 30 minutos em milissegundos
                val dia = LocalDate.ofEpochDay(blocoInicio / (24 * 60 * 60 * 1000))
                    .format(formatterDia)
                val horaInicio = LocalDateTime.ofEpochSecond(blocoInicio / 1000, 0, zoneId.rules.getOffset(Instant.ofEpochMilli(blocoInicio)))
                    .format(formatterHora)
                val horaFim = LocalDateTime.ofEpochSecond(blocoFim / 1000, 0, zoneId.rules.getOffset(Instant.ofEpochMilli(blocoFim)))
                    .format(formatterHora)

                blocosDisponiveis.computeIfAbsent(dia) { mutableListOf() }
                    .add("[$horaInicio] [$horaFim]")

                blocoInicio = blocoFim
            }
        }

        return blocosDisponiveis
    }
}
