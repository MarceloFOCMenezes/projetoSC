# Guia de Implementação - Dashboard Frontend

## 📋 Visão Geral

Este documento orienta a implementação das telas de dashboard no frontend, utilizando os endpoints da API já desenvolvidos no backend.

## 🎯 Telas a Implementar

### 1. Dashboard Operacional
- **Cards principais**: Pedidos Hoje, Pendentes, Produzindo, Concluídos
- **Ranking**: Top 5 ingredientes mais utilizados
- **Navegação**: Botões para "Visão Operacional" e "Visão Gerencial"

### 2. Dashboard Gerencial
- **Estatísticas por período**: Total de pedidos, receita, ticket médio
- **Gráficos**: Evolução temporal de pedidos
- **Top clientes**: Ranking de clientes por valor gasto
- **Filtros de data**: Seleção de período personalizado

## 🔌 Endpoints da API

### Base URL
```
http://localhost:8080/dashboard
```

### 1. Dados Principais do Dashboard
```http
GET /dashboard/overview
```

**Resposta:**
```json
{
  "pedidosHoje": 42,
  "pendentes": 8,
  "produzindo": 5,
  "concluidos": 29
}
```

### 2. Top Ingredientes por Tipo
```http
GET /dashboard/top-ingredientes
```

**Resposta:**
```json
{
  "massa": [
    {
      "id": 1,
      "nome": "Massa de Chocolate",
      "tipoIngrediente": "Massa",
      "quantidadePedidos": 15,
      "percentual": 45.5,
      "posicao": 1
    }
  ],
  "recheio": [
    {
      "id": 5,
      "nome": "Brigadeiro",
      "tipoIngrediente": "Recheio",
      "quantidadePedidos": 12,
      "percentual": 60.0,
      "posicao": 1
    }
  ],
  "adicional": [
    {
      "id": 8,
      "nome": "Granulado",
      "tipoIngrediente": "Adicional",
      "quantidadePedidos": 8,
      "percentual": 40.0,
      "posicao": 1
    }
  ]
}
```

### 3. Estatísticas Gerais (Dashboard Gerencial)
```http
GET /dashboard/estatisticas?dataInicio=2025-11-01&dataFim=2025-11-30
```
**Parâmetros obrigatórios:**
- `dataInicio`: Data de início no formato yyyy-mm-dd
- `dataFim`: Data de fim no formato yyyy-mm-dd

**Resposta:**
```json
{
  "totalPedidosPeriodo": 156,
  "receitaTotalPeriodo": 8750.00,
  "ticketMedio": 56.09,
  "taxaConclusao": 94.2
}
```

### 4. Dados para Gráfico por Período
```http
GET /dashboard/pedidos-periodo?dataInicio=2025-11-01&dataFim=2025-11-30
```
**Parâmetros obrigatórios:**
- `dataInicio`: Data de início no formato yyyy-mm-dd
- `dataFim`: Data de fim no formato yyyy-mm-dd

**Nota:** Este endpoint utiliza a `dt_entrega_esperada` dos pedidos, não a `dt_pedido`.

**Resposta:**
```json
[
  {
    "data": "2025-11-24",
    "quantidade": 12
  },
  {
    "data": "2025-11-25",
    "quantidade": 8
  }
]
```

### 5. Top Clientes por Período
```http
GET /dashboard/top-clientes?dataInicio=2025-11-01&dataFim=2025-11-30
```
**Parâmetros obrigatórios:**
- `dataInicio`: Data de início no formato yyyy-mm-dd
- `dataFim`: Data de fim no formato yyyy-mm-dd

**Nota:** Retorna os 10 clientes que mais gastaram no período, baseado na `dt_entrega_esperada`.

**Resposta:**
```json
[
  {
    "id": 1,
    "nome": "Maria Silva",
    "email": "maria@email.com",
    "quantidadePedidos": 8,
    "valorTotal": 450.00,
    "posicao": 1
  }
]
```

## 🎨 Implementação do Layout

