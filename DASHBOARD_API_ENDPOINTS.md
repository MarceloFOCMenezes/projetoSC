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

### 6. **GET /dashboard/comparacao-pedidos**
- **Propósito**: Comparar pedidos concluídos entre períodos
- **Parâmetros obrigatórios**:
  - `dataInicio`: Data inicial (formato: yyyy-MM-dd)
  - `dataFim`: Data final (formato: yyyy-MM-dd)
- **Critério**: Status 7 (concluídos) + data do pedido (`dt_pedido`)
- **Retorna**:
  - `periodoAtual`: Total de pedidos no período selecionado
  - `periodoAnterior`: Total de pedidos no período anterior (mesma duração)
  - `percentualCrescimento`: % de crescimento ou declínio

**Exemplo de resposta:**
```json
{
  "periodoAtual": 150,
  "periodoAnterior": 120,
  "percentualCrescimento": 25.0
}
```

### 7. **GET /dashboard/preco-medio**
- **Propósito**: Calcular preço médio dos pedidos concluídos
- **Parâmetros obrigatórios**:
  - `dataInicio`: Data inicial (formato: yyyy-MM-dd)
  - `dataFim`: Data final (formato: yyyy-MM-dd)
- **Critério**: Status 7 (concluídos) + data do pedido (`dt_pedido`)
- **Retorna**:
  - `precoMedio`: Valor médio dos pedidos (2 casas decimais)
  - `totalPedidos`: Quantidade de pedidos no período

**Exemplo de resposta:**
```json
{
  "precoMedio": 85.50,
  "totalPedidos": 150
}
```

### 8. **GET /dashboard/ranking-clientes**
- **Propósito**: Ranking de clientes por valor gasto
- **Parâmetros**:
  - `dataInicio`: Data inicial (formato: yyyy-MM-dd) - obrigatório
  - `dataFim`: Data final (formato: yyyy-MM-dd) - obrigatório
  - `limit`: Quantidade de resultados (padrão: 10) - opcional
- **Critério**: Status 7 (concluídos) + data do pedido (`dt_pedido`)
- **Retorna**: Lista ordenada por valor total gasto (decrescente)

**Exemplo de resposta:**
```json
[
  {
    "clienteId": 1,
    "clienteNome": "João Silva",
    "clienteEmail": "joao@email.com",
    "totalGasto": 1250.00,
    "quantidadePedidos": 15
  }
]
```

### 9. **GET /dashboard/ranking-ingredientes**
- **Propósito**: Ranking de ingredientes mais pedidos
- **Parâmetros**:
  - `dataInicio`: Data inicial (formato: yyyy-MM-dd) - obrigatório
  - `dataFim`: Data final (formato: yyyy-MM-dd) - obrigatório
  - `limit`: Quantidade de resultados (padrão: 10) - opcional
- **Critério**: Status 7 (concluídos) + data do pedido (`dt_pedido`)
- **Retorna**: Lista ordenada por quantidade de pedidos (decrescente)

**Exemplo de resposta:**
```json
[
  {
    "ingredienteId": 5,
    "ingredienteNome": "Chocolate Belga",
    "quantidadePedidos": 320
  }
]
```

## 📋 Diferenças entre Endpoints

| Endpoint | Formato Data | Campo Referência | Status | Observações |
|----------|-------------|------------------|--------|-------------|
| `/estatisticas` | yyyy-MM-dd | dt_entrega_esperada | Variados | Dashboard gerencial |
| `/pedidos-periodo` | yyyy-MM-dd | dt_entrega_esperada | Todos | Gráfico temporal |
| `/top-clientes` | yyyy-MM-dd | dt_entrega_esperada | 4, 5 | Clientes antigos |
| **`/comparacao-pedidos`** | yyyy-MM-dd | dt_pedido | **7** | Novos indicadores |
| **`/preco-medio`** | yyyy-MM-dd | dt_pedido | **7** | Novos indicadores |
| **`/ranking-clientes`** | yyyy-MM-dd | dt_pedido | **7** | Novos indicadores |
| **`/ranking-ingredientes`** | yyyy-MM-dd | dt_pedido | **7** | Novos indicadores |

## Status dos Pedidos assumidos:
- 1: Carrinho
- 2: Pendente
- 3: Em Produção
- 4: Pronto
- 5: Entregue
- **7: Concluído** (usado nos novos endpoints)

## Arquivos criados/modificados:
- `dto/DashboardOverviewDTO.kt`
- `dto/IngredienteRankingDTO.kt`
- `dto/DashboardExtendedDTO.kt`
- `dto/AlertasDTO.kt`
- `services/DashboardService.kt`
- `controller/DashboardController.kt`
- `repository/PedidoRepository.kt` (adicionadas queries)
- `repository/IngredienteRepository.kt` (adicionadas queries)