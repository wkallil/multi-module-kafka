# 🛠️ Tecnologias Utilizadas

- Java 21 (Multi-module Project)

- Spring Framework (Spring Boot 4.0)

- Apache Kafka (comunicação assíncrona e event‑driven)

- PostgreSQL (banco de dados relacional)

- Docker & Docker Compose (containerização dos serviços)

- Flyway (versionamento e migração de banco de dados)

- MapStruct (mapeamento entre entidades e DTOs)

- JUnit 5 (testes unitários)

- Mockito (mocking em testes)

- HATEAOS (Hypermedia as the Engine of Application State)

- Postman (testes e validação de APIs REST)

🚀 **Sistema orientado a eventos, desacoplado e escalável, seguindo boas práticas de arquitetura moderna.**

# 🔄 Fluxo Completo da Aplicação

## 📊 Visão Geral do Processo

Quando você cria um pedido, acontece uma série de eventos automáticos:

```
Cliente → Order Service → Kafka → Inventory Service → Kafka → Order Service
   │            │            │            │              │           │
   │            │            │            │              │           │
   1️⃣         2️⃣          3️⃣          4️⃣            5️⃣         6️⃣
```

---

## 🎬 Passo a Passo Detalhado

### 1️⃣ Cliente Cria um Pedido

**Endpoint:** `POST http://localhost:8084/api/orders`

**Request Body:**
```json
{
  "customerName": "Gabrielle Oliveira",
  "items": [
    {
      "productCode": "PROD-001",
      "productName": "Laptop Dell XPS 15",
      "quantity": 2,
      "unitPrice": 1200
    }
  ]
}
```

**O que acontece internamente:**
```java
// OrderService.createOrder()
1. Cria objeto Order
2. Gera número único (ex: ORD-A1B2C3D4)
3. Define status como PENDING
4. Adiciona items à ordem
5. Calcula total (2 × 1200 = 2400)
6. Salva no banco de dados (order_db)
```

**Resposta Imediata (201 Created):**
```json
{
  "id": 1,
  "orderNumber": "ORD-A1B2C3D4",
  "customerName": "Gabrielle Oliveira",
  "status": "PENDING",  ← Status inicial
  "totalAmount": 2400.00,
  "items": [...],
  "createdAt": "2026-01-20T15:30:00"
}
```

---

### 2️⃣ Order Service Publica Evento no Kafka

**Automaticamente após salvar o pedido:**

```java
// OrderService.createOrder() (continuação)
7. Cria OrderCreatedEvent
8. Publica no tópico "order-created" do Kafka
```

**Mensagem enviada ao Kafka:**
```json
{
  "orderNumber": "ORD-A1B2C3D4",
  "items": [
    {
      "productCode": "PROD-001",
      "quantity": 2
    }
  ]
}
```

**Tópico Kafka:** `order-created`

**Como verificar (Kafka UI):**
```
http://localhost:8090
→ Topics
→ order-created
→ Messages
```

---

### 3️⃣ Inventory Service Consome o Evento

**Automaticamente (sem intervenção manual):**

```java
// KafkaConsumerService.consumeOrderCreated()
1. Recebe mensagem do tópico "order-created"
2. Extrai orderNumber e items
3. Chama InventoryService.processOrderRequest()
```

---

### 4️⃣ Inventory Service Verifica Estoque

**Processo de verificação:**

```java
// InventoryService.processOrderRequest()
Para cada item no pedido:
  1. Busca produto no banco (inventory_db)
  2. Verifica se existe
  3. Verifica se tem quantidade suficiente
  
Cenários possíveis:
  ✅ AVAILABLE: Produto existe + quantidade OK
  ⚠️ PARTIALLY_AVAILABLE: Produto existe + quantidade insuficiente
  ❌ UNAVAILABLE: Produto não existe
```

**Exemplo de verificação:**

```sql
-- Busca no banco inventory_db
SELECT * FROM inventory WHERE product_code = 'PROD-001';

-- Resultado:
-- product_code: PROD-001
-- available_quantity: 50  ← Tem estoque!
-- reserved_quantity: 0
```

**Decisões:**
- Solicitado: 2 unidades
- Disponível: 50 unidades
- ✅ Resultado: AVAILABLE

**Ação tomada:**
```java
// Reserva o estoque
inventory.reserveStock(2);
// available_quantity: 50 → 48
// reserved_quantity: 0 → 2
```

---

### 5️⃣ Inventory Service Envia Resposta via Kafka

**Publicação automática:**

```java
// InventoryService.processOrderRequest() (continuação)
4. Cria InventoryResponseEvent
5. Publica no tópico "inventory-response"
```

**Mensagem enviada:**
```json
{
  "orderNumber": "ORD-A1B2C3D4",
  "status": "AVAILABLE",
  "message": "All items are available and reserved successfully"
}
```

