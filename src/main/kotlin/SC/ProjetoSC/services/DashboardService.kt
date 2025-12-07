package sc.projetosc.services

import sc.projetosc.repository.PedidoRepository
import sc.projetosc.repository.IngredienteRepository
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import sc.projetosc.dto.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Service
class DashboardService(
    private val pedidoRepository: PedidoRepository,
    private val ingredienteRepository: IngredienteRepository
) {
    private val logger = LoggerFactory.getLogger(DashboardService::class.java)

    fun getDashboardOverview(): DashboardOverviewDTO {
        return try {
            val pedidosHoje = pedidoRepository.countPedidosHoje() ?: 0
            val pendentes = pedidoRepository.countPedidosPendentes() ?: 0
            val produzindo = pedidoRepository.countPedidosProduzindo() ?: 0
            val concluidos = pedidoRepository.countPedidosConcluidos() ?: 0

            DashboardOverviewDTO(
                pedidosHoje = pedidosHoje,
                pendentes = pendentes,
                produzindo = produzindo,
                concluidos = concluidos
            )
        } catch (e: Exception) {
            println("Erro ao buscar dados do overview: ${e.message}")
            throw RuntimeException("Erro ao processar dados do dashboard", e)
        }
    }

    fun getTop5Ingredientes(dataInicio: String?, dataFim: String?): RankingIngredientesPorTipoDTO {
        return try {
            logger.info("Buscando top 5 ingredientes por tipo")
            
            // Buscar ingredientes por tipo
            val massa = getTop5PorTipo("massa", dataInicio, dataFim)
            val recheio = getTop5PorTipo("cobertura", dataInicio, dataFim)
            val adicional = getTop5PorTipo("adicionais", dataInicio, dataFim)
            
            logger.info("Top ingredientes encontrados - Massa: ${massa.size}, Recheio: ${recheio.size}, Adicional: ${adicional.size}")
            
            RankingIngredientesPorTipoDTO(
                massa = massa,
                recheio = recheio,
                adicional = adicional
            )
        } catch (e: Exception) {
            logger.error("Erro ao buscar top 5 ingredientes", e)
            RankingIngredientesPorTipoDTO(
                massa = emptyList(),
                recheio = emptyList(),
                adicional = emptyList()
            )
        }
    }
    
    private fun getTop5PorTipo(tipo: String, dataInicio: String?, dataFim: String?): List<IngredientePorTipoDTO> {
        return try {
            val resultados = if (dataInicio != null && dataFim != null) {
                // Validar formato das datas
                if (!isValidDateFormat(dataInicio) || !isValidDateFormat(dataFim)) {
                    throw IllegalArgumentException("Formato de data inválido")
                }
                ingredienteRepository.findTop5IngredientesPorTipoComPeriodo(tipo, dataInicio, dataFim)
            } else {
                ingredienteRepository.findTop5IngredientesPorTipo(tipo)
            }

            if (resultados.isEmpty()) {
                // Se não houver ingredientes com pedidos, buscar todos do tipo
                val semPedidos = ingredienteRepository.findIngredientesPorTipoSemPedidos(tipo)
                return semPedidos.take(5).mapIndexed { index, array ->
                    val id = (array[0] as Number).toInt()
                    val nome = array[1]?.toString() ?: "Ingrediente sem nome"
                    val tipoIngrediente = array[2]?.toString() ?: tipo
                    val premium = when (val premiumValue = array[3]) {
                        is Number -> premiumValue.toInt() == 1
                        is Boolean -> premiumValue
                        else -> false
                    }

                    IngredientePorTipoDTO(
                        id = id,
                        nome = nome,
                        tipoIngrediente = tipoIngrediente,
                        quantidadePedidos = 0,
                        posicao = index + 1,
                        premium = premium
                    )
                }
            }
            
            // Calcular total para percentuais
            val total = resultados.sumOf { (it[4] as Number).toLong() }.toDouble()

            resultados.take(5).mapIndexed { index, array ->
                val id = (array[0] as Number).toInt()
                val nome = array[1]?.toString() ?: "Ingrediente sem nome"
                val tipoIngrediente = array[2]?.toString() ?: tipo
                val premium = when (val premiumValue = array[3]) {
                    is Number -> premiumValue.toInt() == 1
                    is Boolean -> premiumValue
                    else -> false
                }
                val quantidade = (array[4] as Number).toInt()
                val percentual = if (total > 0) (quantidade / total) * 100 else 0.0

                IngredientePorTipoDTO(
                    id = id,
                    nome = nome,
                    tipoIngrediente = tipoIngrediente,
                    quantidadePedidos = quantidade,
                    posicao = index + 1,
                    premium = premium
                )
            }
        } catch (e: Exception) {
            logger.error("Erro ao buscar ingredientes do tipo $tipo", e)
            emptyList()
        }
    }

    fun getEstatisticasGerais(dataInicio: String, dataFim: String): EstatisticasGeraisDTO {
        return try {
            // Validar se as datas não são nulas ou vazias
            if (dataInicio.isBlank() || dataFim.isBlank()) {
                throw IllegalArgumentException("Datas de início e fim são obrigatórias")
            }
            
            val totalPedidosPeriodo = pedidoRepository.countPedidosPeriodo(dataInicio, dataFim) ?: 0
            val receitaTotalPeriodo = pedidoRepository.sumReceitaPeriodo(dataInicio, dataFim) ?: 0.0
            val ticketMedio = pedidoRepository.avgTicketMedioPeriodo(dataInicio, dataFim) ?: 0.0
            val taxaConclusao = pedidoRepository.calcTaxaConclusaoPeriodo(dataInicio, dataFim) ?: 0.0

            EstatisticasGeraisDTO(
                totalPedidosPeriodo = totalPedidosPeriodo,
                receitaTotalPeriodo = receitaTotalPeriodo,
                ticketMedio = ticketMedio,
                taxaConclusao = taxaConclusao
            )
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            println("Erro ao buscar estatísticas gerais: ${e.message}")
            throw RuntimeException("Erro ao processar estatísticas do período", e)
        }
    }

    fun getPedidosPorPeriodo(dataInicio: String, dataFim: String): List<PedidosPeriodoDTO> {
        return try {
            // Validar se as datas não são nulas ou vazias
            if (dataInicio.isBlank() || dataFim.isBlank()) {
                throw IllegalArgumentException("Datas de início e fim são obrigatórias")
            }
            
            val resultados = pedidoRepository.getPedidosPorPeriodo(dataInicio, dataFim)
            
            resultados.map { array ->
                try {
                    val data = LocalDate.parse(array[0].toString())
                    val quantidade = (array[1] as Number).toInt()
                    
                    PedidosPeriodoDTO(
                        data = data,
                        quantidade = quantidade
                    )
                } catch (e: Exception) {
                    println("Erro ao processar linha de resultado: ${e.message}")
                    throw RuntimeException("Erro ao processar dados de pedidos por período", e)
                }
            }
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            println("Erro ao buscar pedidos por período: ${e.message}")
            throw RuntimeException("Erro ao processar dados de pedidos por período", e)
        }
    }

    fun getTopClientes(dataInicio: String, dataFim: String): List<TopClienteDTO> {
        return try {
            // Validar se as datas não são nulas ou vazias
            if (dataInicio.isBlank() || dataFim.isBlank()) {
                throw IllegalArgumentException("Datas de início e fim são obrigatórias")
            }
            
            val resultados = pedidoRepository.getTopClientesPorPeriodo(dataInicio, dataFim)
            
            resultados.mapIndexed { index, array ->
                try {
                    val id = (array[0] as Number).toInt()
                    val nome = array[1]?.toString() ?: "Nome não informado"
                    val email = array[2]?.toString() ?: "Email não informado"
                    val quantidadePedidos = (array[3] as Number).toInt()
                    val valorTotal = (array[4] as Number).toDouble()
                    
                    TopClienteDTO(
                        id = id,
                        nome = nome,
                        email = email,
                        quantidadePedidos = quantidadePedidos,
                        valorTotal = valorTotal,
                        posicao = index + 1
                    )
                } catch (e: Exception) {
                    println("Erro ao processar cliente na posição $index: ${e.message}")
                    throw RuntimeException("Erro ao processar dados do cliente", e)
                }
            }
        } catch (e: IllegalArgumentException) {
            throw e
        } catch (e: Exception) {
            println("Erro ao buscar top clientes: ${e.message}")
            throw RuntimeException("Erro ao processar ranking de clientes", e)
        }
    }

    fun getComparacaoPedidos(dataInicio: String, dataFim: String): PedidosComparisonDTO {
        val inicio = LocalDate.parse(dataInicio)
        val fim = LocalDate.parse(dataFim)
        val diasPeriodo = ChronoUnit.DAYS.between(inicio, fim)
        
        val dataInicioAnterior = inicio.minusDays(diasPeriodo + 1)
        val dataFimAnterior = inicio.minusDays(1)
        
        val pedidosAtual = pedidoRepository.countPedidosConcluidosByPeriodo(
            dataInicio, 
            dataFim
        ) ?: 0
        
        val pedidosAnterior = pedidoRepository.countPedidosConcluidosByPeriodo(
            dataInicioAnterior.toString(), 
            dataFimAnterior.toString()
        ) ?: 0
        
        val percentualCrescimento = if (pedidosAnterior > 0) {
            ((pedidosAtual - pedidosAnterior).toDouble() / pedidosAnterior) * 100
        } else {
            if (pedidosAtual > 0) 100.0 else 0.0
        }
        
        return PedidosComparisonDTO(
            periodoAtual = pedidosAtual,
            periodoAnterior = pedidosAnterior,
            percentualCrescimento = (percentualCrescimento * 10).roundToInt() / 10.0
        )
    }

    fun getPrecoMedio(dataInicio: String, dataFim: String): PrecoMedioDTO {
        val precoMedio = pedidoRepository.calcularPrecoMedioPedidosConcluidos(dataInicio, dataFim) ?: 0.0
        val totalPedidos = pedidoRepository.countPedidosConcluidosByPeriodo(dataInicio, dataFim) ?: 0
        
        return PrecoMedioDTO(
            precoMedio = (precoMedio * 100).roundToInt() / 100.0,
            totalPedidos = totalPedidos
        )
    }

    fun getRankingClientes(dataInicio: String, dataFim: String, limit: Int): List<ClienteRankingDTO> {
        val resultados = pedidoRepository.findTopClientesByValorGasto(dataInicio, dataFim, limit)
        
        return resultados.map { row ->
            ClienteRankingDTO(
                clienteId = (row[0] as Number).toInt(),
                clienteNome = row[1] as String,
                clienteEmail = row[2] as String,
                totalGasto = (row[3] as Number).toDouble(),
                quantidadePedidos = (row[4] as Number).toInt()
            )
        }
    }

    fun getRankingIngredientes(dataInicio: String, dataFim: String, limit: Int): List<IngredienteRankingDTO> {
        val resultados = pedidoRepository.findTopIngredientesMaisPedidos(dataInicio, dataFim, limit)
        
        return resultados.map { row ->
            IngredienteRankingDTO(
                ingredienteId = (row[0] as Number).toInt(),
                ingredienteNome = row[1] as String,
                quantidadePedidos = (row[2] as Number).toInt(),
                premium = (row[3] as Number).toInt() == 1
            )
        }
    }

    private fun isValidDateFormat(date: String): Boolean {
        return try {
            val pattern = Regex("^\\d{4}-\\d{2}-\\d{2}$")
            if (!pattern.matches(date)) return false
            java.time.LocalDate.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }
}