### Estrutura da Página
```html
<div class="dashboard-container">
  <!-- Header com navegação -->
  <div class="dashboard-header">
    <h1>Dashboard</h1>
    <div class="view-toggle">
      <button class="btn-primary">VISÃO OPERACIONAL</button>
      <button class="btn-secondary">VISÃO GERENCIAL</button>
    </div>
  </div>

  <!-- Cards principais -->
  <div class="stats-grid">
    <div class="stat-card pedidos-hoje">
      <div class="stat-icon">📋</div>
      <div class="stat-content">
        <div class="stat-label">Pedidos Hoje</div>
        <div class="stat-value">42</div>
        <div class="stat-sublabel">atual</div>
      </div>
    </div>
    <!-- Repetir para outros cards -->
  </div>

  <!-- Top 5 Ingredientes -->
  <div class="ingredients-section">
    <h2>Top 5 Ingredientes</h2>
    <div class="ingredients-toggle">
      <button id="btnIngredientesGeral" class="toggle-btn active">Geral</button>
      <button id="btnIngredientesPorTipo" class="toggle-btn">Por Tipo</button>
    </div>
    <div id="ingredients-list-geral" class="ingredients-list">
      <!-- Lista geral de ingredientes -->
    </div>
    <div id="ingredients-list-por-tipo" class="ingredients-list" style="display: none;">
      <div class="ingredients-category">
        <h3>🍰 Massa</h3>
        <div id="ingredients-massa" class="category-list"></div>
      </div>
      <div class="ingredients-category">
        <h3>🥧 Recheio</h3>
        <div id="ingredients-recheio" class="category-list"></div>
      </div>
      <div class="ingredients-category">
        <h3>🍯 Adicional</h3>
        <div id="ingredients-adicional" class="category-list"></div>
      </div>
    </div>
  </div>

  <!-- Top Clientes (Dashboard Gerencial) -->
  <div class="top-clientes-section" style="display: none;">
    <div class="section-header">
      <h2>Top Clientes</h2>
      <div class="date-filter">
        <input type="date" id="dataInicio" placeholder="Data Início">
        <input type="date" id="dataFim" placeholder="Data Fim">
        <button id="filtrarClientes">Filtrar</button>
      </div>
    </div>
    <div class="top-clientes-list">
      <!-- Lista de clientes será preenchida via JavaScript -->
    </div>
  </div>
</div>
```

### CSS Sugerido
```css
.dashboard-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.view-toggle {
  display: flex;
  gap: 10px;
}

.btn-primary {
  background-color: #4a2c2a;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
}

.btn-secondary {
  background-color: transparent;
  color: #666;
  border: 1px solid #ddd;
  padding: 10px 20px;
  border-radius: 8px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-left: 4px solid;
}

.stat-card.pedidos-hoje { border-left-color: #8B4513; }
.stat-card.pendentes { border-left-color: #FF8C00; }
.stat-card.produzindo { border-left-color: #4169E1; }
.stat-card.concluidos { border-left-color: #32CD32; }

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 2em;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #666;
  font-size: 0.9em;
}

.stat-sublabel {
  color: #999;
  font-size: 0.8em;
}

.ingredients-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.ingredient-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.ingredient-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.ingredient-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5em;
}

.ingredient-details h4 {
  margin: 0;
  color: #333;
}

.ingredient-details p {
  margin: 5px 0 0 0;
  color: #666;
  font-size: 0.9em;
}

.ingredient-percentage {
  font-size: 1.2em;
  font-weight: bold;
  color: #4a2c2a;
}

.top-clientes-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-top: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.date-filter {
  display: flex;
  gap: 10px;
  align-items: center;
}

.date-filter input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.9em;
}

.date-filter button {
  padding: 8px 16px;
  background-color: #4a2c2a;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.cliente-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.cliente-info h4 {
  margin: 0;
  color: #333;
  font-size: 1em;
}

.cliente-info p {
  margin: 5px 0 0 0;
  color: #666;
  font-size: 0.9em;
}

.cliente-valor {
  font-size: 1.1em;
  font-weight: bold;
  color: #32CD32;
}

.ingredients-toggle {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.toggle-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.toggle-btn.active {
  background: #4a2c2a;
  color: white;
  border-color: #4a2c2a;
}

.ingredients-category {
  margin-bottom: 25px;
}

.ingredients-category h3 {
  color: #4a2c2a;
  margin-bottom: 10px;
  padding-bottom: 5px;
  border-bottom: 2px solid #f0f0f0;
}

.category-list {
  margin-left: 10px;
}

.category-empty {
  color: #999;
  font-style: italic;
  padding: 10px 0;
}
```