**Tópico Kafka:** `inventory-response`

---

### 6️⃣ Order Service Atualiza Status do Pedido

**Consumo automático:**

```java
// KafkaConsumerService.consumeInventoryResponse()
1. Recebe mensagem do tópico "inventory-response"
2. Extrai orderNumber e status
3. Chama OrderService.updateOrderStatus()
```

**Atualização do banco:**
```java
// OrderService.updateOrderStatus()
1. Busca pedido por orderNumber
2. Converte status:
   - AVAILABLE → APPROVED
   - PARTIALLY_AVAILABLE → ON_HOLD
   - UNAVAILABLE → REJECTED
3. Atualiza no banco de dados
```

**Banco de dados atualizado:**
```sql
-- Antes
status: PENDING

-- Depois
status: APPROVED
updated_at: 2026-01-20T15:30:05
```

---

## 🔍 Como Verificar Cada Etapa

### Verificar Pedido Criado

**Endpoint:** `GET http://localhost:8084/api/orders/number/ORD-A1B2C3D4`

**Resposta:**
```json
{
  "id": 1,
  "orderNumber": "ORD-A1B2C3D4",
  "status": "APPROVED",  ← Status atualizado!
  "totalAmount": 2400.00,
  ...
}
```

### Verificar Estoque Atualizado

**Endpoint:** `GET http://localhost:8083/api/inventory/PROD-001`

**Resposta:**
```json
{
  "productCode": "PROD-001",
  "productName": "Laptop Dell XPS 15",
  "availableQuantity": 48,    ← Era 50, agora 48
  "reservedQuantity": 2,      ← Reservado para o pedido
  "totalQuantity": 50
}
```


---

## 📋 Endpoints Úteis

### Order Service (Porta 8084)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/orders` | Criar novo pedido |
| GET | `/api/orders` | Listar todos os pedidos |
| GET | `/api/orders/{id}` | Buscar por ID |
| GET | `/api/orders/number/{orderNumber}` | Buscar por número |
| GET | `/api/orders/status/{status}` | Filtrar por status |

**Status possíveis:** PENDING, APPROVED, ON_HOLD, REJECTED

### Inventory Service (Porta 8083)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/inventory` | Listar todo estoque |
| GET | `/api/inventory/{productCode}` | Buscar produto |
| GET | `/api/inventory/{productCode}/check?quantity=X` | Verificar disponibilidade |
| GET | `/api/inventory/backorders` | Listar backorders |
| POST | `/api/inventory` | Adicionar produto |
| PUT | `/api/inventory/{productCode}` | Atualizar estoque |

---


---

## 🎓 Kafka - O Que Acontece nos Bastidores

### Producer (Order Service)
```java
// Quando você cria um pedido:
kafkaTemplate.send(
    "order-created",           // Tópico
    "ORD-A1B2C3D4",           // Key (usado para particionamento)
    orderCreatedEvent         // Value (dados do pedido)
);
```

### Consumer (Inventory Service)
```java
// Escuta constantemente o tópico:
@KafkaListener(topics = "order-created", groupId = "inventory-service-group")
public void consumeOrderCreated(OrderCreatedEvent event) {
    // Processa automaticamente quando mensagem chega
}
```

### Você NÃO precisa:
- ❌ Chamar endpoints manualmente
- ❌ Configurar listeners
- ❌ Gerenciar conexões Kafka
- ✅ Tudo é automático após criar o pedido!

---

## ✅ Checklist de Verificação

Após criar um pedido, verifique:

- [ ] Pedido foi criado com status PENDING
- [ ] Order Service logou "Sending order created event"
- [ ] Inventory Service logou "Received order created event"
- [ ] Inventory Service logou "Successfully processed order"
- [ ] Order Service logou "Order status updated successfully"
- [ ] Status do pedido mudou para APPROVED/ON_HOLD/REJECTED
- [ ] Estoque foi atualizado (se APPROVED)
- [ ] Backorder foi criado (se ON_HOLD)

---

## 🎯 Resumo

**O que você precisa fazer:**
1. ✅ Criar pedido: `POST /api/orders`
2. ✅ Aguardar 1-3 segundos
3. ✅ Verificar status: `GET /api/orders/number/{orderNumber}`

**O que acontece automaticamente:**
1. 🤖 Order Service → Kafka
2. 🤖 Kafka → Inventory Service
3. 🤖 Inventory Service verifica estoque
4. 🤖 Inventory Service → Kafka
5. 🤖 Kafka → Order Service
6. 🤖 Status atualizado!

**Você NÃO precisa:**
- ❌ Chamar Inventory Service manualmente
- ❌ Gerenciar Kafka
- ❌ Atualizar status manualmente
- ✅ Tudo é event-driven e assíncrono!