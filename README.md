# Assistente de Tarefas API

API REST para gerenciamento de tarefas com um **assistente de IA** que entende linguagem natural. Além do CRUD tradicional, é possível conversar com o assistente (por exemplo, *"crie uma tarefa para estudar Spring amanhã"* ou *"quais tarefas estão atrasadas?"*) e ele executa as ações no banco de dados por meio de **Function Calling** (Spring AI + Ollama).

## Sumário

- [Tecnologias](#tecnologias)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Como executar](#como-executar)
- [Variáveis de ambiente](#variáveis-de-ambiente)
- [Documentação da API](#documentação-da-api)
- [Exemplos de uso](#exemplos-de-uso)
- [Assistente de IA](#assistente-de-ia)
- [Testes](#testes)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Limitações conhecidas e próximos passos](#limitações-conhecidas-e-próximos-passos)

## Tecnologias

| Camada | Tecnologia |
| --- | --- |
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.2 (Web MVC, Data JPA, Validation) |
| IA | Spring AI 2.0.2 + Ollama (modelo `llama3.2:1b`) |
| Banco de dados | PostgreSQL 16 |
| Migrações | Flyway |
| Documentação | springdoc-openapi (Swagger UI) |
| Testes | JUnit 5, Spring Boot Test, H2 (em memória) |
| Outros | Lombok, Docker Compose, pgAdmin 4 |

## Funcionalidades

- CRUD completo de tarefas (título, descrição, prioridade, categoria, data de vencimento e status de conclusão).
- Validação de entrada com mensagens de erro padronizadas em português.
- Assistente conversacional com memória das últimas 10 mensagens por conversa.
- Ferramentas (tools) expostas ao modelo: listar, buscar por ID, buscar por título, filtrar por categoria, listar atrasadas, listar as que vencem hoje, contar pendentes, criar, atualizar, concluir/reabrir e excluir tarefas.
- Documentação interativa com Swagger UI.

## Arquitetura

```
Cliente HTTP
   │
   ├── /api/tasks ──► TaskController ──► TaskService ──► TaskRepository ──► PostgreSQL
   │                                          ▲
   └── /api/chat ───► ChatController ──► ChatService (ChatClient + memória)
                                              │
                                              ▼
                                  Ollama (LLM) ◄──► TaskTools ──► TaskService
```

O `ChatService` envia a mensagem do usuário ao modelo junto com um *system prompt* (regras, categorias, prioridades e data atual). Quando o modelo decide agir, ele chama um método de `TaskTools`, que reaproveita o mesmo `TaskService` usado pela API REST, então as regras de negócio e validações são compartilhadas.

## Pré-requisitos

- [Java 21](https://adoptium.net/) (JDK)
- [Docker](https://www.docker.com/) e Docker Compose
- [Ollama](https://ollama.com/) instalado e em execução
- Maven não é necessário: o projeto inclui o Maven Wrapper (`mvnw`)

## Como executar

### 1. Clonar o repositório

```bash
git clone <url-do-repositorio>
cd assistente-tarefas-api
```

### 2. Criar o arquivo `.env`

Na raiz do projeto, crie um arquivo `.env` com as senhas do banco e do pgAdmin:

```env
DB_PASSWORD=sua_senha_do_postgres
PGADMIN_PASSWORD=sua_senha_do_pgadmin
```

> O arquivo `.env` está no `.gitignore`. Nunca o envie ao repositório.

### 3. Subir o PostgreSQL e o pgAdmin

```bash
docker compose up -d
```

O Docker Compose lê o `.env` automaticamente.

| Serviço | Endereço | Credenciais |
| --- | --- | --- |
| PostgreSQL | `localhost:5432` (banco `assistente_tarefas`, usuário `postgres`) | senha: `DB_PASSWORD` |
| pgAdmin | http://localhost:5050 | e-mail `admin@assistente.com`, senha: `PGADMIN_PASSWORD` |

### 4. Baixar o modelo no Ollama

```bash
ollama pull llama3.2:1b
```

Garanta que o Ollama esteja rodando em `http://localhost:11434` (padrão).

### 5. Exportar `DB_PASSWORD` e iniciar a aplicação

O Spring Boot **não lê o arquivo `.env`**, ele lê variáveis de ambiente. Por isso, exporte `DB_PASSWORD` antes de rodar:

**Linux / macOS**

```bash
export DB_PASSWORD=sua_senha_do_postgres
./mvnw spring-boot:run
```

**Windows (PowerShell)**

```powershell
$env:DB_PASSWORD="sua_senha_do_postgres"
.\mvnw.cmd spring-boot:run
```

**IntelliJ IDEA:** em *Run > Edit Configurations > Environment variables*, adicione `DB_PASSWORD=sua_senha_do_postgres`.

A API ficará disponível em **http://localhost:8080**. Na primeira execução, o Flyway cria a tabela `tasks` automaticamente.

## Variáveis de ambiente

| Variável | Obrigatória | Descrição |
| --- | --- | --- |
| `DB_PASSWORD` | Sim | Senha do PostgreSQL (usada pelo Compose e pela aplicação) |
| `PGADMIN_PASSWORD` | Sim (Compose) | Senha do usuário admin do pgAdmin |

Outras configurações ficam em `src/main/resources/application.properties`:

| Propriedade | Valor padrão |
| --- | --- |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/assistente_tarefas` |
| `spring.ai.ollama.base-url` | `http://localhost:11434` |
| `spring.ai.ollama.chat.options.model` | `llama3.2:1b` |

## Documentação da API

Com a aplicação rodando, acesse o Swagger UI:

- http://localhost:8080/swagger-ui/index.html
- Especificação OpenAPI (JSON): http://localhost:8080/v3/api-docs

### Endpoints

| Método | Rota | Descrição | Resposta de sucesso |
| --- | --- | --- | --- |
| `GET` | `/api/tasks` | Lista todas as tarefas | `200` |
| `GET` | `/api/tasks/{id}` | Busca uma tarefa por ID | `200` |
| `POST` | `/api/tasks` | Cria uma tarefa | `201` |
| `PUT` | `/api/tasks/{id}` | Atualiza uma tarefa | `200` |
| `DELETE` | `/api/tasks/{id}` | Exclui uma tarefa | `204` |
| `POST` | `/api/chat` | Envia uma mensagem ao assistente de IA | `200` |

### Modelo de dados

**Prioridade (`priority`):** `LOW`, `MEDIUM`, `HIGH`
**Categoria (`category`):** `STUDY`, `WORK`, `PERSONAL`, `OTHER`

| Campo | Tipo | Regras |
| --- | --- | --- |
| `title` | string | Obrigatório, até 150 caracteres |
| `description` | string | Opcional, até 500 caracteres |
| `priority` | enum | Obrigatório |
| `category` | enum | Obrigatório |
| `dueDate` | data (`YYYY-MM-DD`) | Opcional, não pode estar no passado |
| `completed` | boolean | Apenas na atualização; novas tarefas começam como `false` |

### Formato dos erros

```json
// 400: erro de validação
{
  "timestamp": "2026-11-12T10:30:00",
  "status": 400,
  "error": "Erro de validação",
  "messages": { "title": "O título é obrigatório" }
}

// 404: tarefa não encontrada
{
  "timestamp": "2026-11-12T10:30:00",
  "status": 404,
  "error": "Não encontrado",
  "message": "Tarefa não encontrada"
}
```

## Exemplos de uso

### Criar uma tarefa

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Estudar Spring AI",
    "description": "Ler a documentação sobre tool calling",
    "priority": "HIGH",
    "category": "STUDY",
    "dueDate": "2026-12-01"
  }'
```

### Atualizar / concluir uma tarefa

```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Estudar Spring AI",
    "description": "Ler a documentação sobre tool calling",
    "completed": true,
    "priority": "HIGH",
    "category": "STUDY",
    "dueDate": "2026-12-01"
  }'
```

### Conversar com o assistente

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "conversationId": "minha-conversa-1",
    "message": "Crie uma tarefa de prioridade alta para entregar o relatório do estágio amanhã"
  }'
```

Resposta:

```json
{ "response": "Tarefa criada com sucesso! ..." }
```

Use o mesmo `conversationId` nas mensagens seguintes para manter o contexto da conversa (por exemplo, *"marque essa tarefa como concluída"*).

## Assistente de IA

O assistente é guiado por um *system prompt* que o instrui a:

- responder sempre em português brasileiro;
- **não inventar** tarefas, quantidades ou datas de vencimento;
- inferir automaticamente categoria e prioridade ao criar tarefas;
- localizar tarefas pelo título (`searchTasksByTitle`) em vez de pedir o ID, e pedir confirmação quando houver várias correspondências;
- interpretar "hoje", "amanhã" e "depois de amanhã" a partir da data atual.

As ferramentas disponíveis estão em `tools/TaskTools.java`:

| Ferramenta | O que faz |
| --- | --- |
| `getAllTasks` | Lista todas as tarefas |
| `getTaskById` | Busca por ID |
| `searchTasksByTitle` | Busca por título (parcial, sem diferenciar maiúsculas) |
| `getTasksByCategory` | Filtra por categoria |
| `getOverdueTasks` | Tarefas não concluídas com vencimento anterior a hoje |
| `getTasksDueToday` | Tarefas que vencem hoje |
| `countPendingTasks` | Conta tarefas não concluídas |
| `createTask` | Cria uma tarefa |
| `updateTask` | Atualiza uma tarefa |
| `setTaskCompletion` | Marca como concluída ou pendente |
| `deleteTask` | Exclui uma tarefa |

> **Dica:** o modelo padrão (`llama3.2:1b`) é muito leve e pode errar chamadas de ferramentas. Para resultados mais confiáveis, use um modelo maior com suporte a tool calling (por exemplo `llama3.1:8b` ou `qwen2.5:7b`) e ajuste `spring.ai.ollama.chat.options.model`.

## Testes

```bash
./mvnw test
```

Os testes usam o profile `test` com banco **H2 em memória**, então não precisam de PostgreSQL nem de Ollama em execução.

| Teste | O que cobre |
| --- | --- |
| `AssistenteTarefasApiApplicationTests` | Carregamento do contexto da aplicação |
| `TaskRepositoryTest` | Persistência e busca de tarefas com Spring Data JPA |

## Estrutura do projeto

```
assistente-tarefas-api/
├── compose.yaml                      # PostgreSQL + pgAdmin
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/tsu/assistente_tarefas_api/
    │   │   ├── config/               # OpenAPI e memória do chat
    │   │   ├── controller/           # TaskController e ChatController
    │   │   ├── dto/                  # Records de request/response
    │   │   ├── entity/               # Task, Priority, Category
    │   │   ├── exception/            # Tratamento global de erros
    │   │   ├── mapper/               # Conversão entidade ⇄ DTO
    │   │   ├── repository/           # TaskRepository (Spring Data JPA)
    │   │   ├── service/              # TaskService e ChatService
    │   │   └── tools/                # Ferramentas expostas à IA
    │   └── resources/
    │       ├── application.properties
    │       └── db/migration/         # Migrações Flyway
    └── test/                         # Testes e configuração do profile test
```

## Limitações conhecidas e próximos passos

- A memória do chat é **em processo** (`MessageWindowChatMemory`): é perdida ao reiniciar a aplicação.
- Não há autenticação nem separação de tarefas por usuário.
- Ideias de evolução: cobrir controllers e services com testes, Dockerfile para a própria API, paginação e filtros em `GET /api/tasks`, autenticação (Spring Security) e streaming das respostas do assistente.

## Autor

Gabriel Tsuyoshi Sato