## 🔄 Lógica de Atualização

### JavaScript/TypeScript
```javascript
class DashboardController {
  constructor() {
    this.baseUrl = 'http://localhost:8080/dashboard';
    this.updateInterval = 30000; // 30 segundos
    this.currentView = 'operacional';
    this.setupEventListeners();
  }

  setupEventListeners() {
    // Toggle entre visões
    document.querySelectorAll('.view-toggle button').forEach(btn => {
      btn.addEventListener('click', (e) => {
        this.switchView(e.target.textContent.includes('OPERACIONAL') ? 'operacional' : 'gerencial');
      });
    });

    // Toggle entre ingredientes
    document.getElementById('btnIngredientesGeral')?.addEventListener('click', () => {
      this.switchIngredientsView('geral');
    });

    document.getElementById('btnIngredientesPorTipo')?.addEventListener('click', () => {
      this.switchIngredientsView('por-tipo');
    });

    // Filtro de clientes
    document.getElementById('filtrarClientes')?.addEventListener('click', () => {
      this.filtrarTopClientes();
    });
  }

  switchIngredientsView(view) {
    // Atualizar botões
    document.querySelectorAll('.ingredients-toggle button').forEach(btn => {
      btn.classList.toggle('active', btn.id.includes(view === 'geral' ? 'Geral' : 'PorTipo'));
    });

    // Mostrar/ocultar listas
    document.getElementById('ingredients-list-geral').style.display = view === 'geral' ? 'block' : 'none';
    document.getElementById('ingredients-list-por-tipo').style.display = view === 'por-tipo' ? 'block' : 'none';

    if (view === 'por-tipo') {
      // Os dados já estão carregados, apenas alterna a visualização
    }
  }

  switchView(view) {
    this.currentView = view;
    
    // Atualizar botões
    document.querySelectorAll('.view-toggle button').forEach(btn => {
      btn.className = btn.textContent.includes(view.toUpperCase()) ? 'btn-primary' : 'btn-secondary';
    });

    // Mostrar/ocultar seções
    document.querySelector('.ingredients-section').style.display = view === 'operacional' ? 'block' : 'none';
    document.querySelector('.top-clientes-section').style.display = view === 'gerencial' ? 'block' : 'none';

    if (view === 'gerencial') {
      this.loadGerencialData();
    }
  }

  async loadGerencialData() {
    // Carregar dados iniciais para o mês atual
    const hoje = new Date();
    const primeiroDia = new Date(hoje.getFullYear(), hoje.getMonth(), 1);
    const ultimoDia = new Date(hoje.getFullYear(), hoje.getMonth() + 1, 0);
    
    const dataInicio = primeiroDia.toISOString().split('T')[0];
    const dataFim = ultimoDia.toISOString().split('T')[0];
    
    await this.carregarTopClientes(dataInicio, dataFim);
  }

  async filtrarTopClientes() {
    const dataInicio = document.getElementById('dataInicio').value;
    const dataFim = document.getElementById('dataFim').value;
    
    if (!dataInicio || !dataFim) {
      alert('Por favor, selecione as datas de início e fim.');
      return;
    }
    
    await this.carregarTopClientes(dataInicio, dataFim);
  }

  async loadDashboardData() {
    try {
      // Carregar dados principais
      const overview = await this.fetchOverview();
      this.updateOverviewCards(overview);

      // Carregar ingredientes (apenas visão operacional)
      if (this.currentView === 'operacional') {
        const ingredients = await this.fetchTopIngredients();
        this.updateIngredientesPorTipo(ingredients);
      }

    } catch (error) {
      console.error('Erro ao carregar dados do dashboard:', error);
      this.showErrorMessage();
    }
  }

  async loadIngredientesPorTipo() {
    // Método removido - agora o endpoint principal já retorna por tipo
  }

  async carregarTopClientes(dataInicio, dataFim) {
    try {
      const topClientes = await this.fetchTopClientes(dataInicio, dataFim);
      this.updateTopClientes(topClientes);
    } catch (error) {
      console.error('Erro ao carregar top clientes:', error);
    }
  }

  async fetchOverview() {
    const response = await fetch(`${this.baseUrl}/overview`);
    if (!response.ok) throw new Error('Erro ao buscar overview');
    return response.json();
  }

  async fetchTopIngredients() {
    const response = await fetch(`${this.baseUrl}/top-ingredientes`);
    if (!response.ok) throw new Error('Erro ao buscar ingredientes');
    return response.json();
  }

  async fetchTopClientes(dataInicio, dataFim) {
    const params = new URLSearchParams({ dataInicio, dataFim });
    const response = await fetch(`${this.baseUrl}/top-clientes?${params}`);
    if (!response.ok) throw new Error('Erro ao buscar top clientes');
    return response.json();
  }

  updateOverviewCards(data) {
    document.querySelector('.pedidos-hoje .stat-value').textContent = data.pedidosHoje;
    document.querySelector('.pendentes .stat-value').textContent = data.pendentes;
    document.querySelector('.produzindo .stat-value').textContent = data.produzindo;
    document.querySelector('.concluidos .stat-value').textContent = data.concluidos;
  }

  updateIngredientsRanking(ingredients) {
    const container = document.getElementById('ingredients-list-geral');
    container.innerHTML = '';

    if (ingredients.length === 0) {
      container.innerHTML = '<p>Nenhum ingrediente encontrado.</p>';
      return;
    }

    ingredients.forEach((ingredient, index) => {
      const item = this.createIngredientItem(ingredient, index + 1);
      container.appendChild(item);
    });
  }

  updateIngredientesPorTipo(ranking) {
    this.renderCategoryIngredients('massa', ranking.massa);
    this.renderCategoryIngredients('recheio', ranking.recheio);
    this.renderCategoryIngredients('adicional', ranking.adicional);
  }

  renderCategoryIngredients(category, ingredients) {
    const container = document.getElementById(`ingredients-${category}`);
    container.innerHTML = '';

    if (!ingredients || ingredients.length === 0) {
      container.innerHTML = '<div class="category-empty">Nenhum ingrediente nesta categoria</div>';
      return;
    }

    ingredients.forEach(ingredient => {
      const item = this.createIngredientItemPorTipo(ingredient);
      container.appendChild(item);
    });
  }

  createIngredientItemPorTipo(ingredient) {
    const div = document.createElement('div');
    div.className = 'ingredient-item';
    
    const iconColors = {
      'Massa': '#8B4513',
      'Recheio': '#FF69B4', 
      'Adicional': '#32CD32'
    };
    const iconColor = iconColors[ingredient.tipoIngrediente] || '#ccc';

    div.innerHTML = `
      <div class="ingredient-info">
        <div class="ingredient-icon" style="background-color: ${iconColor}">
          ${this.getIngredientIcon(ingredient.nome)}
        </div>
        <div class="ingredient-details">
          <h4>#${ingredient.posicao} ${ingredient.nome}</h4>
          <p>${ingredient.quantidadePedidos} pedidos</p>
        </div>
      </div>
      <div class="ingredient-percentage">${ingredient.percentual.toFixed(1)}%</div>
    `;

    return div;
  }

  createIngredientItem(ingredient, position) {
    const div = document.createElement('div');
    div.className = 'ingredient-item';
    
    const iconColors = ['#8B4513', '#FF69B4', '#FFB6C1', '#DDA0DD', '#98FB98'];
    const iconColor = iconColors[position - 1] || '#ccc';

    div.innerHTML = `
      <div class="ingredient-info">
        <div class="ingredient-icon" style="background-color: ${iconColor}">
          ${this.getIngredientIcon(ingredient.nome)}
        </div>
        <div class="ingredient-details">
          <h4>#${position} ${ingredient.nome}</h4>
          <p>${ingredient.quantidadePedidos} pedidos</p>
        </div>
      </div>
      <div class="ingredient-percentage">${ingredient.percentual}%</div>
    `;

    return div;
  }

  getIngredientIcon(nome) {
    const icons = {
      'Chocolate': '🍫',
      'Morango': '🍓', 
      'Brigadeiro': '🧁',
      'Vanilla': '🍦',
      'Caramelo': '🍮'
    };
    return icons[nome] || '🎂';
  }

  updateTopClientes(clientes) {
    const container = document.querySelector('.top-clientes-list');
    container.innerHTML = '';
    
    if (clientes.length === 0) {
      container.innerHTML = '<p>Nenhum cliente encontrado no período selecionado.</p>';
      return;
    }
    
    clientes.forEach(cliente => {
      const item = this.createClienteItem(cliente);
      container.appendChild(item);
    });
  }

  createClienteItem(cliente) {
    const div = document.createElement('div');
    div.className = 'cliente-item';
    
    div.innerHTML = `
      <div class="cliente-info">
        <h4>#${cliente.posicao} ${cliente.nome}</h4>
        <p>${cliente.quantidadePedidos} pedidos • ${cliente.email}</p>
      </div>
      <div class="cliente-valor">R$ ${cliente.valorTotal.toFixed(2)}</div>
    `;
    
    return div;
  }

  showErrorMessage() {
    // Implementar notificação de erro
    console.error('Erro ao carregar dados');
  }

  startAutoUpdate() {
    this.loadDashboardData(); // Carregamento inicial
    
    setInterval(() => {
      this.loadDashboardData();
    }, this.updateInterval);
  }
}

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
  const dashboard = new DashboardController();
  
  // Configurar datas padrão (mês atual)
  const hoje = new Date();
  const primeiroDia = new Date(hoje.getFullYear(), hoje.getMonth(), 1);
  const ultimoDia = new Date(hoje.getFullYear(), hoje.getMonth() + 1, 0);
  
  document.getElementById('dataInicio').value = primeiroDia.toISOString().split('T')[0];
  document.getElementById('dataFim').value = ultimoDia.toISOString().split('T')[0];
  
  dashboard.startAutoUpdate();
});
```

