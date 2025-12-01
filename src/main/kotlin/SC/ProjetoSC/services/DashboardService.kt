package sc.projetosc.services

import sc.projetosc.dto.DashboardOverviewDTO
import sc.projetosc.dto.EstatisticasGeraisDTO
import sc.projetosc.dto.PedidosPeriodoDTO
import sc.projetosc.dto.TopClienteDTO
import sc.projetosc.dto.IngredientePorTipoDTO
import sc.projetosc.dto.RankingIngredientesPorTipoDTO
import sc.projetosc.dto.IngredienteRankingDTO
import sc.projetosc.repository.PedidoRepository
import sc.projetosc.repository.IngredienteRepository
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.time.LocalDate

@Service
class DashboardService(
    private val pedidoRepository: PedidoRepository,
    private val ingredienteRepository: IngredienteRepository
) {
    private val logger = LoggerFactory.getLogger(DashboardService::class.java)

    fun getDashboardOverview(): DashboardOverviewDTO {
        return try {
            val pedidosHoje = pedidoRepository.countPedidosHoje()
            val pendentes = pedidoRepository.countPedidosPendentes()
            val produzindo = pedidoRepository.countPedidosProduzindo()
            val concluidos = pedidoRepository.countPedidosConcluidos()

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

    fun getTop5Ingredientes(): RankingIngredientesPorTipoDTO {
        return try {
            logger.info("Buscando top 5 ingredientes por tipo")
            
            // Buscar ingredientes por tipo
            val massa = getTop5PorTipo("massa")
            val recheio = getTop5PorTipo("cobertura")
            val adicional = getTop5PorTipo("adicionais")
            
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
    
    private fun getTop5PorTipo(tipo: String): List<IngredientePorTipoDTO> {
        return try {
            val resultados = ingredienteRepository.findTop5IngredientesPorTipo(tipo)
            
            if (resultados.isEmpty()) {
                // Se não houver ingredientes com pedidos, buscar todos do tipo
                val semPedidos = ingredienteRepository.findIngredientesPorTipoSemPedidos(tipo)
                return semPedidos.take(5).mapIndexed { index, array ->
                    val id = (array[0] as Number).toInt()
                    val nome = array[1]?.toString() ?: "Ingrediente sem nome"
                    val tipoIngrediente = array[2]?.toString() ?: tipo
                    
                    IngredientePorTipoDTO(
                        id = id,
                        nome = nome,
                        tipoIngrediente = tipoIngrediente,
                        quantidadePedidos = 0,
                        posicao = index + 1
                    )
                }
            }
            
            // Calcular total para percentuais
            val total = resultados.sumOf { (it[3] as Number).toLong() }.toDouble()
            
            resultados.take(5).mapIndexed { index, array ->
                val id = (array[0] as Number).toInt()
                val nome = array[1]?.toString() ?: "Ingrediente sem nome"
                val tipoIngrediente = array[2]?.toString() ?: tipo
                val quantidade = (array[3] as Number).toInt()
                val percentual = if (total > 0) (quantidade / total) * 100 else 0.0
                
                IngredientePorTipoDTO(
                    id = id,
                    nome = nome,
                    tipoIngrediente = tipoIngrediente,
                    quantidadePedidos = quantidade,
                    posicao = index + 1
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
            
            val totalPedidosPeriodo = pedidoRepository.countPedidosPeriodo(dataInicio, dataFim)
            val receitaTotalPeriodo = pedidoRepository.sumReceitaPeriodo(dataInicio, dataFim)
            val ticketMedio = pedidoRepository.avgTicketMedioPeriodo(dataInicio, dataFim)
            val taxaConclusao = pedidoRepository.calcTaxaConclusaoPeriodo(dataInicio, dataFim)

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

}