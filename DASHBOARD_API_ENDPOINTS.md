# Dashboard API Endpoints

## Endpoints criados para suportar as telas do dashboard:

### 1. **GET /dashboard/overview**
- **Propósito**: Dados principais do dashboard (cards superiores)
- **Retorna**: 
  - `pedidosHoje`: Número de pedidos realizados hoje
  - `pendentes`: Número de pedidos pendentes (status = 2)
  - `produzindo`: Número de pedidos em produção (status = 3)  
  - `concluidos`: Número de pedidos concluídos hoje (status = 4)

### 2. **GET /dashboard/top-ingredientes**
- **Propósito**: Top 5 ingredientes mais utilizados (seção inferior da tela)
- **Retorna**: Lista com:
  - `id`: ID do ingrediente
  - `nome`: Nome do ingrediente
  - `quantidadePedidos`: Número de pedidos que contêm o ingrediente
  - `percentual`: Percentual de uso
  - `posicao`: Posição no ranking (1-5)

### 3. **GET /dashboard/estatisticas**
- **Propósito**: Estatísticas gerais para visão gerencial
- **Retorna**:
  - `totalPedidosMes`: Total de pedidos no mês atual
  - `receitaTotalMes`: Receita total do mês
  - `ticketMedio`: Valor médio por pedido
  - `taxaConclusao`: Taxa de conclusão de pedidos (%)

### 4. **GET /dashboard/pedidos-7-dias**
- **Propósito**: Dados para gráfico temporal dos últimos 7 dias
- **Retorna**: Lista com data e quantidade de pedidos por dia

### 5. **GET /dashboard/alertas**
- **Propósito**: Alertas operacionais que requerem atenção
- **Retorna**: Lista de alertas com tipo, título, descrição e prioridade

### 6. **GET /dashboard/alta-demanda**
- **Propósito**: Dias com alta demanda nos próximos 7 dias
- **Retorna**: Lista de dias com mais de 10 pedidos agendados

## Status dos Pedidos assumidos:
- 1: Carrinho
- 2: Pendente
- 3: Em Produção
- 4: Concluído
- 5: Entregue

## Arquivos criados/modificados:
- `dto/DashboardOverviewDTO.kt`
- `dto/IngredienteRankingDTO.kt`
- `dto/DashboardExtendedDTO.kt`
- `dto/AlertasDTO.kt`
- `services/DashboardService.kt`
- `controller/DashboardController.kt`
- `repository/PedidoRepository.kt` (adicionadas queries)
- `repository/IngredienteRepository.kt` (adicionadas queries)