## 📊 Componentes Adicionais

### 1. Gráfico de Pedidos (Chart.js)
```javascript
async function createPedidosChart(dataInicio, dataFim) {
  if (!dataInicio || !dataFim) {
    console.error('Datas de início e fim são obrigatórias');
    return;
  }
  
  const params = new URLSearchParams({ dataInicio, dataFim });
  const data = await fetch(`/dashboard/pedidos-periodo?${params}`).then(r => r.json());
  
  const ctx = document.getElementById('pedidosChart').getContext('2d');
  new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.map(d => new Date(d.data).toLocaleDateString('pt-BR')),
      datasets: [{
        label: 'Pedidos por Data Esperada',
        data: data.map(d => d.quantidade),
        borderColor: '#4a2c2a',
        backgroundColor: 'rgba(74, 44, 42, 0.1)',
        tension: 0.1,
        fill: true
      }]
    },
    options: {
      responsive: true,
      plugins: {
        title: {
          display: true,
          text: `Pedidos por Período (${dataInicio} a ${dataFim})`
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  });
}
```

### 2. Componente Top Clientes
```javascript
function renderTopClientes(clientes, dataInicio, dataFim) {
  const container = document.querySelector('.top-clientes-container');
  
  if (!clientes || clientes.length === 0) {
    container.innerHTML = `
      <div class="empty-state">
        <p>Nenhum cliente encontrado no período de ${dataInicio} a ${dataFim}</p>
      </div>
    `;
    return;
  }
  
  const clientesHTML = clientes.map(cliente => `
    <div class="cliente-item">
      <div class="cliente-ranking">#${cliente.posicao}</div>
      <div class="cliente-info">
        <h4>${cliente.nome}</h4>
        <p class="cliente-email">${cliente.email}</p>
        <p class="cliente-stats">${cliente.quantidadePedidos} pedidos no período</p>
      </div>
      <div class="cliente-valor">
        <span class="valor-total">R$ ${cliente.valorTotal.toFixed(2)}</span>
        <span class="valor-medio">Médio: R$ ${(cliente.valorTotal / cliente.quantidadePedidos).toFixed(2)}</span>
      </div>
    </div>
  `).join('');
  
  container.innerHTML = `
    <div class="periodo-info">
      <small>Período: ${new Date(dataInicio).toLocaleDateString('pt-BR')} - ${new Date(dataFim).toLocaleDateString('pt-BR')}</small>
    </div>
    ${clientesHTML}
  `;
}

// CSS adicional para o componente
const additionalCSS = `
.cliente-ranking {
  font-size: 1.5em;
  font-weight: bold;
  color: #4a2c2a;
  width: 40px;
}

