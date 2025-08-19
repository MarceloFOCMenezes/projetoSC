package sc.projetosc.Services

import SC.ProjetoSC.Enum.FormaPagamentoEnum
import SC.ProjetoSC.entity.Endereco
import SC.ProjetoSC.entity.Pedido
import SC.ProjetoSC.entity.Usuario
import SC.ProjetoSC.repository.*
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
import java.time.ZoneId

@Service
class GoogleCalendarServices (
    private val calendar: com.google.api.services.calendar.Calendar,
    private val itemPedidoRepository: ItemPedidoRepository,
    private val pedidoRepository: PedidoRepository,
    private val clienteRepository: UsuarioRepository,
    private val enderecoRepository: EnderecoRepository,
    private val statusRepository: StatusPedidoRepository
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
        val zoneId = ZoneId.of("America/Sao_Paulo")
        val startDateTime = pedido.dtEntregaEsperada?.atZone(zoneId)?.toInstant()?.toEpochMilli() ?: throw Exception("Data de entrega inválida.")
        val endDateTime = pedido.dtEntregaEsperada?.plusHours(1)?.atZone(zoneId)?.toInstant()?.toEpochMilli() ?: throw Exception("Data de entrega inválida.")

        // Verificar disponibilidade na agenda
        val eventos = calendar.events().list("eledocesprojeto@gmail.com")
            .setTimeMin(DateTime(startDateTime))
            .setTimeMax(DateTime(endDateTime))
            .setSingleEvents(true)
            .setOrderBy("startTime")
            .execute()
            .items

        if (eventos.any { evento ->
                val eventoInicio = evento.start.dateTime?.value ?: evento.start.date.value
                val eventoFim = evento.end.dateTime?.value ?: evento.end.date.value
                (startDateTime < eventoFim && endDateTime > eventoInicio) // Verifica sobreposição
            }) {
            throw Exception("Não há disponibilidade de pelo menos 30 minutos na agenda para este horário.")
        }

        // Criar evento no Google Calendar
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        val startDateTimeFormatted = DateTime(pedido.dtEntregaEsperada?.atZone(zoneId)?.format(formatter))
        val endDateTimeFormatted = DateTime(pedido.dtEntregaEsperada?.plusHours(1)?.atZone(zoneId)?.format(formatter))

        val event = Event()
            .setSummary("Pedido #${pedido.id}")
            .setDescription(descricaoEvento)
            .setLocation(if (isRetirada) "Retirada no ateliê" else "Entrega em: ${endereco?.logradouro}, ${endereco?.numero}")
            .setStart(EventDateTime().setDateTime(startDateTimeFormatted).setTimeZone("America/Sao_Paulo"))
            .setEnd(EventDateTime().setDateTime(endDateTimeFormatted).setTimeZone("America/Sao_Paulo"))
            .setColorId(if (isRetirada) "2" else "1")
            .setVisibility("private")

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

    fun listarHorariosDisponiveisPorPeriodo(periodo: String): Map<String, List<String>> {
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

    fun listarPedidosPorPeriodo(periodo: String): Map<String, List<Pedido>> {
        val hoje = LocalDate.now()
        val inicioPeriodo = when (periodo.lowercase()) {
            "dia" -> hoje.atStartOfDay()
            "semana" -> hoje.minusDays(7).atStartOfDay()
            "quinzena" -> hoje.minusDays(15).atStartOfDay()
            "mes" -> hoje.minusMonths(1).atStartOfDay()
            else -> throw IllegalArgumentException("Período inválido. Use 'dia', 'semana', 'quinzena' ou 'mes'.")
        }
        val fimPeriodo = hoje.atTime(23, 59, 59)

        val pedidos = pedidoRepository.findAllByDtEntregaEsperadaBetween(
            inicioPeriodo, fimPeriodo
        ).sortedBy { it.dtEntregaEsperada }

        val pedidosAgrupados = pedidos.groupBy { pedido ->
            pedido.dtEntregaEsperada?.toLocalDate().toString()
        }
        return pedidosAgrupados
    }

    // -----------------------------------------------------------------------------------------------
    // funcao de teste, criar 60 pedido pra popular a agenda e facilitar os testes
    fun criarPedidosEAgendar(): List<Pedido> {
        val pedidosCriados = mutableListOf<Pedido>()
        val hoje = LocalDate.now()
        val clienteId = 1 // ID do cliente padrão
        val enderecoId = 1 // ID do endereço padrão
        val statusId = 1 // Status inicial do pedido

        // Gerar pedidos até dezembro
        var totalPedidos = 0
        for (i in 1..60) {
            val dataEntrega = hoje.plusDays(i.toLong())
            if (dataEntrega.monthValue > 12) break // Garantir que não ultrapasse dezembro

            // Gerar uma quantidade aleatória de pedidos para o dia (entre 1 e 7)
            val quantidadePedidos = (1..7).random()

            for (j in 1..quantidadePedidos) {
                var horarioEntrega = dataEntrega.atTime(10 + j, 0) // Horários variando a cada hora
                var tentativa = 0
                var eventoCriado = false

                while (tentativa < 10) { // Tentar até 10 horários diferentes no mesmo dia
                    try {
                        val pedido = Pedido(
                            cliente = clienteRepository.findById(clienteId).orElseThrow { Exception("Cliente não encontrado.") },
                            endereco = enderecoRepository.findById(enderecoId).orElseThrow { Exception("Endereço não encontrado.") },
                            statusPedido = statusRepository.findById(statusId).orElseThrow { Exception("Status não encontrado.") },
                            dtPedido = LocalDateTime.now(),
                            dtEntregaEsperada = horarioEntrega,
                            precoTotal = 100.0 + totalPedidos, // Preço variável
                            isRetirada = false,
                            formaPagamento = if (totalPedidos % 2 == 0) FormaPagamentoEnum.pix else FormaPagamentoEnum.debito
                        )

                        // Salvar pedido no banco
                        pedidoRepository.save(pedido)
                        pedidosCriados.add(pedido)

                        // Criar evento no Google Calendar
                        agendarEvento(pedido.id!!)
                        totalPedidos++
                        eventoCriado = true
                        break
                    } catch (e: Exception) {
                        // Tentar próximo horário disponível
                        horarioEntrega = horarioEntrega.plusMinutes(30)
                        tentativa++
                    }
                }

                if (!eventoCriado) {
                    println("Não foi possível agendar o pedido no dia ${dataEntrega}.")
                }
            }
        }
        return pedidosCriados
    }
//    // excluir todos os pedidos do banco e da agenda
//    fun excluirEventosDePedidos() {
//        val eventos = calendar.events().list("eledocesprojeto@gmail.com")
//            .setQ("Pedido") // Filtra eventos que contenham "Pedido" no título
//            .setSingleEvents(true)
//            .execute()
//            .items
//
//        eventos.forEach { evento ->
//            if (evento.summary?.contains("Pedido") == true) {
//                calendar.events().delete("eledocesprojeto@gmail.com", evento.id).execute()
//            }
//        }
//    }
    // todo: continuar o get de listar pedidos por periodo, pq ta retornando vazio
}