.valor-medio {
  font-size: 0.8em;
  color: #666;
  display: block;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #666;
}

.periodo-info {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 6px;
  margin-bottom: 20px;
  text-align: center;
}
`;
```

## ✅ Checklist de Implementação

### Fase 1 - Básico
- [ ] Estrutura HTML do dashboard
- [ ] CSS para layout responsivo
- [ ] Conexão com API - endpoint `/overview`
- [ ] Exibição dos 4 cards principais
- [ ] Conexão com API - endpoint `/top-ingredientes`
- [ ] Renderização da lista de ingredientes

### Fase 2 - Melhorias
- [ ] Auto-atualização a cada 30 segundos
- [ ] Tratamento de erros de API
- [ ] Loading states
- [ ] Responsividade mobile
- [ ] Transições e animações
- [ ] Toggle entre visão operacional e gerencial
- [ ] Filtros de data funcionais
- [ ] Validação de datas

### Fase 3 - Avançado
- [ ] Gráficos temporais com Chart.js
- [ ] Top clientes por período personalizado
- [ ] Dashboard gerencial completo
- [ ] Exportação de relatórios
- [ ] Comparação entre períodos
- [ ] Métricas avançadas (crescimento, tendências)

## 🔧 Configurações

### Variáveis de Ambiente
```javascript
const API_CONFIG = {
  baseUrl: process.env.REACT_APP_API_URL || 'http://localhost:8080',
  refreshInterval: 30000,
  timeout: 10000
};
```

### Headers para Requisições
```javascript
const defaultHeaders = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
  // Adicionar autenticação se necessário
};
```

### Importante - Dados Baseados em Data de Entrega
⚠️ **ATENÇÃO**: Todos os endpoints que trabalham com períodos (`/estatisticas`, `/pedidos-periodo`, `/top-clientes`) utilizam a **`dt_entrega_esperada`** dos pedidos como critério de filtro, não a `dt_pedido`.

### Formato de Datas
- Todas as datas devem ser enviadas no formato `yyyy-mm-dd` (ISO 8601)
- Exemplo: `2025-11-30`
- As datas são obrigatórias para os endpoints que trabalham com períodos

## 🚀 Deploy e Performance

### Otimizações Recomendadas
- **Cache**: Implementar cache local para reduzir requisições
- **Lazy Loading**: Carregar componentes sob demanda
- **Debounce**: Para atualizações frequentes
- **Error Boundary**: Isolamento de erros por componente

### Monitoramento
- Log de erros de API
- Métricas de performance
- Tempo de carregamento dos componentes

---

**🎯 Resultado Esperado**: Dashboard funcional que replica o design apresentado, com dados em tempo real e experiência de usuário fluida.

## 📋 Resumo dos Novos Endpoints Implementados

### Ingredientes por Tipo
✅ **Endpoint**: `GET /dashboard/top-ingredientes-por-tipo`
- Separa ingredientes em 3 categorias: **Massa**, **Recheio** e **Adicional**
- Retorna até 5 ingredientes por categoria (ou menos se não houver)
- Calcula percentual de uso dentro de cada categoria
- Interface com toggle entre visão geral e por tipo

### Estrutura de Resposta:
```json
{
  "massa": [...],     // Top ingredientes tipo Massa
  "recheio": [...],   // Top ingredientes tipo Recheio  
  "adicional": [...] // Top ingredientes tipo Adicional
}
```

### Funcionalidades Frontend:
- **Toggle de visualização**: Geral vs Por Tipo
- **Ícones por categoria**: 🍰 Massa, 🥧 Recheio, 🍯 Adicional
- **Cores diferenciadas** por tipo de ingrediente
- **Estados vazios** informativos quando não há dados